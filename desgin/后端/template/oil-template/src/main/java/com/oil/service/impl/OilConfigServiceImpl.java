package com.oil.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.oil.entity.OilConfig;
import com.oil.mapper.postgres.OilConfigMapper;
import com.oil.service.OilConfigService;
import org.springframework.stereotype.Service;

/**
 * ClassName: OilConfigServiceImpl
 * Description:
 *
 * @Author Jinze_Wei
 * @Create 2024/7/5
 * @Version 1.0
 */
@Service
public class OilConfigServiceImpl extends ServiceImpl<OilConfigMapper, OilConfig> implements OilConfigService {
}
