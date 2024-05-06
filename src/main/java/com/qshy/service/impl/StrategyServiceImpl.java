package com.qshy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.Draft;
import com.qshy.entity.Strategy;
import com.qshy.mapper.DraftMapper;
import com.qshy.mapper.StrategyMapper;
import com.qshy.mapper.UserMapper;
import com.qshy.service.IStrategyService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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
public class StrategyServiceImpl extends ServiceImpl<StrategyMapper, Strategy> implements IStrategyService {

    @Autowired
    private StrategyMapper strategyMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DraftMapper draftMapper;

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
//            list.setTotal(tmp.size());
            list.setRecords(nlist);
        }
        return list;
    }

    @Override
    public Page<Strategy> allStrategyListPage(int pageNum, int pageSize) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
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
//            list.setTotal(tmp.size());
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
    public Page<Strategy> getUserStrategyPage(String userId, Integer pageSize, Integer pageNum) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
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
//            list.setTotal(tmp.size());
            list.setRecords(nlist);
        }
        return list;
    }

    @Override
    public List<Draft> getUserDraftList(String userId) {
        QueryWrapper<Draft> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Draft> list = draftMapper.selectList(wrapper);
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

    @Override
    public List<Strategy> getUserStrategyList(String userId) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
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

    @Override
    public Page<Strategy> searchConnectionList(String strategyName, String userId,
                                               Integer pageNum, Integer pageSize) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        if (strategyName != null) {
            wrapper.like("strategy_name", strategyName);
        }
        wrapper.like("collection_json", "\"" + userId + "\"");
        wrapper.eq("open", 1);
        Page<Strategy> page = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Strategy> list = page.getRecords();
        List<Strategy> cleaned = new ArrayList<>();
        if (list != null) {
            for (Strategy strategy : list) {
                List<String> users = JSON.parseArray(strategy.getCollectionJson(), String.class);
                if (!users.contains(userId)) {
                    continue;
                }
                String s = userMapper.selectUserAvatar(strategy.getUserId());
                String name = userMapper.selectUserName(strategy.getUserId());
                strategy.setAvatar(s);
                strategy.setOwnerName(name);
                strategy.setCollections(JSON.parseArray(strategy.getCollectionJson(), String.class));
                strategy.setLikes(JSON.parseArray(strategy.getLikeJson(), String.class));
                cleaned.add(strategy);
            }
//            page.setTotal(cleaned.size());
            page.setRecords(cleaned);
        }
        return page;
    }

    @Override
    public Page<Strategy> allStrategyPageCollection(Integer pageNum, Integer pageSize) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("open", 1);
        Page<Strategy> list = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        if (list != null) {
            List<Strategy> tmp = list.getRecords();
            List<Strategy> nlist = new ArrayList<>();
            for (Strategy strategy : tmp) {
                String userId = strategy.getUserId();
                String s = userMapper.selectUserAvatar(strategy.getUserId());
                String name = userMapper.selectUserName(strategy.getUserId());
                strategy.setAvatar(s);
                strategy.setOwnerName(name);
                strategy.setCollections(JSON.parseArray(strategy.getCollectionJson(), String.class));
                strategy.setLikes(JSON.parseArray(strategy.getLikeJson(), String.class));
                nlist.add(strategy);
            }
            Collections.sort(nlist);
//            list.setTotal(tmp.size());
            list.setRecords(nlist);
        }
        return list;
    }

    @Override
    public Page<Strategy> getUserCollectedStrategyList(String userId, Integer pageNum, Integer pageSize) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("open", 1);
        wrapper.like("collection_json", "\"" + userId + "\"");
        Page<Strategy> page = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Strategy> list = page.getRecords();
        List<Strategy> cleaned = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String name = userMapper.selectUserName(list.get(i).getUserId());
                String s = userMapper.selectUserAvatar(list.get(i).getUserId());
                List<String> users = JSON.parseArray(list.get(i).getCollectionJson(), String.class);
                if (!users.contains(userId)) {
                    continue;
                }
                list.get(i).setAvatar(s);
                list.get(i).setOwnerName(name);
                list.get(i).setCollections(JSON.parseArray(list.get(i).getCollectionJson(), String.class));
                list.get(i).setLikes(JSON.parseArray(list.get(i).getLikeJson(), String.class));
                cleaned.add(list.get(i));
            }
//            page.setTotal(cleaned.size());
            page.setRecords(cleaned);
        }
        return page;
    }

    @Override
    public Page<Strategy> searchUserStrategy(String strategyName, String userId,
                                             Integer pageNum, Integer pageSize) {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        if (strategyName != null)
            wrapper.like("strategy_name", strategyName);
        wrapper.eq("user_id", userId);
        Page<Strategy> page = strategyMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        List<Strategy> list = page.getRecords();
        List<Strategy> cleaned = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String name = userMapper.selectUserName(list.get(i).getUserId());
                String s = userMapper.selectUserAvatar(list.get(i).getUserId());
                list.get(i).setAvatar(s);
                list.get(i).setOwnerName(name);
                list.get(i).setCollections(JSON.parseArray(list.get(i).getCollectionJson(), String.class));
                list.get(i).setLikes(JSON.parseArray(list.get(i).getLikeJson(), String.class));
                cleaned.add(list.get(i));
            }
//            page.setTotal(list.size());
            page.setRecords(cleaned);
        }
        return page;
    }

    @Override
    public List<Strategy> allStrategyListByCollection() {
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("open", 1);
        List<Strategy> list = strategyMapper.selectList(wrapper);
        List<Strategy> cleaned = new ArrayList<>();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String name = userMapper.selectUserName(list.get(i).getUserId());
                String s = userMapper.selectUserAvatar(list.get(i).getUserId());
                list.get(i).setAvatar(s);
                list.get(i).setOwnerName(name);
                list.get(i).setCollections(JSON.parseArray(list.get(i).getCollectionJson(), String.class));
                list.get(i).setLikes(JSON.parseArray(list.get(i).getLikeJson(), String.class));
            }
            Collections.sort(list);
            int len = Math.min(list.size(), 3);
            for (int j = 0; j < len; j++) {
                cleaned.add(list.get(j));
            }
        }
        return cleaned;
    }
}
