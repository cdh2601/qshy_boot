package com.qshy.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qshy.entity.Comment;
import com.qshy.entity.Scenic;
import com.qshy.mapper.CommentMapper;
import com.qshy.mapper.ScenicMapper;
import com.qshy.mapper.UserMapper;
import com.qshy.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    @Override
    public List<Comment> getToUserComments(String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("to_user_id", userId);
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            list.get(i).setAvatar(s);
        }
        return list;
    }

    @Override
    public List<Comment> getUserComments(String userId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        String s = userMapper.selectUserAvatar(userId);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setAvatar(s);
        }
        return list;
    }

    @Override
    public List<Comment> getStrategyComments(String strategyId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", strategyId);
        wrapper.eq("type", "strategy");
        wrapper.orderByDesc("comment_time");
//        System.out.println("strategyId------------------" + strategyId);
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId = list.get(i).getCommentId();
            wrapper.clear();
            wrapper.eq("parent_id", commentId);
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        return list;
    }

    @Override
    public List<Comment> getCommentAnswers(String commentId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("parent_id", commentId);
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            list.get(i).setAvatar(s);
        }
        return list;
    }

    @Override
    public List<Comment> getScenicComments(String scenicId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", scenicId);
        wrapper.eq("type", "scenic");
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId = list.get(i).getCommentId();
            wrapper.clear();
            wrapper.eq("parent_id", commentId);
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        return list;
    }

    @Override
    public List<String> getCommentUsers(String parentId) {
        return commentMapper.selectUserList(parentId);
    }

    @Override
    public List<Comment> deleteScenicComment(String commentId, String scenicId, String type) {
        Comment comment = commentMapper.selectById(commentId);
        //删除子评论
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();//
        wrapper.eq("parent_id", commentId);
        wrapper.eq("type","comment");
        commentMapper.delete(wrapper);
        //删除图片资源
        List<String> images = JSON.parseArray(comment.getImages(), String.class);
        for (int j = 0; j < images.size(); j++) {
            int index = images.get(j).indexOf("/comment/");
            String name = images.get(j).substring(index);
            String path = "D:/QSHY" + name;
            File file = new File(path);
            if (file.exists())
                file.delete();
        }
        //删除本评论
        commentMapper.deleteById(commentId);
        //调整景点评分
        Scenic scenic = scenicMapper.selectById(scenicId);
        wrapper.clear();
        wrapper.eq("parent_id", scenicId);
        wrapper.eq("type", type);
        wrapper.orderByDesc("comment_time");
        double score = 0;
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId_ = list.get(i).getCommentId();
            score += list.get(i).getScore();
            wrapper.clear();
            wrapper.eq("parent_id", commentId_);
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images_ = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images_);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        scenic.setScore(score / list.size());
        UpdateWrapper<Scenic> wrapper2 = new UpdateWrapper<>();
        wrapper2.eq("scenic_id", scenicId);
        scenicMapper.update(scenic, wrapper2);
        return list;
    }

    @Override
    public List<Comment> deleteChildComment(String commentId, String parentId, String type, String articleId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("comment_id", commentId);
        wrapper.eq("parent_id", parentId);
        wrapper.eq("type", "comment");
        //删除本评论
        commentMapper.delete(wrapper);
        wrapper.clear();
        wrapper.eq("parent_id", articleId);
        wrapper.eq("type", type);
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId_ = list.get(i).getCommentId();
            wrapper.clear();
            wrapper.eq("parent_id", commentId_);
            wrapper.eq("type", "comment");
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images_ = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images_);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        return list;
    }

    @Override
    public boolean prepareComment(String commentId, String parentId, String userId) {
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setCommentId(commentId);
        comment.setParentId(parentId);
        comment.setCommentTime(LocalDateTime.now());
        comment.setText("");
        comment.setType("");
        comment.setFavourJson("[]");
        comment.setImages("[]");
        int insert = commentMapper.insert(comment);
        return insert == 1;
    }

    @Override
    public List<Comment> getRouteComments(String routeId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", routeId);
        wrapper.eq("type", "route");
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId = list.get(i).getCommentId();
            wrapper.clear();
            wrapper.eq("parent_id", commentId);
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        return list;
    }

    @Override
    public List<Comment> deleteStrategyComment(String commentId, String parentId, String type) {
        //删除子评论
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();//
        wrapper.eq("parent_id", commentId);
        wrapper.eq("type", "comment");
        commentMapper.delete(wrapper);
        //删除本评论
        commentMapper.deleteById(commentId);
        wrapper.clear();
        wrapper.eq("parent_id", parentId);
        wrapper.eq("type", type);
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId_ = list.get(i).getCommentId();
            wrapper.clear();
            wrapper.eq("parent_id", commentId_);
            wrapper.eq("type", "comment");
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images_ = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images_);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        return list;
    }

    @Override
    public List<Comment> deleteRouteComment(String commentId, String parentId, String type) {
        //删除子评论
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();//
        wrapper.eq("parent_id", commentId);
        wrapper.eq("type", "comment");
        commentMapper.delete(wrapper);
        //删除本评论
        commentMapper.deleteById(commentId);
        wrapper.clear();
        wrapper.eq("parent_id", parentId);
        wrapper.eq("type", type);
        wrapper.orderByDesc("comment_time");
        List<Comment> list = commentMapper.selectList(wrapper);
        for (int i = 0; i < list.size(); i++) {
            String commentId_ = list.get(i).getCommentId();
            wrapper.clear();
            wrapper.eq("parent_id", commentId_);
            wrapper.eq("type", "comment");
            wrapper.orderByDesc("comment_time");
            List<Comment> children = commentMapper.selectList(wrapper);
            for (int j = 0; j < children.size(); j++) {
                String fromUserId = children.get(j).getUserId();
                String s = userMapper.selectUserAvatar(fromUserId);
                String userName = userMapper.selectUserName(fromUserId);
                String favours = children.get(j).getFavourJson();
                children.get(j).setAvatar(s);
                children.get(j).setUserName(userName);
                List<String> favour = JSON.parseArray(favours, String.class);
                children.get(j).setFavour(favour);
            }
            String fromUserId = list.get(i).getUserId();
            String s = userMapper.selectUserAvatar(fromUserId);
            String userName = userMapper.selectUserName(fromUserId);
            String favours = list.get(i).getFavourJson();
            list.get(i).setAvatar(s);
            list.get(i).setUserName(userName);
            List<String> images_ = JSON.parseArray(list.get(i).getImages(), String.class);
            List<String> favour = JSON.parseArray(favours, String.class);
            list.get(i).setFavour(favour);
            list.get(i).setImgs(images_);
            list.get(i).setChildren(children);
            list.get(i).setShowSub(false);
        }
        return list;
    }
}
