package com.suancaiyu.controller;

import ch.qos.logback.core.util.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.suancaiyu.entity.Person;
import com.suancaiyu.entity.Picture;
import com.suancaiyu.entity.PictureSearchRequest;
import com.suancaiyu.entity.RestResult;
import com.suancaiyu.mapper.PersonMapper;
import com.suancaiyu.mapper.PictureMapper;
import com.suancaiyu.util.JwtUtil;
import com.suancaiyu.util.ResultGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("person")
public class PersonController {

    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PictureMapper pictureMapper;
    @Autowired
    private  ResultGenerator resultGenerator;

    private static final String TARGET_FOLDER = "/home/web/lawulawu/picture/";

    private static final String URL = "http://65.49.194.52:8090/";


    @PostMapping("insertPerson")
    public String insertPerson(@RequestBody Person person){
        personMapper.insert(person);
        return "插入成功！";
    }

    /**
     * 匹配 /user/login 地址 ,限定POST方法
     * 。@NotNull 在字段前添加注解代表验证该字段，如果为空则报异常
     * @return 登陆成功则返回相关信息，否则返回错误提示
     */
    @PostMapping("/login" )
    public RestResult login(@RequestBody Person request) {
        Person person = personMapper.checkLogin(request.getUsername(),request.getPassword());
        if(person != null) {
            return resultGenerator.getSuccessResult("登陆成功",JwtUtil.getToken(person));
        }
        return resultGenerator.getFailResult("用户名/密码错误");
    }

    /**
     * 根据token获取图片
     */
    @PostMapping( "/restResult")
    public RestResult RestResult(@RequestBody PictureSearchRequest search, HttpServletRequest request){
        String token = request.getHeader("authorization").substring(7);

        Person person = JwtUtil.verify(token);
        if(person == null){
            return resultGenerator.getFailResult("未登录");
        }

        List<Picture> pictureList = new ArrayList();
        Boolean hasMore = false;

        int start = (search.getPage()-1)*search.getPageSize();
        int end = start + search.getPageSize();

        if(!search.getNote().isEmpty()){
            pictureList = pictureMapper.selectByNote(search.getNote(),start,end,person.getId());
            if(!pictureMapper.selectByNote(search.getNote(),end,end+1,person.getId()).isEmpty()) hasMore = true;
        }else{
            pictureList = pictureMapper.select(start,end,person.getId());
            if(!pictureMapper.select(end,end+1,person.getId()).isEmpty()) hasMore = true;
        }

        HashMap result = new HashMap();
        result.put("username",person.getUsername());
        result.put("pictureList",pictureList);
        result.put("hasMore",hasMore);

        return resultGenerator.getSuccessResult(result);
    }

    /**
     * 根据token上传图片
     */
    @PostMapping("/upload") // 映射的URL
    public RestResult handleFileUpload(@RequestParam("file") MultipartFile file,@RequestParam("description")String description, HttpServletRequest request) {
        String token = request.getHeader("authorization").substring(7);

        Person person = JwtUtil.verify(token);
        if(person == null){
            return resultGenerator.getFailResult("未登录");
        }

        if (file.isEmpty()) {
            return resultGenerator.getFailResult("文件为空");
        }

        try {
            String urlFilename = UUID.randomUUID() + file.getOriginalFilename();

            // 获取文件名并构建文件路径
            byte[] bytes = file.getBytes();
            Path path = Paths.get(TARGET_FOLDER + urlFilename);

            // 保存文件到服务器
            Files.write(path, bytes);

            //保存文件记录到数据库
            Picture picture = new Picture();
            picture.setPersonId(person.getId());
            picture.setUrl(URL + urlFilename);
            picture.setName(file.getOriginalFilename());
            picture.setNote(description);

            ImageIcon imageFile = new ImageIcon(TARGET_FOLDER + urlFilename);
            float width = imageFile.getIconWidth();
            float height = imageFile.getIconHeight();

            float widthDivisionHeight = 338.75f/width;
            width = 338.75f;
            height = height*widthDivisionHeight;

            picture.setWidth(width);
            picture.setHeight(height);

            pictureMapper.insert(picture);

            return resultGenerator.getSuccessResult("文件上传成功: " + file.getOriginalFilename(),null);

        } catch (IOException e) {
            e.printStackTrace();
            return resultGenerator.getFailResult("文件上传失败！");
        }
    }

    @GetMapping("/getUsername")
    public RestResult getUsername(HttpServletRequest request) {
        String token = request.getHeader("authorization").substring(7);

        Person person = JwtUtil.verify(token);
        if (person == null) {
            return resultGenerator.getFailResult("未登录");
        }

        return resultGenerator.getFailResult(person.getUsername());
    }

    @PostMapping("/register")
    public RestResult register(@RequestBody Person person,HttpServletRequest request) {
        String registerCode = request.getHeader("registerCode");
        if(registerCode.isEmpty() || !registerCode.equals("dfiii88876")){
            return resultGenerator.getFailResult("注册失败");
        }

        personMapper.insertPerson(person.getUsername(),person.getPassword(),person.getAddress());

        return resultGenerator.getSuccessResult("注册成功");
    }
}
