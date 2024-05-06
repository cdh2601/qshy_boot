package com.qshy.service;

import com.qshy.entity.TravelRecord;
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
public interface ITravelRecordService extends IService<TravelRecord> {

    List<TravelRecord> getUsersRecords(String userId);

    List<TravelRecord> getAllRecords();

    boolean shareRecord(String recordId, String userId);

}
