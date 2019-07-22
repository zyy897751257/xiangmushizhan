package com.zyy.test;

import com.zyy.pinyougou.common.FastDFSClient;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import java.io.IOException;

public class FastdfsTest {

    /*@Test
    public void uploadFastdfs() throws Exception {
        ClientGlobal.init("D:\\JavaCode\\pinyougou\\pinyougou-shop-web\\src\\main\\resources\\config\\fdfs_client.conf");
        TrackerClient trackerClient = new TrackerClient();

        TrackerServer trackerServer = trackerClient.getConnection();

        StorageClient storageClient = new StorageClient(trackerServer, null);

        String[] jpgs = storageClient.upload_file("C:\\Users\\Zyy\\Pictures\\123.jpg", "jpg", null);

        for (String jpg : jpgs) {
            System.out.println(jpg);
        }
    }*/

    @Test
    public void upload() throws Exception {
        FastDFSClient client = new FastDFSClient("G:\\JavaCode\\pinyougou\\pinyougou\\pinyougou-shop-web\\src\\main\\resources\\config\\fastdfs_client.conf");
        String jpg = client.uploadFile("C:\\Users\\Zyy\\Pictures\\banner3.jpg", "jpg");
        System.out.println(jpg);
    }
}
