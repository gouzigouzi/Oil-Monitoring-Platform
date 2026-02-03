package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * ClassName: UserMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/25
 * @Version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("select t.company from loader.user_tb t where t.is_deleted = 0")
    String[] getCompanyList();

    void deleteByUsernameList();
}
