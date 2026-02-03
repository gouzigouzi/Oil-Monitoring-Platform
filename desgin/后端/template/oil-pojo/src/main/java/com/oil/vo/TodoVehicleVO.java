package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: TodoVehicleVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/31
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoVehicleVO implements Serializable {
    private String deviceNo;
    private String deviceName;
    private String deviceLocation;
    private LocalDateTime lastChangeDatetime;
}
