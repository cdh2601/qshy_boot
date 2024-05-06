package com.qshy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qshy.entity.User;
import com.qshy.mapper.UserMapper;
import com.qshy.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qshy.util.MySecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean checkExist(String email) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", email);
        try {
            return userMapper.selectOne(queryWrapper) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean regist(User user) {
        try {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            String userId = uuid.substring(0, 12);
            String dp = MySecurityUtil.desEncrypt(user.getPassword());
            String encode = passwordEncoder.encode(dp);
            user.setPassword(encode);
            user.setUserId(userId);
            user.setCreateTime(LocalDateTime.now());
            user.setAvatar("http://localhost:8080/QSHY/system/defaultavatar.jpg");
            user.setTravelAge(0);
            user.setLikeType(user.getLikeType());
            int i = userMapper.insert(user);
            return i > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean deleteUser(String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        int delete = userMapper.delete(wrapper);
        return delete == 1;
    }
}
