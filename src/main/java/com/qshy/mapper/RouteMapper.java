package com.qshy.mapper;

import com.qshy.entity.Route;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qshy.entity.SingleRoute;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-01-07
 */

@Mapper
public interface RouteMapper extends BaseMapper<Route> {

    List<SingleRoute> selectScenicIds(String routeId);

    List<String> selectRouteIdOfSc(@Param("id") String scid);

    List<String> selectRouteIdOfArea(@Param("sqlLike") String sqlLike);
}
