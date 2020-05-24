package com.changgou.file.pojo;

import lombok.Data;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/22 13:45
 * @Description:
 */
@Data
public class FastDFSfile {
    //文件名称
    private String name;
    //文件内容
    private byte[] content;
    //文件扩展名
    private String ext;
    //文件MD5摘要值
    private String md5;
    //文件创作作者
    private String author;
}
