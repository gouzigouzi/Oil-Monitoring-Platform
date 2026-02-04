package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.OilChange;
import com.oil.entity.OilConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * ClassName: OilChangeMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/17
 * @Version 1.0
 */
@Mapper
public interface OilChangeMapper extends BaseMapper<OilChange> {
    @Select("SELECT sum(t.oil_runtime) from loader.loader_oil_change_tb t")
    Double getAccumulatedOilRuntime();
}
