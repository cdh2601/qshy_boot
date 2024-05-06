package com.qshy.controller;


import cn.hutool.core.util.StrUtil;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qshy.entity.Code;
import com.qshy.entity.Result;
import com.qshy.entity.User;
import com.qshy.service.IUserService;
import com.qshy.service.impl.UserServiceImpl;
import com.qshy.util.CheckCodeUtil;
import com.qshy.util.MySecurityUtil;
import com.qshy.util.TokenUtil;


import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

//import javax.mail.internet.MimeMessage;
//import javax.servlet.http.HttpSession;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2023-01-07
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender jms;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String from;

    @Transactional
    @PostMapping("/register")
    @ResponseBody
    public Result userRegist(@RequestBody HashMap<String, Object> data, HttpSession session) {
        String userStr = JSON.toJSONString(data.get("user"));
        User user = JSON.parseObject(userStr, User.class);
        String email = user.getEmail();
        String password = user.getPassword();
        String checkCode = (String) (data.get("CheckCode"));
        String checkCode1 = TokenUtil.GetCheckCode(user.getEmail() + "r");
//        System.out.println("用户注册\n" + user + "\n" + checkCode1 + checkCode);
        if (checkCode1 == null) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码已过期");
        }
        if (!checkCode1.equals(checkCode)) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码错误");
        }
        //判断是否为空
        if (StrUtil.isBlank(email) || StrUtil.isBlank(password))
            return new Result(null, Code.OTHER_EVENT_ERROR, "邮箱或密码不能为空");
        try {
            if (!userService.checkExist(email)) {
                if (userService.regist(user)) {
//                    System.out.println("注册成功");
//                    注册成功  验证码立即失效
                    TokenUtil.DestroyCheckCode(user.getEmail() + "r");
                    return new Result(null, Code.SUCCESS, "注册成功,点击左侧进行登录");
                } else {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }
            } else {
                return new Result(null, Code.USER_EXIST, "用户已存在");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Result(null, Code.SYSTEM_ERROR, "注册失败");
    }

    @RequestMapping("/emailCheck")
    public Result getCheckCode(@RequestParam(value = "email") String email, @RequestParam(defaultValue = "r") String type, HttpSession session) {
        MimeMessage message = null;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            String s = CheckCodeUtil.generateVerifyCode(6);
            helper.setTo(email); // 接收地址
            if ("r".equals(type))
                helper.setSubject("您正在使用邮箱注册QSHY，若非本人操作请忽略此邮件"); // 标题
            else if ("rs".equals(type)) {
                helper.setSubject("您正在重置QSHY密码，若非本人操作请忽略此邮件"); // 标题
            } else {
                return new Result(null, Code.SYSTEM_ERROR, "发送失败,类型错误");
            }
            Context context = new Context();
            context.setVariable("code", s);
            TokenUtil.SetCheckCode(email + type, s);
            String template = templateEngine.process("emailTmp", context);
            helper.setText(template, true);
            jms.send(message);
//            System.out.println("发送成功");
            return new Result(null, Code.SUCCESS, "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "发送失败");
        }
    }

    @RequestMapping("/login")
    @ResponseBody
    public Result Login(@RequestParam String username, @RequestParam String password) {
        //判断是否为空
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password))
            return new Result(null, Code.OTHER_EVENT_ERROR, "邮箱或密码不能为空");
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("email", username);
        User check;
        try {
            String s = MySecurityUtil.desEncrypt(password);
            check = userService.getOne(wrapper);
            if (check == null) {
                return new Result(null, Code.USER_EXIST, "用户不存在，请注册");
            } else {
//                System.out.println(s + "\n" + check.getPassword());
                boolean matches = passwordEncoder.matches(s, check.getPassword());
                if (matches) {
                    check.setLoginTime(LocalDateTime.now());
                    userService.updateById(check);
                    HashMap<String, Object> data = new HashMap<String, Object>();
                    check.setPassword(null);
                    check.setCreateTime(null);
                    check.setLoginTime(null);
                    String token = TokenUtil.generateToken(check);
                    data.put("user", check);
                    data.put("token", token);
//                    System.out.println("登录成功" + token);
                    return new Result(data, Code.SUCCESS, "登录成功");
                } else
                    return new Result(null, Code.LOGIN_ERROR, "密码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
    }

    @RequestMapping("/logOut")
    @ResponseBody
    public Result Logout(@RequestParam String userId,
                         @RequestHeader(value = "token") String token) {

        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        User check;
        try {
            check = userService.getOne(wrapper);
            check.setPassword(null);
            check.setCreateTime(null);
            check.setLoginTime(null);
            boolean b = TokenUtil.Destroy(token);
            if (b)
                return new Result(null, Code.SUCCESS, "登出成功");
            else
                return new Result(null, Code.SYSTEM_ERROR, "登出成失败");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
    }

    @Transactional
    @RequestMapping("/delete")
    @ResponseBody
    public Result Delete(@RequestParam String userId,
                         @RequestParam String password,
                         @RequestHeader(value = "token") String token) {

        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        String s = MySecurityUtil.desEncrypt(password);
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("user_id", userId);
        User check;
        try {
            check = userService.getOne(wrapper);
            if (check == null) {
                return new Result(null, Code.LOGIN_ERROR, "密码错误");
            }
            if (!passwordEncoder.matches(s, check.getPassword())) {
                return new Result(null, Code.LOGIN_ERROR, "密码错误");
            }
            check.setPassword(null);
            check.setCreateTime(null);
            check.setLoginTime(null);
            boolean b = TokenUtil.Destroy(token);
            boolean d = userService.deleteUser(userId);
            if (b && d) {
                //删除资源
                File home = new File("D:/QSHY/" + userId);
                if (home.exists()) {
                    FileUtils.deleteDirectory(home);
                }
                return new Result(null, Code.SUCCESS, "删除账户成功");
            } else {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return new Result(null, Code.SYSTEM_ERROR, "删除账户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "系统错误");
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public Result saveUser(@RequestParam(value = "userName") String name,
                           @RequestParam(value = "userId") String userId,
                           @RequestParam(value = "travelAge") Integer travelAge,
                           @RequestParam(value = "introduction") String introduction,
                           @RequestHeader(value = "token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        User user = TokenUtil.getUser(token);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", user.getUserId());
        User one = userService.getOne(wrapper);
        if (!"".equals(name) && name != null)
            one.setUserName(name);
        if (!"".equals(introduction) && introduction != null) {
            one.setIntroduction(introduction);
        }
        UpdateWrapper<User> wrapper1 = new UpdateWrapper<>();
        wrapper1.eq("user_id", userId);
        wrapper1.set("user_name", name);
        wrapper1.set("introduction", introduction);
        wrapper1.set("travel_age", travelAge);
        boolean b = userService.update(wrapper1);
        if (b) {
            one.setPassword(null);
            TokenUtil.changeUser(token, one);
            HashMap<String, Object> data = new HashMap<>();
            data.put("user", one);
            return new Result(data, Code.SUCCESS, "个人信息更新成功");

        }
        return new Result(null, Code.SYSTEM_ERROR, "个人信息更新失败");
    }

    @RequestMapping("/getInfo")
    @ResponseBody
    public Result getUser(@RequestHeader(value = "token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        User user = TokenUtil.getUser(token);
        HashMap<String, Object> data = new HashMap<>();
        data.put("user", user);
        return new Result(data, Code.SUCCESS, "获取成功");
    }

    @RequestMapping("/changePassword")
    @ResponseBody
    public Result changePasswd(@RequestParam(value = "email") String email,
                               @RequestParam(value = "checkCode") String checkCode,
                               @RequestParam(value = "oldpass") String oldpass,
                               @RequestParam(value = "newpass") String newpass,
                               @RequestHeader(value = "token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
//        System.out.println("codeKey-----" + email + "rs");
        String s = TokenUtil.GetCheckCode(email + "rs");
        if (s == null) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码已过期");
        }
        if (!s.equals(checkCode)) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码错误");
        }
        boolean updatePasswd = false;
        if (!"".equals(newpass) && newpass != null) {
            QueryWrapper<User> wrapper = new QueryWrapper<User>();
            wrapper.eq("email", email);
            oldpass = MySecurityUtil.desEncrypt(oldpass);
            newpass = MySecurityUtil.desEncrypt(newpass);
            User one = userService.getOne(wrapper);
            if (passwordEncoder.matches(oldpass, one.getPassword())) {
                one.setPassword(newpass);
                UpdateWrapper<User> wrapper1 = new UpdateWrapper<>();
                wrapper1.eq("email", email);
                wrapper1.set("password", passwordEncoder.encode(newpass));
                updatePasswd = userService.update(wrapper1);
                if (!updatePasswd) {
                    return new Result(null, Code.SYSTEM_ERROR, "个人密码更新失败");
                } else {
                    //更新成功删除验证码
                    TokenUtil.DestroyCheckCode(email + "rs");
                    one.setPassword(null);
                    TokenUtil.changeUser(token, one);
                    HashMap<String, Object> data = new HashMap<>();
                    data.put("user", one);
                    return new Result(data, Code.SUCCESS, "个人密码更新成功");
                }
            } else
                return new Result(null, Code.PASS_WORD_WRONG, "原密码输入错误");
        }
        return new Result(null, Code.USER_NOT_EXIST, "密码不能为空");
    }

    @RequestMapping("/header")
    @ResponseBody
    public Result UpLoadPics(@RequestParam String userId,
                             @RequestParam MultipartFile file,
                             @RequestHeader(value = "token") String token) {
        if (!TokenUtil.verify(token)) {
            return new Result(null, Code.SYSTEM_ERROR, "未登录");
        }
        File pichome = new File("D:/QSHY/" + userId);
        if (!pichome.exists()) {
            pichome.mkdirs();
        }
        String fileName = file.getOriginalFilename();
        String filePath = pichome.getAbsolutePath() + "/" + fileName;
        try {
//            System.out.println("newavatar----" + filePath);
            //存入数据库，
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("user_id", userId);
            User one = TokenUtil.getUser(token);
            one.setPassword(null);
            //删除之前的头像
            String lastUrl = one.getAvatar();
            if (lastUrl != null && (!"".equals(lastUrl))) {
                String lastPath = "D:/" + lastUrl.substring(lastUrl.indexOf("QSHY"));
//                System.out.println("oldavatar----" + lastPath);
                File lastHeader = new File(lastPath);
                if (lastHeader.exists()) {
                    lastHeader.delete();
//                    System.out.println("oldavatar exists" + dbool);
                }
            }
            //将文件保存指定目录
            File newpic = new File(filePath);
            if (newpic.exists()) {
                newpic.delete();
            }
            file.transferTo(newpic);
            String fileUrl = "http://localhost:8080/QSHY/" + userId + "/" + fileName;
            one.setAvatar(fileUrl);
            userService.update(one, wrapper);
            HashMap<String, Object> data = new HashMap<>();
            data.put("user", one);
            return new Result(data, Code.SUCCESS, "更换头像成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(null, Code.SYSTEM_ERROR, "上传失败,可能存在同名图片,请修改");
        }
    }

    @RequestMapping("/forgetPassword")
    @ResponseBody
    public Result resetPw(@RequestParam String checkCode,
                          HttpSession session,
                          @RequestParam String email, @RequestParam String password) {

        String checkCode1 = TokenUtil.GetCheckCode(email + "rs");
        if (checkCode1 == null) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码已过期");
        }
        if (!checkCode1.equals(checkCode)) {
            return new Result(null, Code.SYSTEM_ERROR, "验证码错误");
        }
        String s = MySecurityUtil.desEncrypt(password);
        String pwd = passwordEncoder.encode(s);
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>();
        wrapper.eq("email", email);
        wrapper.set("password", pwd);
        boolean update = userService.update(wrapper);
        if (update) {
            TokenUtil.DestroyCheckCode(email + "rs");
            return new Result(null, Code.SUCCESS, "修改成功");
        }
        return new Result(null, Code.SYSTEM_ERROR, "修改失败");
    }

    @RequestMapping("/deleteCheckCode")
    @ResponseBody
    public Result deleteCheckCode(@RequestParam String email) {
        TokenUtil.DestroyCheckCode(email);
        return new Result(null, Code.SUCCESS, "清除成功");
    }
}
