package com.oil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oil.dto.VehicleDTO;
import com.oil.dto.VehiclePageQueryDTO;
import com.oil.entity.User;
import com.oil.entity.Vehicle;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.vo.*;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * ClassName: VehicleService
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
public interface VehicleService extends IService<Vehicle> {
    /**
     * 获取车辆的活动区域省份列表
     * @return
     */
    Result<Set<String>> regionList();

    /**
     * 按条件分页查询车辆
     *
     * @param vehiclePageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(VehiclePageQueryDTO vehiclePageQueryDTO);

    /**
     * 按照车辆编号查询车辆
     * @param deviceNo
     * @return
     */
    Result<VehicleVO> queryByDeviceNo(String deviceNo);

    /**
     * 根据车辆编号列表删除车辆
     * @param deviceNoList
     * @return
     */
    Result deleteByDeviceNoList(List<String> deviceNoList);

    /**
     * 管理者根据车辆编号列表删除车辆
     * @param deviceNoList
     * @return
     */
    @Transactional
    Result deleteByDeviceNoListByAdmin(List<String> deviceNoList);

    /**
     * 新增车辆
     *
     * @param vehicleDTO
     * @return
     */
    Result addVehicle(VehicleDTO vehicleDTO);

    /**
     * 管理者新增车辆
     * @param vehicleDTO
     * @param user
     */
    Result addVehicleByAdmin(VehicleDTO vehicleDTO, User user);

    /**
     * 修改车辆信息
     * @param vehicleDTO
     * @return
     */
    Result editVehicle(VehicleDTO vehicleDTO);

    /**
     * 管理者修改车辆信息
     * @param vehicleDTO
     * @return
     */
    Result editVehicleByAdmin(VehicleDTO vehicleDTO);

    /**
     * 获取车辆的编号列表
     * @return
     */
    Result<List<String>> deviceNoList();

    /**
     * 查询车辆的详细信息
     * @param deviceNo
     * @return
     */
    Result<VehicleDetailVO> detail(String deviceNo);

    /**
     * 新增车辆
     * @param vehicleDTO
     * @param user
     * @return
     */
    @Transactional
    Result handleAddVehicle(VehicleDTO vehicleDTO, User user, Double deviceAccumulatedRuntime);

    /**
     * 获取车辆的最新数据
     * @param deviceNo
     * @return
     */
    Double getDeviceAccumulatedRuntime(String deviceNo);
    /**
     * 计算获得车辆换油时间的累积运行时间
     * @param deviceNo
     * @param lastChangeDatetime
     * @param deviceAccumulatedRuntime
     * @return
     */
    Double getLastChangeAccumulatedRuntime(String deviceNo, LocalDateTime lastChangeDatetime, Double deviceAccumulatedRuntime);

    /**
     * 根据车辆的运行时间排序后返回
     * @return
     */
    Result<List<VehicleRankVO>> rank();

    /**
     * 根据
     * @return
     */
    Result<DashBoardVO> accumulated();

    /**
     * 根据城市名称获取车辆分布情况
     * @param deviceLocationProvince
     * @return
     */
    Result<List<VehicleScatterVO>> vehicleScatter(String deviceLocationProvince);
}
