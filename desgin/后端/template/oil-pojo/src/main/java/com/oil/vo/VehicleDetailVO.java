package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: VehicleDetailVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/16
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDetailVO implements Serializable {
    private String deviceNo;
    private String deviceName;
    private String deviceLocation;
    private String deviceType;
    private String deviceEngineType;
    private Double oilRuntime;
    private Double deviceAccumulatedRuntime;
    private String oilType;
    private Integer changeCount;
    private Double oilLifeTimePercent;
    private LocalDateTime lastChangeDatetime;
    private String deviceManager;
    private String deviceManagerPhone;
}
