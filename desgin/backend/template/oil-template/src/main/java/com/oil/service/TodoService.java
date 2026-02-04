package com.oil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oil.dto.TodoPageQueryDTO;
import com.oil.entity.Todo;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.vo.OilSampleVO;
import com.oil.vo.TodoVehicleVO;
import com.oil.vo.VehicleVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: TodoService
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/25
 * @Version 1.0
 */
public interface TodoService extends IService<Todo> {
    /**
     * 申请列表分页查询
     * @param todoPageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(TodoPageQueryDTO todoPageQueryDTO);

    /**
     * 根据id列表删除申请
     * @param todoIdList
     * @return
     */
    Result deleteByTodoIdList(List<Integer> todoIdList);

    /**
     * 根据id列表同意申请
     * @param todoIdList
     * @return
     */
    @Transactional
    Result agree(List<Integer> todoIdList);

    /**
     * 拒绝申请
     * @param rejectIdList
     * @param rejectReason
     * @param detailReason
     * @return
     */
    Result reject(List<Integer> rejectIdList, String rejectReason, String detailReason);

    /**
     * 根据todoId查看车辆信息
     * @param todoId
     * @return
     */
    Result<VehicleVO> viewVehicle(Integer todoId);
    /**
     * 根据todoId查看机油样本信息
     * @param todoId
     * @return
     */
    Result<OilSampleVO> viewSample(Integer todoId);

    /**
     * 根据车辆的编号列表查看车辆信息
     * @param todoId
     * @return
     */
    Result<List<TodoVehicleVO>> viewDeleteVehicle(Integer todoId);
}
