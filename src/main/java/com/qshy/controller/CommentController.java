package com.qshy.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qshy.entity.*;
import com.qshy.service.impl.CommentServiceImpl;
import com.qshy.service.impl.RouteServiceImpl;
import com.qshy.service.impl.ScenicServiceImpl;
import com.qshy.service.impl.StrategyServiceImpl;
import com.qshy.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentServiceImpl commentService;


    @Autowired
    private ScenicServiceImpl scenicService;

    @Autowired
    private RouteServiceImpl routeService;

    @Autowired
    private StrategyServiceImpl strategyService;

    /**
     * 获取其他用户对该用户的评论
     *
     * @param token
     * @param userId
     * @return
     */
    @RequestMapping("/toUserComments")
    @ResponseBody
    public Result toUserComments(@RequestHeader("token") String token,
                                 @RequestParam String userId) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }

        List<Comment> list = commentService.getToUserComments(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 获取该用户发表的评论
     *
     * @param token
     * @param userId
     * @return
     */
    @RequestMapping("/userComments")
    @ResponseBody
    public Result getUserComments(@RequestHeader("token") String token,
                                  @RequestParam String userId) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }

        List<Comment> list = commentService.getUserComments(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 获取该景点的评论
     *
     * @param scenicId
     * @return
     */
    @RequestMapping("/getScenicComments")
    @ResponseBody
    public Result getScenicComments(
            @RequestParam("articleId") String scenicId) {
        List<Comment> list = commentService.getScenicComments(scenicId);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 获取该攻略的评论
     *
     * @param strategyId
     * @return
     */
    @RequestMapping("/getStrategyComments")
    @ResponseBody
    public Result getStrategyComments(@RequestParam("articleId") String strategyId) {

        List<Comment> list = commentService.getStrategyComments(strategyId);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 获取该攻略的评论
     *
     * @param routeId
     * @return
     */
    @RequestMapping("/getRouteComments")
    @ResponseBody
    public Result getRouteComments(@RequestParam("articleId") String routeId) {

        List<Comment> list = commentService.getRouteComments(routeId);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 获取该评论的回复
     *
     * @param commentId
     * @return
     */
    @RequestMapping("/answers")
    @ResponseBody
    public Result getCommentAnswers(
            @RequestParam String commentId) {

        List<Comment> list = commentService.getCommentAnswers(commentId);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 获取该评论内容
     *
     * @param commentId
     * @return
     */
    @RequestMapping("/text")
    @ResponseBody
    public Result getCommentText(
            @RequestParam String commentId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("comment_id", commentId);
        List<Comment> list = commentService.list(wrapper);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    /**
     * 删除评论
     *
     * @param token
     * @param commentId
     * @param userId
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public Result deleteComment(@RequestHeader("token") String token,
                                @RequestParam String commentId,
                                @RequestParam String userId) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("comment_id", commentId);
        boolean remove = commentService.remove(wrapper);
        if (remove) {
            wrapper.clear();
            wrapper.eq("user_id", userId);
            List<Comment> list = commentService.list(wrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }

    /**
     * 发表景点评论
     *
     * @param token
     * @return
     */
    @RequestMapping("/saveScenicComment")
    @ResponseBody
    public Result saveScenicComment(@RequestParam String commentText,
                                    @RequestParam String userId,
                                    @RequestParam String scenicId,
                                    @RequestParam(defaultValue = "") String commentId,
                                    @RequestParam Integer score,
                                    @RequestParam(required = false) MultipartFile file,
                                    @RequestHeader("token") String token
    ) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        if (file == null) {//不带图片的评论
            Comment comment = new Comment();
            comment.setParentId(scenicId);
            comment.setUserId(userId);
            comment.setCommentId(commentId);
            comment.setType("scenic");
            comment.setImages(JSON.toJSONString(new ArrayList<>()));
            comment.setText(commentText);
            comment.setFavourJson(JSON.toJSONString(new ArrayList<>()));
            comment.setScore(score);
            comment.setCommentTime(LocalDateTime.now());
            boolean save = commentService.updateById(comment);
            if (save) {//存入数据库成功后写入
                QueryWrapper<Comment> wrapper = new QueryWrapper<>();
                wrapper.eq("parent_id", scenicId);
                wrapper.orderByDesc("comment_time");
                List<Comment> list = commentService.list(wrapper);
                Scenic byId = scenicService.getById(scenicId);
                byId.setScore((byId.getScore() * (list.size() - 1) + score) / list.size());
                UpdateWrapper<Scenic> wrapper1 = new UpdateWrapper<>();
                wrapper1.eq("scenic_id", scenicId);
                scenicService.update(byId, wrapper1);
                Map<String, Object> map = new HashMap<>();
                map.put("comments", list);
                map.put("commentId", comment.getCommentId());
                return new Result(map, Code.SUCCESS, "评论成功");
            }
            return new Result(null, Code.SYSTEM_ERROR, "评论失败");
        }//带图片的保存
        try {
            File commentHome = new File("D:/QSHY/comment/" + scenicId + "/" + userId);
            if (!commentHome.exists()) {
                commentHome.mkdirs();
            }
            UUID id = UUID.randomUUID();
            String[] idd = id.toString().split("-");
            String filename = file.getOriginalFilename();
            int length = filename.split("\\.").length;
            String fileType = filename.split("\\.")[length - 1];
            String newname = idd[0] + idd[1] + idd[2] + "." + fileType;
            String filePath = commentHome.getAbsolutePath() + "/" + newname;
            File commentPic = new File(filePath);
            if (commentPic.exists()) {
                return new Result(null, Code.FILE_EXIST, "已有同名图片存在");
            }
            //构建评论
            Comment comment;
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("comment_id", commentId);
            comment = commentService.getOne(wrapper);
            boolean save;
//            System.out.println("null----------------------comment");
            comment.setText(commentText);
            comment.setUserId(userId);
            comment.setType("scenic");
            comment.setParentId(scenicId);
            comment.setCommentId(commentId);//12位id
            comment.setScore(score);
            comment.setFavourJson(JSON.toJSONString(new ArrayList<>()));
            comment.setCommentTime(LocalDateTime.now());
            if (comment.getImages() == null) {
                List<String> imgs = new ArrayList<>();
                imgs.add("http://localhost:8080/QSHY/comment/" + scenicId + "/" + userId + "/" + newname);
                comment.setImgs(imgs);
                comment.setImages(JSON.toJSONString(imgs));
            } else {
                List<String> images = JSON.parseArray(comment.getImages(), String.class);
                images.add("http://localhost:8080/QSHY/comment/" + scenicId + "/" + userId + "/" + newname);
                comment.setImgs(images);
                comment.setImages(JSON.toJSONString(images));
            }
            save = commentService.update(comment, wrapper);
            if (save) {//存入数据库成功后写入
                file.transferTo(commentPic);
                QueryWrapper<Comment> wrapper1 = new QueryWrapper<>();
                comment.setType("scenic");
                wrapper1.eq("parent_id", scenicId);
                wrapper1.orderByDesc("comment_time");
                List<Comment> list = commentService.list(wrapper1);
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).setImgs(JSON.parseArray(list.get(i).getImages(), String.class));
                }
                Scenic byId = scenicService.getById(scenicId);
                byId.setScore((byId.getScore() * (list.size() - 1) + score) / list.size());
                UpdateWrapper<Scenic> wrapper2 = new UpdateWrapper<>();
                wrapper2.eq("scenic_id", scenicId);
                scenicService.update(byId, wrapper2);
                Map<String, Object> map = new HashMap<>();
                map.put("comments", list);
                map.put("commentId", comment.getCommentId());
                return new Result(map, Code.SUCCESS, "评论成功");
            }
            return new Result(null, Code.SYSTEM_ERROR, "评论失败");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "评论失败");
        }
    }

    /**
     * 发表攻略评论
     *
     * @param token
     * @return
     */
    @RequestMapping("/saveStrategyComment")
    @ResponseBody
    public Result saveStrategyComment(@RequestParam String commentText,
                                      @RequestParam String userId,
                                      @RequestParam String strategyId,
                                      @RequestHeader("token") String token
    ) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setType("strategy");
        comment.setImages(JSON.toJSONString(new ArrayList<>()));
        comment.setText(commentText);
        comment.setParentId(strategyId);
        comment.setFavourJson(JSON.toJSONString(new ArrayList<>()));
        comment.setCommentId(idd[0] + idd[1]);//12位id
        comment.setCommentTime(LocalDateTime.now());
        boolean save = commentService.saveOrUpdate(comment);
        if (save) {//存入数据库成功后写入
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", strategyId);
            wrapper.orderByDesc("comment_time");
            List<Comment> list = commentService.list(wrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            map.put("commentId", comment.getCommentId());
            return new Result(map, Code.SUCCESS, "评论成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "评论失败");
    }

    /**
     * 发表路线评论
     * 不包含图片
     *
     * @param token
     * @return
     */
    @RequestMapping("/saveRouteComment")
    @ResponseBody
    public Result saveRouteComment(@RequestParam String commentText,
                                   @RequestParam String userId,
                                   @RequestParam String routeId,
                                   @RequestHeader("token") String token
    ) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        //不带图片的评论
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        Comment comment = new Comment();
        comment.setParentId(routeId);
        comment.setUserId(userId);
        comment.setType("route");
        comment.setCommentId(idd[0] + idd[1]);
        comment.setImages(JSON.toJSONString(new ArrayList<>()));
        comment.setText(commentText);
        comment.setFavourJson(JSON.toJSONString(new ArrayList<>()));
        comment.setScore(0);
        comment.setCommentTime(LocalDateTime.now());
        boolean save = commentService.saveOrUpdate(comment);
        if (save) {//存入数据库成功后写入
            QueryWrapper<Comment> wrapper = new QueryWrapper<>();
            wrapper.eq("parent_id", routeId);
            wrapper.orderByDesc("comment_time");
            List<Comment> list = commentService.list(wrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            map.put("commentId", comment.getCommentId());
            return new Result(map, Code.SUCCESS, "评论成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "评论失败");

    }

    /**
     * @param commentId
     * @return
     */
    @RequestMapping("/like")
    @ResponseBody
    public Result giveCommentLike(@RequestParam String commentId,
                                  @RequestParam String userId,
                                  @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("comment_id", commentId);
        Comment comment = commentService.getOne(wrapper);
        List<String> favour = JSON.parseArray(comment.getFavourJson(), String.class);
        favour.add(userId);
        comment.setFavourJson(JSON.toJSONString(favour));
        UpdateWrapper<Comment> wrapper1 = new UpdateWrapper<>();
        wrapper1.eq("comment_id", commentId);
        boolean update = commentService.update(comment, wrapper1);
        if (update) {
            return new Result(null, Code.SUCCESS, "点赞成功");
        }
        return new Result(null, Code.SUCCESS, "点赞失败");
    }

    /**
     * 对评论进行回复
     * 二级评论  没有点赞也不可再被回复
     *
     * @param parentId
     * @param userId
     * @param text
     * @param token
     * @return
     */
    @RequestMapping("/subComment")
    @ResponseBody
    public Result giveSubComment(@RequestParam String parentId,
                                 @RequestParam String userId,
                                 @RequestParam String text,
                                 @RequestParam String parentType,
                                 @RequestParam String typedId,
                                 @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("comment_id", parentId);
        Comment parentC = commentService.getOne(wrapper);
        Comment comment = new Comment();
        comment.setToUserId(parentC.getUserId());
        comment.setCommentId(idd[0] + idd[1]);
        comment.setParentId(parentId);
        comment.setUserId(userId);
        comment.setType("comment");
        comment.setCommentTime(LocalDateTime.now());
        comment.setText(text);
        boolean update = commentService.save(comment);
        if (update) {
            List<Comment> list = null;
//            System.out.println("ptype" + parentType);
//            System.out.println("pId" + typedId);
            if ("Scenic".equals(parentType)) {
                list = commentService.getScenicComments(typedId);
            } else if ("Strategy".equals(parentType)) {
                list = commentService.getStrategyComments(typedId);
            } else if ("Route".equals(parentType)) {
                list = commentService.getRouteComments(typedId);
            }
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            return new Result(map, Code.SUCCESS, "回复成功");
        }
        return new Result(null, Code.SUCCESS, "回复失败");
    }

    @RequestMapping("delScenicComment")
    @ResponseBody
    public Result deleteScenicComment(@RequestParam String commentId, @RequestParam String parentId, @RequestParam String type, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        type = type.toLowerCase(Locale.ROOT);
        List<Comment> list = commentService.deleteScenicComment(commentId, parentId, type);
        if (list != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }

    @RequestMapping("delRouteComment")
    @ResponseBody
    public Result deleteRouteComment(@RequestParam String commentId, @RequestParam String parentId, @RequestParam String type, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        type = type.toLowerCase(Locale.ROOT);
        List<Comment> list = commentService.deleteRouteComment(commentId, parentId, type);
        if (list != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }

    @RequestMapping("delStrategyComment")
    @ResponseBody
    public Result delStrategyComment(@RequestParam String commentId, @RequestParam String parentId, @RequestParam String type, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        type = type.toLowerCase(Locale.ROOT);
        List<Comment> list = commentService.deleteStrategyComment(commentId, parentId, type);
        if (list != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }

    @RequestMapping("delChildComment")
    @ResponseBody
    public Result deleteChildComment(@RequestParam String commentId,
                                     @RequestParam String parentId, @RequestParam String type,
                                     @RequestParam String articleId,
                                     @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        type = type.toLowerCase(Locale.ROOT);
        List<Comment> list = commentService.deleteChildComment(commentId, parentId, type, articleId);
        if (list != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("comments", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }


    @RequestMapping("prepareComment")
    @ResponseBody
    public Result prepareComment(@RequestParam String commentId, @RequestParam String parentId, @RequestParam String userId, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        boolean a = commentService.prepareComment(commentId, parentId, userId);
        if (a) {
            Map<String, Object> map = new HashMap<>();
            map.put("prepared", a);
            return new Result(null, Code.SUCCESS, "准备成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "准备失败");
    }

    @RequestMapping("delPrepareComment")
    @ResponseBody
    public Result delPrepareComment(@RequestParam String commentId, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        boolean a = commentService.removeById(commentId);
        if (a) {
            Map<String, Object> map = new HashMap<>();
            map.put("del", a);
            return new Result(null, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }
}
