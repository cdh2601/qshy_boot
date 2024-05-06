package com.qshy.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.alibaba.fastjson.JSON;
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
public class Strategy implements Serializable, Comparable<Strategy> {

    private static final long serialVersionUID = 1L;
    @TableId(value = "strategy_id", type = IdType.INPUT)
    private String strategyId;

    private String strategyName;

    private String userId;

    private String location;
    private boolean open;
    private boolean type;
    private String text;

    /**
     * 收藏
     */
    private String collectionJson;

    /**
     * 点赞
     */
    private String likeJson;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    @TableField(exist = false)
    private String avatar;
    @TableField(exist = false)
    private String ownerName;
    @TableField(exist = false)
    private List<String> collections;
    @TableField(exist = false)
    private List<String> likes;



    @Override
    public int compareTo(Strategy o) {
        int lenl = JSON.parseArray(o.getCollectionJson(), String.class).size();
        int lent = JSON.parseArray(this.getCollectionJson(), String.class).size();
        if (lenl > lent) {
            return 1;
        }
        return -1;
    }
}
