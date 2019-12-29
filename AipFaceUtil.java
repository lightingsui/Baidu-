package com.repository.utils;


import com.baidu.aip.face.AipFace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 人脸识别客户端
 *
 * @author ：lightingsui
 * @since ：Created in 2019/10/28 8:35
 */
public class AipFaceUtil {
    /** 百度应用ID **/
    private static final String APP_ID;
    /** 应用账号 **/
    private static final String API_KEY;
    /** 应用密码 **/
    private static final String SECRET_KEY;

    /** 配置属性集合 **/
    private static Properties properties = new Properties();

    static {
        // 获取配置信息
        InputStream resourceAsStream = AipFaceUtil.class.getClassLoader().getResourceAsStream("message.properties");
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        APP_ID = properties.getProperty("baidu.app_id");
        API_KEY = properties.getProperty("baidu.api_key");
        SECRET_KEY = properties.getProperty("baidu.secret_key");
    }

    private AipFaceUtil(){}

    /**
     * 获取一个百度 API 客户端
     *
     * <p>获取一个百度 API 客户端
     *
     * @return 百度客户端
     */
    public static AipFace getAipFace(){
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        return client;
    }
}
