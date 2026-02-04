package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: EngineLineVO
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
public class EngineLineVO implements Serializable {
    private List<String> date;
    private List<Double> oilTempData;
    private List<Double> engineSpeedData;
    private List<Double> engineLoadData;
}
