package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

/**
 * ClassName: EnginePieVO
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
public class EnginePieVO implements Serializable {
    private List<Long> value;
}
