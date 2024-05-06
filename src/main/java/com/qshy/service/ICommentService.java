package com.qshy.service;

import com.qshy.entity.Comment;
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
public interface ICommentService extends IService<Comment> {
    List<Comment> getToUserComments(String userId);

    List<Comment> getUserComments(String userId);

    List<Comment> getStrategyComments(String strategyId);

    List<Comment> getCommentAnswers(String commentId);

    List<Comment> getScenicComments(String scenicId);

    List<String> getCommentUsers(String parentId);

    List<Comment> deleteScenicComment(String commentId, String scenicId, String type);

    List<Comment> deleteChildComment(String commentId, String parentId, String type, String articleId);

    boolean prepareComment(String commentId, String parentId, String userId);

    List<Comment> getRouteComments(String routeId);

    List<Comment> deleteStrategyComment(String commentId, String parentId, String type);

    List<Comment> deleteRouteComment(String commentId, String parentId, String type);
}
