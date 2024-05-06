package com.qshy.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qshy.entity.Code;
import com.qshy.entity.Result;
import com.qshy.entity.Scenic;
import com.qshy.service.IScenicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Classname PictureController
 * @Description TODO
 * @Date 2023/1/8 21:37
 * @Created by senorisky
 */
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Autowired
    private IScenicService scenicService;

    @RequestMapping("/scenic/upLoadPic")
    @ResponseBody
    public Result upScenicPic(@RequestParam String scenicId,
                              @RequestParam MultipartFile file) {
        File pichome = new File("D:/QSHY/scenics/" + scenicId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String filePath = pichome.getAbsolutePath() + "/" + fileName;
        File pic = new File(filePath);
        if (pic.exists()) {
            return new Result(null, Code.SUCCESS, "存在同名图片");
        }
        try {
            file.transferTo(pic);
            QueryWrapper<Scenic> wrapper1 = new QueryWrapper<>();
            wrapper1.eq("scenic_id", scenicId);
            Scenic one = scenicService.getOne(wrapper1);
            String scenicImgs = one.getScenicImgs();
            List<String> list;
            if (scenicImgs == null) {
                list = new ArrayList<>();
            } else {
                list = JSON.parseArray(scenicImgs, String.class);
            }
            list.add("http://localhost:8080/QSHY/scenics/" + scenicId + "/" + fileName);
            UpdateWrapper<Scenic> wrapper = new UpdateWrapper<>();
            wrapper.eq("scenic_id", scenicId);
            wrapper.set("scenic_imgs", JSON.toJSONString(list));
            scenicService.update(wrapper);
            return new Result(null, Code.SUCCESS, "上传保存成功");
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "IO错误");
        }

    }
}
