package com.wanding.xingpos.version.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * XML帮助类
 */
public class XmlUtils {

    public static UpdateInfo getUpdateInfo(InputStream is) throws Exception{
        //用pull解析器解析服务器返回的xml文件 (xml封装了版本号)
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "utf-8");//设置解析的数据源
        int type = parser.getEventType();
        UpdateInfo info = new UpdateInfo();//实体
        while(type != XmlPullParser.END_DOCUMENT ){
            switch (type) {
                case XmlPullParser.START_TAG:
                    if("version".equals(parser.getName())){
                        info.setVersion(parser.nextText()); //获取版本号
                    }else if ("url".equals(parser.getName())){
                        info.setUrl(parser.nextText()); //获取要升级的APK文件
                    }else if ("description".equals(parser.getName())){
                        info.setDescription(parser.nextText()); //获取该文件的信息
                    }
                    break;
                default:

                    break;
            }
            type = parser.next();
        }
        return info;
    }
}
