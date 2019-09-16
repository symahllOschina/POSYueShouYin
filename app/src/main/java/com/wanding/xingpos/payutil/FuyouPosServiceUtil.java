package com.wanding.xingpos.payutil;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * 调用富友POS内置服务接口，支付，查询业务类
 */
public class FuyouPosServiceUtil {

    /**
     * 签到请求码
     */
    public static final int SIGN_REQUEST_CODE = 110;
    /**
     * 支付请求码
     */
    public static final int PAY_REQUEST_CODE = 1;
    /**
     * 扫码请求码
     */
    public static final int SCAN_REQUEST_CODE = 11;
    /**
     * 打印请求码
     */
    public static final int PRINT_REQUEST_CODE = 99;

    /**
     * 签到
     */
    public static void signInReq(Activity activity){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "签到");
            intent.putExtras(bundle);
            Log.e("签到Bundle的值：",bundle.toString());
            activity.startActivityForResult(intent, SIGN_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码
     */
    public static void scanReq(Activity activity){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.NewSetScanCodeActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("flag", "true");
            intent.putExtras(bundle);
            Log.e("扫码Bundle的值：",bundle.toString());
            activity.startActivityForResult(intent, SCAN_REQUEST_CODE);


        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 消费，微信，支付宝，银联二维码支付
     *
     */
    public static void payReq(Activity activity, String payType, String total_fee, String order_noStr, boolean isFrontCamera){
        //String payType = "";//银行卡，微信，支付宝，银联二维码分别顺序对应：040,010,020,030
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if("040".equals(payType)){
                bundle.putString("transName", "消费");
            }else if("010".equals(payType)){
                bundle.putString("transName", "微信消费");
            }else if("020".equals(payType)){
                bundle.putString("transName", "支付宝消费");
            }else if("060".equals(payType)){
                bundle.putString("transName", "银联二维码消费");
            }
            bundle.putString("amount", total_fee);
//            bundle.putString("amount", "000000000001");
            bundle.putString("orderNumber", order_noStr);
            if(!"040".equals(payType)){
                bundle.putString("isPrintTicket", "true");//为true时调用打印；为false时不调用打印
                if (isFrontCamera) {
                    bundle.putString("isFrontCamera", "false");//是否打开前置摄像头(传true时，打开前置。传false不打开前置)
                }else{
                    bundle.putString("isFrontCamera", "true");//是否打开前置摄像头(传true时，打开前置。传false不打开前置)
                }
            }
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            Log.e("支付SDK参数值：",bundle.toString());
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码退款（只支持凭证号退款，并且需区分退款类型）
     */
    public static void refundReq(Activity activity, String payType, String total_fee, String oldTrace, String order_noStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if("WX".equals(payType)){
                bundle.putString("transName", "微信退款");
            }else if("ALI".equals(payType)){
                bundle.putString("transName", "支付宝退款");
            }else if("UNIONPAY".equals(payType)){
                bundle.putString("transName", "银联二维码退款");
            }
            bundle.putString("amount", total_fee);
            bundle.putString("oldTrace", oldTrace);
            bundle.putString("orderNumber", order_noStr);
            bundle.putString("isPrintTicket", "true");//为true时调用打印；为false时不调用打印
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            Log.e("退款SDK参数值：",bundle.toString());
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 银行卡消费撤销
     */
    public static void cardRefundReq(Activity activity, String oldTrace, String order_noStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "消费撤销");
            bundle.putString("amount", "");
            bundle.putString("oldTrace", oldTrace);//凭证号
            bundle.putString("isManagePwd", "false");//撤销时不显示输入密码(false-不显示输入密码，true-显示输入密码)
            bundle.putString("orderNumber", order_noStr);
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码查询（不支持补打印）
     */
    public static void scanQueryReq(Activity activity, String orderIdStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "订单号查询");
//            bundle.putString("orderNumber", orderIdStr);//订单号	商户定义的订单号
            bundle.putString("orderNumber", "FU2018081410091602561271243221");//订单号	商户定义的订单号
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("NotFoundException：", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 扫码查询（支持补打印：但仅支持正向交易）
     * 该接口支持银行卡、微信、支付宝消费交易联机查询，适用于当前查询终端本地没有结果时调用此接口查询服务端，补打凭条。撤销交易该接口暂不支持查询。
     */
    public static void scanQueryOrderReq(Activity activity, String payType, String oldTrace, String orderIdStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if("WX".equals(payType)){
                bundle.putString("transName", "微信失败查询");
            }else if("ALI".equals(payType)){
                bundle.putString("transName", "支付宝失败查询");
            }else if("UNIONPAY".equals(payType)){
                bundle.putString("transName", "银联二维码消费失败交易查询");
            }else if("BANK".equals(payType)){
                bundle.putString("transName", "消费失败查询");
            }
            bundle.putString("oldTrace", oldTrace);
            bundle.putString("orderNumber", orderIdStr);//订单号	商户定义的订单号
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("NotFoundException：", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 预授权，预授权撤销，预授权完成，预授权完成撤销
     */
    public static void authReq(Activity activity, String type){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            if("1".equals(type)){
                bundle.putString("transName", "预授权");
            }else if("2".equals(type)){
                bundle.putString("transName", "预授权撤销");
            }else if("3".equals(type)){
                bundle.putString("transName", "预授权完成（请求）");
            }else if("4".equals(type)){
                bundle.putString("transName", "预授权完成撤销");
            }
            bundle.putString("version", "1.0.7");
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    /**
     * 结算（清空POS流水）
     */
    public static void settleReq(Activity activity){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.MainActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("transName", "微信银行卡结算");
            bundle.putString("isPrintSettleTicket", "false");
            bundle.putString("version", "1.0.7");
            Log.e("调结算Bundle的值：",bundle.toString());
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PAY_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }

    public static void printTextReq(Activity activity,String printTextStr){
        try {
            ComponentName component = new ComponentName("com.fuyousf.android.fuious", "com.fuyousf.android.fuious.CustomPrinterActivity");
            Intent intent = new Intent();
            intent.setComponent(component);
            Bundle bundle = new Bundle();
            bundle.putString("data", printTextStr);
            bundle.putString("isPrintTicket", "true");
            Log.e("调打印Bundle的值：",bundle.toString());
            intent.putExtras(bundle);
            activity.startActivityForResult(intent, PRINT_REQUEST_CODE);
        } catch(ActivityNotFoundException e) {
            //TODO:
            Log.e("Fouyou_Exception", "找不到界面");
        } catch(Exception e) {
            //TODO:
            Log.e("Exception：", "异常");
        }
    }
}
