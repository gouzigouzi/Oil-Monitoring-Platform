package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: DashBoardVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/11/15
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardVO implements Serializable {
    Long accumulatedVehicleCount;
    Double accumulatedSavingMoney;
    Double accumulatedExtraRuntime;
    Double accumulatedSavedChangeCount;
}
