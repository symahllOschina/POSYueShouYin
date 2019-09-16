package com.wanding.xingpos.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class Utils {
	public static final String TEST_IMAGE_URL = "http://115.28.147.19/images/product/20150130/641394c5f5bf40b69a083bea59d4a7f8/e78f811c7695401da691d71678b2a367_800.jpg";

	/**
	 * 根据手机分辨率把dp转换成px(像素)
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机分辨率把px转换成dp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean isEmpty(String s) {
		return s == null || s.length() == 0 || "".equals(s.trim()) || "null".equals(s.trim());
	}

	public static boolean isEmptyTwo(String str) {
		if (str == null || "".equals(str) || "null".equalsIgnoreCase(str)) {
			return true;
		}
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	public static boolean isNotEmpty(String s) {
		return s != null && s.length() != 0 && !"".equals(s.trim()) && !"null".equals(s.trim());
	}
	
	/** 
	 * 验证手机格式 
	 * 故先要整清楚现在已经开放了多少个号码段，国家号码段分配如下：
	 *
	 *	移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	 *	
	 *	联通：130、131、132、152、155、156、185、186
	 *	
	 *	电信：133、153、180、189、（1349卫通）
	 *
	 *	注意此种方式仅支持以上号段，如177电信号也会表示不匹配
	 */  
	
	public static boolean isMobileNO(String mobiles){
		//注意此种方式仅支持以上号段，如177电信号也会表示不匹配
		Pattern p = compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		//这里代表电话号码必须以14、13、15、18、17开头的11位数，可以根据自己的需求进行修改
		Pattern p1 = compile("^((14[0-9])|(13[0-9])|(15[0-9])|(18[0-9])|(17[0-9]))\\d{8}$");
		//
		Pattern p2 = compile("^(13[0-9]|14[57]|15[0-35-9]|17[6-8]|18[0-9])[0-9]{8}$");
		Matcher m = p1.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 *  判断邮编是否合法
	 *  中国邮政编码为6位数字，第一位不为0
	 */
	public static boolean isZipNO(String zipString){
	      String str = "^[1-9][0-9]{5}$";
	      return compile(str).matcher(zipString).matches();
	}
	
	/**
	 * 判断邮箱格式是否正确
	 */
	 public static boolean isEmail(String email){
		 if (null==email || "".equals(email))
		 {
			 return false;
		 }

		 //简单匹配
		 //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");
		 //复杂匹配
		 Pattern p =  compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		 Matcher m = p.matcher(email);
		 return m.matches();  
	 }
	 
	 /**
	  *  防止按钮被连续点击
	  */
	private static long lastClickTime;
    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 700) {
            return true;   
        }   
        lastClickTime = time;   
        return false;   
    }
    
    /**
     *  检查当前网络状态(只能判断当前是否连接网络，不能判断网络是否可用)
     *  必须添加访问当前网络状态权限
     *   *	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     */
    public static boolean isNetworkAvailable(Activity activity){
    	Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null)
        {
            return false;
        }else
        {
        	 // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            
            if (networkInfo != null && networkInfo.length > 0)
            {
            	for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    
    /**
     * 获取当前网络连接的类型信息
     *	   -1：没有网络 
     *		1：WIFI网络
     *		2：wap网络
     *		3：net网络 
     */
    public static int getConnectedType(Context context) {
    	if (context != null) 
    	{
	    	ConnectivityManager mConnectivityManager = (ConnectivityManager) context
	    	.getSystemService(Context.CONNECTIVITY_SERVICE);
	    	NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
	    	if (mNetworkInfo != null && mNetworkInfo.isAvailable()) 
	    	{
	    		return mNetworkInfo.getType();
	    	}
	    	}
	    	return -1;
    }
    
    /**
     *  判断WIFi 是否可用
     */
    public boolean isWifiConnected(Context context) {
    	if (context != null) 
    	{
        	ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        	NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        	if (mWiFiNetworkInfo != null) 
        	{
        		return mWiFiNetworkInfo.isAvailable();
        	}
        }
        return false;
    }
   
    /**
     * 判断MOBILE网络是否可用
     */
    public boolean isMobileConnected(Context context) {
    	if (context != null) 
    	{
    		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    		NetworkInfo mMobileNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    	if (mMobileNetworkInfo != null) 
	    	{
	    		return mMobileNetworkInfo.isAvailable();
	    	}
    	}
    	return false;
    }
    
    /**
	 * 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网
	 *  Android 环境下： ping -c 1 -w 100 sina.cn
	   -c: 表示次数，1 为1次 -w: 表示deadline, time out的时间，单位为秒，10为10秒。
	   连起来的意思是，ping 主机sina.cn 一次，超时为10秒
	 */
//	public static  boolean ping() {
//        String result = null; 
//        try { 
//                String ip = "http://www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网 
//                Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping网址3次 
//                /**读取ping的内容，可以不加 
//                InputStream input = p.getInputStream(); 
//                BufferedReader in = new BufferedReader(new InputStreamReader(input)); 
//                StringBuffer stringBuffer = new StringBuffer(); 
//                String content = ""; 
//                while ((content = in.readLine()) != null) { 
//                        stringBuffer.append(content); 
//                } 
//                Log.d("------ping-----", "result content : " + stringBuffer.toString()); 
//                */
//                // ping的状态 
//                int status = p.waitFor(); 
//                if (status == 0) { 
//                        result = "success"; 
//                        return true; 
//                } else { 
//                        result = "failed"; 
//                } 
//        } catch (IOException e) { 
//                result = "IOException"; 
//        } catch (InterruptedException e) { 
//                result = "InterruptedException"; 
//        } finally { 
//                Log.d("----result---", "result = " + result); 
//        } 
//        return false;
//	}
    
    
    /**  
     * 获取屏幕宽高  
     * 获取的宽高都为屏幕的像素px
     * */
    //获取屏幕的宽度
    public static int getDisplayWidth(Activity activity){
    	int width = 0;
    	Display display= activity.getWindow().getWindowManager().getDefaultDisplay();
    	DisplayMetrics dm=new DisplayMetrics();
    	display.getMetrics(dm);  
    	width=dm.widthPixels;
    	return width;
    }
	//获取屏幕的宽度
	public static int getDisplayWidth(Context context){
		int width = 0;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		return width;
	}
    //获取屏幕的高度
    public static int getDisplayHeight(Activity activity){
    	int height = 0;
    	Display display= activity.getWindow().getWindowManager().getDefaultDisplay();
    	DisplayMetrics dm=new DisplayMetrics();
    	display.getMetrics(dm);  
    	height=dm.heightPixels;  
    	return height;
    }
	//获取屏幕的高度
	public static int getDisplayHeight(Context context){
		int height = 0;
		WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		height = dm.heightPixels;
		return height;
	}
    
    /**
     * 判断是否为网址格式（仅判断str内容是否匹配“http://或https://”）
     */
    public static boolean isUrl(String str){
        // 转换为小写  
        str = str.toLowerCase();  
        String[] regex = { "http://", "https://" };
        boolean isUrl = false;  
        for (int i = 0; i < regex.length; i++) {  
            isUrl = isUrl || (str.contains(regex[i])) && str.indexOf(regex[i]) == 0;  
        }  
        return isUrl;  
    }

	/**
	 * 获取当前程序的版本号
	 */
	public static String getVersionName(Context context) throws Exception {
		//获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		//getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
		return packInfo.versionName;
	}
}
