package com.oil.mapper.postgres;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.oil.entity.Vehicle;
import com.oil.vo.VehicleScatterVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ClassName: VehicleMapper
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@Mapper
public interface VehicleMapper extends BaseMapper<Vehicle> {
    @Select("select t.device_location_province from loader.loader_info_tb t where t.is_deleted = 0")
    List<String> getRegionProvinceList();

    @Select("select t.device_location_province from loader.loader_info_tb t where t.is_deleted = 0 and t.company = #{company}")
    List<String> getRegionProvinceListByCompany(String company);

    void deleteByDeviceNoList(List<String> deviceNoList);
    @Select("select t.device_name from loader.loader_info_tb t where t.device_no = #{deviceNo} and t.is_deleted = 0")
    String getDeviceName(String deviceNo);

    @Select("select t.device_no from loader.loader_info_tb t where t.is_deleted = 0")
    List<String> getDeviceNoList();
    @Select("select t.device_no from loader.loader_info_tb t where t.is_deleted = 0 and t.device_location_province = #{region}")
    List<String> getDeviceNoListByRegion(String region);
    @Select("select device_location_province as city_name, count(*) as vehicle_count from loader.loader_info_tb group by device_location_province;")
    List<VehicleScatterVO> getCountryScatter(String deviceLocationProvince);
    @Select("select " +
            "device_location_city as city_name, count(*) as vehicle_count " +
            "from loader.loader_info_tb " +
            "where device_location_province = #{deviceLocationProvince} " +
            "group by device_location_city")
    List<VehicleScatterVO> getCityScatter(String deviceLocationProvince);
}
