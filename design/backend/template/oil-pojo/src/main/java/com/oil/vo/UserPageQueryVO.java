package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: UserVO
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
public class UserPageQueryVO implements Serializable {
    private String username;
    private String company;
    private Integer isEnabled;
    private Integer permission;
    private LocalDateTime createTime;
}
