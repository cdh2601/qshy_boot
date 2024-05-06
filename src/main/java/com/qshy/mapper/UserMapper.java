package com.qshy.mapper;

import com.qshy.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    String selectUserAvatar(@Param("userId") String userId);

    String selectUserName(@Param("userId")  String fromUserId);
}
