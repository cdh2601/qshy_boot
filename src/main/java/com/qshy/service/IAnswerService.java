package com.qshy.service;

import com.qshy.entity.Answer;
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
public interface IAnswerService extends IService<Answer> {
    List<Answer> questionAnswerList(String questionId);

    List<Answer> toUserAnswerList(String userId);

    List<Answer> userAnswerList(String userId);
}
