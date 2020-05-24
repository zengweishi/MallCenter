package com.changgou.file.com.changgou.file.util;

import com.changgou.file.pojo.FastDFSfile;
import lombok.extern.slf4j.Slf4j;
import org.csource.fastdfs.*;
import org.csource.common.NameValuePair;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/5/22 14:50
 * @Description:FastDFS信息获取以及文件相关操作
 */
@Slf4j
public class FastDFSClient {
    /**
     * 初始化加载FastDFS的TrackerServer配置
     */
    static {
        try {
            //获取fdfs_client.conf文件路径
            String path = new ClassPathResource("fdfs_client.conf").getFile().getAbsolutePath();
            //初始化TrackerServer
            ClientGlobal.init(path);
        }catch (Exception e) {
            log.error("初始化加载FastDFS的TrackerServer配置异常",e);
        }
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    public static String[] upload(FastDFSfile file) {
        //附加参数：获取文件作者
        NameValuePair[] meta_list = new NameValuePair[1];
        meta_list[0] = new NameValuePair("author",file.getAuthor());

        //接收返回的数据
        String[] result = null;
        StorageClient storageClient = null;
        try {
            //获取StorageClient
            storageClient = getStorageClient();
            //文件上传
            result = storageClient.upload_file(file.getContent(), file.getExt(), meta_list);
        } catch (Exception e) {
            log.error("{}文件上传异常:message{}",file.getName(),e.getMessage());
        }
        if (storageClient != null && result == null) {
            log.error("文件上传失败，errorCode:{}",storageClient.getErrorCode());
        }
        //获取组名
        String groupName = result[0];
        //获取文件存储路径
        String path = result[1];
        return result;
    }

    /**
     * 获取文件信息
     * @param groupName 组名
     * @param remoteFileName 文件存储完整名称
     * @return
     */
    public static FileInfo getFile(String groupName,String remoteFileName) {
        try {
            log.error("获取文件信息：groupName:{},remoteFileName{}",groupName,remoteFileName);
            StorageClient storageClient = getStorageClient();
            return storageClient.get_file_info(groupName, remoteFileName);
        } catch (Exception e) {
            log.error("获取文件信息异常，{}",e.getMessage());
        }
        return null;
    }

    /**
     * 文件下载
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static InputStream downLoadFile(String groupName,String remoteFileName) {
        try {
            log.error("下载文件：groupName:{},remoteFileName{}",groupName,remoteFileName);
            StorageClient storageClient = getStorageClient();
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            return new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            log.error("文件下载异常，{}",e.getMessage());
        }
        return null;
    }

    /**
     * 文件删除
     * @param groupName
     * @param remoteFileName
     */
    public static void deleteFile(String groupName,String remoteFileName) {
        try {
            log.error("删除文件：groupName:{},remoteFileName{}",groupName,remoteFileName);
            StorageClient storageClient = getStorageClient();
            storageClient.delete_file(groupName,remoteFileName);
        } catch (Exception e) {
            log.error("文件删除异常,{}",e.getMessage());
        }
    }

    /**
     * 获取Storage组
     * @param groupName
     * @return
     */
    public static StorageServer[] getStorageGroup(String groupName) throws IOException {
        //获取TrackerClient
        TrackerClient trackerClient = new TrackerClient();
        //获取TrackerServer
        TrackerServer trackerServer = trackerClient.getConnection();
        //获取Storage组
       return trackerClient.getStoreStorages(trackerServer,groupName);
    }


    /**
     * 获取Storage信息（IP，端口）
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static ServerInfo[] getStorageInfo(String groupName,String remoteFileName) throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        TrackerServer trackerServer = trackerClient.getConnection();
        return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
    }

    /**
     * 获取Tracker的URL地址
     * @return
     * @throws IOException
     */
    public static String getTrackerUrl() throws IOException {
        return "http://"+getTrackerServer().getInetSocketAddress().getHostString()+":"+ClientGlobal.getG_tracker_http_port()+"/";
    }

    /**
     * 获取StorageClient
     * @return
     * @throws IOException
     */
    private static StorageClient getStorageClient() throws IOException {
        //先获取TrackerServer
        TrackerServer trackerServer = getTrackerServer();
        return new StorageClient(trackerServer, null);
    }

    /**
     * 获取TrackerServer
     * @return
     * @throws IOException
     */
    private static TrackerServer getTrackerServer() throws IOException {
        TrackerClient trackerClient = new TrackerClient();
        return trackerClient.getConnection();
    }


}
