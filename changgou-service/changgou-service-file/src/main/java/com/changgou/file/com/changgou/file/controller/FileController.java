package com.changgou.file.com.changgou.file.controller;

import com.changgou.common.pojo.Result;
import com.changgou.common.pojo.StatusCode;
import com.changgou.file.com.changgou.file.util.FastDFSClient;
import com.changgou.file.pojo.FastDFSfile;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/24 22:09
 * @Description:
 */
@RestController
@CrossOrigin
public class FileController {
    @PostMapping("/upload")
    public Result upload(@RequestParam("file")MultipartFile file) throws IOException {
        FastDFSfile fastDFSfile = new FastDFSfile();
        fastDFSfile.setName(file.getOriginalFilename());
        fastDFSfile.setContent(file.getBytes());
        fastDFSfile.setExt(StringUtils.getFilenameExtension(file.getOriginalFilename()));

        String[] upload = FastDFSClient.upload(fastDFSfile);
        String url = FastDFSClient.getTrackerUrl()+upload[0]+"/"+upload[1];
        return new Result(true, StatusCode.OK,"文件上传成功",url);
    }


}
