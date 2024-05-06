package com.qshy.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.*;
import com.qshy.service.impl.DraftServiceImpl;
import com.qshy.service.impl.StrategyServiceImpl;
import com.qshy.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyServiceImpl strategyService;

    @Autowired
    private DraftServiceImpl draftService;


    @RequestMapping("/list")
    @ResponseBody
    public Result getStrategyList(@RequestParam(defaultValue = "1") Integer pageNum,
                                  @RequestParam(defaultValue = "8") Integer pageSize) {
        Page<Strategy> list = strategyService.allStrategyListPage(pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", list);
        return new Result(map, Code.SUCCESS, "获取攻略列表成功");
    }

    @RequestMapping("/pageByCollection")
    @ResponseBody
    public Result pageByCollection() {
        List<Strategy> list = strategyService.allStrategyListByCollection();
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", list);
        return new Result(map, Code.SUCCESS, "获取攻略列表成功");
    }

    @RequestMapping("/search")
    @ResponseBody
    public Result searchStrategies(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize,
                                   @RequestParam String strategyName) {
        Page<Strategy> page = strategyService.searchStrategyList(pageNum, pageSize, strategyName);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", page);
        return new Result(map, Code.SUCCESS, "获取攻略列表成功");
    }

    @RequestMapping("/getUserCollectionStrategies")
    @ResponseBody
    public Result getUserCollectionStrategies(@RequestParam String userId,
                                              @RequestParam(defaultValue = "1") Integer pageNum,
                                              @RequestParam(defaultValue = "8") Integer pageSize,
                                              @RequestHeader String token
    ) {
        Page<Strategy> page = strategyService.getUserCollectedStrategyList(userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", page);
        return new Result(map, Code.SUCCESS, "获取攻略列表成功");
    }

    @RequestMapping("/searchCollection")
    @ResponseBody
    public Result searchCollection(
            @RequestParam String userId,
            @RequestHeader String token,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "8") Integer pageSize,
            @RequestParam(required = false) String strategyName) {
        Page<Strategy> page = strategyService.searchConnectionList(strategyName, userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", page);
        return new Result(map, Code.SUCCESS, "获取攻略列表成功");
    }

    @RequestMapping("/userStrategySearch")
    @ResponseBody
    public Result userStrategySearch(
            @RequestParam String userId,
            @RequestHeader String token,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "8") Integer pageSize,
            @RequestParam(required = false) String strategyName) {
        Page<Strategy> page = strategyService.searchUserStrategy(strategyName, userId, pageNum, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", page);
        return new Result(map, Code.SUCCESS, "获取攻略列表成功");
    }

    @RequestMapping("/info")
    @ResponseBody
    public Result getStrategyInfo(@RequestParam String strategyId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("strategy_id", strategyId);
        Strategy strategy = strategyService.getOneStrategyInfo(strategyId);
        Map<String, Object> map = new HashMap<>();
        map.put("strategy", strategy);
        return new Result(map, Code.SUCCESS, "获取攻略成功");
    }

    @RequestMapping("/upLoadPic")
    @ResponseBody
    public Result upLoadPic(@RequestParam MultipartFile file, @RequestParam String strategyId, @RequestParam String userId, @RequestHeader String token) {
//        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        File strategyHome = new File("D:/QSHY/strategy/" + strategyId + "/" + userId);
        if (!strategyHome.exists()) {
            strategyHome.mkdirs();
        }
        UUID id = UUID.randomUUID();
        String[] idd = id.toString().split("-");
        String filename = file.getOriginalFilename();
        int length = filename.split("\\.").length;
        String fileType = filename.split("\\.")[length - 1];
        String newname = idd[0] + idd[1] + idd[2] + "." + fileType;
        String filePath = strategyHome.getAbsolutePath() + "/" + newname;
        File nfile = new File(filePath);
        if (nfile.exists()) {
            return new Result(null, Code.FILE_EXIST, "已有同名图片存在");
        }
        String url = null;
        try {
            file.transferTo(nfile);
            url = "http://localhost:8080/QSHY/strategy/" + strategyId + "/" + userId + "/" + newname;
            Map<String, Object> map = new HashMap<>();
            map.put("url", url);
            map.put("name", newname);
            return new Result(map, Code.SUCCESS, "图片上传成功");
        } catch (Exception e) {
            return new Result(null, Code.FILE_EXIST, "图片上传失败");
        }
    }

    @RequestMapping("/deletePic")
    @ResponseBody
    public Result deletePic(@RequestParam List<String> delurls,
                            @RequestHeader String token) {
//        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        try {
            List<String> urls = new ArrayList<>();
            for (String picurl : delurls) {
                int index = picurl.indexOf("/QSHY/strategy/");
                String rePath = picurl.substring(index);
                File picture = new File("D:" + rePath);
                if (!picture.exists()) {
                    urls.add(picurl);
                    picture.delete();
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("urls", urls);
            return new Result(map, Code.SUCCESS, "图片删除成功");
        } catch (Exception e) {
            return new Result(null, Code.FILE_NOT_EXIST, "图片删除失败");
        }
    }

    @RequestMapping("/collection")
    @ResponseBody
    public Result giveLove(@RequestParam String strategyId, @RequestParam String userId, @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "用户未登录");
        }
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        //按点赞数降序
        wrapper.eq("strategy_id", strategyId);
        Strategy one = strategyService.getOne(wrapper);
        if (one.getCollectionJson() == null) {
            one.setCollectionJson("[]");
        }
        List<String> list = JSON.parseArray(one.getCollectionJson(), String.class);
        if (!list.contains(userId))
            list.add(userId);
        else {
            return new Result(null, Code.SUCCESS, "已搜藏");
        }
        one.setCollectionJson(JSON.toJSONString(list));
        one.setCollections(list);
        boolean save = strategyService.update(one, wrapper);
        if (save) {
            return new Result(null, Code.SUCCESS, "收藏成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "收藏失败");
        }
    }

    @RequestMapping("/deCollection")
    @ResponseBody
    public Result delove(@RequestParam String strategyId, @RequestParam String userId, @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "用户未登录");
        }
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        //按点赞数降序
        wrapper.eq("strategy_id", strategyId);
        Strategy one = strategyService.getOne(wrapper);
        List<String> list = JSON.parseArray(one.getCollectionJson(), String.class);
        if (list.contains(userId))
            list.remove(userId);
        else
            return new Result(null, Code.SUCCESS, "未收藏");
        one.setCollectionJson(JSON.toJSONString(list));
        one.setCollections(list);
        boolean save = strategyService.update(one, wrapper);
        if (save) {
            return new Result(null, Code.SUCCESS, "解除收藏成功");
        } else {
            return new Result(null, Code.SYSTEM_ERROR, "解除收藏失败");
        }
    }

    @RequestMapping("/initStrategy")
    @ResponseBody
    public Result initStrategy(@RequestParam String userId,
                               @RequestParam String strategyId,
                               @RequestHeader String token) {
//        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        Strategy strategy = new Strategy();
        strategy.setStrategyId(strategyId);
        strategy.setUserId(userId);
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setOpen(false);
        strategy.setType(true);
        strategy.setText("");
        strategyService.save(strategy);
        return new Result(null, Code.SUCCESS, "创建成功");
    }

    @RequestMapping("/confirmPic")
    @ResponseBody
    public Result confirmPic(@RequestParam String userId,
                             @RequestParam List<String> imageList,
                             @RequestParam String strategyId,
                             @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        String fileHome = "D:/QSHY/strategy/" + strategyId + "/" + userId;
        File homeFile = new File(fileHome);
        String[] list = homeFile.list();
        if (list != null)
            for (String i : list) {
//                System.out.println("list---------------" + i);
                String fur = "http://localhost:8080/QSHY/strategy/" + strategyId + "/" + userId + "/" + i;
                if (!imageList.contains(fur)) {
                    File img = new File(fileHome + "/" + i);
                    img.delete();
                }
            }
        return new Result(null, Code.SUCCESS, "图片确认成功");
    }

    @Transactional
    @RequestMapping("/uploadStrategy")
    @ResponseBody
    public Result uploadStrategy(@RequestBody Strategy strategy,
                                 @RequestParam String draftId
                             ) {
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setCollectionJson("[]");
        strategy.setLikeJson("[]");
        boolean update = strategyService.saveOrUpdate(strategy);
        Draft draft = new Draft();
        draft.setDraftId(draftId);
        draft.setCollectionJson("[]");
        draft.setLikeJson("[]");
        draft.setCreateTime(LocalDateTime.now());
        draft.setOpen(false);
        draft.setType(false);
        draft.setUserId(strategy.getUserId());
        draft.setText(strategy.getText());
        draft.setDraftName(strategy.getStrategyName());
        boolean drupdate = draftService.saveOrUpdate(draft);
        if (update && drupdate) {
            List<Draft> list = strategyService.getUserDraftList(strategy.getUserId());
            Map<String, Object> map = new HashMap<>();
            map.put("drafts", list);
            return new Result(map, Code.SUCCESS, "攻略上传成功");
        }
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return new Result(null, Code.FILE_NOT_EXIST, "攻略上传失败");
    }

    @RequestMapping("/uploadDraft")
    @ResponseBody
    public Result uploadDraft(@RequestBody Draft strategy) {
        strategy.setCreateTime(LocalDateTime.now());
        strategy.setLikeJson("[]");
        strategy.setCollectionJson("[]");
        boolean update = draftService.saveOrUpdate(strategy);
        if (update) {
            List<Draft> list = strategyService.getUserDraftList(strategy.getUserId());
            Map<String, Object> map = new HashMap<>();
            map.put("drafts", list);
            return new Result(map, Code.SUCCESS, "攻略上传成功");
        }
        return new Result(null, Code.FILE_NOT_EXIST, "攻略上传失败");
    }

    @RequestMapping("/getUserStrategyList")
    @ResponseBody
    public Result getUserStrategies(@RequestParam String userId,
                                    @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        List<Strategy> list = strategyService.getUserStrategyList(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", list);
        return new Result(map, Code.SUCCESS, "获取攻略发布列表成功");
    }

    @RequestMapping("/getUserStrategyPage")
    @ResponseBody
    public Result getUserStrategyPage(@RequestParam String userId,
                                      @RequestParam Integer pageSize, @RequestParam Integer pageNum,
                                      @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        Page<Strategy> list = strategyService.getUserStrategyPage(userId, pageSize, pageNum);
        Map<String, Object> map = new HashMap<>();
        map.put("strategies", list);
        return new Result(map, Code.SUCCESS, "获取攻略发布列表成功");
    }

    @RequestMapping("/getUserDrafts")
    @ResponseBody
    public Result getUserDraftList(@RequestParam String userId,
                                   @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        List<Draft> list = strategyService.getUserDraftList(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("drafts", list);
        return new Result(map, Code.SUCCESS, "获取攻略草稿列表成功");
    }

    @RequestMapping("/delDraft")
    @ResponseBody
    public Result delDraft(@RequestParam String userId,
                           @RequestParam String draftId,
                           @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        QueryWrapper<Draft> wrapper = new QueryWrapper<>();
        wrapper.eq("draft_id", draftId);
        boolean a = draftService.remove(wrapper);
        if (a) {
            List<Draft> list = strategyService.getUserDraftList(userId);
            Map<String, Object> map = new HashMap<>();
            map.put("drafts", list);
            return new Result(map, Code.SUCCESS, "删除草稿成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除草稿失败");
    }

    @RequestMapping("/draftInfo")
    @ResponseBody
    public Result delDraft(
            @RequestParam String draftId,
            @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        Draft one = draftService.getById(draftId);
        Map<String, Object> map = new HashMap<>();
        map.put("draft", one);
        return new Result(map, Code.SUCCESS, "获取草稿信息成功");
    }

    @RequestMapping("/delIssue")
    @ResponseBody
    public Result delIssue(@RequestParam String userId,
                           @RequestParam String strategyId,
                           @RequestHeader String token) {
        //        if (!TokenUtil.verify(token)) {
//            return new Result(null, Code.SYSTEM_ERROR, "未登录");
//        }
        QueryWrapper<Strategy> wrapper = new QueryWrapper<>();
        wrapper.eq("strategy_id", strategyId);
        boolean a = strategyService.remove(wrapper);
        if (a) {
            List<Strategy> list = strategyService.getUserStrategyList(userId);
            Map<String, Object> map = new HashMap<>();
            map.put("strategies", list);
            return new Result(map, Code.SUCCESS, "删除成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "删除失败");
    }
}
