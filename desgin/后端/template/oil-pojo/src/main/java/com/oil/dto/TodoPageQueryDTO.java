package com.oil.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * ClassName: TodoPageQueryDTO
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
public class TodoPageQueryDTO implements Serializable {
    private String information;
    private String company;
    private Integer submitType;
    private Integer todoStatus;
    private Integer page;
    private Integer pageSize;
}
