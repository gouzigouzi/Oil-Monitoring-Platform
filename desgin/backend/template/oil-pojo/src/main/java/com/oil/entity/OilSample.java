package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: OilSamples
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Getter
@Setter
@TableName("loader_oil_sample_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilSample implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String deviceNo;
    private String oilType;
    private String engineType;
    private Double oilRuntime;
    private LocalDateTime oilSampleDate;
    private LocalDateTime oilChangeDate;
    private Integer oilCycle;
    private String oilSampleId;
    private Double oilTan;
    private Double oilTbn;
    private Double oilWater;
    private Double oilViscosity;
    private Double oilFe;
    private Double oilCu;
    private Double oilAl;
    private Double oilSi;
    private String createUser;
    private String company;
    private Integer isEnd;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
