package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: VehicleVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/2
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleVO implements Serializable {
    private String deviceNo;
    private String deviceType;
    private String deviceEngineType;
    private Integer changeCount;
    private String oilType;
    private String deviceManager;
    private String deviceManagerPhone;
    private String lastChangeDatetime;
    private String deviceLocation;
    private String deviceLocationProvince;
    private String deviceLocationCity;
}
