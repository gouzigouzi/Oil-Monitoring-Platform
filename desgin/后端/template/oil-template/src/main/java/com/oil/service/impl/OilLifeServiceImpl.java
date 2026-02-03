package com.oil.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oil.constant.*;
import com.oil.dto.OilChangeDTO;
import com.oil.entity.OilChange;
import com.oil.entity.OilConfig;
import com.oil.entity.OilLife;
import com.oil.entity.Vehicle;
import com.oil.exception.ParamNotFoundException;
import com.oil.mapper.clickhouse.TboxMapper;
import com.oil.mapper.postgres.OilChangeMapper;
import com.oil.mapper.postgres.OilConfigMapper;
import com.oil.mapper.postgres.OilLifeMapper;
import com.oil.mapper.postgres.VehicleMapper;
import com.oil.result.Result;
import com.oil.service.OilLifeService;
import com.oil.service.VehicleService;
import com.oil.utils.DoubleUtil;
import com.oil.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: OilLifeServiceImpl
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Service
@Slf4j
public class OilLifeServiceImpl extends ServiceImpl<OilLifeMapper, OilLife> implements OilLifeService {
    @Autowired
    OilChangeMapper oilChangeMapper;
    @Override
    public Result<OilLifeVO> life(String deviceNo) {
        if (deviceNo == null || "".equals(deviceNo)){
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        List<String> date = new ArrayList<>();
        List<Double> life = new ArrayList<>();
        List<OilLife> oilLifeList = list(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, deviceNo)
                .eq(OilLife::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilLife::getDatetime)
                .last("limit " + OilConfigConstant.LIMIT_LIFE));
        if (oilLifeList == null || oilLifeList.size()==0){
            log.error(VehicleConstant.VEHICLE_OIL_LIFE_NOT_EXIST+":{}", deviceNo);
        }else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(OilConfigConstant.LIFE_DATE_FORMAT);
            for (int i=oilLifeList.size()-1;i>=0;i--){
                OilLife oilLife = oilLifeList.get(i);
                date.add(oilLife.getDatetime().format(fmt));
                double value = DoubleUtil.round(oilLife.getOilLifeTimePercent() * 100, 2);
                life.add(value);
            }
        }
        return Result.success(new OilLifeVO(date, life));
    }

    @Override
    public Result<EngineLineVO> engineLine(String deviceNo) {
        if (deviceNo == null || "".equals(deviceNo)){
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        List<OilLife> oilLifeList = list(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, deviceNo)
                .eq(OilLife::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilLife::getDatetime)
                .last("limit " + OilConfigConstant.LIMIT_LIFE));
        List<String> date = new ArrayList<>();
        List<Double> oilTempData = new ArrayList<>();
        List<Double> engineSpeedData = new ArrayList<>();
        List<Double> engineLoadData = new ArrayList<>();
        if (oilLifeList == null || oilLifeList.size()==0){
            log.error(VehicleConstant.VEHICLE_OIL_LIFE_NOT_EXIST+":{}", deviceNo);
        }else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(OilConfigConstant.LIFE_DATE_FORMAT);
            for (int i=oilLifeList.size()-1;i>=0;i--){
                OilLife oilLife = oilLifeList.get(i);
                date.add(oilLife.getDatetime().format(fmt));
                oilTempData.add(DoubleUtil.round(oilLife.getOilTemperatureAverage(), 2));
                engineSpeedData.add(DoubleUtil.round(oilLife.getEngineSpeedAverage()/OilConfigConstant.DIVISOR, 2));
                engineLoadData.add(DoubleUtil.round(oilLife.getEngineLoadAverage(), 2));
            }
        }
        return Result.success(new EngineLineVO(date, oilTempData, engineSpeedData, engineLoadData));
    }

    @Override
    public Result<EngineBarVO> engineBar(String deviceNo) {
        if (deviceNo == null || "".equals(deviceNo)){
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        List<String> date = new ArrayList<>();
        List<Double> oilTempData = new ArrayList<>();
        List<Double> engineSpeedData = new ArrayList<>();
        List<Double> engineLoadData = new ArrayList<>();
        List<OilLife> oilLifeList = list(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, deviceNo)
                .eq(OilLife::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilLife::getDatetime)
                .last("limit " + OilConfigConstant.LIMIT_LIFE_BAR));
        if (oilLifeList == null || oilLifeList.size()==0){
            log.error(VehicleConstant.VEHICLE_OIL_LIFE_NOT_EXIST+":{}", deviceNo);
        }else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(OilConfigConstant.LIFE_DATE_FORMAT);
            long sum;
            for (int i=oilLifeList.size()-1;i>=0;i--){
                OilLife oilLife = oilLifeList.get(i);
                date.add(oilLife.getDatetime().format(fmt));
                sum = oilLife.getOilTemperatureDangerCount() + oilLife.getEngineSpeedDangerCount() + oilLife.getEngineLoadDangerCount();
                double oilRatio = 0.333;
                double engineSpeedRatio = 0.333;
                double engineLoadRatio = 0.333;
                if (sum!=0){
                    oilRatio = (double) (oilLife.getOilTemperatureDangerCount()*1.0 / sum);
                    engineSpeedRatio = (double) (oilLife.getEngineSpeedDangerCount()*1.0 / sum);
                    engineLoadRatio = (double) (oilLife.getEngineLoadDangerCount()*1.0 / sum);
                }
                oilTempData.add(DoubleUtil.round(oilRatio*oilLife.getOilLifeLossPercent(), 4)*10);
                engineSpeedData.add(DoubleUtil.round(engineSpeedRatio*oilLife.getOilLifeLossPercent(), 4)*10);
                engineLoadData.add(DoubleUtil.round(engineLoadRatio*oilLife.getOilLifeLossPercent(), 4)*10);
            }
        }
        return Result.success(new EngineBarVO(date, oilTempData, engineSpeedData, engineLoadData));
    }

    @Override
    public Result<EnginePieVO> enginePie(String deviceNo) {
        if (deviceNo == null || "".equals(deviceNo)){
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        List<Long> value = new ArrayList<>();
        OilLife oilLife = getOne(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, deviceNo)
                .eq(OilLife::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilLife::getDatetime).last("limit 1"));
        if (oilLife == null){
            log.error(VehicleConstant.VEHICLE_OIL_LIFE_NOT_EXIST+":{}", deviceNo);
        }else {
            value.add(oilLife.getOilTemperatureDangerCount());
            value.add(oilLife.getEngineSpeedDangerCount());
            value.add(oilLife.getEngineLoadDangerCount());
        }
        return Result.success(new EnginePieVO(value));
    }

    @Override
    public Result<List<OilChangeVO>> changeHistory(String deviceNo) {
        if (deviceNo == null || "".equals(deviceNo)){
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        List<OilChangeVO> oilChangeVOList = new ArrayList<>();
        List<OilChange> oilChangeList = oilChangeMapper.selectList(new LambdaQueryWrapper<OilChange>()
                .eq(OilChange::getDeviceNo, deviceNo)
                .eq(OilChange::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilChange::getOilChangeDate)
                .last("limit " + OilConfigConstant.LIMIT_CHANGE));
        if (oilChangeList == null || oilChangeList.size() == 0){
            log.error(VehicleConstant.VEHICLE_OIL_CHANGE_NOT_EXIST+":{}", deviceNo);
        }else {
            for (OilChange oilChange : oilChangeList) {
                OilChangeVO oilChangeVO = new OilChangeVO();
                BeanUtils.copyProperties(oilChange, oilChangeVO);
                Double life = oilChange.getOilLifeTimePercent();
                if (life>OilStatusConstant.HEALTHY_THRESHOLD){
                    oilChangeVO.setOilStatus(OilStatusConstant.HEALTH);
                }else if (life>OilStatusConstant.DANGER_THRESHOLD){
                    oilChangeVO.setOilStatus(OilStatusConstant.AGED);
                }else {
                    oilChangeVO.setOilStatus(OilStatusConstant.DANGER);
                }
                oilChangeVOList.add(oilChangeVO);
            }
        }
        return Result.success(oilChangeVOList);
    }

    @Autowired
    VehicleMapper vehicleMapper;
    @Autowired
    OilLifeMapper oilLifeMapper;
    @Autowired
    OilConfigMapper oilConfigMapper;
    @Autowired
    TboxMapper tboxMapper;
    @Autowired
    VehicleService vehicleService;
    @Override
    public Result changeOil(OilChangeDTO oilChangeDTO) {
        if (oilChangeDTO == null){
            throw new ParamNotFoundException(OilLifeConstant.OIL_LIFE_DTO_NOT_EXIST);
        }
        String deviceNo = oilChangeDTO.getDeviceNo();
        if (deviceNo==null || "".equals(deviceNo)){
            throw new ParamNotFoundException(OilLifeConstant.OIL_LIFE_DTO_NOT_EXIST);
        }
        LocalDateTime lastChangeDatetime = oilChangeDTO.getLastChangeDatetime();
        LocalDateTime now = LocalDateTime.now();
        if (lastChangeDatetime==null){
            throw new ParamNotFoundException(OilLifeConstant.OIL_LIFE_DTO_NOT_EXIST);
        }
        // 判断换油时间的有效性：传递的换油时间不能比旧的换油时间更早
        Vehicle vehicle = vehicleMapper.selectOne(new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getDeviceNo, deviceNo)
                .eq(Vehicle::getIsDeleted, StatusConstant.DISABLE));
        if (vehicle==null){
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        if (vehicle.getLastChangeDatetime().isAfter(lastChangeDatetime)){
            throw new ParamNotFoundException(OilLifeConstant.INVALID_CHANGE_DATETIME);
        }
        Integer count = vehicle.getChangeCount();
        // 插入一条换油数据
        OilLife oilLife = oilLifeMapper.selectOne(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, deviceNo)
                .eq(OilLife::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilLife::getDatetime).last("limit 1"));
        if (oilLife==null){
            throw new ParamNotFoundException(OilLifeConstant.OIL_LIFE_NOT_FIND);
        }
        OilConfig oilConfig = oilConfigMapper.selectOne(new LambdaQueryWrapper<OilConfig>()
                .eq(OilConfig::getDeviceNo, deviceNo)
                .eq(OilConfig::getIsDeleted, StatusConstant.DISABLE));
        if (oilConfig==null){
            throw new ParamNotFoundException(OilConfigConstant.OIL_CONFIG_NOT_FIND);
        }
        OilChange oilChange = new OilChange();
        oilChange.setDeviceNo(deviceNo);
        oilChange.setOilRuntime(oilLife.getOilRuntime());
        oilChange.setOilChangeDate(lastChangeDatetime);
        oilChange.setOilChangeCount(count);
        oilChange.setOilLifeTimePercent(oilLife.getOilLifeTimePercent());
        oilChange.setCreateTime(now);
        oilChange.setUpdateTime(now);
        oilChange.setIsDeleted(StatusConstant.DISABLE);
        oilChangeMapper.insert(oilChange);
        // 修改车辆信息的数据和车辆的配置表数据
        vehicle.setChangeCount(count+1);
        vehicle.setLastChangeDatetime(lastChangeDatetime);
        vehicle.setUpdateTime(now);
        vehicleMapper.updateById(vehicle);
        oilConfig.setLastChangeDatetime(lastChangeDatetime);
        oilConfig.setChangeCount(count+1);
        oilConfig.setUpdateTime(now);
        // 计算剩余使用寿命
        Integer totalWorkingHours = tboxMapper.getTotalWorkingHours(deviceNo);
        if (totalWorkingHours == null){
            throw new ParamNotFoundException(OilLifeConstant.TOTAL_WORKING_HOURS_NOT_FIND);
        }
        Long maxLife = oilConfig.getOilMaxLife();
        double deviceAccumulatedRuntime = totalWorkingHours*0.05;
        Double lastChangeAccumulatedRuntime = vehicleService.getLastChangeAccumulatedRuntime(deviceNo, lastChangeDatetime, deviceAccumulatedRuntime);
        oilConfig.setLastChangeAccumulatedRuntime(lastChangeAccumulatedRuntime);
        double oilRunTime = deviceAccumulatedRuntime - lastChangeAccumulatedRuntime;
        oilRunTime = (oilRunTime*3600)<maxLife? DoubleUtil.round(oilRunTime, 2):0.00;
        Double oilLifeTimePercent = (maxLife-oilRunTime*3600)/maxLife;
        oilConfig.setOilRemainLife(maxLife-oilRunTime*3600);
        oilConfigMapper.updateById(oilConfig);
        // 插入一条新的机油数据
        oilLife.setId(null);
        oilLife.setDatetime(now);
        oilLife.setOilLifeTimePercent(oilLifeTimePercent);
        oilLife.setOilRuntime(oilRunTime);
        oilLife.setDeviceAccumulatedRuntime(deviceAccumulatedRuntime);
        oilLife.setLastChangeAccumulatedRuntime(lastChangeAccumulatedRuntime);
        oilLife.setLastChangeDatetime(lastChangeDatetime);
        oilLife.setChangeCount(count+1);
        oilLife.setCreateTime(now);
        oilLife.setUpdateTime(now);
        oilLifeMapper.insert(oilLife);
        return Result.success();
    }
}
