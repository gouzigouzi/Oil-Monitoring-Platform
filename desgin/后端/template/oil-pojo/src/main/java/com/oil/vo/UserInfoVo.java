package com.oil.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * ClassName: UserInfoVo
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/11
 * @Version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVo implements Serializable {
    private List<String> roles;
    private String introduction;
    private String avatar;
    private String name;
}
