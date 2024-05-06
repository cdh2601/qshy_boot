package com.qshy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qshy.entity.Comment;
import com.qshy.entity.Route;
import com.qshy.entity.RouteScenic;
import com.qshy.entity.Scenic;
import com.qshy.mapper.*;
import com.qshy.service.IRouteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.rowset.spi.SyncResolver;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route> implements IRouteService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Autowired
    private RouteMapper routeMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<Route> getDefaultRoutes(String area, String search) {
        List<Route> routes = null;
        //获取所有路线
        if (search != null) {
            routes = new ArrayList<>();
            QueryWrapper<Route> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("score");
            wrapper.eq("open", "1");
            String sqlLike = "%" + search + "%";
            List<String> scid = scenicMapper.selectidListByName(sqlLike);
            List<Route> tmp = routeMapper.selectList(wrapper);
            for (Route i : tmp) {
                List<String> rscs = JSON.parseArray(i.getScenicJson(), String.class);
                for (String j : scid) {
                    if (rscs.contains(j)) {
                        routes.add(i);
                        break;
                    }
                }
            }
        } else if (area != null) {
            QueryWrapper<Route> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("score");
            wrapper.eq("open", "1");
            List<Route> tmp = routeMapper.selectList(wrapper);
            routes = new ArrayList<>();
            for (Route i : tmp) {
                List<String> list = JSON.parseArray(i.getLocation(), String.class);
                if (list.contains(area)) {
                    routes.add(i);
                }
            }
        } else {
            QueryWrapper<Route> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("score");
            wrapper.eq("open", "1");
            routes = routeMapper.selectList(wrapper);
        }
        for (int i = 0; i < routes.size(); i++) {
            //获取 用户名
            String uname = userMapper.selectUserName(routes.get(i).getUserId());
            //获取区
            String locationJson = routes.get(i).getLocation();
            //获取景点
            List<String> scenicIds = JSON.parseArray(routes.get(i).getScenicJson(), String.class);
//            System.out.println(scenicIds);
            List<RouteScenic> scenics1 = new ArrayList<>();
            for (int j = 0; j < scenicIds.size(); j++) {
                Scenic scenic = scenicMapper.selectById(scenicIds.get(j));
                String imgs = scenic.getScenicImgs();
                String text = scenic.getTextJson();
                String l = scenic.getLikeJson();
                String k = scenic.getCollectionJson();
                String tic = scenic.getTicketJson();
                String tr = scenic.getTrafficJson();
                String op = scenic.getOpenTimeJson();
                if (imgs == null) {
                    scenic.setScenicImages(new ArrayList<>());
                    scenic.setScenicImgs("[]");
                } else {
                    scenic.setScenicImages(JSON.parseArray(imgs, String.class));
                }
                if (text == null) {
                    scenic.setText(new ArrayList<>());
                    scenic.setTextJson("[]");
                } else {
                    scenic.setText(JSON.parseArray(text, String.class));
                }
                if (!"".equals(l) && l != null) {
                    scenic.setLikesUsers(JSON.parseArray(l, String.class));
                } else {
                    scenic.setLikesUsers(new ArrayList<>());
                }
                if (!"".equals(k) && k != null) {
                    scenic.setCollectionUsers(JSON.parseArray(k, String.class));
                } else {
                    scenic.setCollectionUsers(new ArrayList<>());
                }
                if (!"".equals(tic) && tic != null) {
                    scenic.setTicket(JSON.parseArray(tic, String.class));
                } else {
                    scenic.setTicket(new ArrayList<>());
                }
                if (!"".equals(tr) && tr != null) {
                    scenic.setTraffic(JSON.parseArray(tr, String.class));
                } else {
                    scenic.setTraffic(new ArrayList<>());
                }
                if (!"".equals(op) && op != null) {
                    scenic.setOpenTime(JSON.parseArray(op, String.class));
                } else {
                    scenic.setOpenTime(new ArrayList<>());
                }
                scenic.setCommentUsers(commentMapper.selectUserList(scenic.getScenicId()));
                if (j != scenicIds.size() - 1) {
                    Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                    scenics1.add(new RouteScenic(scenic, nt.getLongitude(), nt.getLatitude()));
                } else {
                    scenics1.add(new RouteScenic(scenic, -1, -1));
                }
            }
            routes.get(i).setUserName(uname);
            String cjson = routes.get(i).getCollectionJson();
            if (cjson == null) {
                routes.get(i).setCollectionUsers(new ArrayList<>());
            } else {
                routes.get(i).setCollectionUsers(JSON.parseArray(cjson, String.class));
            }
            routes.get(i).setCommentUsers(commentMapper.selectUserList(routes.get(i).getRouteId()));
            routes.get(i).setLocationList(JSON.parseArray(locationJson, String.class));
            routes.get(i).setScenics(scenics1);
            routes.get(i).setScenicIds(scenicIds);
        }
        return routes;
    }

    @Override
    public Route getInitRoutes(String userId, String routeId, String startName, String endName) {
        //获取路线
        Route route = new Route();
        route.setRouteId(routeId);
        route.setUserId(userId);
        route.setScore(5);
        route.setCollectionJson("[]");
        route.setCommentUsers(new ArrayList<>());
        route.setCollectionUsers(new ArrayList<>());
        route.setOpen("0");
        //获取 用户名
        String uname = userMapper.selectUserName(userId);
        //初始化 起始和终点
        QueryWrapper<Scenic> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("scenic_name", startName);
        Scenic st = scenicMapper.selectOne(wrapper1);
        st.setCommentUsers(commentMapper.selectUserList(st.getScenicId()));
        if (st.getCollectionJson() == null) {
            st.setCollectionUsers(new ArrayList<>());
        } else
            st.setCollectionUsers(JSON.parseArray(st.getCollectionJson(), String.class));
        if (st.getLikeJson() == null) {
            st.setCollectionUsers(new ArrayList<>());
        } else
            st.setLikesUsers(JSON.parseArray(st.getLikeJson(), String.class));
        wrapper1.clear();
        wrapper1.eq("scenic_name", endName);
        Scenic en = scenicMapper.selectOne(wrapper1);
        en.setCommentUsers(commentMapper.selectUserList(en.getScenicId()));
        if (en.getCollectionJson() == null) {
            en.setCollectionUsers(new ArrayList<>());
        } else
            en.setCollectionUsers(JSON.parseArray(en.getCollectionJson(), String.class));
        if (en.getLikeJson() == null) {
            en.setCollectionUsers(new ArrayList<>());
        } else
            en.setLikesUsers(JSON.parseArray(en.getLikeJson(), String.class));
        route.setRouteName(st.getScenicName() + "+" + en.getScenicName());
        //获取区
        if (!st.getArea().equals(en.getArea())) {
            route.setLocation("[\"" + st.getArea() + "\"," + "\"" + en.getArea() + "\"" + "]");
        } else route.setLocation("[\"" + st.getArea() + "\"" + "]");
        List<String> scenicids = new ArrayList<>();
        scenicids.add(st.getScenicId());
        scenicids.add(en.getScenicId());
        route.setScenicJson(JSON.toJSONString(scenicids));
        //写入route
        int insert = routeMapper.insert(route);
        if (insert == 1) {
            //构造景点列表
            List<RouteScenic> list2 = new ArrayList<>();
            st.setScenicImages(JSON.parseArray(st.getScenicImgs(), String.class));
            st.setText(JSON.parseArray(st.getTextJson(), String.class));
            en.setScenicImages(JSON.parseArray(en.getScenicImgs(), String.class));
            en.setText(JSON.parseArray(st.getTextJson(), String.class));
            list2.add(new RouteScenic(st, en.getLongitude(), en.getLatitude()));
            list2.add(new RouteScenic(en, -1, -1));
            route.setScenics(list2);
            route.setUserName(uname);
            route.setCollectionUsers(new ArrayList<>());
            route.setCommentUsers(new ArrayList<>());
            route.setScenicIds(scenicids);
            route.setLocationList(JSON.parseArray(route.getLocation(), String.class));
            return route;
        }
        return null;
    }

    @Override
    public Route addScenicToRoute(String routeId, String scenicId) {
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("route_id", routeId);
        //获取当前路线
        Route route = routeMapper.selectOne(wrapper);
        List<String> scenicIds = JSON.parseArray(route.getScenicJson(), String.class);
        if (scenicIds.contains(scenicId)) {
            return null;
        }
        int scleng = scenicIds.size();
        scenicIds.add(scleng - 1, scenicId);
        route.setScenicJson(JSON.toJSONString(scenicIds));
        //获取 用户名
        String uname = userMapper.selectUserName(route.getUserId());
        //获取区
        List<String> areas = new ArrayList<>();
        List<RouteScenic> scenics1 = new ArrayList<>();
        String name = "";
        for (int j = 0; j < scenicIds.size(); j++) {
            Scenic tmp = scenicMapper.selectById(scenicIds.get(j));
            String imgs = tmp.getScenicImgs();
            String text = tmp.getTextJson();
            String l = tmp.getLikeJson();
            String k = tmp.getCollectionJson();
            String tic = tmp.getTicketJson();
            String tr = tmp.getTrafficJson();
            String op = tmp.getOpenTimeJson();
            if (!areas.contains(tmp.getArea())) {
                areas.add(tmp.getArea());
            }
            if (imgs == null) {
                tmp.setScenicImages(new ArrayList<>());
                tmp.setScenicImgs("[]");
            } else {
                tmp.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                tmp.setText(new ArrayList<>());
                tmp.setTextJson("[]");
            } else {
                tmp.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(l) && l != null) {
                tmp.setLikesUsers(JSON.parseArray(l, String.class));
            } else {
                tmp.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                tmp.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                tmp.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                tmp.setTicket(JSON.parseArray(tic, String.class));
            } else {
                tmp.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                tmp.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                tmp.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                tmp.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                tmp.setOpenTime(new ArrayList<>());
            }
            tmp.setCommentUsers(commentMapper.selectUserList(tmp.getScenicId()));
            if (j != scenicIds.size() - 1) {
                Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                scenics1.add(new RouteScenic(tmp, nt.getLongitude(), nt.getLatitude()));
                name = name + tmp.getScenicName() + "+";
            } else {
                scenics1.add(new RouteScenic(tmp, -1, -1));
                name = name + tmp.getScenicName();
            }

        }
        route.setLocation(JSON.toJSONString(areas));
        route.setRouteName(name);//改名
        //写入route
        UpdateWrapper<Route> wrapper2 = new UpdateWrapper<>();
        wrapper2.eq("route_id", routeId);
        int insert = routeMapper.update(route, wrapper2);
        if (insert == 1) {
            route.setUserName(uname);
            route.setScenics(scenics1);
            route.setLocationList(areas);
            route.setScenicIds(scenicIds);
            route.setCollectionUsers(JSON.parseArray(route.getCollectionJson(), String.class));
            route.setCommentUsers(commentMapper.selectUserList(route.getRouteId()));
            return route;
        }
        return null;
    }

    @Override
    public Route deleteScenicOfRoute(String routeId, String scenicId) {
        //获取当前的路线
        Route route = routeMapper.selectById(routeId);
        List<String> scenicIds = JSON.parseArray(route.getScenicJson(), String.class);
        for (int i = 0; i < scenicIds.size(); i++) {
            if (scenicIds.get(i).equals(scenicId)) {
                scenicIds.remove(i);
                break;
            }
            if (i == scenicIds.size() - 1) {
                return null;
            }
        }
        route.setScenicJson(JSON.toJSONString(scenicIds));
        //获取 用户名
        String uname = userMapper.selectUserName(route.getUserId());
        List<String> areas = new ArrayList<>();
        List<RouteScenic> scenics1 = new ArrayList<>();
        String name = "";
        for (int j = 0; j < scenicIds.size(); j++) {
            Scenic tmp = scenicMapper.selectById(scenicIds.get(j));

            String imgs = tmp.getScenicImgs();
            String text = tmp.getTextJson();
            String l = tmp.getLikeJson();
            String k = tmp.getCollectionJson();
            String tic = tmp.getTicketJson();
            String tr = tmp.getTrafficJson();
            String op = tmp.getOpenTimeJson();
            if (!areas.contains(tmp.getArea())) {
                areas.add(tmp.getArea());
            }
            if (imgs == null) {
                tmp.setScenicImages(new ArrayList<>());
                tmp.setScenicImgs("[]");
            } else {
                tmp.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                tmp.setText(new ArrayList<>());
                tmp.setTextJson("[]");
            } else {
                tmp.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(l) && l != null) {
                tmp.setLikesUsers(JSON.parseArray(l, String.class));
            } else {
                tmp.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                tmp.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                tmp.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                tmp.setTicket(JSON.parseArray(tic, String.class));
            } else {
                tmp.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                tmp.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                tmp.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                tmp.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                tmp.setOpenTime(new ArrayList<>());
            }
            tmp.setCommentUsers(commentMapper.selectUserList(tmp.getScenicId()));
            if (j != scenicIds.size() - 1) {
                Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                scenics1.add(new RouteScenic(tmp, nt.getLongitude(), nt.getLatitude()));
                name = name + tmp.getScenicName() + "+";
            } else {
                scenics1.add(new RouteScenic(tmp, -1, -1));
                name = name + tmp.getScenicName();
            }

        }
        route.setRouteName(name);
        route.setLocation(JSON.toJSONString(areas));
        //写入route
        UpdateWrapper<Route> wrapper2 = new UpdateWrapper<>();
        wrapper2.eq("route_id", routeId);
        int insert = routeMapper.update(route, wrapper2);
        if (insert == 1) {
            route.setUserName(uname);
            route.setScenics(scenics1);
            route.setLocationList(areas);
            route.setCollectionUsers(JSON.parseArray(route.getCollectionJson(), String.class));
            route.setCommentUsers(commentMapper.selectUserList(route.getRouteId()));
            return route;
        }
        return null;
    }

    @Override
    public Route getSingleRoute(String routeId) {
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("route_id", routeId);
        //获取当前路线
        Route route = routeMapper.selectOne(wrapper);
        List<String> scenicIds = JSON.parseArray(route.getScenicJson(), String.class);
        //获取 用户名
        String uname = userMapper.selectUserName(route.getUserId());
        //获取区
        List<String> areas = new ArrayList<>();
        List<RouteScenic> scenics1 = new ArrayList<>();
        String name = "";
        for (int j = 0; j < scenicIds.size(); j++) {
            Scenic tmp = scenicMapper.selectById(scenicIds.get(j));
            String imgs = tmp.getScenicImgs();
            String text = tmp.getTextJson();
            String l = tmp.getLikeJson();
            String k = tmp.getCollectionJson();
            String tic = tmp.getTicketJson();
            String tr = tmp.getTrafficJson();
            String op = tmp.getOpenTimeJson();
            if (!areas.contains(tmp.getArea())) {
                areas.add(tmp.getArea());
            }
            if (imgs == null) {
                tmp.setScenicImages(new ArrayList<>());
                tmp.setScenicImgs("[]");
            } else {
                tmp.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                tmp.setText(new ArrayList<>());
                tmp.setTextJson("[]");
            } else {
                tmp.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(l) && l != null) {
                tmp.setLikesUsers(JSON.parseArray(l, String.class));
            } else {
                tmp.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                tmp.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                tmp.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                tmp.setTicket(JSON.parseArray(tic, String.class));
            } else {
                tmp.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                tmp.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                tmp.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                tmp.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                tmp.setOpenTime(new ArrayList<>());
            }
            tmp.setCommentUsers(commentMapper.selectUserList(tmp.getScenicId()));
            if (j != scenicIds.size() - 1) {
                Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                scenics1.add(new RouteScenic(tmp, nt.getLongitude(), nt.getLatitude()));
                name = name + tmp.getScenicName() + "+";
            } else {
                scenics1.add(new RouteScenic(tmp, -1, -1));
                name = name + tmp.getScenicName();
            }

        }
        route.setUserName(uname);
        route.setScenics(scenics1);
        route.setLocationList(areas);
        route.setScenicIds(scenicIds);
        route.setCollectionUsers(JSON.parseArray(route.getCollectionJson(), String.class));
        route.setCommentUsers(commentMapper.selectUserList(route.getRouteId()));
        return route;
    }

    @Override
    public List<Route> getUserRoutes(String userId) {
        List<Route> routes = null;
        //获取所有路线
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        routes = routeMapper.selectList(wrapper);
        for (int i = 0; i < routes.size(); i++) {
            //获取 用户名
            String uname = userMapper.selectUserName(routes.get(i).getUserId());
            //获取区
            String locationJson = routes.get(i).getLocation();
            //获取景点
            List<String> scenicIds = JSON.parseArray(routes.get(i).getScenicJson(), String.class);
//            System.out.println(scenicIds);
            List<RouteScenic> scenics1 = new ArrayList<>();
            for (int j = 0; j < scenicIds.size(); j++) {
                Scenic scenic = scenicMapper.selectById(scenicIds.get(j));
                String imgs = scenic.getScenicImgs();
                String text = scenic.getTextJson();
                String l = scenic.getLikeJson();
                String k = scenic.getCollectionJson();
                String tic = scenic.getTicketJson();
                String tr = scenic.getTrafficJson();
                String op = scenic.getOpenTimeJson();
                if (imgs == null) {
                    scenic.setScenicImages(new ArrayList<>());
                    scenic.setScenicImgs("[]");
                } else {
                    scenic.setScenicImages(JSON.parseArray(imgs, String.class));
                }
                if (text == null) {
                    scenic.setText(new ArrayList<>());
                    scenic.setTextJson("[]");
                } else {
                    scenic.setText(JSON.parseArray(text, String.class));
                }
                if (!"".equals(l) && l != null) {
                    scenic.setLikesUsers(JSON.parseArray(l, String.class));
                } else {
                    scenic.setLikesUsers(new ArrayList<>());
                }
                if (!"".equals(k) && k != null) {
                    scenic.setCollectionUsers(JSON.parseArray(k, String.class));
                } else {
                    scenic.setCollectionUsers(new ArrayList<>());
                }
                if (!"".equals(tic) && tic != null) {
                    scenic.setTicket(JSON.parseArray(tic, String.class));
                } else {
                    scenic.setTicket(new ArrayList<>());
                }
                if (!"".equals(tr) && tr != null) {
                    scenic.setTraffic(JSON.parseArray(tr, String.class));
                } else {
                    scenic.setTraffic(new ArrayList<>());
                }
                if (!"".equals(op) && op != null) {
                    scenic.setOpenTime(JSON.parseArray(op, String.class));
                } else {
                    scenic.setOpenTime(new ArrayList<>());
                }
                scenic.setCommentUsers(commentMapper.selectUserList(scenic.getScenicId()));
                if (j != scenicIds.size() - 1) {
                    Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                    scenics1.add(new RouteScenic(scenic, nt.getLongitude(), nt.getLatitude()));
                } else {
                    scenics1.add(new RouteScenic(scenic, -1, -1));
                }
            }
            routes.get(i).setUserName(uname);
            String cjson = routes.get(i).getCollectionJson();
            if (cjson == null) {
                routes.get(i).setCollectionUsers(new ArrayList<>());
            } else {
                routes.get(i).setCollectionUsers(JSON.parseArray(cjson, String.class));
            }
            routes.get(i).setCommentUsers(commentMapper.selectUserList(routes.get(i).getRouteId()));
            routes.get(i).setLocationList(JSON.parseArray(locationJson, String.class));
            routes.get(i).setScenics(scenics1);
            routes.get(i).setScenicIds(scenicIds);
        }
        return routes;
    }

    @Override
    public List<Route> searchUserConnection(String scenicName, String area, String userId) {
        List<Route> routes = null;
        //获取所有路线
        List<String> routesId = null;
        if (scenicName != null) {
            String scid = scenicMapper.selectScId(scenicName);
            routesId = routeMapper.selectRouteIdOfSc(scid);
        } else if (area != null) {
            String sqlLike = "%\"%" + area + "%\"%";
            routesId = routeMapper.selectRouteIdOfArea(sqlLike);
        } else {
            return null;
        }
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("score");
        wrapper.eq("open", "1");
        wrapper.in("route_id", routesId);
        routes = routeMapper.selectList(wrapper);
        List<Route> cleanedRoute = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            List<String> users = JSON.parseArray(routes.get(i).getCollectionJson(), String.class);
            if (!users.contains(userId)) {
                continue;
            }
            //获取 用户名
            String uname = userMapper.selectUserName(routes.get(i).getUserId());
            //获取区
            String locationJson = routes.get(i).getLocation();
            //获取景点
            List<String> scenicIds = JSON.parseArray(routes.get(i).getScenicJson(), String.class);
//            System.out.println(scenicIds);
            List<RouteScenic> scenics1 = new ArrayList<>();
            for (int j = 0; j < scenicIds.size(); j++) {
                Scenic scenic = scenicMapper.selectById(scenicIds.get(j));
                String imgs = scenic.getScenicImgs();
                String text = scenic.getTextJson();
                String l = scenic.getLikeJson();
                String k = scenic.getCollectionJson();
                String tic = scenic.getTicketJson();
                String tr = scenic.getTrafficJson();
                String op = scenic.getOpenTimeJson();
                if (imgs == null) {
                    scenic.setScenicImages(new ArrayList<>());
                    scenic.setScenicImgs("[]");
                } else {
                    scenic.setScenicImages(JSON.parseArray(imgs, String.class));
                }
                if (text == null) {
                    scenic.setText(new ArrayList<>());
                    scenic.setTextJson("[]");
                } else {
                    scenic.setText(JSON.parseArray(text, String.class));
                }
                if (!"".equals(l) && l != null) {
                    scenic.setLikesUsers(JSON.parseArray(l, String.class));
                } else {
                    scenic.setLikesUsers(new ArrayList<>());
                }
                if (!"".equals(k) && k != null) {
                    scenic.setCollectionUsers(JSON.parseArray(k, String.class));
                } else {
                    scenic.setCollectionUsers(new ArrayList<>());
                }
                if (!"".equals(tic) && tic != null) {
                    scenic.setTicket(JSON.parseArray(tic, String.class));
                } else {
                    scenic.setTicket(new ArrayList<>());
                }
                if (!"".equals(tr) && tr != null) {
                    scenic.setTraffic(JSON.parseArray(tr, String.class));
                } else {
                    scenic.setTraffic(new ArrayList<>());
                }
                if (!"".equals(op) && op != null) {
                    scenic.setOpenTime(JSON.parseArray(op, String.class));
                } else {
                    scenic.setOpenTime(new ArrayList<>());
                }
                scenic.setCommentUsers(commentMapper.selectUserList(scenic.getScenicId()));
                if (j != scenicIds.size() - 1) {
                    Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                    scenics1.add(new RouteScenic(scenic, nt.getLongitude(), nt.getLatitude()));
                } else {
                    scenics1.add(new RouteScenic(scenic, -1, -1));
                }
            }
            routes.get(i).setUserName(uname);
            String cjson = routes.get(i).getCollectionJson();
            if (cjson == null) {
                routes.get(i).setCollectionUsers(new ArrayList<>());
            } else {
                routes.get(i).setCollectionUsers(JSON.parseArray(cjson, String.class));
            }
            routes.get(i).setCommentUsers(commentMapper.selectUserList(routes.get(i).getRouteId()));
            routes.get(i).setLocationList(JSON.parseArray(locationJson, String.class));
            routes.get(i).setScenics(scenics1);
            routes.get(i).setScenicIds(scenicIds);
            cleanedRoute.add(routes.get(i));
        }
        return cleanedRoute;
    }

    @Override
    public List<Route> getUserCollectedRoutes(String userId) {
        List<Route> routes = null;
        //获取所有路线
        QueryWrapper<Route> wrapper = new QueryWrapper<>();
        wrapper.eq("open", "1");
        routes = routeMapper.selectList(wrapper);
        List<Route> cleanedRoute = new ArrayList<>();
        for (int i = 0; i < routes.size(); i++) {
            List<String> users = JSON.parseArray(routes.get(i).getCollectionJson(), String.class);
            if (!users.contains(userId)) {
                continue;
            }
            //获取 用户名
            String uname = userMapper.selectUserName(routes.get(i).getUserId());
            //获取区
            String locationJson = routes.get(i).getLocation();
            //获取景点
            List<String> scenicIds = JSON.parseArray(routes.get(i).getScenicJson(), String.class);
//            System.out.println(scenicIds);
            List<RouteScenic> scenics1 = new ArrayList<>();
            for (int j = 0; j < scenicIds.size(); j++) {
                Scenic scenic = scenicMapper.selectById(scenicIds.get(j));
                String imgs = scenic.getScenicImgs();
                String text = scenic.getTextJson();
                String l = scenic.getLikeJson();
                String k = scenic.getCollectionJson();
                String tic = scenic.getTicketJson();
                String tr = scenic.getTrafficJson();
                String op = scenic.getOpenTimeJson();
                if (imgs == null) {
                    scenic.setScenicImages(new ArrayList<>());
                    scenic.setScenicImgs("[]");
                } else {
                    scenic.setScenicImages(JSON.parseArray(imgs, String.class));
                }
                if (text == null) {
                    scenic.setText(new ArrayList<>());
                    scenic.setTextJson("[]");
                } else {
                    scenic.setText(JSON.parseArray(text, String.class));
                }
                if (!"".equals(l) && l != null) {
                    scenic.setLikesUsers(JSON.parseArray(l, String.class));
                } else {
                    scenic.setLikesUsers(new ArrayList<>());
                }
                if (!"".equals(k) && k != null) {
                    scenic.setCollectionUsers(JSON.parseArray(k, String.class));
                } else {
                    scenic.setCollectionUsers(new ArrayList<>());
                }
                if (!"".equals(tic) && tic != null) {
                    scenic.setTicket(JSON.parseArray(tic, String.class));
                } else {
                    scenic.setTicket(new ArrayList<>());
                }
                if (!"".equals(tr) && tr != null) {
                    scenic.setTraffic(JSON.parseArray(tr, String.class));
                } else {
                    scenic.setTraffic(new ArrayList<>());
                }
                if (!"".equals(op) && op != null) {
                    scenic.setOpenTime(JSON.parseArray(op, String.class));
                } else {
                    scenic.setOpenTime(new ArrayList<>());
                }
                scenic.setCommentUsers(commentMapper.selectUserList(scenic.getScenicId()));
                if (j != scenicIds.size() - 1) {
                    Scenic nt = scenicMapper.selectById(scenicIds.get(j + 1));
                    scenics1.add(new RouteScenic(scenic, nt.getLongitude(), nt.getLatitude()));
                } else {
                    scenics1.add(new RouteScenic(scenic, -1, -1));
                }
            }
            routes.get(i).setUserName(uname);
            String cjson = routes.get(i).getCollectionJson();
            if (cjson == null) {
                routes.get(i).setCollectionUsers(new ArrayList<>());
            } else {
                routes.get(i).setCollectionUsers(JSON.parseArray(cjson, String.class));
            }
            routes.get(i).setCommentUsers(commentMapper.selectUserList(routes.get(i).getRouteId()));
            routes.get(i).setLocationList(JSON.parseArray(locationJson, String.class));
            routes.get(i).setScenics(scenics1);
            routes.get(i).setScenicIds(scenicIds);
            cleanedRoute.add(routes.get(i));
        }
        return cleanedRoute;
    }

    @Override
    public boolean removeRoute(String routeId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", routeId);
        wrapper.eq("type", "route");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (Comment i : list) {
            wrapper.clear();
            wrapper.eq("parent_id", i.getCommentId());
            wrapper.eq("type", "comment");
            commentMapper.delete(wrapper);
            wrapper.clear();
            wrapper.eq("comment_id", i.getCommentId());
            commentMapper.delete(wrapper);
        }
        int i = routeMapper.deleteById(routeId);
        return i == 1;
    }

}
