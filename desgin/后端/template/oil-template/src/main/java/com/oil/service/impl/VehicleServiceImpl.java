package com.oil.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oil.constant.*;
import com.oil.dto.VehicleDTO;
import com.oil.dto.VehiclePageQueryDTO;
import com.oil.entity.*;
import com.oil.exception.ParamNotFoundException;
import com.oil.exception.RegionNotFindException;
import com.oil.exception.VehicleException;
import com.oil.mapper.clickhouse.TboxMapper;
import com.oil.mapper.postgres.*;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.VehicleService;
import com.oil.utils.DoubleUtil;
import com.oil.utils.UserHolder;
import com.oil.vo.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: VehicleServiceImpl
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Service
@Slf4j
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {
    @Autowired
    VehicleMapper vehicleMapper;
    @Autowired
    OilLifeMapper oilLifeMapper;
    @Autowired
    OilConfigMapper oilConfigMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TodoMapper todoMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public static final ExecutorService VEHICLE_EXECUTOR = Executors.newSingleThreadExecutor();
    public static final int RETRY_COUNT = 3;

    private VehicleService proxy;

    @PostConstruct
    private void startExecutor() {
        try {
            log.info("开启消费者组");
            stringRedisTemplate.opsForStream().createGroup(RedisConstant.VEHICLE_STREAM, "g1");
            log.info("开启新增车辆的线程池");
        } catch (Exception e) {
            log.info("已经存在Redis消息队列");
        }
        VEHICLE_EXECUTOR.submit(new addVehicleHandler());
    }

    @Override
    public Result<Set<String>> regionList() {
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user == null) {
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        List<String> provinceList = new ArrayList<>();
        if (UserConstant.ADMIN_PERMISSION.equals(user.getPermission())) {
            provinceList = vehicleMapper.getRegionProvinceList();
        } else {
            provinceList = vehicleMapper.getRegionProvinceListByCompany(user.getCompany());
        }
        if (provinceList == null) {
            throw new RegionNotFindException(MessageConstant.REGION_NOT_FOUND);
        }
        // 对找到的活动区域进行去重
        Set<String> regionList = new HashSet<>(provinceList);
        return Result.success(regionList);
    }

    @Override
    public Result<PageResult> pageQuery(VehiclePageQueryDTO vehiclePageQueryDTO) {
        if (vehiclePageQueryDTO == null) {
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_PAGE_QUERY_NOT_EXIST);
        }
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user == null) {
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        return getPageQuery(vehiclePageQueryDTO, user);
    }

    private Result<PageResult> getPageQuery(VehiclePageQueryDTO vehiclePageQueryDTO, User user) {
        Page<Vehicle> page = new Page<>(vehiclePageQueryDTO.getPage(), vehiclePageQueryDTO.getPageSize());
        LambdaQueryWrapper<Vehicle> lqw = new LambdaQueryWrapper<>();
        lqw.eq(null != vehiclePageQueryDTO.getRegion() && !vehiclePageQueryDTO.getRegion().equals(""), Vehicle::getDeviceLocationProvince, vehiclePageQueryDTO.getRegion())
                .eq(Vehicle::getIsDeleted, StatusConstant.DISABLE)
                .eq(UserConstant.EDITOR_PERMISSION.equals(user.getPermission()), Vehicle::getCompany, user.getCompany())
                .gt(null != vehiclePageQueryDTO.getStartDateTime(), Vehicle::getLastChangeDatetime, vehiclePageQueryDTO.getStartDateTime())
                .lt(null != vehiclePageQueryDTO.getEndDateTime(), Vehicle::getLastChangeDatetime, vehiclePageQueryDTO.getEndDateTime());
        lqw.and(null != vehiclePageQueryDTO.getInformation() && !vehiclePageQueryDTO.getInformation().equals(""),
                w -> w.like(Vehicle::getDeviceNo, vehiclePageQueryDTO.getInformation())
                        .or()
                        .like(Vehicle::getDeviceName, vehiclePageQueryDTO.getInformation())
                        .or()
                        .like(Vehicle::getDeviceLocation, vehiclePageQueryDTO.getInformation())
                        .or()
                        .like(Vehicle::getDeviceManager, vehiclePageQueryDTO.getInformation())).orderByDesc(Vehicle::getUpdateTime);
        page = vehicleMapper.selectPage(page, lqw);
        List<Vehicle> vehicleList = page.getRecords();
        if (page.getTotal() == 0 || vehicleList.size() == 0) {
            return Result.success(new PageResult(page.getTotal(), new ArrayList<VehiclePageQueryVO>()));
        }
        List<String> vehicleIdList = vehicleList.stream().map(Vehicle::getDeviceNo).toList();
        List<HashMap<String, Object>> oilLifeList = oilLifeMapper.getOilLifeMap(vehicleIdList);
        Map<String, Double> oilLifeMap = convertListToMap(oilLifeList);
        if (vehicleList.size() != oilLifeList.size()) {
            throw new VehicleException(VehicleConstant.VEHICLE_OIL_LIFE_NOT_EXIST);
        }
        List<VehiclePageQueryVO> vehiclePageQueryVOList = new ArrayList<>();
        for (Vehicle vehicle : vehicleList) {
            VehiclePageQueryVO vehicleVO = new VehiclePageQueryVO();
            BeanUtils.copyProperties(vehicle, vehicleVO);
            if (oilLifeMap.get(vehicle.getDeviceNo()) > OilStatusConstant.HEALTHY_THRESHOLD) {
                vehicleVO.setOilStatus(OilStatusConstant.HEALTH);
            } else if (oilLifeMap.get(vehicle.getDeviceNo()) < OilStatusConstant.DANGER_THRESHOLD) {
                vehicleVO.setOilStatus(OilStatusConstant.DANGER);
            } else {
                vehicleVO.setOilStatus(OilStatusConstant.AGED);
            }
            vehiclePageQueryVOList.add(vehicleVO);
        }
        return Result.success(new PageResult(page.getTotal(), vehiclePageQueryVOList));
    }

    @Override
    public Result<VehicleVO> queryByDeviceNo(String deviceNo) {
        if (deviceNo == null) {
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        LambdaQueryWrapper<Vehicle> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Vehicle::getDeviceNo, deviceNo).eq(Vehicle::getIsDeleted, StatusConstant.DISABLE);
        Vehicle vehicle = getOne(lqw);
        if (vehicle == null) {
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_NOT_EXIST);
        }
        VehicleVO vehicleVO = new VehicleVO();
        BeanUtils.copyProperties(vehicle, vehicleVO);
        vehicleVO.setLastChangeDatetime(vehicle.getLastChangeDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return Result.success(vehicleVO);
    }

    @Override
    public Result deleteByDeviceNoList(List<String> deviceNoList) {
        if (deviceNoList == null || deviceNoList.isEmpty()) {
            throw new ParamNotFoundException(VehicleConstant.VEHICLE_LIST_IS_EMPTY);
        }
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user == null) {
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        if (UserConstant.ADMIN_PERMISSION.equals(user.getPermission())) {
            log.info("admin {} delete vehicle", user.getUsername());
            VehicleService proxy = (VehicleService) AopContext.currentProxy();
            return proxy.deleteByDeviceNoListByAdmin(deviceNoList);
        } else {
            log.info("editor {} delete vehicle", user.getUsername());
            return submitDeleteByEditor(deviceNoList, user);
        }
    }

    public Result deleteByDeviceNoListByAdmin(List<String> deviceNoList) {
        //        删除车辆的信息需要同时操作几个数据库表：
        //        1、将车辆信息表中对应的车辆的逻辑删除键置为1
        //        2、将车辆机油寿命配置表的逻辑删除键置为1
        vehicleMapper.deleteByDeviceNoList(deviceNoList);
        oilConfigMapper.deleteByDeviceNoList(deviceNoList);
        return Result.success();
    }

    private Result submitDeleteByEditor(List<String> deviceNoList, User user) {
        LocalDateTime now = LocalDateTime.now();
        String operation = TodoConstant.VEHICLE_PREFIX + deviceNoList.toString();
        Todo todo = Todo.builder()
                .username(user.getUsername())
                .company(user.getCompany())
                .submitType(TodoConstant.DELETE_VEHICLE_TYPE)
                .operation(operation)
                .parameter(JSON.toJSONString(deviceNoList))
                .todoStatus(TodoConstant.TODO_STATUS)
                .isDeleted(StatusConstant.DISABLE)
                .createTime(now)
                .updateTime(now)
                .build();
        todoMapper.insert(todo);
        return Result.success();
    }

    @Override
    public Result addVehicle(VehicleDTO vehicleDTO) {
        if (vehicleDTO == null) {
            throw new ParamNotFoundException(VehicleConstant.ADDED_VEHICLE_IS_NULL);
        }
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user == null) {
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        if (UserConstant.ADMIN_PERMISSION.equals(user.getPermission())) {
            // 使用了代理对象的方法，避免事务失效
            log.info("admin {} add vehicle", user.getUsername());
            proxy = (VehicleService) AopContext.currentProxy();
            return proxy.addVehicleByAdmin(vehicleDTO, user);
        } else {
            log.info("editor {} add vehicle", user.getUsername());
            return submitAddOrEditByEditor(vehicleDTO, user, TodoConstant.ADD_VEHICLE_TYPE);
        }
    }

    private class addVehicleHandler implements Runnable {
        RecordId recordId;

        @Override
        public void run() {
//            int count = 0;
            while (true) {
                try {
                    // 从redis消息队列中获取到任务, XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.vehicle
                    List<MapRecord<String, Object, Object>> readList = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(RedisConstant.VEHICLE_STREAM, ReadOffset.lastConsumed())
                    );
                    if (readList == null || readList.isEmpty()) {
                        continue;
                    }
                    // 解析消息队列里面的信息
                    log.info("异步新增车辆....");
                    MapRecord<String, Object, Object> record = readList.get(0);
                    recordId = record.getId();
                    VehicleDTO vehicleDTO = BeanUtil.fillBeanWithMap(record.getValue(), new VehicleDTO(), true);
                    User user = BeanUtil.fillBeanWithMap(record.getValue(), new User(), true);
                    Double deviceAccumulatedRuntime = Double.parseDouble((String) record.getValue().get("deviceAccumulatedRuntime"));
                    proxy.handleAddVehicle(vehicleDTO, user, deviceAccumulatedRuntime);
                    // ACK
                    stringRedisTemplate.opsForStream().acknowledge(RedisConstant.VEHICLE_STREAM, "g1", recordId);
                } catch (VehicleException e) {
                    log.error("id为：" + recordId + e.getMessage());
                    // ACK
                    stringRedisTemplate.opsForStream().acknowledge(RedisConstant.VEHICLE_STREAM, "g1", recordId);
                } catch (Exception e) {
//                    e.printStackTrace();
                    log.error("id为：" + recordId + "的消息队列出现异常，处理pending list的队列,异常为:" + e.getMessage());
                    handlePendingList();
                }
            }
        }
    }

    RecordId pendingId;

    private void handlePendingList() {
        int count = RETRY_COUNT;
        while (true) {
            try {
                // 从redis消息队列中获取到任务, XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.vehicle
                List<MapRecord<String, Object, Object>> readList = stringRedisTemplate.opsForStream().read(
                        Consumer.from("g1", "c1"),
                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                        StreamOffset.create(RedisConstant.VEHICLE_STREAM, ReadOffset.from("0"))
                );
                if (readList == null || readList.isEmpty()) {
                    break;
                }
                // 解析消息队列里面的信息
                MapRecord<String, Object, Object> record = readList.get(0);
                pendingId = record.getId();
                VehicleDTO vehicleDTO = BeanUtil.fillBeanWithMap(record.getValue(), new VehicleDTO(), true);
                User user = BeanUtil.fillBeanWithMap(record.getValue(), new User(), true);
                Double deviceAccumulatedRuntime = Double.parseDouble((String) record.getValue().get("deviceAccumulatedRuntime"));
                proxy.handleAddVehicle(vehicleDTO, user, deviceAccumulatedRuntime);
                // ACK
                stringRedisTemplate.opsForStream().acknowledge(RedisConstant.VEHICLE_STREAM, "g1", pendingId);
            } catch (VehicleException e) {
                log.error(e.getMessage());
                // ACK
                stringRedisTemplate.opsForStream().acknowledge(RedisConstant.VEHICLE_STREAM, "g1", pendingId);
            } catch (Exception e) {
                log.error("id为：" + pendingId + "的pending list消息队列出现异常,异常为:" + e.getMessage());
                count--;
                try {
                    Thread.sleep(20);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                if (count == 0) {
                    // ACK
                    log.error("id为:" + pendingId + "的任务连续三次无法完成消费！");
                    stringRedisTemplate.opsForStream().acknowledge(RedisConstant.VEHICLE_STREAM, "g1", pendingId);
                    break;
                }
                ;
            }
        }
    }

    public Result addVehicleByAdmin(VehicleDTO vehicleDTO, User user) {
        // 前置判断：1、如果车辆的数据没有存在数据库，则新增失败
        //         2、如果数据库已经存在这台车，新增失败
        Vehicle vehicle = getOne(new LambdaQueryWrapper<Vehicle>()
                .eq(null != vehicleDTO.getDeviceNo(), Vehicle::getDeviceNo, vehicleDTO.getDeviceNo()));
        if (vehicle != null && StatusConstant.DISABLE.equals(vehicle.getIsDeleted())) {
            // 如果没有被删除说明已经存在
            throw new VehicleException(VehicleConstant.VEHICLE_ALREADY_EXIST);
        }
        Double deviceAccumulatedRuntime = getDeviceAccumulatedRuntime(vehicleDTO.getDeviceNo());
        if (deviceAccumulatedRuntime == null) {
            throw new VehicleException(VehicleConstant.VEHICLE_ACCUMULATED_RUNTIME_NOT_FOUND);
        }
        // 将任务放到redis消息队列进行异步进行
        Map<String, Object> map = new HashMap<>();
        BeanUtil.beanToMap(vehicleDTO, map, CopyOptions.create().setFieldValueEditor((k, v) -> {
            if (v instanceof LocalDateTime) {
                String format = "yyyy-MM-dd HH:mm:ss";
                return ((LocalDateTime) v).format(DateTimeFormatter.ofPattern(format));
            }
            return v.toString();
        }));
        map.put("username", user.getUsername());
        map.put("company", user.getCompany());
        map.put("deviceAccumulatedRuntime", deviceAccumulatedRuntime.toString());
        RecordId recordId = stringRedisTemplate.opsForStream().add(RedisConstant.VEHICLE_STREAM, map);
        if (recordId == null) {
            throw new VehicleException(RedisConstant.ADD_STREAM_FAIL);
        }
        return Result.success();
    }

    public Result handleAddVehicle(VehicleDTO vehicleDTO, User user, Double deviceAccumulatedRuntime) {
        LocalDateTime now = LocalDateTime.now();
        OilConfig oilConfig = null;
        Double lastChangeAccumulatedRuntime = null;
        Vehicle vehicle = getOne(new LambdaQueryWrapper<Vehicle>()
                .eq(null != vehicleDTO.getDeviceNo(), Vehicle::getDeviceNo, vehicleDTO.getDeviceNo()));
        if (vehicle != null) {
            // 说明之前这辆车存在过数据库里面
            if (StatusConstant.DISABLE.equals(vehicle.getIsDeleted())) {
                // 如果没有被删除说明已经存在
                throw new VehicleException(VehicleConstant.VEHICLE_ALREADY_EXIST);
            }
            // 激活之前的车辆配置信息
            if (!vehicle.getDeviceLocationCity().equals(vehicleDTO.getDeviceLocationCity())) {
                int count = getDeviceLocationId(vehicleDTO);
                String deviceName = vehicleDTO.getDeviceLocationCity() + count;
                vehicle.setDeviceName(deviceName);
                vehicle.setDeviceLocationId(count);
            }
            BeanUtils.copyProperties(vehicleDTO, vehicle);
            vehicle.setDeviceAccumulatedRuntime(deviceAccumulatedRuntime);
            vehicle.setCreateUser(user.getUsername());
            vehicle.setCompany(user.getCompany());
            vehicle.setIsDeleted(StatusConstant.DISABLE);
            vehicle.setUpdateTime(now);
            vehicleMapper.updateById(vehicle);
            // 模拟异常
//            int a = 1/0;
            // 激活之前的机油配置表
            oilConfig = oilConfigMapper.selectOne(new LambdaQueryWrapper<OilConfig>()
                    .eq(null != vehicleDTO.getDeviceNo(), OilConfig::getDeviceNo, vehicleDTO.getDeviceNo())
                    .orderByDesc(OilConfig::getCreateTime)
                    .last("limit 1"));
            if (oilConfig == null) {
                throw new VehicleException(VehicleConstant.VEHICLE_CONFIG_NOT_FOUND);
            }
            lastChangeAccumulatedRuntime = getLastChangeAccumulatedRuntime(vehicleDTO.getDeviceNo(), vehicleDTO.getLastChangeDatetime(), deviceAccumulatedRuntime);
            oilConfig.setChangeCount(vehicleDTO.getChangeCount());
            oilConfig.setLastChangeAccumulatedRuntime(lastChangeAccumulatedRuntime);
            oilConfig.setLastChangeDatetime(vehicleDTO.getLastChangeDatetime());
            oilConfig.setIsDeleted(StatusConstant.DISABLE);
            oilConfig.setUpdateTime(LocalDateTime.now());
//            oilConfigMapper.updateById(oilConfig);
        } else {
            // 说明这台车之前没有存进过数据库,需要进行以下操作：
            // 1.新增车辆到车辆数据库
            int count = getDeviceLocationId(vehicleDTO);
            String deviceName = vehicleDTO.getDeviceLocationCity() + count;
            Vehicle newVehicle = Vehicle.builder().build();
            BeanUtils.copyProperties(vehicleDTO, newVehicle);
            newVehicle.setDeviceName(deviceName);
            newVehicle.setDeviceLocationId(count);
            newVehicle.setDeviceAccumulatedRuntime(deviceAccumulatedRuntime);
            newVehicle.setCreateUser(user.getUsername());
            newVehicle.setCompany(user.getCompany());
            newVehicle.setIsDeleted(StatusConstant.DISABLE);
            newVehicle.setUpdateTime(now);
            newVehicle.setCreateTime(now);
            save(newVehicle);
            // 模拟异常
            // 2. 新增车辆对应的机油配置信息到机油配置信息数据库
            oilConfig = oilConfigMapper.selectOne(new LambdaQueryWrapper<OilConfig>()
                    .eq(OilConfig::getDeviceNo, OilConfigConstant.INIT_CONFIG)
                    .orderByDesc(OilConfig::getCreateTime)
                    .last("limit 1"));
            lastChangeAccumulatedRuntime = getLastChangeAccumulatedRuntime(vehicleDTO.getDeviceNo(), vehicleDTO.getLastChangeDatetime(), deviceAccumulatedRuntime);
            oilConfig.setId(null);
            oilConfig.setDeviceNo(vehicleDTO.getDeviceNo());
            oilConfig.setChangeCount(vehicleDTO.getChangeCount());
            oilConfig.setLastChangeAccumulatedRuntime(lastChangeAccumulatedRuntime);
            oilConfig.setLastChangeDatetime(vehicleDTO.getLastChangeDatetime());
            oilConfig.setUpdateTime(now);
            oilConfig.setCreateTime(now);
//            oilConfigMapper.insert(oilConfig);
        }
        // 3. 新增一条默认的机油寿命数据到机油寿命信息数据库
        OilLife initOilLife = oilLifeMapper.selectOne(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, OilConfigConstant.INIT_LIFE));
        initOilLife.setId(null);
        initOilLife.setDeviceNo(vehicleDTO.getDeviceNo());
        initOilLife.setDeviceAccumulatedRuntime(deviceAccumulatedRuntime);
        Long maxLife = oilConfig.getOilMaxLife();
        initOilLife.setLastChangeAccumulatedRuntime(lastChangeAccumulatedRuntime);
        double oilRunTime = deviceAccumulatedRuntime - lastChangeAccumulatedRuntime;
        oilRunTime = (oilRunTime * 3600) < maxLife ? DoubleUtil.round(oilRunTime, 2) : 0.00;
        initOilLife.setOilRuntime(oilRunTime);
        Double oilLifeTimePercent = (maxLife - oilRunTime * 3600) / maxLife;
        oilConfig.setOilRemainLife(maxLife * oilLifeTimePercent);
        if (vehicle != null) {
            oilConfigMapper.updateById(oilConfig);
        } else {
            oilConfigMapper.insert(oilConfig);
        }
        initOilLife.setOilLifeTimePercent(oilLifeTimePercent);
        initOilLife.setLastChangeDatetime(vehicleDTO.getLastChangeDatetime());
        initOilLife.setChangeCount(vehicleDTO.getChangeCount());
        initOilLife.setDatetime(now);
        initOilLife.setCreateTime(now);
        initOilLife.setUpdateTime(now);
        oilLifeMapper.insert(initOilLife);
        return Result.success();
    }


    @Override
    public Result editVehicle(VehicleDTO vehicleDTO) {
        if (vehicleDTO == null) {
            throw new ParamNotFoundException(VehicleConstant.ADDED_VEHICLE_IS_NULL);
        }
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user == null) {
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        if (UserConstant.ADMIN_PERMISSION.equals(user.getPermission())) {
            log.info("admin {} edit vehicle", user.getUsername());
            return editVehicleByAdmin(vehicleDTO);
        } else {
            log.info("editor {} edit vehicle", user.getUsername());
            return submitAddOrEditByEditor(vehicleDTO, user, TodoConstant.EDIT_VEHICLE_TYPE);
        }
    }

    public Result editVehicleByAdmin(VehicleDTO vehicleDTO) {
        Vehicle vehicle = getOne(new LambdaQueryWrapper<Vehicle>()
                .eq(null != vehicleDTO.getDeviceNo(), Vehicle::getDeviceNo, vehicleDTO.getDeviceNo())
                .eq(Vehicle::getIsDeleted, StatusConstant.DISABLE));
        if (vehicle == null) {
            throw new VehicleException(VehicleConstant.VEHICLE_NOT_EXIST);
        }
        boolean isChangeName = !vehicleDTO.getDeviceLocation().equals(vehicle.getDeviceLocation()) ||
                !vehicleDTO.getDeviceLocationCity().equals(vehicle.getDeviceLocationCity());
        // 如果车辆修改了地区，需要对车辆进行名称修改
        BeanUtils.copyProperties(vehicleDTO, vehicle);
        if (isChangeName) {
            vehicle.setDeviceName(vehicleDTO.getDeviceLocationCity() + getDeviceLocationId(vehicleDTO));
        }
        vehicle.setUpdateTime(LocalDateTime.now());
        updateById(vehicle);
        return Result.success();
    }

    @Override
    public Result<List<String>> deviceNoList() {
        List<String> deviceNoList = vehicleMapper.getDeviceNoList();
        return Result.success(deviceNoList);
    }

    @Override
    public Result<VehicleDetailVO> detail(String deviceNo) {
        if (deviceNo == null || "".equals(deviceNo)) {
            throw new VehicleException(VehicleConstant.VEHICLE_DEVICE_NO_NOT_EXIST);
        }
        Vehicle vehicle = getOne(new LambdaQueryWrapper<Vehicle>().eq(Vehicle::getDeviceNo, deviceNo));
        if (vehicle == null) {
            throw new VehicleException(VehicleConstant.VEHICLE_NOT_EXIST);
        }
        VehicleDetailVO vehicleDetailVO = new VehicleDetailVO();
        BeanUtils.copyProperties(vehicle, vehicleDetailVO);
        OilLife oilLife = oilLifeMapper.selectOne(new LambdaQueryWrapper<OilLife>()
                .eq(OilLife::getDeviceNo, deviceNo)
                .eq(OilLife::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(OilLife::getCreateTime)
                .last("limit 1"));
        if (oilLife == null) {
            throw new VehicleException(VehicleConstant.VEHICLE_OIL_LIFE_NOT_EXIST);
        }
        vehicleDetailVO.setOilRuntime(oilLife.getOilRuntime());
        vehicleDetailVO.setOilLifeTimePercent(oilLife.getOilLifeTimePercent());
        return Result.success(vehicleDetailVO);
    }

    private Result submitAddOrEditByEditor(VehicleDTO vehicleDTO, User user, Integer submitType) {
        LocalDateTime now = LocalDateTime.now();
        String operation = TodoConstant.VEHICLE_PREFIX + vehicleDTO.getDeviceNo();
        Todo todo = Todo.builder()
                .username(user.getUsername())
                .company(user.getCompany())
                .submitType(submitType)
                .operation(operation)
                .parameter(JSON.toJSONString(vehicleDTO))
                .todoStatus(TodoConstant.TODO_STATUS)
                .isDeleted(StatusConstant.DISABLE)
                .createTime(now)
                .updateTime(now)
                .build();
        todoMapper.insert(todo);
        return Result.success();
    }

    @Autowired
    TboxMapper tboxMapper;

    public Double getDeviceAccumulatedRuntime(String deviceNo) {
        // TODO:功能改进:当数据量很大的时候，sql语句查询会很慢
        Integer hours = tboxMapper.getTotalWorkingHours(deviceNo);
        if (hours == null) return null;
        return DoubleUtil.round(hours * 0.05, 2);
    }

    public Double getLastChangeAccumulatedRuntime(String deviceNo, LocalDateTime lastChangeDatetime, Double deviceAccumulatedRuntime) {
        String format = "yyyy-MM-dd HH:mm:ss";
        Tbox tbox = tboxMapper.getLastChangeAccumulatedRuntime(deviceNo, lastChangeDatetime.format(DateTimeFormatter.ofPattern(format)));
        // 如果没查到前面的tbox数据，则输出机油运行时间为0
        if (tbox == null) return deviceAccumulatedRuntime;
//        double lastChangeAccumulatedRuntime = deviceAccumulatedRuntime - DoubleUtil.round(tbox.getEngineTotalWorkingHours() * 0.05, 2);
//        return (lastChangeAccumulatedRuntime*3600)<maxLife? DoubleUtil.round(lastChangeAccumulatedRuntime, 2):0.00;
        return DoubleUtil.round(tbox.getEngineTotalWorkingHours() * 0.05, 2);
    }

    @Override
    public Result<List<VehicleRankVO>> rank() {
        List<Vehicle> vehicleList = vehicleMapper.selectList(new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getIsDeleted, StatusConstant.DISABLE)
                .orderByDesc(Vehicle::getDeviceAccumulatedRuntime)
                .last("limit 4"));
        List<VehicleRankVO> vehicleRankVOList = new ArrayList<>(vehicleList.size());
        List<String> vehicleIdList = vehicleList.stream().map(Vehicle::getDeviceNo).toList();
        List<HashMap<String, Object>> oilLifeList = oilLifeMapper.getOilLifeMap(vehicleIdList);
        Map<String, Double> oilLifeMap = convertListToMap(oilLifeList);
        for (Vehicle vehicle : vehicleList) {
            VehicleRankVO vehicleRankVO = new VehicleRankVO();
            BeanUtil.copyProperties(vehicle, vehicleRankVO);
            vehicleRankVO.setLastChangeDatetime(vehicle.getLastChangeDatetime().format(DateTimeFormatter.ofPattern(OilConfigConstant.LIFE_DATE_FORMAT)));
            if (oilLifeMap.get(vehicle.getDeviceNo()) > OilStatusConstant.HEALTHY_THRESHOLD) {
                vehicleRankVO.setOilStatus(OilStatusConstant.HEALTH);
            } else if (oilLifeMap.get(vehicle.getDeviceNo()) < OilStatusConstant.DANGER_THRESHOLD) {
                vehicleRankVO.setOilStatus(OilStatusConstant.DANGER);
            } else {
                vehicleRankVO.setOilStatus(OilStatusConstant.AGED);
            }
            vehicleRankVOList.add(vehicleRankVO);
        }
        return Result.success(vehicleRankVOList);
    }

    @Autowired
    OilChangeMapper oilChangeMapper;
    @Override
    public Result<DashBoardVO> accumulated() {
        long accumulatedVehicleCount = count(new LambdaQueryWrapper<Vehicle>()
                .eq(Vehicle::getIsDeleted, StatusConstant.DISABLE));
        Double oilRuntime = oilChangeMapper.getAccumulatedOilRuntime();
        double accumulatedOilRuntime = (oilRuntime==null)? 0.0:oilRuntime;
        long accumulatedOilChangeCount = oilChangeMapper.selectCount(new LambdaQueryWrapper<OilChange>());
        double accumulatedExtraRuntime = Math.max(0, accumulatedOilRuntime - accumulatedOilChangeCount*OilConfigConstant.OIL_STANDARD_RUNTIME);
        double accumulatedSavedChangeCount = Math.ceil(accumulatedExtraRuntime /OilConfigConstant.OIL_STANDARD_RUNTIME);
        double accumulatedSavingMoney = Math.ceil(accumulatedSavedChangeCount*OilConfigConstant.OIL_STANDARD_PRICE);
        DashBoardVO dashBoardVO = DashBoardVO.builder()
                .accumulatedExtraRuntime(accumulatedExtraRuntime)
                .accumulatedVehicleCount(accumulatedVehicleCount)
                .accumulatedSavedChangeCount(accumulatedSavedChangeCount)
                .accumulatedSavingMoney(accumulatedSavingMoney)
                .build();
        return Result.success(dashBoardVO);
    }

    @Override
    public Result<List<VehicleScatterVO>> vehicleScatter(String deviceLocationProvince) {
        if (deviceLocationProvince==null || "".equals(deviceLocationProvince)){
            throw new ParamNotFoundException(VehicleConstant.INVALID_CITY_NAME);
        }
        List<VehicleScatterVO> vehicleScatterVOList = new ArrayList<>();
        if ("city".equals(deviceLocationProvince)){
            return Result.success(vehicleScatterVOList);
        }else if ("全国".equals(deviceLocationProvince)){
            vehicleScatterVOList = vehicleMapper.getCountryScatter(deviceLocationProvince);
            vehicleScatterVOList.forEach(vehicleScatterVO -> {
                String provinceName = vehicleScatterVO.getCityName();
                vehicleScatterVO.setCityName(VehicleConstant.SPECIAL_CITIES_MAP.getOrDefault(provinceName, provinceName + "省"));
            });
        }else {
            if (VehicleConstant.DISTINCT_INVERSE_MAP.containsKey(deviceLocationProvince)){
                return Result.success(vehicleScatterVOList);
            }
            String provinceName = VehicleConstant.SPECIAL_CITIES_INVERSE_MAP.getOrDefault(deviceLocationProvince, deviceLocationProvince.substring(0, deviceLocationProvince.length()-1));
            vehicleScatterVOList = vehicleMapper.getCityScatter(provinceName);
        }
        return Result.success(vehicleScatterVOList);
    }

    private boolean isTimeDifferenceExceedThreshold(LocalDateTime time1, LocalDateTime time2, long thresholdHours) {
        // 计算两个时间的差值
        Duration duration = Duration.between(time1, time2);
        // 判断时间差是否超过阈值
        return Math.abs(duration.toHours()) > thresholdHours;
    }

    private Map<String, Double> convertListToMap(List<HashMap<String, Object>> list) {
        Map<String, Double> resultMap = new HashMap<>();
        for (HashMap<String, Object> map : list) {
            String device_no = (String) map.get("device_no");
            Double oilLifePercent = (Double) map.get("oil_life_time_percent");
            resultMap.put(device_no, oilLifePercent);
        }
        return resultMap;
    }

    private int getDeviceLocationId(VehicleDTO vehicleDTO) {
        int count = 1;
        Vehicle vehicle = getOne(new LambdaQueryWrapper<Vehicle>().eq(Vehicle::getDeviceLocationCity, vehicleDTO.getDeviceLocationCity()).orderByDesc(Vehicle::getDeviceName).last("limit 1"));
        if (vehicle != null) {
            int idx = 0;
            String deviceName = vehicle.getDeviceName();
            for (int i = 0; i < deviceName.length(); i++) {
                if (deviceName.charAt(i) >= '0' && deviceName.charAt(i) <= '9') {
                    idx = i;
                    break;
                }
            }
            count = Integer.parseInt(deviceName.substring(idx)) + 1;
        }
        return count;
    }
}
