package com.oil.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.oil.dto.OilSampleDTO;
import com.oil.dto.OilSamplePageQueryDTO;
import com.oil.entity.OilSample;
import com.oil.entity.User;
import com.oil.result.PageResult;
import com.oil.result.Result;
import com.oil.vo.OilSampleVO;

import java.util.List;

/**
 * ClassName: OilSampleService
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/13
 * @Version 1.0
 */
public interface OilSampleService extends IService<OilSample> {
    /**
     * 机油样本分页查询
     * @param oilSamplePageQueryDTO
     * @return
     */
    Result<PageResult> pageQuery(OilSamplePageQueryDTO oilSamplePageQueryDTO);

    /**
     * 机油样本批量删除
     * @param oilSampleNoList
     * @return
     */
    Result deleteByOilSampleList(List<String> oilSampleNoList);

    /**
     * 根据机油样本编号查询机油样本数据
     * @param oilSampleId
     * @return
     */
    Result<OilSampleVO> queryByOilSampleId(String oilSampleId);

    /**
     * 新增机油样本
     * @param oilSampleDTO
     * @return
     */
    Result addOilSample(OilSampleDTO oilSampleDTO);

    /**
     * 管理者新增机油样本
     * @param oilSampleDTO
     * @param user
     * @return
     */
    Result addOilSampleByAdmin(OilSampleDTO oilSampleDTO, User user);
}
