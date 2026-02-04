package com.oil.controller;

import com.oil.dto.VehicleDTO;
import com.oil.dto.VehiclePageQueryDTO;
import com.oil.entity.Vehicle;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.VehicleService;
import com.oil.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * ClassName: VehicleController
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
@RestController
@RequestMapping("vehicle")
@Slf4j
public class VehicleController {
    @Autowired
    private VehicleService vehicleService;

    @GetMapping("region")
    public Result<Set<String>> region(){
        return vehicleService.regionList();
    }

    @GetMapping("page")
    public Result<PageResult> page(VehiclePageQueryDTO vehiclePageQueryDTO){
        log.info("车辆分页查询：{}", vehiclePageQueryDTO);
        return vehicleService.pageQuery(vehiclePageQueryDTO);
    }

    @GetMapping("query")
    public Result<VehicleVO> query(String deviceNo){
        log.info("查询的车辆编号为：{}", deviceNo);
        return vehicleService.queryByDeviceNo(deviceNo);
    }

    @DeleteMapping("delete")
    public Result delete(@RequestBody List<String> deviceNoList){
        log.info("车辆编号为:{}", deviceNoList);
        return vehicleService.deleteByDeviceNoList(deviceNoList);
    }

    @PostMapping("add")
    public Result add(@RequestBody VehicleDTO vehicleDTO){
        log.info("新增车辆：{}", vehicleDTO);
        log.info(vehicleDTO.getLastChangeDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return vehicleService.addVehicle(vehicleDTO);
    }

    @PostMapping("edit")
    public Result edit(@RequestBody VehicleDTO vehicleDTO){
        log.info("修改车辆信息：{}", vehicleDTO);
        return vehicleService.editVehicle(vehicleDTO);
    }

    @GetMapping("list")
    public Result<List<String>> deviceNoList(){
        return vehicleService.deviceNoList();
    }

    @GetMapping("detail")
    public Result<VehicleDetailVO> detail(String deviceNo){
        log.info("查询车辆的详细信息：{}", deviceNo);
        return vehicleService.detail(deviceNo);
    }

    @GetMapping("rank")
    public Result<List<VehicleRankVO>> rank(){
        return vehicleService.rank();
    }

    @GetMapping("accumulated")
    public Result<DashBoardVO> accumulated(){
        return vehicleService.accumulated();
    }

    @GetMapping("vehicleScatter")
    public Result<List<VehicleScatterVO>> vehicleScatter(String deviceLocationProvince){
        return vehicleService.vehicleScatter(deviceLocationProvince);
    }
}
