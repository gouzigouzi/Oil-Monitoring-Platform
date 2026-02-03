package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.Todo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: TodoMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/25
 * @Version 1.0
 */
@Mapper
public interface TodoMapper extends BaseMapper<Todo> {
    void deleteByTodoIdList(List<Integer> todoIdList);

    void agreeByTodoIdList(List<Integer> todoIdList);

    void reject(List<Integer> rejectIdList, @Param("rejectReason") String rejectReason);
}
