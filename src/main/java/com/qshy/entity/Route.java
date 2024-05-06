package com.qshy.entity;

import java.io.Serializable;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class Route implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "route_id", type = IdType.INPUT)
    private String routeId;

    private String introduction;

    private String routeName;

    private double score;

    private String location;

    private String open;

    private String scenicJson;

    private String collectionJson;

    /**
     * 可能需要一个默认用户
     */
    private String userId;
    @TableField(exist = false)
    private String userName;

    @TableField(exist = false)
    private List<String> locationList;

    @TableField(exist = false)
    private List<RouteScenic> scenics;

    @TableField(exist = false)
    private List<String> collectionUsers;

    @TableField(exist = false)
    private List<String> commentUsers;

    @TableField(exist = false)
    private List<String> scenicIds;

}
