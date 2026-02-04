package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.OilLife;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;

/**
 * ClassName: OilLifeMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Mapper
public interface OilLifeMapper extends BaseMapper<OilLife> {
    List<HashMap<String, Object>> getOilLifeMap(List<String> vehicleIdList);
    @Select("select * from loader.loader_oil_life_tb where id = 2")
    OilLife get();
}
