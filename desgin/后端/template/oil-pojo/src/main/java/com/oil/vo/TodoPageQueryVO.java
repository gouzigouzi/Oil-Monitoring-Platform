package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: TodoPageQueryVO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/25
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoPageQueryVO implements Serializable {
    private Integer id;
    private String username;
    private String company;
    private String operation;
    private Integer submitType;
    private Integer todoStatus;
    private LocalDateTime createTime;
}
