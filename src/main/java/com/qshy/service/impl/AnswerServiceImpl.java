package com.qshy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qshy.entity.Answer;
import com.qshy.mapper.AnswerMapper;
import com.qshy.mapper.UserMapper;
import com.qshy.service.IAnswerService;
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
public class AnswerServiceImpl extends ServiceImpl<AnswerMapper, Answer> implements IAnswerService {

    @Autowired
    private AnswerMapper answerMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Answer> questionAnswerList(String questionId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("question_id", questionId);
        wrapper.orderByDesc("answer_time");
        List<Answer> list = answerMapper.selectList(wrapper);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String userId = list.get(i).getUserId();
                String s = userMapper.selectUserAvatar(userId);
                list.get(i).setAvatar(s);
            }
        }
        return list;
    }

    @Override
    public List<Answer> toUserAnswerList(String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("to_user_id", userId);
        wrapper.orderByDesc("answer_time");
        List<Answer> list = answerMapper.selectList(wrapper);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                String from = list.get(i).getUserId();
                String s = userMapper.selectUserAvatar(from);
                list.get(i).setAvatar(s);
            }
        }
        return list;
    }

    @Override
    public List<Answer> userAnswerList(String userId) {
        return null;
    }
}
