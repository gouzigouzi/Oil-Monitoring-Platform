package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: OilConfig
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/5
 * @Version 1.0
 */
@Getter
@Setter
@TableName("loader_oil_config_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilConfig implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String deviceNo;
    private Long oilMaxLife;
    private Double oilRemainLife;
    private Integer sampleTime;
    private Integer oilTemperatureMild;
    private Integer oilTemperatureDanger;
    private Double oilTemperatureDefaultPenalty;
    private Double oilTemperatureMildPenalty;
    private Double oilTemperatureDangerPenalty;
    private Integer engineSpeedMild;
    private Integer engineSpeedDanger;
    private Double engineSpeedDefaultPenalty;
    private Double engineSpeedMildPenalty;
    private Double engineSpeedDangerPenalty;
    private Integer engineLoadMild;
    private Integer engineLoadDanger;
    private Double engineLoadDefaultPenalty;
    private Double engineLoadMildPenalty;
    private Double engineLoadDangerPenalty;
    private Integer ageThreshold;
    private Integer ageInterval;
    private Double defaultPenalty;
    private Double ageRate;
    private Double lastChangeAccumulatedRuntime;
    private LocalDateTime lastChangeDatetime;
    private Integer changeCount;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
