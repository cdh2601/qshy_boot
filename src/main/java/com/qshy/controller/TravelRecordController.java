package com.qshy.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qshy.entity.Code;
import com.qshy.entity.Result;
import com.qshy.entity.TravelRecord;
import com.qshy.service.impl.TravelRecordServiceImpl;
import com.qshy.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/travelRecord")
public class TravelRecordController {

    @Autowired
    private TravelRecordServiceImpl travelRecordService;

    @RequestMapping("/getAll")
    @ResponseBody
    public Result getAllRecord(@RequestParam(defaultValue = "") String userId, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        HashMap<String, Object> map = new HashMap<>();
        List<TravelRecord> records;
        if (!"".equals(userId)) {
            records = travelRecordService.getUsersRecords(userId);

        } else {
            records = travelRecordService.getAllRecords();
        }
        map.put("records", records);
        return new Result(map, Code.SUCCESS, "获取成功");
    }

    @RequestMapping("/generate")
    @ResponseBody
    public Result generateRecord(@RequestBody TravelRecord record, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        record.setTravelTime(LocalDateTime.now());
        boolean save = travelRecordService.save(record);
        if (save) {
            String userId = record.getUserId();
            List<TravelRecord> list = travelRecordService.getUsersRecords(userId);
            Map<String, Object> map = new HashMap<>();
            map.put("records", list);
            return new Result(map, Code.SUCCESS, "生成成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "生成失败");
    }

    @RequestMapping("/share")
    @ResponseBody
    public Result shareRecord(@RequestParam String userId, @RequestParam String recordId, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }

        boolean save = travelRecordService.shareRecord(recordId, userId);
        if (save) {
            List<TravelRecord> list = travelRecordService.getUsersRecords(userId);
            Map<String, Object> map = new HashMap<>();
            map.put("records", list);
            return new Result(map, Code.SUCCESS, "分享成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "分享失败");
    }


    @RequestMapping("/delete")
    @ResponseBody
    public Result deleteRecord(@RequestParam String recordId, @RequestParam String userId, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("travel_record_id", recordId);
        boolean save = travelRecordService.remove(wrapper);
        if (save) {
            List<TravelRecord> list = travelRecordService.getUsersRecords(userId);
            Map<String, Object> map = new HashMap<>();
            map.put("records", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }

    @RequestMapping("/info")
    @ResponseBody
    public Result recordInfo(@RequestParam String recordId, @RequestHeader String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("travel_record_id", recordId);
        TravelRecord record = travelRecordService.getOne(wrapper);
        if (record != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("record", record);
            return new Result(map, Code.SUCCESS, "获取成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "获取失败");
    }
}
