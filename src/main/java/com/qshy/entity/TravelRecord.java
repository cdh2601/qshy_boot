package com.qshy.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

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
public class TravelRecord implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "travel_record_id", type = IdType.INPUT)
    private String travelRecordId;

    private String travelRecordName;

    private String userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime travelTime;

    private String share;

    private String climate;

    private String text;

    private String location;

    @TableField(exist = false)
    private String avatar;

    private String introduction;

}
