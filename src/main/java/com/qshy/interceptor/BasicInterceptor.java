package com.qshy.interceptor;



import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Classname BasicInterceptor
 * @Description TODO
 * @Date 2022/11/17 9:09
 * @Created by senorisky
 */
public class BasicInterceptor implements HandlerInterceptor {

    //Controller逻辑执行之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = request.getHeader("lm-token");
//        System.out.println(token);
//        if (!TokenUtil.verify(token)) {
//            throw new RuntimeException("没有登录");
//        }
        return true;
    }

    //Controller逻辑执行完毕但是视图解析器还未进行解析之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    //Controller逻辑和视图解析器执行完毕
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

}
