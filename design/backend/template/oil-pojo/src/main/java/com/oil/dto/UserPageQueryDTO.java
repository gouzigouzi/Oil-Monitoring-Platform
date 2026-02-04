package com.oil.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ClassName: UserPageQueryDTO
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/19
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPageQueryDTO implements Serializable {
    private String information;
    private String company;
    private Integer page;
    private Integer pageSize;
}
