package com.qshy.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qshy.entity.*;
import com.qshy.service.impl.AnswerServiceImpl;
import com.qshy.service.impl.QuestionServiceImpl;
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
 * @author senorisky
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/answer")
public class AnswerController {
    @Autowired
    private AnswerServiceImpl answerService;

    /**
     * 获取一个问题的所有回答
     *
     * @param questionId
     * @param token
     * @return
     */
    @RequestMapping("/getAnswers")
    @ResponseBody
    public Result getQuestionAnswers(@RequestParam String questionId,
                                     @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        List<Answer> list = answerService.questionAnswerList(questionId);
        Map<String, Object> map = new HashMap<>();
        map.put("answers", list);
        return new Result(map, Code.SUCCESS, "获取问题回答成功");
    }

    /**
     * 发表回答
     *
     * @param answer
     * @param token
     * @return
     */
    @RequestMapping("/giveAnswer")
    @ResponseBody
    public Result giveQuestionAnswer(@RequestBody Answer answer,
                                     @RequestHeader("token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        String parentId = answer.getQuestionId();

        UUID uuid = UUID.randomUUID();
        answer.setAnswerId(uuid.toString());
        answer.setAnswerTime(LocalDateTime.now());
        boolean save = answerService.save(answer);
        if (save) {
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("question_id", answer.getQuestionId());
            wrapper.orderByDesc("answer_time");
            List<Answer> list = answerService.list(wrapper);
            Map<String, Object> map = new HashMap<>();
            map.put("answers", list);
            return new Result(map, Code.SUCCESS, "回答成功");
        }
        return new Result(null, Code.SUCCESS, "回答失败");
    }

    /**
     * 获取该用户收到的回答
     *
     * @param token
     * @param userId
     * @return
     */
    @RequestMapping("/toUserAnswers")
    @ResponseBody
    public Result toUserAnswers(@RequestHeader("token") String token,
                                @RequestParam String userId) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }

        List<Answer> list = answerService.toUserAnswerList(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("answers", list);
        return new Result(map, Code.SUCCESS, "获取成功");
    }
}
