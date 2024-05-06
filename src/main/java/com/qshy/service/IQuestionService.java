package com.qshy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.Question;
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
public interface IQuestionService extends IService<Question> {
    List<Question> userQuestionList(String userId);

    List<Question> allQuestionList();


    Page<Question> searchQuestion(Integer pageNum, Integer pageSize, String str);
}
