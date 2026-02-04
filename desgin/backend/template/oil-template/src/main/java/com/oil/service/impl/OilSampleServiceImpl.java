package com.oil.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oil.constant.*;
import com.oil.dto.OilSampleDTO;
import com.oil.dto.OilSamplePageQueryDTO;
import com.oil.entity.OilSample;
import com.oil.entity.Tbox;
import com.oil.entity.Todo;
import com.oil.entity.User;
import com.oil.exception.OilSampleException;
import com.oil.exception.ParamNotFoundException;
import com.oil.exception.VehicleException;
import com.oil.mapper.clickhouse.TboxMapper;
import com.oil.mapper.postgres.OilSampleMapper;
import com.oil.mapper.postgres.TodoMapper;
import com.oil.mapper.postgres.UserMapper;
import com.oil.mapper.postgres.VehicleMapper;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.OilSampleService;
import com.oil.utils.DoubleUtil;
import com.oil.utils.UserHolder;
import com.oil.vo.OilSamplePageQueryVO;
import com.oil.vo.OilSampleVO;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: OilSampleServiceImpl
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/13
 * @Version 1.0
 */
@Service
@Slf4j
public class OilSampleServiceImpl extends ServiceImpl<OilSampleMapper, OilSample> implements OilSampleService {
    @Autowired
    OilSampleMapper oilSampleMapper;
    @Autowired
    VehicleMapper vehicleMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    TodoMapper todoMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static final ExecutorService SAMPLE_EXECUTOR = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void startExecutor(){
        try {
            log.info("创建机油样本的消费者组");
            stringRedisTemplate.opsForStream().createGroup(RedisConstant.SAMPLE_STREAM, "g1");
        }catch (Exception e){
            log.info("Redis的消息队列已存在");
        }
        log.info("开启机油样本更新线程池");
//        SAMPLE_EXECUTOR.submit(new updateModel());
    }
    private class updateModel implements Runnable {
        /**
         * When an object implementing interface {@code Runnable} is used
         * to create a thread, starting the thread causes the object's
         * {@code run} method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method {@code run} is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            while (true){
                try {
                    // 从redis消息队列中获取到任务, XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.vehicle
                    List<MapRecord<String, Object, Object>> readList = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(RedisConstant.SAMPLE_STREAM, ReadOffset.lastConsumed())
                    );
                    if (readList == null || readList.isEmpty()){
                        continue;
                    }
                    // 解析消息队列里面的信息
                    log.info("异步更新模型参数");
                    MapRecord<String, Object, Object> record = readList.get(0);
                    handleUpdateModel();
                    // ACK
                    stringRedisTemplate.opsForStream().acknowledge(RedisConstant.SAMPLE_STREAM, "g1", record.getId());
                }catch (Exception e){
                    log.error("消息队列出现异常，处理pending list的队列");
                    handlePendingList();
                }
            }
        }

    }
    private void handlePendingList() {
        while (true){
            try {
                // 从redis消息队列中获取到任务, XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 2000 STREAMS stream.vehicle
                List<MapRecord<String, Object, Object>> readList = stringRedisTemplate.opsForStream().read(
                        Consumer.from("g1", "c1"),
                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                        StreamOffset.create(RedisConstant.SAMPLE_STREAM, ReadOffset.from("0"))
                );
                if (readList == null || readList.isEmpty()){
                    break;
                }
                // 解析消息队列里面的信息
                log.info("异步更新模型参数");
                MapRecord<String, Object, Object> record = readList.get(0);
                handleUpdateModel();
                // ACK
                stringRedisTemplate.opsForStream().acknowledge(RedisConstant.SAMPLE_STREAM, "g1", record.getId());
            }catch (Exception e){
                log.error("pending list队列出现异常");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void handleUpdateModel() {
        // TODO:调用python接口进行参数的更新
        log.info("开始调用模型参数更新接口");
    }

    @Override
    public Result<PageResult> pageQuery(OilSamplePageQueryDTO oilSamplePageQueryDTO) {
        if (oilSamplePageQueryDTO == null){
            throw new ParamNotFoundException(OilSampleConstant.OIL_SAMPLE_PAGE_QUERY_NOT_EXIST);
        }
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user==null){
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        return getPageQuery(oilSamplePageQueryDTO, user);
    }

    private Result<PageResult> getPageQuery(OilSamplePageQueryDTO oilSamplePageQueryDTO, User user) {
        List<String> deviceNoList = null;
        if (null!= oilSamplePageQueryDTO.getRegion() && !oilSamplePageQueryDTO.getRegion().equals("")){
             deviceNoList = vehicleMapper.getDeviceNoListByRegion(oilSamplePageQueryDTO.getRegion());
        }
        Page<OilSample> page = new Page<>(oilSamplePageQueryDTO.getPage(), oilSamplePageQueryDTO.getPageSize());
        LambdaQueryWrapper<OilSample> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OilSample::getIsDeleted, StatusConstant.DISABLE)
                .eq(UserConstant.EDITOR_PERMISSION.equals(user.getPermission()), OilSample::getCompany, user.getCompany())
                .gt(null!= oilSamplePageQueryDTO.getStartDateTime(), OilSample::getOilSampleDate, oilSamplePageQueryDTO.getStartDateTime())
                .lt(null!= oilSamplePageQueryDTO.getEndDateTime(), OilSample::getOilSampleDate, oilSamplePageQueryDTO.getEndDateTime())
                .in(null!=deviceNoList, OilSample::getDeviceNo, deviceNoList);
        lqw.and(null!= oilSamplePageQueryDTO.getInformation() && !oilSamplePageQueryDTO.getInformation().equals(""),
                w->w.like(OilSample::getDeviceNo, oilSamplePageQueryDTO.getInformation())
                        .or()
                        .like(OilSample::getOilSampleId, oilSamplePageQueryDTO.getInformation()))
                .orderByDesc(OilSample::getUpdateTime);
        page = oilSampleMapper.selectPage(page, lqw);
        List<OilSample> oilSamplesList = page.getRecords();
        if (page.getTotal()==0 || oilSamplesList.size()==0){
            return Result.success(new PageResult(page.getTotal(), new ArrayList<OilSamplePageQueryVO>()));
        }
        List<OilSamplePageQueryVO> oilSamplePageQueryVOList = new ArrayList<>();
        for (OilSample oilSamples : oilSamplesList) {
            OilSamplePageQueryVO oilSampleVO = new OilSamplePageQueryVO();
            BeanUtils.copyProperties(oilSamples, oilSampleVO);
            String oilSampleId = oilSamples.getOilSampleId();
            String[] split = oilSampleId.split(OilSampleConstant.OIL_SAMPLE_ID_SEPARATOR);
            String deviceName = split[1];
            oilSampleVO.setDeviceName(deviceName);
            oilSamplePageQueryVOList.add(oilSampleVO);
        }
        return Result.success(new PageResult(page.getTotal(), oilSamplePageQueryVOList));
    }

    @Override
    public Result deleteByOilSampleList(List<String> oilSampleNoList) {
        if (oilSampleNoList == null || oilSampleNoList.isEmpty()){
            throw new ParamNotFoundException(OilSampleConstant.OIL_SAMPLE_LIST_IS_EMPTY);
        }
        oilSampleMapper.deleteByOilSampleList(oilSampleNoList);
        return Result.success();
    }

    @Override
    public Result<OilSampleVO> queryByOilSampleId(String oilSampleId) {
        if (oilSampleId == null){
            throw new ParamNotFoundException(OilSampleConstant.OIL_SAMPLE_ID_IS_NULL);
        }
        LambdaQueryWrapper<OilSample> lqw = new LambdaQueryWrapper<>();
        lqw.eq(OilSample::getOilSampleId, oilSampleId).eq(OilSample::getIsDeleted, StatusConstant.DISABLE);
        OilSample oilSample = getOne(lqw);
        if (oilSample == null){
            throw new ParamNotFoundException(OilSampleConstant.OIL_SAMPLE_IS_NOT_EXIST);
        }
        OilSampleVO oilSampleVO = new OilSampleVO();
        BeanUtils.copyProperties(oilSample, oilSampleVO);
        return Result.success(oilSampleVO);
    }

    @Override
    public Result addOilSample(OilSampleDTO oilSampleDTO) {
        if (oilSampleDTO == null){
            throw new ParamNotFoundException(OilSampleConstant.ADDED_OIL_SAMPLE_IS_NULL);
        }
        // 获取创建者的身份信息
        User user = userMapper.selectById(UserHolder.getUser());
        if (user==null){
            throw new ParamNotFoundException(UserConstant.TOKEN_INVALID);
        }
        if (UserConstant.ADMIN_PERMISSION.equals(user.getPermission())){
            // 使用了代理对象的方法，避免事务失效
            log.info("admin {} add sample", user.getUsername());
            return addOilSampleByAdmin(oilSampleDTO, user);
        }else {
            log.info("editor {} add sample", user.getUsername());
            return submitAddByEditor(oilSampleDTO, user);
        }
    }

    public Result addOilSampleByAdmin(OilSampleDTO oilSampleDTO, User user) {
        // TODO: 从clickhouse数据库中获取相应机油运行时间
        Double oilRuntime = getOilRuntime(oilSampleDTO.getDeviceNo(), oilSampleDTO.getOilChangeDate(), oilSampleDTO.getOilSampleDate());
        if (oilRuntime == null){
            throw new OilSampleException(OilSampleConstant.OIL_RUNTIME_NOT_FOUND);
        }
        String deviceName = vehicleMapper.getDeviceName(oilSampleDTO.getDeviceNo());
        if (deviceName==null){
            throw new VehicleException(VehicleConstant.VEHICLE_DEVICE_NAME_NOT_EXIST);
        }
        String oilSampleId = OilSampleConstant.OIL_SAMPLE_ID_PREFIX + oilSampleDTO.getOilCycle()
                + OilSampleConstant.OIL_SAMPLE_ID_SEPARATOR
                + deviceName
                + OilSampleConstant.OIL_SAMPLE_ID_SEPARATOR
                + oilRuntime
                + OilSampleConstant.OIL_SAMPLE_ID_SUFFIX;
        OilSample oilSample = getOne(new LambdaQueryWrapper<OilSample>()
                .eq(OilSample::getOilSampleId, oilSampleId));
        if (oilSample!=null){
            // 说明之前这个样本存在过数据库里面
            if (StatusConstant.DISABLE.equals(oilSample.getIsDeleted())){
                // 如果没有被删除说明已经存在
                throw new OilSampleException(OilSampleConstant.OIL_SAMPLE_ALREADY_EXIST);
            }
            throw new OilSampleException(OilSampleConstant.OIL_SAMPLE_CAN_NOT_UPDATE);
        }
        LocalDateTime now = LocalDateTime.now();
        OilSample newOilSample = new OilSample();
        BeanUtils.copyProperties(oilSampleDTO, newOilSample);
        newOilSample.setOilSampleId(oilSampleId);
        newOilSample.setOilRuntime(oilRuntime);
        newOilSample.setCreateUser(user.getUsername());
        newOilSample.setCompany(user.getCompany());
        newOilSample.setIsDeleted(StatusConstant.DISABLE);
        newOilSample.setUpdateTime(now);
        newOilSample.setCreateTime(now);
        save(newOilSample);
        if (StatusConstant.ENABLE.equals(oilSampleDTO.getIsEnd())){
            // 说明有一个新的机油更新周期，需要模型参数进行更新
            Map<String, Object> map = new HashMap<>();
            map.put("model"+System.currentTimeMillis(), LocalDateTime.now().toString());
            stringRedisTemplate.opsForStream().add(RedisConstant.SAMPLE_STREAM, map);
        }
        return Result.success();
    }

    private Result submitAddByEditor(OilSampleDTO oilSampleDTO, User user){
        // TODO: 从clickhouse数据库中获取相应机油运行时间
        Double oilRuntime = getOilRuntime(oilSampleDTO.getDeviceNo(), oilSampleDTO.getOilChangeDate(), oilSampleDTO.getOilSampleDate());
        if (oilRuntime == null){
            throw new OilSampleException(OilSampleConstant.OIL_RUNTIME_NOT_FOUND);
        }
        String deviceName = vehicleMapper.getDeviceName(oilSampleDTO.getDeviceNo());
        if (deviceName==null){
            throw new VehicleException(VehicleConstant.VEHICLE_DEVICE_NAME_NOT_EXIST);
        }
        String oilSampleId = OilSampleConstant.OIL_SAMPLE_ID_PREFIX + oilSampleDTO.getOilCycle()
                + OilSampleConstant.OIL_SAMPLE_ID_SEPARATOR
                + deviceName
                + OilSampleConstant.OIL_SAMPLE_ID_SEPARATOR
                + oilRuntime
                + OilSampleConstant.OIL_SAMPLE_ID_SUFFIX;
        String operation = TodoConstant.VEHICLE_PREFIX + oilSampleId;
        LocalDateTime now = LocalDateTime.now();
        Todo todo = Todo.builder()
                .username(user.getUsername())
                .company(user.getCompany())
                .submitType(TodoConstant.ADD_OIL_SAMPLE_TYPE)
                .operation(operation)
                .parameter(JSON.toJSONString(oilSampleDTO))
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
    private Double getOilRuntime(String deviceNo, LocalDateTime oilChangeDate, LocalDateTime oilSampleDate) {
        Double lastChangeRuntime = getOilRuntimeByDatetime(deviceNo, oilChangeDate);
        Double sampleRuntime = getOilRuntimeByDatetime(deviceNo, oilSampleDate);
        if (lastChangeRuntime==null || sampleRuntime==null || sampleRuntime < lastChangeRuntime){
            return null;
        }
        return DoubleUtil.round(sampleRuntime-lastChangeRuntime, 2);
    }

    public Double getOilRuntimeByDatetime(String deviceNo, LocalDateTime datetime) {
        String format = "yyyy-MM-dd HH:mm:ss";
        Tbox tbox = tboxMapper.getLastChangeAccumulatedRuntime(deviceNo, datetime.format(DateTimeFormatter.ofPattern(format)));
        // 如果没查到前面的tbox数据，则输出机油运行时间为0
        if (tbox == null) return null;
        return DoubleUtil.round(tbox.getEngineTotalWorkingHours() * 0.05, 2);
    }
}
