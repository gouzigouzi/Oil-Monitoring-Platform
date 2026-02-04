package com.oil.controller;

import com.oil.dto.OilSampleDTO;
import com.oil.dto.OilSamplePageQueryDTO;
import com.oil.dto.VehicleDTO;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.service.OilSampleService;
import com.oil.vo.OilSampleVO;
import com.oil.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ClassName: OilSampleController
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/13
 * @Version 1.0
 */
@RestController
@RequestMapping("oilsample")
@Slf4j
public class OilSampleController {
    @Autowired
    private OilSampleService oilSampleService;

    @GetMapping("page")
    public Result<PageResult> page(OilSamplePageQueryDTO oilSamplePageQueryDTO){
        log.info("机油样本分页查询：{}", oilSamplePageQueryDTO);
        return oilSampleService.pageQuery(oilSamplePageQueryDTO);
    }

    @DeleteMapping("delete")
    public Result delete(@RequestBody List<String> oilSampleNoList){
        log.info("机油样本编号为:{}", oilSampleNoList);
        return oilSampleService.deleteByOilSampleList(oilSampleNoList);
    }

    @GetMapping("query")
    public Result<OilSampleVO> query(String oilSampleId){
        log.info("查询的机油样本编号为：{}", oilSampleId);
        return oilSampleService.queryByOilSampleId(oilSampleId);
    }

    @PostMapping("add")
    public Result add(@RequestBody OilSampleDTO oilSampleDTO){
        log.info("新增机油样本：{}", oilSampleDTO);
        return oilSampleService.addOilSample(oilSampleDTO);
    }

}
