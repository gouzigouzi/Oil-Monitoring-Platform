package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ClassName: VeiclePageQueryVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehiclePageQueryVO implements Serializable {
    private String deviceNo;
    private String deviceName;
    private String deviceLocation;
    private Integer oilStatus;
    private LocalDateTime lastChangeDatetime;
}
