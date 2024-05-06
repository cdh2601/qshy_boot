package com.qshy.mapper;

import com.qshy.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-01-07
 */

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    List<String> selectUserAvatars(@Param("toUserId") String toUserId);

    List<String> selectUserList(String parentId);
}
