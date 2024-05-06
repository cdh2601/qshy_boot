package com.qshy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qshy.entity.TravelRecord;
import com.qshy.mapper.TravelRecordMapper;
import com.qshy.mapper.UserMapper;
import com.qshy.service.ITravelRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class TravelRecordServiceImpl extends ServiceImpl<TravelRecordMapper, TravelRecord> implements ITravelRecordService {
    @Autowired
    private TravelRecordMapper travelRecordMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<TravelRecord> getUsersRecords(String userId) {
        QueryWrapper w = new QueryWrapper<>();
        w.eq("user_id", userId);
        w.orderByDesc("create_time");
        List<TravelRecord> list = travelRecordMapper.selectList(w);
        String avatar = userMapper.selectUserAvatar(userId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setAvatar(avatar);
        }
        return list;
    }

    @Override
    public List<TravelRecord> getAllRecords() {
        QueryWrapper w = new QueryWrapper<>();
        w.eq("share", "1");
        w.orderByDesc("create_time");
        List<TravelRecord> list = travelRecordMapper.selectList(w);
        for (int i = 0; i < list.size(); i++) {
            String avatar = userMapper.selectUserAvatar(list.get(i).getUserId());
            list.get(i).setAvatar(avatar);
        }
        return list;
    }

    @Override
    public boolean shareRecord(String recordId, String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("travel_record_id", recordId);
        TravelRecord travelRecord = travelRecordMapper.selectOne(wrapper);
        travelRecord.setShare("1");
        int update = travelRecordMapper.update(travelRecord, wrapper);
        return update == 1;
    }
}
