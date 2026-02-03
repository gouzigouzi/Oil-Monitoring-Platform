package com.oil;

import cn.hutool.core.bean.BeanUtil;

import com.oil.constant.RedisConstant;
import com.oil.entity.User;
import com.oil.mapper.postgres.UserMapper;
import com.oil.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;

@SpringBootTest
class OilTemplateApplicationTests {
    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Test
    void contextLoads() {
//        System.out.println("hi");
//        IPage<User> page = new Page<>(1,1);
//        IPage<User> page1 = userMapper.selectPage(page, null);
//        System.out.println(page1.getTotal());
    }
    @Test
    void redisTest(){
//        String s = stringRedisTemplate.opsForValue().get("apple");
//        System.out.println(s);
//        Map<String, Object> map = new HashMap<>();
//        map.put("username", "hahha");
//        map.put("company", "浙江大学");
//        stringRedisTemplate.opsForStream().add(RedisConstant.VEHICLE_STREAM, map);
//        List<MapRecord<String, Object, Object>> readList = stringRedisTemplate.opsForStream().read(
//                Consumer.from("g1", "c1"),
//                StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
//                StreamOffset.create(RedisConstant.VEHICLE_STREAM, ReadOffset.lastConsumed())
//        );
//        if (readList == null || readList.isEmpty()){
//            return;
//        }
//        MapRecord<String, Object, Object> record = readList.get(0);
////        VehicleDTO vehicleDTO = BeanUtil.fillBeanWithMap(record.getValue(), new VehicleDTO(), true);
//        User user = BeanUtil.fillBeanWithMap(record.getValue(), new User(), true);
//        System.out.println(user);
    }
    @Test
    void consumeMessage(){
        List<MapRecord<String, Object, Object>> readList = stringRedisTemplate.opsForStream().read(
                Consumer.from("g1", "c1"),
                StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                StreamOffset.create(RedisConstant.VEHICLE_STREAM, ReadOffset.from("0"))
        );
        if (readList == null || readList.isEmpty()){
            return;
        }
        // 解析消息队列里面的信息
        MapRecord<String, Object, Object> record = readList.get(0);
//        VehicleDTO vehicleDTO = BeanUtil.fillBeanWithMap(record.getValue(), new VehicleDTO(), true);
        User user = BeanUtil.fillBeanWithMap(record.getValue(), new User(), true);
        System.out.println(user.toString());
    }
//    @Test
//    public void test(){
////        stringRedisTemplate.opsForStream().cr;
//        stringRedisTemplate.opsForStream().createGroup(RedisConstant.VEHICLE_STREAM,"g1");
//        System.out.println(LocalDateTime.now());
//    }
}
