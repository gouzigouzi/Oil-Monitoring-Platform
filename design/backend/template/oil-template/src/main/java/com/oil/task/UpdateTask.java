package com.oil.task;

import com.oil.entity.Vehicle;
import com.oil.mapper.postgres.VehicleMapper;
import com.oil.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

/**
 * ClassName: UpdateTask
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/11/13
 * @Version 1.0
 */
@Component
@Slf4j
public class UpdateTask {
    @Autowired
    VehicleMapper vehicleMapper;
    @Autowired
    RestTemplate restTemplate;
    @Value("${python.host}")
    String host;
    @Value("${python.port}")
    String port;

    @Scheduled(cron = "0 0 9 1/1 * ?")
//    @Scheduled(cron = "0/50 * * * * ?")
//    @Scheduled(cron = "0 0 0/1 * * ?")
    public void updateOilLife(){
        log.info("每天定时更新工程机械机油剩余寿命");
        List<String> vehicleDeviceNoList = vehicleMapper.getDeviceNoList();
        for (String deviceNo : vehicleDeviceNoList) {
            //TODO: 功能改进：车辆少的时候可以使用单线程，车辆多了之后的采用多线程
            String url = "http://" + host + ":" + port + "/update/" + deviceNo;
            try {
                ResponseEntity<Result> response = restTemplate.getForEntity(url, Result.class);
                if (response.getStatusCode().is2xxSuccessful()){
                    Result result = response.getBody();
                    if (Objects.requireNonNull(result).getCode().equals(Result.SUCCESS_CODE)){
                        log.info(deviceNo + "车辆计算成功！");
                    }else {
                        log.error(deviceNo + "车辆计算出错，出错原因：" + result.getMsg());
                    }
                }else {
                    log.error(deviceNo + "调用python接口失败!" + "  " + response.getStatusCode());
                }
            }catch (Exception e){
                log.error(deviceNo + "调用python接口失败" + "  " + e.getMessage());
            }
        }
    }
}
