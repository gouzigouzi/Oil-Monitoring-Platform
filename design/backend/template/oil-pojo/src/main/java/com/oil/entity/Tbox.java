package com.oil.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * ClassName: Tbox
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/11/11
 * @Version 1.0
 */
@Getter
@Setter
@TableName("tboxdata")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tbox {
    String tDate;
    String deviceNo;
    String generateTime;
    Integer oilTemperature;
    Integer coolingFluidTemperature;
    Integer engineSpeed;
    Integer vehicleSpeed;
    Integer frictionTorque;
    Integer engineLoadRat;
    Integer oilPressure;
    Integer engineTotalWorkingHours;
}
