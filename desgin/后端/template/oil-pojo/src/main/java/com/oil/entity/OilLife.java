package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * ClassName: OilLife
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Getter
@Setter
@TableName("loader_oil_life_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilLife implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String deviceNo;
    private LocalDateTime datetime;
    private Double oilLifeTimePercent;
    private Double oilRuntime;
    private Double deviceAccumulatedRuntime;
    private Double lastChangeAccumulatedRuntime;
    private LocalDateTime lastChangeDatetime;
    private Integer changeCount;
    private Long oilTemperatureDangerCount;
    private Long engineSpeedDangerCount;
    private Long engineLoadDangerCount;
    private Double oilLifeLossPercent;
    private Double oilTemperatureAverage;
    private Double engineSpeedAverage;
    private Double engineLoadAverage;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
