package com.qshy.service;

import com.qshy.entity.Route;
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
public interface IRouteService extends IService<Route> {

    List<Route> getDefaultRoutes(String area, String search);

    Route getInitRoutes(String userId, String routeId, String startName, String endName);

    Route addScenicToRoute(String routeId, String scenicId);

    Route deleteScenicOfRoute(String routeId, String scenicId);

    Route getSingleRoute(String routeId);

    List<Route> getUserRoutes(String userId);

    List<Route> searchUserConnection(String scenicName, String area, String userId);

    List<Route> getUserCollectedRoutes(String userId);

    boolean removeRoute(String routeId);
}
