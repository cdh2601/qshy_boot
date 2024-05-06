package com.qshy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.Draft;
import com.qshy.entity.Strategy;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
public interface IStrategyService extends IService<Strategy> {


    Page<Strategy> searchStrategyList(int pageNum, int pageSize, String str);

    Page<Strategy> allStrategyListPage(int pageNum, int pageSize);

    Strategy getOneStrategyInfo(String strategyId);

    Page<Strategy> getUserStrategyPage(String userId, Integer pageSize, Integer pageNum);

    List<Draft> getUserDraftList(String userId);

    List<Strategy> getUserStrategyList(String userId);

    Page<Strategy> searchConnectionList(String strategyName, String userId, Integer pageNum, Integer pageSize);

    Page<Strategy> allStrategyPageCollection(Integer pageNum, Integer pageSize);

    Page<Strategy> getUserCollectedStrategyList(String userId, Integer pageNum, Integer pageSize);

    Page<Strategy> searchUserStrategy(String strategyName, String userId, Integer pageNum, Integer pageSize);

    List<Strategy> allStrategyListByCollection();
}
