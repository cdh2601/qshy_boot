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
public class Scenic implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "scenic_id", type = IdType.INPUT)
    private String scenicId;

    private String scenicName;

    private String introduction;

    /**
     * 点赞
     */
    private String likeJson;

    private String textJson;
    private String ticketJson;

    private String openTimeJson;

    private String phone;

    private String trafficJson;

    private String location;

    private Float latitude;

    private Float longitude;

    private String scenicImgs;

    private String month;

    private String stype;

    private String area;

    private double score;

    private String collectionJson;

    @TableField(exist = false)
    private List<String> scenicImages;

    @TableField(exist = false)
    private List<String> collectionUsers;

    @TableField(exist = false)
    private List<String> likesUsers;

    @TableField(exist = false)
    private List<String> commentUsers;

    @TableField(exist = false)
    private List<String> text;

    @TableField(exist = false)
    private List<String> openTime;

    @TableField(exist = false)
    private List<String> ticket;

    @TableField(exist = false)
    private List<String> traffic;

    private String playTime;
}
