package com.qshy.entity;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "comment_id", type = IdType.INPUT)
    private String commentId;

    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDateTime commentTime;

    private String text;

    private String type;

    private String parentId;
    private int likes;
    private String toUserId;
    private int score;
    private int replyNum;
    private String images;
    private String favourJson;

    /**
     * 发表者的头像
     */
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private boolean showSub;//是否显示次级评论  默认都是false  但是次级评论本身这条无效  因为没有三阶评论
    @TableField(exist = false)
    private String subText = "查看回复";//是否显示次级评论  默认都是false  但是次级评论本身这条无效  因为没有三阶评论
    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private List<String> imgs;
    @TableField(exist = false)
    private List<Comment> children;
    @TableField(exist = false)
    private List<String> favour;
}
