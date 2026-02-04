package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: Todo
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/25
 * @Version 1.0
 */
@Getter
@Setter
@TableName("todo_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Todo implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String company;
    private Integer submitType;
    private String operation;
    private Integer todoStatus;
    private String rejectReason;
    private String parameter;
    private Integer isDeleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
