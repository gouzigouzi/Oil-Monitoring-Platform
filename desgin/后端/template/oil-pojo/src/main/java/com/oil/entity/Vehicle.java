package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: Vehicle
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Getter
@Setter
@TableName("loader_info_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String deviceNo;
    private String deviceName;
    private String deviceLocation;
    private String deviceLocationProvince;
    private String deviceLocationCity;
    private Integer deviceLocationId;
    private String deviceType;
    private String deviceEngineType;
    private Double deviceAccumulatedRuntime;
    private LocalDateTime lastChangeDatetime;
    private Integer changeCount;
    private String deviceManager;
    private String deviceManagerPhone;
    private String oilType;
    private String createUser;
    private String company;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
