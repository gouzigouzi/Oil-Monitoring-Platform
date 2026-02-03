package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: VehicleRankVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/11/14
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRankVO implements Serializable {
    private String deviceNo;
    private String deviceName;
    private String lastChangeDatetime;
    private String deviceLocation;
    private Integer oilStatus;
    private Double deviceAccumulatedRuntime;
}
