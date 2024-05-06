package com.qshy.controller;


import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.*;
import com.qshy.service.impl.CommentServiceImpl;
import com.qshy.service.impl.ScenicServiceImpl;
import com.qshy.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/scenic")
public class ScenicController {

    @Autowired
    private ScenicServiceImpl scenicService;
    @Autowired
    private CommentServiceImpl commentService;

    @RequestMapping("/getRecommendScenic")
    @ResponseBody
    public Result getRecommendScenic(
            @RequestParam String userId,
            @RequestHeader String token,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "8") Integer pageSize) {
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        if (type != null) {
            wrapper.eq("stype", type);
        }
        wrapper.like("collection_json", "\"" + userId + "\"");
        Page<Scenic> page = scenicService.page(new Page<>(pageNum, pageSize), wrapper);
        List<Scenic> tmp = page.getRecords();
        List<Scenic> cleaned = new ArrayList<>();
        for (Scenic i : tmp) {
            List<String> users = JSON.parseArray(i.getCollectionJson(), String.class);
            if ((users == null) || (!users.contains(userId))) {
                continue;
            }
            String imgs = i.getScenicImgs();
            String text = i.getTextJson();
            String j = i.getLikeJson();
            String k = i.getCollectionJson();
            String tic = i.getTicketJson();
            String tr = i.getTrafficJson();
            String op = i.getOpenTimeJson();
            if (imgs == null) {
                i.setScenicImages(new ArrayList<>());
                i.setScenicImgs("[]");
            } else {
                i.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                i.setText(new ArrayList<>());
                i.setTextJson("[]");
            } else {
//                System.out.println(text);
                i.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(j) && j != null) {
                i.setLikesUsers(JSON.parseArray(j, String.class));
            } else {
                i.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                i.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                i.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                i.setTicket(JSON.parseArray(tic, String.class));
            } else {
                i.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                i.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                i.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                i.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                i.setOpenTime(new ArrayList<>());
            }
            i.setCommentUsers(commentService.getCommentUsers(i.getScenicId()));
            cleaned.add(i);
        }
//        page.setTotal(cleaned.size());
        // 提取用户收藏的景点的stype和area
        if (cleaned.isEmpty()|| "0".equals(userId)) {
            // cleaned为空，随机推荐景点
            List<Scenic> allScenics = scenicService.page(new Page<>(pageNum, 50)).getRecords().stream().limit(50) // 或者使用subList(0, Math.min(8, recommendedPage.getRecords().size()))
                    .collect(Collectors.toList());; // 获取所有景点列表
            int recommendCount = 8; // 推荐数量
            if (allScenics.size() < recommendCount) {
                recommendCount = allScenics.size(); // 调整推荐数量为所有景点的数量
            }
            Random random = new Random();
            Set<Scenic> uniqueRecommendedScenics = new HashSet<>();
            while (uniqueRecommendedScenics.size() < recommendCount) {
                int randomIndex = random.nextInt(allScenics.size());
                uniqueRecommendedScenics.add(allScenics.get(randomIndex));
            }
            List<Scenic> recommendedScenics = new ArrayList<>(uniqueRecommendedScenics); // 将Set转换为List
            // 将推荐景点列表包装到返回对象中
            Map<String, Object> map = new HashMap<>();
            map.put("recommendedScenics", recommendedScenics);
            return new Result(map, Code.SUCCESS, "推荐景点获取成功");
        } else {
            Set<String> stypes = new HashSet<>();
            Set<String> areas = new HashSet<>();
            Set<String> collectedScenicIds = new HashSet<>();
            for (Scenic scenic : cleaned) {
                stypes.add(scenic.getStype());
                areas.add(scenic.getArea());
                collectedScenicIds.add(scenic.getScenicId());
            }
            // 构建查询条件，查找具有相同stype或area的景点
            QueryWrapper<Scenic> queryWrapper = new QueryWrapper<>();
            // 添加额外的条件来排除已收藏的景点
            queryWrapper.notIn("scenic_id", collectedScenicIds)
                    .and(qw -> qw.in("stype", stypes).or().in("area", areas));

            // 执行查询，获取推荐景点列表
            List<Scenic> allScenics = scenicService.page(new Page<>(pageNum, 50),queryWrapper).getRecords().stream().limit(50)
                    .collect(Collectors.toList());; // 获取所有景点列表
            Random random = new Random();
            Set<Scenic> uniqueRecommendedScenics = new HashSet<>();
            while (uniqueRecommendedScenics.size() < 8) {
                int randomIndex = random.nextInt(allScenics.size());
                uniqueRecommendedScenics.add(allScenics.get(randomIndex));
            }
            List<Scenic> recommendedScenics = new ArrayList<>(uniqueRecommendedScenics); // 将Set转换为List
            // 将推荐景点列表包装到返回对象中
            Map<String, Object> map = new HashMap<>();
            map.put("recommendedScenics", allScenics);
            return new Result(map, Code.SUCCESS, "推荐景点获取成功");
//        page.setRecords(cleaned);
//        Map<String, Object> map = new HashMap<>();
//        map.put("scenics", page);
//        return new Result(map, Code.SUCCESS, "获取推荐景点成功");
        }
    }

    @RequestMapping("/addScenic")
    @ResponseBody
    public Result addScenic(@RequestBody Scenic scenic) {
        boolean save = scenicService.save(scenic);
        if (save)
            return new Result(null, Code.SUCCESS, "添加景点成功");
        return new Result(null, Code.SYSTEM_ERROR, "添加景点失败");
    }

    @RequestMapping("/addInfos")
    @ResponseBody
    public Result addInfos(@RequestBody Map<String, Object> body, @RequestParam String scenicId) {
        String infosStr = JSON.toJSONString(body.get("Infos"));
        List<String> Infos = JSON.parseArray(infosStr, String.class);
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        wrapper.eq("scenic_id", scenicId);
        Scenic one = scenicService.getOne(wrapper);
        one.setTextJson(JSON.toJSONString(Infos));
        boolean save = scenicService.saveOrUpdate(one);
        if (save)
            return new Result(null, Code.SUCCESS, "添加景点信息成功");
        return new Result(null, Code.SYSTEM_ERROR, "添加景点信息失败");
    }

    @RequestMapping("/addInfo")
    @ResponseBody
    public Result addSingleInfo(@RequestBody Map<String, Object> body, @RequestParam String scenicId) {
        String infosStr = JSON.toJSONString(body.get("Info"));
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        wrapper.eq("scenic_id", scenicId);
        Scenic one = scenicService.getOne(wrapper);
        List<String> Infos = JSON.parseArray(one.getTextJson(), String.class);
        Infos.add(infosStr);
        one.setTextJson(JSON.toJSONString(Infos));
        boolean save = scenicService.saveOrUpdate(one);
        if (save)
            return new Result(null, Code.SUCCESS, "添加景点信息成功");
        return new Result(null, Code.SYSTEM_ERROR, "添加景点信息失败");
    }

    @RequestMapping("/search")
    @ResponseBody
    public Result searchScenic(@RequestParam(required = false) String scenicName,
                               @RequestParam(required = false) String type,
                               @RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "8") Integer pageSize) {
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        wrapper.like("scenic_name", scenicName);
        Page<Scenic> page = scenicService.page(new Page<>(pageNum, pageSize), wrapper);
        List<Scenic> tmp = page.getRecords();
        for (Scenic i : tmp) {
            String imgs = i.getScenicImgs();
            String text = i.getTextJson();
            String j = i.getLikeJson();
            String k = i.getCollectionJson();
            String tic = i.getTicketJson();
            String tr = i.getTrafficJson();
            String op = i.getOpenTimeJson();
            if (imgs == null) {
                i.setScenicImages(new ArrayList<>());
                i.setScenicImgs("[]");
            } else {
                i.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                i.setText(new ArrayList<>());
                i.setTextJson("[]");
            } else {
//                System.out.println(text);
                i.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(j) && j != null) {
                i.setLikesUsers(JSON.parseArray(j, String.class));
            } else {
                i.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                i.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                i.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                i.setTicket(JSON.parseArray(tic, String.class));
            } else {
                i.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                i.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                i.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                i.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                i.setOpenTime(new ArrayList<>());
            }
            i.setCommentUsers(commentService.getCommentUsers(i.getScenicId()));
        }
//        page.setTotal(tmp.size());
        page.setRecords(tmp);
        Map<String, Object> map = new HashMap<>();
        map.put("scenics", page);
        return new Result(map, Code.SUCCESS, "获取景点列表成功");
    }

    @RequestMapping("/getUserCollectionScenics")
    @ResponseBody
    public Result getUserCollectionScenics(
            @RequestParam String userId,
            @RequestHeader String token,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "8") Integer pageSize) {
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        if (type != null) {
            wrapper.eq("stype", type);
        }
        wrapper.like("collection_json", "\"" + userId + "\"");
        Page<Scenic> page = scenicService.page(new Page<>(pageNum, pageSize), wrapper);
        List<Scenic> tmp = page.getRecords();
        List<Scenic> cleaned = new ArrayList<>();
        for (Scenic i : tmp) {
            List<String> users = JSON.parseArray(i.getCollectionJson(), String.class);
            if ((users == null) || (!users.contains(userId))) {
                continue;
            }
            String imgs = i.getScenicImgs();
            String text = i.getTextJson();
            String j = i.getLikeJson();
            String k = i.getCollectionJson();
            String tic = i.getTicketJson();
            String tr = i.getTrafficJson();
            String op = i.getOpenTimeJson();
            if (imgs == null) {
                i.setScenicImages(new ArrayList<>());
                i.setScenicImgs("[]");
            } else {
                i.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                i.setText(new ArrayList<>());
                i.setTextJson("[]");
            } else {
//                System.out.println(text);
                i.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(j) && j != null) {
                i.setLikesUsers(JSON.parseArray(j, String.class));
            } else {
                i.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                i.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                i.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                i.setTicket(JSON.parseArray(tic, String.class));
            } else {
                i.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                i.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                i.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                i.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                i.setOpenTime(new ArrayList<>());
            }
            i.setCommentUsers(commentService.getCommentUsers(i.getScenicId()));
            cleaned.add(i);
        }
//        page.setTotal(cleaned.size());
        page.setRecords(cleaned);
        Map<String, Object> map = new HashMap<>();
        map.put("scenics", page);
        return new Result(map, Code.SUCCESS, "获取收藏景点成功");
    }

    @RequestMapping("/searchCollection")
    @ResponseBody
    public Result searchCollection(@RequestParam(required = false, name = "search") String scenicName,
                                   @RequestParam String userId,
                                   @RequestParam(required = false) String type,
                                   @RequestHeader String token,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize) {
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        if (scenicName != null)
            wrapper.like("scenic_name", scenicName);
        if (type != null)
            wrapper.eq("stype", type);
//        System.out.println("-------------------" + type + "----------------" + scenicName);
        wrapper.like("collection_json", "\"" + userId + "\"");
        Page<Scenic> page = scenicService.page(new Page<>(pageNum, pageSize), wrapper);
        List<Scenic> tmp = page.getRecords();
        List<Scenic> cleaned = new ArrayList<>();
        for (Scenic i : tmp) {
            List<String> users = JSON.parseArray(i.getCollectionJson(), String.class);
            if ((users == null) || (!users.contains(userId))) {
                continue;
            }
            String imgs = i.getScenicImgs();
            String text = i.getTextJson();
            String j = i.getLikeJson();
            String k = i.getCollectionJson();
            String tic = i.getTicketJson();
            String tr = i.getTrafficJson();
            String op = i.getOpenTimeJson();
            if (imgs == null) {
                i.setScenicImages(new ArrayList<>());
                i.setScenicImgs("[]");
            } else {
                i.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                i.setText(new ArrayList<>());
                i.setTextJson("[]");
            } else {
//                System.out.println(text);
                i.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(j) && j != null) {
                i.setLikesUsers(JSON.parseArray(j, String.class));
            } else {
                i.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                i.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                i.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                i.setTicket(JSON.parseArray(tic, String.class));
            } else {
                i.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && tr != null) {
                i.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                i.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                i.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                i.setOpenTime(new ArrayList<>());
            }
            i.setCommentUsers(commentService.getCommentUsers(i.getScenicId()));
            cleaned.add(i);
        }
//        page.setTotal(tmp.size());
        page.setRecords(cleaned);
        Map<String, Object> map = new HashMap<>();
        map.put("scenics", page);
        return new Result(map, Code.SUCCESS, "获取景点列表成功");
    }

    @RequestMapping("/allInfo")
    @ResponseBody
    public Result getScenicList(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "none") String stype,
                                @RequestParam(defaultValue = "none") String month,
                                @RequestParam(defaultValue = "8") Integer pageSize) {
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        if (!"none".equals(stype))
            wrapper.eq("stype", stype);
        if (!"none".equals(month))
            wrapper.eq("month", month);
        Page<Scenic> list = scenicService.page(new Page<>(pageNum, pageSize), wrapper);
        List<Scenic> tmp = list.getRecords();
        for (Scenic i : tmp) {
            String imgs = i.getScenicImgs();
            String text = i.getTextJson();
            String j = i.getLikeJson();
            String k = i.getCollectionJson();
            String tic = i.getTicketJson();
            String tr = i.getTrafficJson();
            String op = i.getOpenTimeJson();
            if (imgs == null) {
                i.setScenicImages(new ArrayList<>());
                i.setScenicImgs("[]");
            } else {
                i.setScenicImages(JSON.parseArray(imgs, String.class));
            }
            if (text == null) {
                i.setText(new ArrayList<>());
                i.setTextJson("[]");
            } else {
//                System.out.println(text);
                i.setText(JSON.parseArray(text, String.class));
            }
            if (!"".equals(j) && j != null) {
                i.setLikesUsers(JSON.parseArray(j, String.class));
            } else {
                i.setLikesUsers(new ArrayList<>());
            }
            if (!"".equals(k) && k != null) {
                i.setCollectionUsers(JSON.parseArray(k, String.class));
            } else {
                i.setCollectionUsers(new ArrayList<>());
            }
            if (!"".equals(tic) && tic != null) {
                i.setTicket(JSON.parseArray(tic, String.class));
            } else {
                i.setTicket(new ArrayList<>());
            }
            if (!"".equals(tr) && k != null) {
                i.setTraffic(JSON.parseArray(tr, String.class));
            } else {
                i.setTraffic(new ArrayList<>());
            }
            if (!"".equals(op) && op != null) {
                i.setOpenTime(JSON.parseArray(op, String.class));
            } else {
                i.setOpenTime(new ArrayList<>());
            }
            i.setCommentUsers(commentService.getCommentUsers(i.getScenicId()));
        }
//        list.setTotal(tmp.size());
        list.setRecords(tmp);
        Map<String, Object> map = new HashMap<>();
        map.put("scenics", list);
        return new Result(map, Code.SUCCESS, "获取景点列表成功");
    }

    @RequestMapping("/oneInfo")
    @ResponseBody
    public Result getScenicInfo(@RequestParam String scenicId) {
        QueryWrapper<Comment> wrapperScore = new QueryWrapper<>();
        wrapperScore.eq("parent_id", scenicId);
        List<Comment> list = commentService.list(wrapperScore);

        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        wrapper.eq("scenic_id", scenicId);
        Scenic i = scenicService.getOne(wrapper);
        String imgs = i.getScenicImgs();
        String text = i.getTextJson();
        String j = i.getLikeJson();
        String k = i.getCollectionJson();
        String tic = i.getTicketJson();
        String tr = i.getTrafficJson();
        String op = i.getOpenTimeJson();
        if (imgs == null) {
            i.setScenicImages(new ArrayList<>());
            i.setScenicImgs("[]");
        } else {
            i.setScenicImages(JSON.parseArray(imgs, String.class));
        }
        if (text == null) {
            i.setText(new ArrayList<>());
            i.setTextJson("[]");
        } else {
            i.setText(JSON.parseArray(text, String.class));
        }
        if (!"".equals(j) && j != null) {
            i.setLikesUsers(JSON.parseArray(j, String.class));
        } else {
            i.setLikesUsers(new ArrayList<>());
        }
        if (!"".equals(k) && k != null) {
            i.setCollectionUsers(JSON.parseArray(k, String.class));
        } else {
            i.setCollectionUsers(new ArrayList<>());
        }
        if (!"".equals(tic) && tic != null) {
            i.setTicket(JSON.parseArray(tic, String.class));
        } else {
            i.setTicket(new ArrayList<>());
        }
        if (!"".equals(tr) && tr != null) {
            i.setTraffic(JSON.parseArray(tr, String.class));
        } else {
            i.setTraffic(new ArrayList<>());
        }
        if (!"".equals(op) && op != null) {
            i.setOpenTime(JSON.parseArray(op, String.class));
        } else {
            i.setOpenTime(new ArrayList<>());
        }

        if(list.size()>0){
            int totalScore = 0;
            int commentCount = list.size();
            // 计算总分数
            for (Comment comment : list) {
                totalScore += comment.getScore();
            }
            // 计算平均分数
            double averageScore = (double) totalScore / commentCount;
            // 格式化为一位小数
            DecimalFormat df = new DecimalFormat("#.0");
            String formattedAverageScore = df.format(averageScore);
            // 将格式化后的字符串转换为 double 类型
            double finalAverageScore = Double.parseDouble(formattedAverageScore);
            i.setScore(finalAverageScore);

            UpdateWrapper<Scenic> wrapper1 = new UpdateWrapper<>();
            Scenic byId = scenicService.getById(scenicId);
            byId.setScore(finalAverageScore);
            wrapper1.eq("scenic_id", scenicId);
            scenicService.update(byId, wrapper1);
        }else{
            i.setScore(5);
        }
        i.setCommentUsers(commentService.getCommentUsers(i.getScenicId()));
        Map<String, Object> map = new HashMap<>();
        map.put("scenic", i);
        return new Result(map, Code.SUCCESS, "获取单个景点信息成功");
    }

    @RequestMapping("/like")
    @ResponseBody
    public Result giveLike(@RequestParam String scenicId, @RequestParam String userId, @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "用户未登录");
        }
        QueryWrapper<Scenic> wrapper = new QueryWrapper<>();
        wrapper.eq("scenic_id", scenicId);
        Scenic one = scenicService.getOne(wrapper);
        List<String> list = null;
        if (one.getLikesUsers() == null) {
            list = new ArrayList<>();
        } else {
            list = JSON.parseArray(one.getLikeJson(), String.class);
        }
        list.add(userId);
        one.setLikeJson(JSON.toJSONString(list));
        one.setLikesUsers(list);
        boolean save = scenicService.updateById(one);
        if (save) {
            Map<String, Object> map = new HashMap<>();
            map.put("scenic", one);
            return new Result(map, Code.SUCCESS, "点赞成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "点赞失败");
        }
    }

    @RequestMapping("/collection")
    @ResponseBody
    public Result giveLove(@RequestParam String scenicId, @RequestParam String userId, @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "用户未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        //按点赞数降序
        wrapper.eq("scenic_id", scenicId);
        Scenic one = scenicService.getOne(wrapper);
        if (one.getCollectionJson() == null) {
            one.setCollectionJson("[]");
        }
        List<String> list = JSON.parseArray(one.getCollectionJson(), String.class);
        if (!list.contains(userId))
            list.add(userId);
        else {
            return new Result(null, Code.SUCCESS, "已搜藏");
        }
        one.setCollectionJson(JSON.toJSONString(list));
        one.setCollectionUsers(list);
        boolean save = scenicService.update(one, wrapper);
        if (save) {
            Map<String, Object> map = new HashMap<>();
            map.put("scenic", one);
            return new Result(map, Code.SUCCESS, "收藏成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "收藏失败");
        }
    }

    @RequestMapping("/deCollection")
    @ResponseBody
    public Result delove(@RequestParam String scenicId, @RequestParam String userId, @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "用户未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        //按点赞数降序
        wrapper.eq("scenic_id", scenicId);
        Scenic one = scenicService.getOne(wrapper);
        List<String> list = JSON.parseArray(one.getCollectionJson(), String.class);
        if (list.contains(userId))
            list.remove(userId);
        else
            return new Result(null, Code.SUCCESS, "未收藏");
        one.setCollectionJson(JSON.toJSONString(list));
        one.setCollectionUsers(list);
        boolean save = scenicService.update(one, wrapper);
        if (save) {
            Map<String, Object> map = new HashMap<>();
            map.put("scenic", one);
            return new Result(map, Code.SUCCESS, "解除收藏成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "解除收藏失败");
        }
    }

}
