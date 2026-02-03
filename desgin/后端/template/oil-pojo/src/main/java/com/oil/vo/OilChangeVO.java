package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: OilChangeVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/17
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilChangeVO implements Serializable {
    private String deviceNo;
    private LocalDateTime oilChangeDate;
    private Double oilRuntime;
    private Integer oilStatus;
}
