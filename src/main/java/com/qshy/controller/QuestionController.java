package com.qshy.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qshy.entity.Code;
import com.qshy.entity.Question;
import com.qshy.entity.Result;
import com.qshy.entity.Strategy;
import com.qshy.service.impl.QuestionServiceImpl;
import com.qshy.service.impl.StrategyServiceImpl;
import com.qshy.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionServiceImpl questionService;

    @RequestMapping("/list")
    @ResponseBody
    public Result getQuestionList(@RequestParam(defaultValue = "") String userId) {
        List<Question> list;
        HashMap<String, Object> map = new HashMap<>();
        if (!"".equals(userId)) {
            list = questionService.userQuestionList(userId);

        } else {
            list = questionService.allQuestionList();
        }
        map.put("questions", list);
        return new Result(map, Code.SUCCESS, "获取问题列表成功");
    }

    @RequestMapping("/giveQuestion")
    @ResponseBody
    public Result giveQuestion(@RequestBody Question question,
                               @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        UUID uuid = UUID.randomUUID();
        question.setQuestionId(uuid.toString());
        question.setAskTime(LocalDateTime.now());
        boolean save = questionService.save(question);
        if (save) {
            List<Question> list = questionService.list();
            Map<String, Object> map = new HashMap<>();
            map.put("questions", list);
            return new Result(map, Code.SUCCESS, "问题提交成功");
        }

        return new Result(null, Code.SYSTEM_ERROR, "问题提交失败");
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Result deleteQuestion(@RequestParam String questionId,
                                 @RequestParam String userId,
                                 @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("questionId", questionId);
        boolean save = questionService.remove(wrapper);
        if (save) {
            QueryWrapper wrapper1 = new QueryWrapper();
            wrapper1.eq("user_id", userId);
            List<Question> list = questionService.list(wrapper1);
            Map<String, Object> map = new HashMap<>();
            map.put("questions", list);
            return new Result(map, Code.SUCCESS, "问题删除成功");
        }

        return new Result(null, Code.SYSTEM_ERROR, "问题删除失败");
    }

    @RequestMapping("/search")
    @ResponseBody
    public Result searchQuestion(@RequestParam String str,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "8") Integer pageSize,
                                 @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        Page<Question> list;
        HashMap<String, Object> map = new HashMap<>();
        list = questionService.searchQuestion(pageNum, pageSize, str);
        map.put("questions", list);
        return new Result(map, Code.SUCCESS, "搜索成功");

    }
}
