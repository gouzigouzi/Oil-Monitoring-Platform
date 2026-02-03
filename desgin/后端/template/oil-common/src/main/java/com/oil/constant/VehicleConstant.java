package com.oil.constant;

import java.util.Map;

import static java.util.Map.entry;

/**
 * ClassName: VehicleConstant
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
public class VehicleConstant {
    public static final String VEHICLE_OIL_LIFE_NOT_EXIST = "车辆的机油寿命数据不存在！";
    public static final String VEHICLE_NOT_EXIST = "没有找到符合条件的车辆！";
    public static final String VEHICLE_LIST_IS_EMPTY = "车辆列表为空";
    public static final String ADDED_VEHICLE_IS_NULL = "新增车辆为空";
    public static final String VEHICLE_DEVICE_NO_NOT_EXIST = "车辆编号为空";
    public static final String VEHICLE_PAGE_QUERY_NOT_EXIST = "车辆查询条件为空";
    public static final String VEHICLE_ALREADY_EXIST = "车辆已存在";
    public static final String VEHICLE_CONFIG_NOT_FOUND = "车辆配置表不存在";
    public static final String VEHICLE_ACCUMULATED_RUNTIME_NOT_FOUND = "TBox数据库中不存在该车辆";
    public static final String VEHICLE_DEVICE_NAME_NOT_EXIST = "车辆名称不存在";
    public static final String VEHICLE_OIL_CHANGE_NOT_EXIST = "车辆的历史换油记录不存在";
    public static final String INVALID_CITY_NAME = "无效的城市名称";
    public static final Map<String, String> SPECIAL_CITIES_MAP = Map.ofEntries(
            entry("上海", "上海市"),
            entry("北京", "北京市"),
            entry("天津","天津市"),
            entry("重庆","重庆市"),
            entry("新疆","新疆维吾尔自治区"),
            entry("内蒙古", "内蒙古自治区"),
            entry("西藏", "西藏自治区"),
            entry("宁夏", "宁夏回族自治区"),
            entry("广西","广西壮族自治区"),
            entry("香港","香港特别行政区"),
            entry("澳门", "澳门特别行政区"));
    public static final Map<String, String> DISTINCT_INVERSE_MAP = Map.ofEntries(
            entry("上海市", "上海"),
            entry("北京市", "北京"),
            entry("天津市","天津"),
            entry("重庆市","重庆")
    );
    public static final Map<String, String> SPECIAL_CITIES_INVERSE_MAP = Map.ofEntries(
            entry("新疆维吾尔自治区","新疆"),
            entry("内蒙古自治区", "内蒙古"),
            entry("西藏自治区", "西藏"),
            entry("宁夏回族自治区", "宁夏"),
            entry("广西壮族自治区","广西"),
            entry("香港特别行政区","香港"),
            entry("澳门特别行政区", "澳门")
    );
}
