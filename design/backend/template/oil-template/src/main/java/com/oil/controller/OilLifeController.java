package com.oil.controller;

import com.oil.dto.OilChangeDTO;
import com.oil.result.Result;
import com.oil.service.OilLifeService;
import com.oil.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ClassName: OilInformationController
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/16
 * @Version 1.0
 */
@RestController
@RequestMapping("oillife")
@Slf4j
public class OilLifeController {
    @Autowired
    OilLifeService oilLifeService;

    @GetMapping("life")
    public Result<OilLifeVO> life(String deviceNo) {
        log.info("查询车辆编号为：{} 的机油寿命信息", deviceNo);
        return oilLifeService.life(deviceNo);
    }

    @GetMapping("engineline")
    public Result<EngineLineVO> engineLine(String deviceNo) {
        log.info("查询车辆编号为：{} 的TBOX工况信息", deviceNo);
        return oilLifeService.engineLine(deviceNo);
    }

    @GetMapping("enginebar")
    public Result<EngineBarVO> engineBar(String deviceNo) {
        log.info("查询车辆编号为：{} 的工况损失分布情况", deviceNo);
        return oilLifeService.engineBar(deviceNo);
    }

    @GetMapping("enginepie")
    public Result<EnginePieVO> enginePie(String deviceNo) {
        log.info("查询车辆编号为：{} 的TBOX恶劣工况分布", deviceNo);
        return oilLifeService.enginePie(deviceNo);
    }

    @GetMapping("changehistory")
    public Result<List<OilChangeVO>> changeHistory(String deviceNo) {
        log.info("查询车辆编号为：{} 的历史换油记录", deviceNo);
        return oilLifeService.changeHistory(deviceNo);
    }

    @PostMapping("changeoil")
    public Result changeOil(@RequestBody OilChangeDTO oilChangeDTO) {
        log.info("车辆编号为:{}进行机油更换，更换时间为{}", oilChangeDTO.getDeviceNo(), oilChangeDTO.getLastChangeDatetime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")));
        return oilLifeService.changeOil(oilChangeDTO);
    }
}
