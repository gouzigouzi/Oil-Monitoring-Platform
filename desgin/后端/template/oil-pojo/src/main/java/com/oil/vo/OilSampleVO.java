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
public class OilSampleVO implements Serializable {
    private String deviceNo;
    private String engineType;
    private Integer oilCycle;
    private String oilType;
    private LocalDateTime oilChangeDate;
    private LocalDateTime oilSampleDate;
    private Double oilTan;
    private Double oilTbn;
    private Double oilWater;
    private Double oilViscosity;
    private Double oilFe;
    private Double oilCu;
    private Double oilAl;
    private Double oilSi;
    private Integer isEnd;
}
