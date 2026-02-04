package com.oil.mapper.clickhouse;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.OilChange;
import com.oil.entity.Tbox;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * ClassName: TboxMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/11/11
 * @Version 1.0
 */
@Mapper
public interface TboxMapper extends BaseMapper<Tbox> {
    @Select("select engine_total_working_hours from oildb.oil_tb where device_no = #{deviceNo} and oil_temperature > 0 order by generate_time desc limit 1")
    Integer getTotalWorkingHours(String deviceNo);

    @Select("SELECT * from oildb.oil_tb where device_no = #{deviceNo} and oil_temperature > 0 and engine_total_working_hours > 0 order by abs(toUnixTimestamp(generate_time) - toUnixTimestamp(#{lastChangeDatetime})) limit 1")
    Tbox getLastChangeAccumulatedRuntime(String deviceNo, String lastChangeDatetime);
}
