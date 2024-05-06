package com.qshy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.qshy.entity.Draft;
import com.qshy.entity.Strategy;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
public interface IDraft extends IService<Draft> {

    Page<Strategy> searchStrategyList(int pageNum, int pageSize, String str);

    Page<Strategy> allStrategyList(int pageNum, int pageSize);

    Strategy getOneStrategyInfo(String strategyId);

    Page<Strategy> getUserStrategyList(String userId, Integer pageSize, Integer pageNum);

    List<Strategy> getUserIssueStrategyList(String userId);
}
