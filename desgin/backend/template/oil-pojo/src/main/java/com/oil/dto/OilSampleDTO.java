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
 * ClassName: OilSampleDTO
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
public class OilSampleDTO implements Serializable{
    private String deviceNo;
    private String engineType;
    private Integer oilCycle;
    private String oilType;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime oilChangeDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
