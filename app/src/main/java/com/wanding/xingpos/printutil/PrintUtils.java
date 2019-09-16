package com.wanding.xingpos.printutil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @name BusAppForFuyou
 * @class name：com.dm.busapp.utils
 * @class describe
 * @anthor 王彬 E-mail:wangb@wangang.cc
 * @time 2017/8/9 16:59
 * @change
 * @chang time
 * @class describe
 */

public class PrintUtils {
    public PrintUtils() {
    }

    public static JSONObject setStringContent(String var0, int var1, int var2) {
        String var3 = "";
        String size = "m";
        switch (var1) {
            case 1:
                var3 = "left";
                break;
            case 2:
                var3 = "center";
                break;
            case 3:
                var3 = "right";
                break;
            default:
                var3 = "left";
        }
        switch (var2) {
            case 1:
                size = "s";
                break;
            case 2:
                size = "m";
                break;
            case 3:
                size = "l";
                break;
            default:
                size = "m";
                break;
        }
        JSONObject var4 = new JSONObject();

        try {
            var4.put("contenttype", "txt");
            var4.put("content", var0);
            var4.put("size", size);
            var4.put("position", var3);
            var4.put("offset", "0");
            var4.put("bold", "0");
            var4.put("italic", "0");
            var4.put("height", "-1");
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return var4;
    }

    public static JSONObject setfreeLine(String var0) {
        JSONObject var1 = new JSONObject();

        try {
            var1.put("contenttype", "feed-line");
            var1.put("content", var0);
        } catch (JSONException var3) {
            var3.printStackTrace();
        }

        return var1;
    }

    public static JSONObject setTwoDimension(String var0, int var1, int var2) {
        String var3 = "";
        switch (var1) {
            case 1:
                var3 = "left";
                break;
            case 2:
                var3 = "center";
                break;
            case 3:
                var3 = "right";
                break;
            default:
                var3 = "left";
        }

        JSONObject var4 = new JSONObject();

        try {
            var4.put("contenttype", "two-dimension");
            var4.put("content", var0);
            var4.put("size", var2);
            var4.put("position", var3);
        } catch (JSONException var6) {
            var6.printStackTrace();
        }

        return var4;
    }

    public static JSONObject setOneDimension(String var0, int var1, int var2, int var3) {
        String var4 = "";
        switch (var1) {
            case 1:
                var4 = "left";
                break;
            case 2:
                var4 = "center";
                break;
            case 3:
                var4 = "right";
                break;
            default:
                var4 = "left";
        }

        JSONObject var5 = new JSONObject();

        try {
            var5.put("contenttype", "one-dimension");
            var5.put("content", var0);
            var5.put("size", var3);
            var5.put("position", var4);
            var5.put("offset", "0");
            var5.put("height", var2);
            var5.put("bold", "0");
            var5.put("italic", "0");
        } catch (JSONException var7) {
            var7.printStackTrace();
        }

        return var5;
    }

    public static JSONObject setLine() {
        JSONObject var0 = new JSONObject();

        try {
            var0.put("content-type", "line");
        } catch (JSONException var2) {
            var2.printStackTrace();
        }

        return var0;
    }

    public static JSONObject setbitmap(String content, int var0) {
        String var1 = "";
        switch (var0) {
            case 1:
                var1 = "left";
                break;
            case 2:
                var1 = "center";
                break;
            case 3:
                var1 = "right";
                break;
            default:
                var1 = "left";
        }

        JSONObject var2 = new JSONObject();

        try {
            var2.put("contenttype", "bmp");
            var2.put("position", var1);
            var2.put("content", content);
        } catch (JSONException var4) {
            var4.printStackTrace();
        }

        return var2;
    }
}

