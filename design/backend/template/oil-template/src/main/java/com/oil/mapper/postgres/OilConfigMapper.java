package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.OilConfig;
import com.oil.entity.OilLife;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: OilConfigMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/5
 * @Version 1.0
 */
@Mapper
public interface OilConfigMapper extends BaseMapper<OilConfig> {
    void deleteByDeviceNoList(List<String> deviceNoList);

    @Select("select oil_max_life from loader.loader_oil_config_tb where device_no = #{deviceNo} order by create_time limit 1")
    Long getMaxOilLife(String deviceNo);
}
