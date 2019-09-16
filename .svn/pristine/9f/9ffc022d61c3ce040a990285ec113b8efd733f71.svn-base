package com.wanding.xingpos.version.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * APP更新http工具类
 */
public class HttpURLConUtil {

    public static UpdateInfo getUpdateInfo(String path){
        //包装成url的对象
        try {
            URL url = new URL(path);
            HttpURLConnection conn =  (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            InputStream is =conn.getInputStream();
            UpdateInfo info = XmlUtils.getUpdateInfo(is);
            return info;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
