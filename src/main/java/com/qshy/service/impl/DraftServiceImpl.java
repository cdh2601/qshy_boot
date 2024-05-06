package com.qshy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qshy.entity.Draft;
import com.qshy.entity.Strategy;
import com.qshy.mapper.DraftMapper;
import com.qshy.mapper.StrategyMapper;
import com.qshy.mapper.UserMapper;
import com.qshy.service.IDraft;
import com.qshy.service.IStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@Service
public class DraftServiceImpl extends ServiceImpl<DraftMapper, Draft> implements IDraft {

    @Autowired
    private StrategyMapper strategyMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Page<Strategy> searchStrategyList(int pageNum, int pageSize, String str) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        wrapper.like("strategy_name", str);
        wrapper.eq("open", 1);
        Page<Strategy> list = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        if (list != null) {
            List<Strategy> tmp = list.getRecords();
            List<Strategy> nlist = new ArrayList<>();
            for (Strategy strategy : tmp) {
                String userId = strategy.getUserId();
                String s = userMapper.selectUserAvatar(userId);
                String name = userMapper.selectUserName(userId);
                strategy.setAvatar(s);
                strategy.setOwnerName(name);
                strategy.setCollections(JSON.parseArray(strategy.getCollectionJson(), String.class));
                strategy.setLikes(JSON.parseArray(strategy.getLikeJson(), String.class));
                nlist.add(strategy);
            }
            list.setRecords(nlist);
        }
        return list;
    }

    @Override
    public Page<Strategy> allStrategyList(int pageNum, int pageSize) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        Page<Strategy> list = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        if (list != null) {
            List<Strategy> tmp = list.getRecords();
            List<Strategy> nlist = new ArrayList<>();
            for (Strategy strategy : tmp) {
                String userId = strategy.getUserId();
                String s = userMapper.selectUserAvatar(userId);
                String name = userMapper.selectUserName(userId);
                strategy.setAvatar(s);
                strategy.setOwnerName(name);
                strategy.setCollections(JSON.parseArray(strategy.getCollectionJson(), String.class));
                strategy.setLikes(JSON.parseArray(strategy.getLikeJson(), String.class));
                nlist.add(strategy);
            }
            list.setRecords(nlist);
        }
        return list;
    }

    @Override
    public Strategy getOneStrategyInfo(String strategyId) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("strategy_id", strategyId);
        wrapper.orderByDesc("create_time");
        Strategy one = strategyMapper.selectOne(wrapper);
        if (one != null) {
            String userId = one.getUserId();
            String s = userMapper.selectUserAvatar(userId);
            String name = userMapper.selectUserName(userId);
            one.setCollections(JSON.parseArray(one.getCollectionJson(), String.class));
            one.setLikes(JSON.parseArray(one.getLikeJson(), String.class));
            one.setAvatar(s);
            one.setOwnerName(name);
        }
        return one;
    }

    @Override
    public Page<Strategy> getUserStrategyList(String userId, Integer pageSize, Integer pageNum) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("open", 1);
        Page<Strategy> list = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        String name = userMapper.selectUserName(userId);
        String s = userMapper.selectUserAvatar(userId);
        if (list != null) {
            List<Strategy> tmp = list.getRecords();
            List<Strategy> nlist = new ArrayList<>();
            for (Strategy strategy : tmp) {
                strategy.setOwnerName(name);
                strategy.setAvatar(s);
                strategy.setCollections(JSON.parseArray(strategy.getCollectionJson(), String.class));
                strategy.setLikes(JSON.parseArray(strategy.getLikeJson(), String.class));
                nlist.add(strategy);
            }
            list.setRecords(nlist);
        }
        return list;
    }

    @Override
    public List<Strategy> getUserIssueStrategyList(String userId) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("type", 1);
        List<Strategy> list = strategyMapper.selectList(wrapper);
        String name = userMapper.selectUserName(userId);
        String s = userMapper.selectUserAvatar(userId);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setAvatar(s);
                list.get(i).setOwnerName(name);
                list.get(i).setCollections(JSON.parseArray(list.get(i).getCollectionJson(), String.class));
                list.get(i).setLikes(JSON.parseArray(list.get(i).getLikeJson(), String.class));
            }
        }
        return list;
    }
}
