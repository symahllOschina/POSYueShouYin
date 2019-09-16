package com.wanding.xingpos.auth.util;

import com.alibaba.fastjson.JSON;
import com.wanding.xingpos.auth.bean.PreLicensingCancelReq;
import com.wanding.xingpos.auth.bean.PreLicensingPreReq;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.utils.DateFormatUtils;
import com.wanding.xingpos.utils.RandomStringGenerator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 预授权请求参数封装公共类
 */
public class AuthReqUtil {

    private static String pay_ver = "100";
    //terminal_time: 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
//    public static String terminal_time = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
    /**
     * 预授权
     */
    public static String authReq(String authNo, String payType, String totalFee, UserLoginResData loginInitData) throws Exception {

        PreLicensingPreReq req = new PreLicensingPreReq();
        String payVerStr = pay_ver;
        String payTypeStr = payType;
        String merchantNoStr = loginInitData.getMerchant_no();//商户号
        String terminalIdStr = loginInitData.getTerminal_id();//终端号
        //商户订单号,32个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String terminalTraceStr = RandomStringGenerator.getAFRandomNum();
        String terminalTimeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());//终端交易时间，yyyyMMddHHmmss，全局统一时间格式
        String authNoStr = authNo;
        String totalFeeStr = totalFee;//金额（单位分）
        String orderBodyStr = "预授权";//业务类型
        String signTypeStr = "MD5";//加密方式，默认MD5

        req.setPay_ver(payVerStr);
        req.setPay_type(payTypeStr);
        req.setMerchant_no(merchantNoStr);
        req.setTerminal_id(terminalIdStr);
        req.setTerminal_trace(terminalTraceStr);
        req.setTerminal_time(terminalTimeStr);
        req.setAuth_no(authNoStr);
        req.setTotal_fee(totalFeeStr);
        req.setSign_type(signTypeStr);

        //将对象封装为Map
        Map<String, String> map = getAuthMap(req);

        //开始加签
        String signStr = SignUtils.md5Sign(map,loginInitData.getAccess_token(),"UTF-8");

        //加签后赋值
        req.setKey_sign(signStr);

        //将对象转换为最终的请求参数json字符串
        String jsonStr = JSON.toJSONString(req);

        return jsonStr;
    }

    /**
     *  预授权撤销
     */
    public static String authCancelReq(String authNo, String finishAmt, UserLoginResData loginInitData) throws Exception {
        String jsonStr = "";
        PreLicensingCancelReq req = new PreLicensingCancelReq();
        String payVerStr = pay_ver;
        String payTypeStr = "";
        String merchantNoStr = loginInitData.getMerchant_no();//商户号
        String terminalIdStr = loginInitData.getTerminal_id();//终端号
        //商户订单号,32个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String terminalTraceStr = RandomStringGenerator.getAFRandomNum();
        String terminalTimeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());//终端交易时间，yyyyMMddHHmmss，全局统一时间格式
        String authNoStr = authNo;
        String finishAmtStr = finishAmt;//金额（单位分）
        String signTypeStr = "MD5";//加密方式，默认MD5

        req.setPay_ver(payVerStr);
        req.setPay_type(payTypeStr);
        req.setMerchant_no(merchantNoStr);
        req.setTerminal_id(terminalIdStr);
        req.setTerminal_trace(terminalTraceStr);
        req.setTerminal_time(terminalTimeStr);
        req.setCustom_auth_code(authNoStr);
        req.setFinish_amt(finishAmtStr);
        req.setSign_type(signTypeStr);

        //将对象封装为Map
        Map<String, String> map = getAuthCancelMap(req);

        //开始加签
        String signStr = SignUtils.md5Sign(map,loginInitData.getAccess_token(),"UTF-8");

        //加签后赋值
        req.setKey_sign(signStr);

        //将对象转换为最终的请求参数json字符串
        return JSON.toJSONString(req);
    }

    /**
     *  预授权完成
     */
    public static String authConfirmReq(String authNo, String finishAmt, UserLoginResData loginInitData) throws Exception {
        String jsonStr = "";
        PreLicensingCancelReq req = new PreLicensingCancelReq();
        String payVerStr = pay_ver;
        String payTypeStr = "";
        String merchantNoStr = loginInitData.getMerchant_no();//商户号
        String terminalIdStr = loginInitData.getTerminal_id();//终端号
        //商户订单号,32个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
        String terminalTraceStr = RandomStringGenerator.getAFRandomNum();
        String terminalTimeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());//终端交易时间，yyyyMMddHHmmss，全局统一时间格式
        String authNoStr = authNo;
        String finishAmtStr = finishAmt;//金额（单位分）
        String signTypeStr = "MD5";//加密方式，默认MD5

        req.setPay_ver(payVerStr);
        req.setPay_type(payTypeStr);
        req.setMerchant_no(merchantNoStr);
        req.setTerminal_id(terminalIdStr);
        req.setTerminal_trace(terminalTraceStr);
        req.setTerminal_time(terminalTimeStr);
        req.setCustom_auth_code(authNoStr);
        req.setFinish_amt(finishAmtStr);
        req.setSign_type(signTypeStr);

        //将对象封装为Map
        Map<String, String> map = getAuthConfirmMap(req);

        //开始加签
        String signStr = SignUtils.md5Sign(map,loginInitData.getAccess_token(),"UTF-8");

        //加签后赋值
        req.setKey_sign(signStr);

        //将对象转换为最终的请求参数json字符串
        return JSON.toJSONString(req);
    }

    /**
     * 对象转Map
     */
    private static Map<String,String> getAuthMap(PreLicensingPreReq req){
        Map<String,String> map = new HashMap<String,String>();
        map.put("pay_ver",req.getPay_ver());
        map.put("pay_type",req.getPay_type());
        map.put("merchant_no",req.getMerchant_no());
        map.put("terminal_id",req.getTerminal_id());
        map.put("terminal_trace",req.getTerminal_trace());
        map.put("terminal_time",req.getTerminal_time());
        map.put("auth_no",req.getAuth_no());
        map.put("total_fee",req.getTotal_fee());
        map.put("order_body",req.getOrder_body());
        map.put("sign_type",req.getSign_type());

        return map;
    }

    private static Map<String,String> getAuthCancelMap(PreLicensingCancelReq req){
        Map<String,String> map = new HashMap<String,String>();
        map.put("pay_ver",req.getPay_ver());
        map.put("pay_type",req.getPay_type());
        map.put("merchant_no",req.getMerchant_no());
        map.put("terminal_id",req.getTerminal_id());
        map.put("terminal_trace",req.getTerminal_trace());
        map.put("terminal_time",req.getTerminal_time());
        map.put("auth_no",req.getCustom_auth_code());
        map.put("total_fee",req.getFinish_amt());
        map.put("order_body",req.getOrder_body());
        map.put("sign_type",req.getSign_type());

        return map;
    }

    private static Map<String,String> getAuthConfirmMap(PreLicensingCancelReq req){
        Map<String,String> map = new HashMap<String,String>();
        map.put("pay_ver",req.getPay_ver());
        map.put("pay_type",req.getPay_type());
        map.put("merchant_no",req.getMerchant_no());
        map.put("terminal_id",req.getTerminal_id());
        map.put("terminal_trace",req.getTerminal_trace());
        map.put("terminal_time",req.getTerminal_time());
        map.put("auth_no",req.getCustom_auth_code());
        map.put("total_fee",req.getFinish_amt());
        map.put("order_body",req.getOrder_body());
        map.put("sign_type",req.getSign_type());

        return map;
    }

}
