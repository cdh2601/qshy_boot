package com.qshy.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.qshy.entity.Scenic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author
 * @since 2023-01-07
 */

@Mapper
public interface ScenicMapper extends BaseMapper<Scenic> {

    String selectScId(@Param("name") String search);

    List<String> selectAeaList(@Param(Constants.WRAPPER) QueryWrapper<Scenic> wrapper);

    List<String> selectidListByName(@Param("name") String search);
}
