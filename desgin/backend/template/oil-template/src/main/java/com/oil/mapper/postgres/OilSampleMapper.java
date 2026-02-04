package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.OilSample;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: OilSampleMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/13
 * @Version 1.0
 */
@Mapper
public interface OilSampleMapper extends BaseMapper<OilSample> {
    void deleteByOilSampleList(List<String> oilSampleNoList);
}
