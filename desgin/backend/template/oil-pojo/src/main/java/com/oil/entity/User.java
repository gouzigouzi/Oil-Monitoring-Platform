package com.oil.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * ClassName: User
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/25
 * @Version 1.0
 */
@Getter
@Setter
@TableName("user_tb")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String username;
    private String password;
    private Integer gender;
    private Integer isDeleted;
    private Integer isEnabled;
    private String company;
    private Integer permission;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
