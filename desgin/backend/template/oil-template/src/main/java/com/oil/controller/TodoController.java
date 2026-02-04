package com.oil.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oil.constant.StatusConstant;
import com.oil.constant.TodoConstant;
import com.oil.dto.TodoPageQueryDTO;
import com.oil.dto.TodoRejectDTO;
import com.oil.entity.Todo;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.TodoService;
import com.oil.vo.OilSampleVO;
import com.oil.vo.TodoVehicleVO;
import com.oil.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: TodoController
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/25
 * @Version 1.0
 */
@RestController
@RequestMapping("todo")
@Slf4j
public class TodoController {
    @Autowired
    TodoService todoService;

    @GetMapping("page")
    public Result<PageResult> page(TodoPageQueryDTO todoPageQueryDTO){
        log.info("申请列表分页查询:{}", todoPageQueryDTO);
        return todoService.pageQuery(todoPageQueryDTO);
    }

    @GetMapping("count")
    public Result<Long> count(){
        return Result.success(todoService.count(new LambdaQueryWrapper<Todo>()
                .eq(Todo::getTodoStatus, TodoConstant.TODO_STATUS)
                .eq(Todo::getIsDeleted, StatusConstant.DISABLE)));
    }

    @DeleteMapping("delete")
    public Result delete(@RequestBody List<Integer> todoIdList){
        log.info("申请列表id为:{}", todoIdList);
        return todoService.deleteByTodoIdList(todoIdList);
    }

    @PostMapping("agree")
    public Result agree(@RequestBody List<Integer> todoIdList){
        log.info("同意申请,id列表为：{}", todoIdList);
        return todoService.agree(todoIdList);
    }

    @PostMapping("reject")
    public Result reject(@RequestParam List<Integer> rejectIdList, String rejectReason, String detailReason){
//        List<Integer> rejectIdList = todoRejectDTO.getRejectIdList();
//        String rejectReason = todoRejectDTO.getRejectReason();
//        String detailReason = todoRejectDTO.getDetailReason();
        log.info("拒绝申请id列表：{}, 理由：{}+{}", rejectIdList, rejectReason, detailReason);
        return todoService.reject(rejectIdList, rejectReason, detailReason);
    }

    @GetMapping("viewVehicle/{todoId}")
    public Result<VehicleVO> viewVehicle(@PathVariable Integer todoId){
        log.info("查看id为：{}的申请",todoId);
        return todoService.viewVehicle(todoId);
    }

    @GetMapping("viewSample/{todoId}")
    public Result<OilSampleVO> viewSample(@PathVariable Integer todoId){
        log.info("查看id为：{}的申请",todoId);
        return todoService.viewSample(todoId);
    }

    @GetMapping("viewDeleteVehicle/{todoId}")
    public Result<List<TodoVehicleVO>> viewDeleteVehicle(@PathVariable Integer todoId){
        log.info("查看id为：{}的申请",todoId);
        return todoService.viewDeleteVehicle(todoId);
    }
}
