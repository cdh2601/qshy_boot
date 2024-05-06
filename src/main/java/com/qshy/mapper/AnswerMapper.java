package com.qshy.mapper;

import com.qshy.entity.Answer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-01-07
 */

@Mapper
public interface AnswerMapper extends BaseMapper<Answer> {

}
