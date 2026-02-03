import datetime
import json
import clickhouse_driver
import numpy as np
import pandas as pd
import pytz
import sqlalchemy
from flask import Flask, jsonify

app = Flask(__name__)


def clean_data(data):
    # clean nan
    data = data[data['oil_temperature'] != 0].reset_index(drop=True)
    # oil_temp = oil_temp*0.03125-273
    data['oil_temperature'] = (data['oil_temperature'] * 0.03125 - 273).values
    # cooling_fluid_temperature = cooling_fluid_temperature - 40
    data['cooling_fluid_temperature'] = (data['cooling_fluid_temperature'] - 40).values
    # friction_torque = max(friction_torque-125,0)
    data['friction_torque'] = (data['friction_torque'] - 125).values
    data.loc[data['friction_torque'] < 0, 'friction_torque'] = np.zeros(
        data.loc[data['friction_torque'] < 0, 'friction_torque'].size)
    # engine_load_rat = max(engine_load_rat-125,0)
    data['engine_load_rat'] = (data['engine_load_rat'] - 125).values
    data.loc[data['engine_load_rat'] < 0, 'engine_load_rat'] = np.zeros(
        data.loc[data['engine_load_rat'] < 0, 'engine_load_rat'].size)
    # oil_pressure = oil_pressure*4
    data['oil_pressure'] = (data['oil_pressure'] * 4).values
    # engine_total_working_hours = engine_total_working_hours*0.05
    data['engine_total_working_hours'] = (data['engine_total_working_hours'] * 0.05).values
    data['engine_total_working_hours'].replace(0, np.nan, inplace=True)
    data['engine_total_working_hours'].ffill(inplace=True)
    return data


def get_temp_penalty(oil_temp, oil_param):
    if oil_temp < oil_param['oil_temperature_mild']:
        return oil_param['oil_temperature_default_penalty']
    elif oil_param['oil_temperature_mild'] < oil_temp < oil_param['oil_temperature_danger']:
        return oil_param['oil_temperature_mild_penalty']
    else:
        return oil_param['oil_temperature_danger_penalty']


def get_speed_penalty(speed, speed_param):
    if speed < speed_param['engine_speed_mild']:
        return speed_param['engine_speed_default_penalty']
    elif speed_param['engine_speed_mild'] < speed < speed_param['engine_speed_danger']:
        return speed_param['engine_speed_mild_penalty']
    else:
        return speed_param['engine_speed_danger_penalty']


def get_load_penalty(load, load_param):
    if load < load_param['engine_load_mild']:
        return load_param['engine_load_default_penalty']
    elif load_param['engine_load_mild'] < load < load_param['engine_load_danger']:
        return load_param['engine_load_mild_penalty']
    else:
        return load_param['engine_load_danger_penalty']


def get_age_penalty(age, age_param):
    if age < age_param['age_threshold']:
        return age_param['default_penalty']
    else:
        return age_param['default_penalty'] + (age / age_param['age_interval']) * age_param['age_rate']

@app.route('/hello')
def hello():
    return 'hello'


@app.route('/update/<device_no>')
def update_oil_life(device_no):
    response = {
        'code': 20000,
        'msg': '',
    }
    now = datetime.datetime.now(pytz.timezone('Asia/Shanghai'))
    yesterday = (now - datetime.timedelta(days=1)).strftime('%Y-%m-%d')
    now = now.strftime('%Y-%m-%d %H:%M:%S')
#    yesterday = '2023-09-10'
    device_no = device_no + "#"
    TBOX_SQL = "SELECT * FROM oildb.tboxdata WHERE t_date = %(date)s and device_no = %(device)s ORDER BY generate_time"
    values = {"date": yesterday, "device": device_no}
    with conn.cursor() as cursor:
        cursor.execute(TBOX_SQL, values)
        result = cursor.fetchall()
    if len(result) == 0:
        response['code'] = 0
        response['msg'] = device_no + ':data empty!'
        return jsonify(response)
    table_title = [field[0] for field in cursor.description]
    df = pd.DataFrame(result, columns=table_title)
    df = clean_data(df)
    if len(df) == 0:
        response['code'] = 0
        response['msg'] = device_no + ':valid data empty!'
        return jsonify(response)
    postgres_url = 'postgresql://' + postgres['user'] + ':' + postgres['password'] + \
                   '@' + postgres['host'] + '/postgres'
    engine = sqlalchemy.create_engine(postgres_url)
    OIL_CONFIG_SQL = '''
        SELECT * 
        FROM loader.loader_oil_config_tb 
        WHERE device_no = %(device)s 
        ORDER BY update_time DESC limit 1
    '''
    params = {'device': device_no}
    oil_config = pd.read_sql(OIL_CONFIG_SQL, engine, params=params)
    if oil_config.empty:
        response['code'] = 0
        response['msg'] = device_no + ':no oil config!'
        return jsonify(response)
    dict_oil_config = oil_config.to_dict(orient='index')[0]
    max_life = dict_oil_config['oil_max_life']
    remain_life = dict_oil_config['oil_remain_life']
    sample_time = dict_oil_config['sample_time']
    old_remain_life = remain_life
    for _, row in df.iterrows():
        remain_life = remain_life - sample_time * get_temp_penalty(row['oil_temperature'], dict_oil_config) * \
                       get_speed_penalty(row['engine_speed'], dict_oil_config) * \
                       get_load_penalty(row['engine_load_rat'], dict_oil_config) * get_age_penalty(
            row['engine_total_working_hours'], dict_oil_config)
    oil_config['oil_remain_life'] = remain_life
    # update oil config
    UPDATE_OIL_CONFIG_SQL = '''
        UPDATE loader.loader_oil_config_tb
        SET oil_remain_life = %(oil_remain_life)s ,
        update_time = %(update_time)s
        WHERE device_no = %(device)s AND is_deleted = 0
    '''
    update_oil_config_params = {'oil_remain_life': remain_life, 'update_time': now, 'device': device_no}
    with engine.connect() as connection:
        connection.execute(UPDATE_OIL_CONFIG_SQL, update_oil_config_params)
    # update vehicle accumulated runtime
    UPDATE_VEHICLE_SQL = '''
        UPDATE loader.loader_info_tb
        SET device_accumulated_runtime = %(device_accumulated_runtime)s,
        update_time = %(update_time)s
        WHERE device_no = %(device)s AND is_deleted = 0
    '''
    update_vehicle_params = {
        'device_accumulated_runtime': round(df.iloc[-1]['engine_total_working_hours'], 2),
        'update_time': now,
        'device': device_no
    }
    with engine.connect() as connection:
        connection.execute(UPDATE_VEHICLE_SQL, update_vehicle_params)
    # insert oil life
    OIL_LIFE_SQL = '''
        SELECT * 
        FROM loader.loader_oil_life_tb 
        WHERE device_no = %(device)s 
        ORDER BY update_time DESC limit 1
    '''
    init_life_params = {'device': 'init_life'}
    oil_life = pd.read_sql(OIL_LIFE_SQL, engine, params=init_life_params)
    oil_life.drop('id', axis=1, inplace=True)
    oil_life['device_no'] = device_no
    oil_life['datetime'] = now
    oil_life['oil_life_time_percent'] = remain_life / max_life
    oil_life['oil_runtime'] = round(df.iloc[-1]['engine_total_working_hours'] - oil_config['last_change_accumulated_runtime'], 2)
    oil_life['device_accumulated_runtime'] = round(df.iloc[-1]['engine_total_working_hours'], 2)
    oil_life['last_change_accumulated_runtime'] = round(oil_config['last_change_accumulated_runtime'], 2)
    oil_life['last_change_datetime'] = oil_config['last_change_datetime']
    oil_life['change_count'] = oil_config['change_count']
    oil_life['oil_temperature_danger_count'] = (df['oil_temperature']>dict_oil_config['oil_temperature_danger']).sum()
    oil_life['engine_speed_danger_count'] = (df['engine_speed']>dict_oil_config['engine_speed_danger']).sum()
    oil_life['engine_load_danger_count'] = (df['engine_load_rat']>dict_oil_config['engine_load_danger']).sum()
    oil_life['oil_life_loss_percent'] = (old_remain_life-remain_life) / max_life
    oil_life['oil_temperature_average'] = df['oil_temperature'].mean()
    oil_life['engine_speed_average'] = df['engine_speed'].mean()
    oil_life['engine_load_average'] = df['engine_load_rat'].mean()
    oil_life['update_time'] = now
    oil_life['create_time'] = now
    oil_life.to_sql("loader_oil_life_tb", engine, index=False, if_exists='append', schema="loader")
    return jsonify(response)


def read_config():
    with open("config.json") as json_file:
        config = json.load(json_file)
    return config


if __name__ == '__main__':
    config = read_config()
    clickhouse = config['clickhouse']
    conn = clickhouse_driver.connect(
        host=clickhouse['host'],
        port=clickhouse['port'],
        user=clickhouse['user'],
        password=clickhouse['password'],
        database=clickhouse['database']
    )
    postgres = config['postgres']
    app.run(host="0.0.0.0")
