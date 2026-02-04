package com.oil.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: VehicleDTO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/9
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDTO implements Serializable {
    private String deviceNo;
    private String deviceType;
    private String deviceEngineType;
    private Integer changeCount;
    private String oilType;
    private String deviceManager;
    private String deviceManagerPhone;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime lastChangeDatetime;
    private String deviceLocation;
    private String deviceLocationProvince;
    private String deviceLocationCity;
}
