package com.qshy.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qshy.entity.Code;
import com.qshy.entity.Result;
import com.qshy.entity.Route;
import com.qshy.service.impl.RouteServiceImpl;
import com.qshy.util.TokenUtil;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/route")
public class RouteController {
    @Autowired
    private RouteServiceImpl routeService;

    @RequestMapping("/getRoutes")
    @ResponseBody
    public Result getRoutes(@RequestParam(required = false) String area) {
//        System.out.println(area);
        List<Route> list = routeService.getDefaultRoutes(area, null);
        Map<String, Object> map = new HashMap<>();
        map.put("routes", list);
        return new Result(map, Code.SUCCESS, "获取推荐路线成功");
    }

    @RequestMapping("/search")
    @ResponseBody
    public Result searchRoute(@RequestParam String scenicName) {
        List<Route> list = routeService.getDefaultRoutes(null, scenicName);
        Map<String, Object> map = new HashMap<>();
        map.put("routes", list);
        return new Result(map, Code.SUCCESS, "获取推荐路线成功");
    }

    @RequestMapping("/searchConnection")
    @ResponseBody
    public Result searchConnection(@RequestParam(required = false, name = "search") String scenicName,
                                   @RequestParam(required = false) String area,
                                   @RequestParam String userId, @RequestHeader String token) {
        List<Route> list = routeService.searchUserConnection(scenicName, area, userId);
        Map<String, Object> map = new HashMap<>();
        map.put("routes", list);
        return new Result(map, Code.SUCCESS, "获取推荐路线成功");
    }

    @RequestMapping("/initRoute")
    @ResponseBody
    public Result initRoute(@RequestParam String routeId,
                            @RequestParam String userId,
                            @RequestParam String startName,
                            @RequestParam String endName) {
        if (startName == null || endName == null) {
            return new Result(null, Code.SUCCESS, "起点和终点不能为空");
        }
        if (startName.equals(endName)) {
            return new Result(null, Code.SUCCESS, "起点和终点不能相同");
        }
        Route r = routeService.getInitRoutes(userId, routeId, startName, endName);
        if (r != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("route", r);
            return new Result(map, Code.SUCCESS, "路线生成成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线生成失败");
        }
    }

    /**
     * 收藏路线
     *
     * @param routeId
     * @param userId
     * @return
     */
    @RequestMapping("/collectionRoute")
    @ResponseBody
    public Result collectionRoute(@RequestParam String routeId,
                                  @RequestParam String userId,
                                  @RequestHeader String token
    ) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.USER_NOT_EXIST, "未登录");
        }
        Route r = routeService.getById(routeId);
        if (r.getCollectionJson() == null) {
            r.setCollectionJson("[]");
        }
        List<String> list = JSON.parseArray(r.getCollectionJson(), String.class);
        if (!list.contains(userId))
            list.add(userId);
        else
            return new Result(null, Code.SUCCESS, "已收藏");
        r.setCollectionJson(JSON.toJSONString(list));
        r.setCollectionUsers(list);
        UpdateWrapper<Route> wrapper = new UpdateWrapper<>();
        wrapper.eq("route_id", routeId);
        boolean update = routeService.update(r, wrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "路线收藏成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线收藏失败");
        }
    }

    /**
     * 收藏路线
     *
     * @param routeId
     * @param userId
     * @return
     */
    @RequestMapping("/deUserRoute")
    @ResponseBody
    public Result deUserRoute(@RequestParam String routeId,
                              @RequestParam String userId,
                              @RequestHeader String token
    ) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.USER_NOT_EXIST, "未登录");
        }
        boolean update = routeService.removeRoute(routeId);
        if (update) {
            return new Result(null, Code.SUCCESS, "路线删除成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线删除失败");
        }
    }

    /**
     * 收藏路线
     *
     * @param routeId
     * @param userId
     * @return
     */
    @RequestMapping("/deCollectionRoute")
    @ResponseBody
    public Result decollectionRoute(@RequestParam String routeId,
                                    @RequestParam String userId,
                                    @RequestHeader String token
    ) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.USER_NOT_EXIST, "未登录");
        }
        Route r = routeService.getById(routeId);
        if (r.getCollectionJson() == null) {
            r.setCollectionJson("[]");
        }
        List<String> list = JSON.parseArray(r.getCollectionJson(), String.class);
        if (list.contains(userId))
            list.remove(userId);
        else
            return new Result(null, Code.SUCCESS, "未收藏");
        r.setCollectionJson(JSON.toJSONString(list));
        r.setCollectionUsers(list);
        UpdateWrapper<Route> wrapper = new UpdateWrapper<>();
        wrapper.eq("route_id", routeId);
        boolean update = routeService.update(r, wrapper);
        if (update) {
            return new Result(null, Code.SUCCESS, "路线收藏成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线收藏失败");
        }
    }

    @RequestMapping("/singleRoute")
    @ResponseBody
    @Transactional
    public Result singleRoute(@RequestParam String routeId) {
        Route r = routeService.getSingleRoute(routeId);
        if (r != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("route", r);
            return new Result(map, Code.SUCCESS, "路线获取成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线获取失败");
        }
    }

    @RequestMapping("/getUserCollectionRoutes")
    @ResponseBody
    @Transactional
    public Result getUserCollectedRoutes(@RequestParam String userId) {
        List<Route> r = routeService.getUserCollectedRoutes(userId);
        if (r != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("routes", r);
            return new Result(map, Code.SUCCESS, "路线获取成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线获取失败");
        }
    }

    @RequestMapping("/userRoutes")
    @ResponseBody
    @Transactional
    public Result userRoutes(@RequestParam String userId) {
        List<Route> r = routeService.getUserRoutes(userId);
        if (r != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("routes", r);
            return new Result(map, Code.SUCCESS, "路线获取成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线获取失败");
        }
    }

    @RequestMapping("/addScenic")
    @ResponseBody
    @Transactional
    public Result addScenic(@RequestParam String routeId, @RequestParam String scenicId) {
        Route r = routeService.addScenicToRoute(routeId, scenicId);
        if (r != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("route", r);
            return new Result(map, Code.SUCCESS, "路线生成成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线生成失败,可能有重复景点");
        }
    }

    @RequestMapping("/deleteScenic")
    @ResponseBody
    @Transactional
    public Result deleteScenic(@RequestParam String routeId,
                               @RequestParam String scenicId
    ) {
        Route r = routeService.deleteScenicOfRoute(routeId, scenicId);
        if (r != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("route", r);
            return new Result(map, Code.SUCCESS, "路线生成成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线删除失败,可能路线中没有此景点");
        }
    }

    @RequestMapping("/openRoute")
    @ResponseBody
    public Result OpenRoute(@RequestParam String routeId,
                            @RequestParam String open,
                            @RequestHeader String token) {
//        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.USER_NOT_EXIST, "未登录");
//        }
        Route r = routeService.getById(routeId);
        r.setOpen(open);
        UpdateWrapper<Route> wrapper = new UpdateWrapper<>();
        wrapper.eq("route_id", routeId);
        boolean update = routeService.update(r, wrapper);
        if (update) {
            Map<String, Object> map = new HashMap<>();
            map.put("route", r);
            return new Result(map, Code.SUCCESS, "路线开放更改成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线开放更改失败");
        }
    }

    @RequestMapping("/changeRouteName")
    @ResponseBody
    public Result ChangeRouteName(@RequestParam String routeId,
                                  @RequestParam String name,
                                  @RequestHeader String token) {
//        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.USER_NOT_EXIST, "未登录");
//        }
        Route r = routeService.getById(routeId);
        r.setRouteName(name);
        UpdateWrapper<Route> wrapper = new UpdateWrapper<>();
        wrapper.eq("route_id", routeId);
        boolean update = routeService.update(r, wrapper);
        if (update) {
            Map<String, Object> map = new HashMap<>();
            map.put("route", r);
            return new Result(map, Code.SUCCESS, "路线改名成功");
        } else {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new Result(null, Code.ROUTE_ERROR, "路线改名失败");
        }
    }

}
