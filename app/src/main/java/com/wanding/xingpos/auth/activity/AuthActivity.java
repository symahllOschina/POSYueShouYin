package com.wanding.xingpos.auth.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.RandomStringGenerator;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

/**
 * 新大陆POS除预授权外其余动作操作界面
 * 同时和富友POS机预授权全部动作操作界面
 */
public class AuthActivity extends BaseActivity {

    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private String posProvider;

    /**
     * authType:1：预授权300000，2：预授权完成-330000，3：预授权撤销-400000，4：预授权完成撤销-440000
     */
    private String authType;
    /**
     * 签到登录商户信息
     */
    private UserLoginResData loginInitData;

    /**
     * 各支付通道不同的流水号
     */
    private String posOrderNoStr;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        posProvider = MainActivity.posProvider;
        Intent intent = getIntent();
        authType = intent.getStringExtra("authType");
        Log.e("操作区分码：",authType);
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        //预授权操作
        authRequst();

    }

    /**
     * 新大陆界面访问成功返回
     */
    private void newlandResult(Bundle bundle){
        String transamount = "";
        String msgTp = bundle.getString("msg_tp");
        if (TextUtils.equals(msgTp, "0210")) {
            String txndetail = bundle.getString("txndetail");
            Log.e("txndetail支付返回信息：", txndetail);
            try {
                JSONObject job = new JSONObject(txndetail);
                transamount = job.getString("transamount");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Gson gjson  =  GsonUtils.getGson();
            CardPaymentDate posResult = gjson.fromJson(txndetail, CardPaymentDate.class);

            savaOrderPrintText(posResult);

            Log.e("服务环境：", "生产环境");
            Log.e("返回的支付金额信息222：", transamount);

        }
    }

    /**
     * 富友界面访问成功返回
     */
    private void fuyouResult(Bundle bundle){
        String amountStr = bundle.getString("amount");//金额
        String traceNoStr = bundle.getString("traceNo");//凭证号
        String batchNoStr = bundle.getString("batchNo");//批次号
        String referenceNoStr = bundle.getString("referenceNo");//参考号
        String cardNoStr = bundle.getString("cardNo");//卡号
        String typeStr = bundle.getString("type");//卡类型
        String issueStr = bundle.getString("issue");//发卡行
        String dateStr = bundle.getString("date");//日期
        String timeStr = bundle.getString("time");//时间
        String authorizationCodeStr = bundle.getString("authorizationCode");//授权码
        String merchantldStr = bundle.getString("merchantld");//商户号
        String terminalldStr = bundle.getString("terminalld");//终端号
        String merchantNameStr = bundle.getString("merchantName");//商户名称

        Log.e("返回的支付金额信息：", amountStr);
        String totalStr = DecimalUtil.branchToElement(amountStr);

        //获取系统年份
        Calendar date = Calendar.getInstance();
        String year = String.valueOf(date.get(Calendar.YEAR));
        String dateTimeStr = year + dateStr +  timeStr;
        dateStr = year + dateStr;
        Log.e("手动拼接日期时间：",dateTimeStr);
        Log.e("手动拼接日期：",dateStr);

        CardPaymentDate posResult =  new CardPaymentDate();
        posResult.setPriaccount(cardNoStr);
        posResult.setAcqno("");
        posResult.setIisno(issueStr);
        posResult.setSystraceno(traceNoStr);
        posResult.setAuthcode(authorizationCodeStr);//授权码
        posResult.setRefernumber(referenceNoStr);
        posResult.setTranslocaldate(dateStr);
        posResult.setTranslocaltime(timeStr);
        posResult.setTransamount(totalStr);//此处金额是分转元之后的金额

        savaOrderPrintText(posResult);

    }

    /**
     * 保存打印信息(重打印数据)
     */
    private void savaOrderPrintText(CardPaymentDate posResult){
        /**
         * 下面是调用帮助类将一个对象以序列化的方式保存
         * 方便我们在其他界面调用，类似于Intent携带数据
         */
        try {
            MySerialize.saveObject("cardPayOrder",activity,MySerialize.serialize(posResult));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if("1".equals(authType)){


            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
            Boolean cardPayValue = true;
            int cardOption = 1;
            sharedPreferencesUtil.put("cardPayYes", cardPayValue);
            sharedPreferencesUtil.put("cardPayType", "pay");
            sharedPreferencesUtil.put("cardOption", cardOption);

            finish();

        }else if("2".equals(authType)){
            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
            Boolean cardPayValue = true;
            int cardOption = 2;
            sharedPreferencesUtil.put("cardPayYes", cardPayValue);
            sharedPreferencesUtil.put("cardPayType", "pay");
            sharedPreferencesUtil.put("cardOption", cardOption);
            finish();
        }else if("3".equals(authType)){
            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
            Boolean cardPayValue = true;
            int cardOption = 3;
            sharedPreferencesUtil.put("cardPayYes", cardPayValue);
            sharedPreferencesUtil.put("cardPayType", "pay");
            sharedPreferencesUtil.put("cardOption", cardOption);
            finish();
        }else if("4".equals(authType)){
            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
            Boolean cardPayValue = true;
            int cardOption = 4;
            sharedPreferencesUtil.put("cardPayYes", cardPayValue);
            sharedPreferencesUtil.put("cardPayType", "pay");
            sharedPreferencesUtil.put("cardOption", cardOption);
            finish();
        }else{
            finish();
        }
    }

    /**
     * 预授权发起
     */
    private void authRequst(){
        if(Constants.NEW_LAND.equals(posProvider)){
            String totalFeeStr = "";
            xingPosServicePay(authType, totalFeeStr);
        }else if(Constants.FUYOU_SF.equals(posProvider)){
            fuyouPosServicePay(authType);
        }
    }

    /**
     *  星POS 内置接口支付
     *  payToatl:支付金额
     */
    private void xingPosServicePay(String authType,String total_fee){
        String deviceNum = loginInitData.getTrmNo_pos();
        posOrderNoStr = RandomStringGenerator.getNlRandomNum(deviceNum);
        Log.e("生成的订单号：", posOrderNoStr);
        NewPosServiceUtil.authReq(activity, authType, total_fee, posOrderNoStr);

    }

    /**
     * 富友POS 内置支付
     */
    private void fuyouPosServicePay(String authType){
        FuyouPosServiceUtil.authReq(activity, authType);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Bundle bundle=data.getExtras();
            if (requestCode == NewPosServiceUtil.PAY_REQUEST_CODE) {
                if(bundle != null){
                    switch (resultCode) {
                        // 支付成功
                        case Activity.RESULT_OK:
                            if(Constants.NEW_LAND.equals(posProvider)){
                                newlandResult(bundle);
                            }if(Constants.FUYOU_SF.equals(posProvider)){

                            fuyouResult(bundle);

                        }
                            break;
                        // 支付取消
                        case Activity.RESULT_CANCELED:
                            String reason = "操作已取消";
                            reason = bundle.getString("reason");
                            Log.e("失败返回值", reason);
                            if (reason != null) {
                                Log.d("reason", reason);
                                ToastUtil.showText(activity,reason,1);
                            }

                            //关闭界面
                            finish();
                            break;

                        default:
                            break;

                    }
                }

            }


        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
