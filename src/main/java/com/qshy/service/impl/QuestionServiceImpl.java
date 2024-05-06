package com.qshy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.Question;
import com.qshy.mapper.QuestionMapper;
import com.qshy.mapper.UserMapper;
import com.qshy.service.IQuestionService;
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
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements IQuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public List<Question> userQuestionList(String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("ask_time");
        List<Question> list = questionMapper.selectList(wrapper);
        if (list != null) {
            String s = "";
            s = userMapper.selectUserAvatar(userId);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setAvatar(s);
            }
        }
        return list;
    }

    @Override
    public List<Question> allQuestionList() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.orderByDesc("ask_time");
        List<Question> list = questionMapper.selectList(wrapper);
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
    public Page<Question> searchQuestion(Integer pageNum, Integer pageSize, String str) {
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.like("question_name", str);
        wrapper.orderByDesc("ask_time");
        Page<Question> list = questionMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        if (list != null) {
            List<Question> tmp = list.getRecords();
            for (Question i : tmp) {
                String userId = i.getUserId();
                String s = userMapper.selectUserAvatar(userId);
                i.setAvatar(s);
            }
            list.setRecords(tmp);
        }
        return list;
    }
}
