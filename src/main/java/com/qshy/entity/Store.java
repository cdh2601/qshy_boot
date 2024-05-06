package com.qshy.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
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
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Store implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "answer_Id",type = IdType.INPUT)
    private String storeId;

    /**
     * 吃住地点
     */
    private String storeName;

    private Float longitude;

    private Float latitude;

    private String text;

    private String type;

    private String location;


}
