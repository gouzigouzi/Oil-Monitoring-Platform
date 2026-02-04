package com.oil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oil.dto.OilChangeDTO;
import com.oil.entity.OilLife;
import com.oil.result.Result;
import com.oil.vo.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ClassName: OilLifeService
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/6/26
 * @Version 1.0
 */
public interface OilLifeService extends IService<OilLife>{
    /**
     * 得到车辆的机油寿命数据
     * @param deviceNo
     * @return
     */
    Result<OilLifeVO> life(String deviceNo);

    /**
     * 得到车辆的TBOX工况数据
     * @param deviceNo
     * @return
     */
    Result<EngineLineVO> engineLine(String deviceNo);

    /**
     * 得到车辆的恶劣工况损失情况
     * @param deviceNo
     * @return
     */
    Result<EngineBarVO> engineBar(String deviceNo);

    /**
     * 得到车辆的TBOX恶劣工况分布
     * @param deviceNo
     * @return
     */
    Result<EnginePieVO> enginePie(String deviceNo);

    /**
     * 查询车辆的换油历史记录
     * @param deviceNo
     * @return
     */
    Result<List<OilChangeVO>> changeHistory(String deviceNo);

    /**
     * 对车辆进行机油更换
     * @param oilChangeDTO
     * @return
     */
    @Transactional
    Result changeOil(OilChangeDTO oilChangeDTO);
}
