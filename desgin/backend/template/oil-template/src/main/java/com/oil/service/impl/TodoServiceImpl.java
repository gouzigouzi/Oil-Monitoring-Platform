package com.oil.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oil.constant.StatusConstant;
import com.oil.constant.TodoConstant;
import com.oil.dto.OilSampleDTO;
import com.oil.dto.TodoPageQueryDTO;
import com.oil.dto.VehicleDTO;
import com.oil.entity.Todo;
import com.oil.entity.User;
import com.oil.entity.Vehicle;
import com.oil.exception.OilSampleException;
import com.oil.exception.ParamNotFoundException;
import com.oil.exception.VehicleException;
import com.oil.mapper.postgres.TodoMapper;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.OilSampleService;
import com.oil.service.TodoService;
import com.oil.service.VehicleService;
import com.oil.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: TodoServiceImpl
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/25
 * @Version 1.0
 */
@Service
public class TodoServiceImpl extends ServiceImpl<TodoMapper, Todo> implements TodoService {
    @Autowired
    TodoMapper todoMapper;
    @Autowired
    VehicleService vehicleService;
    @Autowired
    OilSampleService oilSampleService;
    @Override
    public Result<PageResult> pageQuery(TodoPageQueryDTO todoPageQueryDTO) {
        if (todoPageQueryDTO == null) {
            throw new ParamNotFoundException(TodoConstant.TODO_PAGE_QUERY_NOT_EXIST);
        }
        Page<Todo> page = new Page<>(todoPageQueryDTO.getPage(), todoPageQueryDTO.getPageSize());
        LambdaQueryWrapper<Todo> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Todo::getIsDeleted, StatusConstant.DISABLE)
                .eq(null != todoPageQueryDTO.getCompany() && !"".equals(todoPageQueryDTO.getCompany()),
                        Todo::getCompany, todoPageQueryDTO.getCompany())
                .eq(null != todoPageQueryDTO.getSubmitType(), Todo::getSubmitType, todoPageQueryDTO.getSubmitType());
        if (TodoConstant.TODO_STATUS.equals(todoPageQueryDTO.getTodoStatus())) {
            lqw.eq(null != todoPageQueryDTO.getTodoStatus(), Todo::getTodoStatus, todoPageQueryDTO.getTodoStatus());
        } else {
            lqw.ge(null != todoPageQueryDTO.getTodoStatus(), Todo::getTodoStatus, todoPageQueryDTO.getTodoStatus());
        }
        lqw.and(null != todoPageQueryDTO.getInformation() && !"".equals(todoPageQueryDTO.getInformation()),
                        w -> w.like(Todo::getUsername, todoPageQueryDTO.getInformation())
                                .or()
                                .like(Todo::getCompany, todoPageQueryDTO.getInformation())
                                .or()
                                .like(Todo::getOperation, todoPageQueryDTO.getInformation()))
                .orderByDesc(Todo::getUpdateTime);
        page = todoMapper.selectPage(page, lqw);
        List<Todo> todoList = page.getRecords();
        List<TodoPageQueryVO> todoPageQueryVOList = new ArrayList<>();
        if (page.getTotal() == 0 || todoList.size() == 0) {
            return Result.success(new PageResult(page.getTotal(), todoPageQueryVOList));
        }
        for (Todo todo : todoList) {
            TodoPageQueryVO todoPageQueryVO = new TodoPageQueryVO();
            BeanUtils.copyProperties(todo, todoPageQueryVO);
            todoPageQueryVOList.add(todoPageQueryVO);
        }
        return Result.success(new PageResult(page.getTotal(), todoPageQueryVOList));
    }

    @Override
    public Result deleteByTodoIdList(List<Integer> todoIdList) {
        if (todoIdList == null || todoIdList.isEmpty()) {
            throw new ParamNotFoundException(TodoConstant.TODO_LIST_IS_EMPTY);
        }
        todoMapper.deleteByTodoIdList(todoIdList);
        return Result.success();
    }

    @Override
    public Result agree(List<Integer> todoIdList) {
        if (todoIdList == null || todoIdList.isEmpty()) {
            throw new ParamNotFoundException(TodoConstant.TODO_LIST_IS_EMPTY);
        }
        List<Todo> todoList = listByIds(todoIdList);
        for (Todo todo : todoList) {
            if (todo==null) continue;
            if (todo.getSubmitType().equals(TodoConstant.ADD_VEHICLE_TYPE)){
                User user = User.builder().username(todo.getUsername()).company(todo.getCompany()).build();
                VehicleDTO vehicleDTO = JSON.parseObject(todo.getParameter(), VehicleDTO.class);
                vehicleService.addVehicleByAdmin(vehicleDTO, user);
            }
            if (todo.getSubmitType().equals(TodoConstant.EDIT_VEHICLE_TYPE)){
                VehicleDTO vehicleDTO = JSON.parseObject(todo.getParameter(), VehicleDTO.class);
                vehicleService.editVehicleByAdmin(vehicleDTO);
            }
            if (todo.getSubmitType().equals(TodoConstant.DELETE_VEHICLE_TYPE)){
                List<String> deviceNoList = JSON.parseArray(todo.getParameter(), String.class);
                vehicleService.deleteByDeviceNoListByAdmin(deviceNoList);
            }
            if (todo.getSubmitType().equals(TodoConstant.ADD_OIL_SAMPLE_TYPE)){
                User user = User.builder().username(todo.getUsername()).company(todo.getCompany()).build();
                OilSampleDTO oilSampleDTO = JSON.parseObject(todo.getParameter(), OilSampleDTO.class);
                oilSampleService.addOilSampleByAdmin(oilSampleDTO, user);
            }
        }
        todoMapper.agreeByTodoIdList(todoIdList);
        return Result.success();
    }

    @Override
    public Result reject(List<Integer> rejectIdList, String rejectReason, String detailReason) {
        if (rejectIdList==null || rejectIdList.size()==0){
            throw new ParamNotFoundException(TodoConstant.TODO_REJECT_ID_IS_EMPTY);
        }
        if (StrUtil.isBlank(rejectReason)) {
            throw new ParamNotFoundException(TodoConstant.TODO_REJECT_IS_EMPTY);
        }
        String reason = StrUtil.isBlank(detailReason) ? rejectReason : rejectReason + ":" + detailReason;
        todoMapper.reject(rejectIdList, reason);
        return Result.success();
    }

    @Override
    public Result<VehicleVO> viewVehicle(Integer todoId) {
        if (todoId == null){
            throw new ParamNotFoundException(TodoConstant.TODO_LIST_IS_EMPTY);
        }
        Todo todo = getById(todoId);
        if (todo==null || todo.getParameter()==null){
            throw new VehicleException(TodoConstant.TODO_ITEM_IS_NULL);
        }
        try {
            VehicleVO vehicleVO = JSON.parseObject(todo.getParameter(), VehicleVO.class);
            return Result.success(vehicleVO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }

    @Override
    public Result<OilSampleVO> viewSample(Integer todoId) {
        if (todoId == null){
            throw new ParamNotFoundException(TodoConstant.TODO_LIST_IS_EMPTY);
        }
        Todo todo = getById(todoId);
        if (todo==null || todo.getParameter()==null){
            throw new OilSampleException(TodoConstant.TODO_ITEM_IS_NULL);
        }
        try {
            OilSampleVO oilSampleVO = JSON.parseObject(todo.getParameter(), OilSampleVO.class);
            return Result.success(oilSampleVO);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }

    @Override
    public Result<List<TodoVehicleVO>> viewDeleteVehicle(Integer todoId) {
        if (todoId == null){
            throw new ParamNotFoundException(TodoConstant.TODO_LIST_IS_EMPTY);
        }
        Todo todo = getById(todoId);
        if (todo==null || todo.getParameter()==null){
            throw new VehicleException(TodoConstant.TODO_ITEM_IS_NULL);
        }
        try {
            List<String> deviceNoList = JSON.parseArray(todo.getParameter(), String.class);
            LambdaQueryWrapper<Vehicle> lqw = new LambdaQueryWrapper<Vehicle>()
                    .in(!deviceNoList.isEmpty(), Vehicle::getDeviceNo, deviceNoList);
            List<Vehicle> vehicleList = vehicleService.list(lqw);
            List<TodoVehicleVO> todoVehicleVOList = new ArrayList<>();
            for (Vehicle vehicle : vehicleList) {
                TodoVehicleVO todoVehicleVO = new TodoVehicleVO();
                BeanUtil.copyProperties(vehicle, todoVehicleVO);
                todoVehicleVOList.add(todoVehicleVO);
            }
            return Result.success(todoVehicleVOList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Result.success();
    }


}
