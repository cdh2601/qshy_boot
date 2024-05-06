package com.qshy.service;

import com.qshy.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
public interface IUserService extends IService<User> {
    boolean checkExist(String email);

    boolean regist(User user);



    boolean deleteUser(String userId);
}
