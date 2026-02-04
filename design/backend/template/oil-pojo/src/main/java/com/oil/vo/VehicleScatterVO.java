package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: VehicleScatterVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/11/16
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleScatterVO implements Serializable {
    String cityName;
    Long vehicleCount;
}
