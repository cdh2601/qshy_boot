package com.qshy.util;

/**
 * @Classname TokenUtil
 * @Description TODO
 * @Date 2022/11/17 8:54
 * @Created by senorisky
 */


import com.qshy.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenUtil {
    /**
     * 创建map用于存储所有的令牌
     * <p>
     * token  -  User
     */
    private static Map<String, User> tokenMap = new HashMap<>();

    private static Map<String, String> codeMap = new HashMap<>();

    public static void SetCheckCode(String key, String val) {
        codeMap.put(key, val);
    }

    public static String GetCheckCode(String key) {
        return codeMap.get(key);
    }

    public static void DestroyCheckCode(String key) {
        if (codeMap.containsKey(key))
            codeMap.remove(key);
    }

    /**
     * 生成token，存储token-user对应关系
     * 返回token令牌
     *
     * @param user
     * @return
     */
    public static String generateToken(User user) {
        //生成唯一不重复的字符串
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, user);
        return token;
    }

    /**
     * 销毁Token
     *
     * @param key
     * @return
     */
    public static boolean Destroy(String key) {
        if (tokenMap.containsKey(key)) {
            User remove = tokenMap.remove(key);
            return remove != null;
        }
        return true;
    }

    /**
     * 验证token是否合法
     *
     * @param token
     * @return
     */
    public static boolean verify(String token) {
//        System.out.println(tokenMap.keySet());
        return tokenMap.containsKey(token);
    }

    /**
     * 根据token获取用户信息
     *
     * @param token
     * @return
     */
    public static User getUser(String token) {
        return tokenMap.get(token);
    }

    /**
     * 用户信息更改时更新Token绑定的用户
     *
     * @param token
     * @return
     */
    public static void changeUser(String token, User user) {
        tokenMap.put(token, user);
    }
}
