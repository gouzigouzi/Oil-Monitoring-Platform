package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: OilSampleVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/15
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilSamplePageQueryVO implements Serializable {
    private String deviceNo;
    private String deviceName;
    private Integer oilCycle;
    private Double oilRuntime;
    private LocalDateTime oilSampleDate;
    private String oilSampleId;
    private LocalDateTime updateTime;
}
