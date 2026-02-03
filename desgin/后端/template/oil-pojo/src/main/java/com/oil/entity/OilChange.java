package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: OilChange
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/5
 * @Version 1.0
 */
@Getter
@Setter
@TableName("loader_oil_change_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OilChange implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String deviceNo;
    private Double oilRuntime;
    private LocalDateTime oilChangeDate;
    private Integer oilChangeCount;
    private Double oilLifeTimePercent;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
