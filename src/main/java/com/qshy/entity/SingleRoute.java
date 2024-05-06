package com.qshy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Classname singleRoute
 * @Description TODO
 * @Date 2023/4/17 14:37
 * @Created by senorisky
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class SingleRoute implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "t_id", type = IdType.AUTO)
    private String tId;

    private String lastId;
    private String nextId;
    private String routeId;
    @TableField(exist = false)
    private float lastLongtitude;
    @TableField(exist = false)
    private float lastLatitude;
    @TableField(exist = false)
    private float nextLongtitude;
    @TableField(exist = false)
    private float nextLatitude;
    @TableField(exist = false)
    private String lastName;
    @TableField(exist = false)
    private String nextName;
}
