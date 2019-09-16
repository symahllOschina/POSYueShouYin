package com.wanding.xingpos.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.MainBaseActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.MainHandlerConstant;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.PosScanpayReqData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.ScanPaymentDate;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.payutil.FieldTypeUtil;
import com.wanding.xingpos.payutil.FuyouPosServiceUtil;
import com.wanding.xingpos.payutil.NewPosServiceUtil;
import com.wanding.xingpos.payutil.PayRequestUtil;
import com.wanding.xingpos.payutil.PayType;
import com.wanding.xingpos.payutil.PayUtils;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.RandomStringGenerator;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.Calendar;

import static com.wanding.xingpos.Constants.FUYOU_SF;

/**
 * 悦收银 主界面
 * https://www.cnblogs.com/wjtaigwh/p/6210534.html
 */
@ContentView(R.layout.activity_cashier)
public class CashierActivity extends MainBaseActivity implements View.OnClickListener,MainHandlerConstant{

    @ViewInject(R.id.menu_title_imageView)
    ImageView imgBack;
    @ViewInject(R.id.menu_title_layout)
    LinearLayout titleLayout;
    @ViewInject(R.id.menu_title_tvTitle)
    TextView tvTitle;
    @ViewInject(R.id.menu_title_imgTitleImg)
    ImageView imgTitleImg;
    @ViewInject(R.id.menu_title_tvOption)
    TextView tvOption;


    /**
     * 底部支付类型
     * 刷卡，微信，支付宝，银联，翼支付,会员
     */
    @ViewInject(R.id.pay_type_cardPayLayout)
    LinearLayout cardPayLayout;
    @ViewInject(R.id.pay_type_wxPayLayout)
    LinearLayout wxPayLayout;
    @ViewInject(R.id.pay_type_aliPayLayout)
    LinearLayout aliPayLayout;
    @ViewInject(R.id.pay_type_bestPayLayout)
    LinearLayout bestPayLayout;
    @ViewInject(R.id.pay_type_unionPayLayout)
    LinearLayout unionPayLayout;
    @ViewInject(R.id.pay_type_memberPayLayout)
    LinearLayout memberPayLayout;

    /**
     * payType:分别顺序对应：040,010,020,030,050
     */
    private String payType = "";
    /**
     * 支付金额(转换后的有效金额)
     */
    private String total_feeStr = null;

    /**
     * 各支付通道不同的流水号
     */
    private String pos_order_noStr;


    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    public static String posProvider;

    /** 银联二维码，支付宝微信支付通道识别码 */
    private boolean wxPayServiceType = true;
    private boolean aliPayServiceType = true;
    private boolean ylPayServiceType = true;
    /** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
    private String printNum = "printNumNo";
    /**  打印字体大小 isDefault:true默认大小，false即为大字体 */
    private boolean isDefault = true;
    /**  cameraType为true表示打开后置摄像头，fasle为前置摄像头 */
    private boolean cameType = true;

    /**
     * POS初始化信息
     */
    private UserLoginResData loginInitData;


    // 主控制类，所有合成控制方法从这个类开始
    public MySyntherizer synthesizer;

    /**
     * 新大陆打印，摄像头参数
     */
    AidlDeviceService aidlDeviceService = null;
    AidlPrinter aidlPrinter = null;
    AidlScanner aidlScanner=null;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "bind device service");
            aidlDeviceService = AidlDeviceService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "unbind device service");
            aidlDeviceService = null;
        }
    };

    /**
     * 富友(打印机)AIDL服务
     */
    PrintInterface printService = null;
    PrintReceiver printReceiver;
    ServiceConnection printServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            printService = PrintInterface.Stub.asInterface(arg1);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }

    };

    class PrintReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra("result");
//			etBack.setText("reason："+result);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("收款");
        synthesizer = MainActivity.synthesizer;
        posProvider = MainActivity.posProvider;Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initListener();


        if(posProvider.equals(Constants.NEW_LAND)){
            //绑定打印机服务
            bindServiceConnection();
        }else if(posProvider.equals(Constants.FUYOU_SF)){
            initPrintService();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //清空StringBuilder，EditText恢复初始值
        //清空EditText
        pending.delete( 0, pending.length() );
        if(pending.length()<=0){
            etSumMoney.setText("￥0.00");
        }
        Log.e(TAG,"onResume()方法被调用....");
        //初始化数据
        initData();


    }


    /**
     * Activity关闭时清空资源，关闭服务（包括打印机服务，扫码服务）
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(posProvider.equals(Constants.NEW_LAND)){
            unbindService(serviceConnection);
            aidlPrinter=null;
            aidlScanner = null;
        }else if(posProvider.equals(Constants.FUYOU_SF)){
            if(null != printReceiver){
                unregisterReceiver(printReceiver);
            }
            unbindService(printServiceConnection);
        }
        Log.e(TAG, "释放资源成功");
    }

    /**
     * 注册所有按钮监听
     */
    private void initListener(){


        imgBack.setOnClickListener(this);


        cardPayLayout.setOnClickListener(this);
        wxPayLayout.setOnClickListener(this);
        aliPayLayout.setOnClickListener(this);
        bestPayLayout.setOnClickListener(this);
        unionPayLayout.setOnClickListener(this);
        memberPayLayout.setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData(){


        //transaction ：交易设置值存储应用本地的文件名称
        SharedPreferencesUtil sharedPreferencesUtil_transaction = new SharedPreferencesUtil(this, "transaction");
        //取出保存的值
        wxPayServiceType = (Boolean) sharedPreferencesUtil_transaction.getSharedPreference("wxPayServiceKey", true);
        aliPayServiceType = (Boolean) sharedPreferencesUtil_transaction.getSharedPreference("aliPayServiceKey", true);
        ylPayServiceType = (Boolean) sharedPreferencesUtil_transaction.getSharedPreference("ylPayServiceKey", true);
        //取出保存的默认支付金额
        //defMoneyNum ：交易设置值存储应用本地的文件名称
        SharedPreferencesUtil sharedPreferencesUtil1 = new SharedPreferencesUtil(this, "defMoneyNum");
        //取出保存的默认值
        String defMoney = (String) sharedPreferencesUtil1.getSharedPreference("defMoneyKey", "");
        if("".equals(defMoney) || "0".equals(defMoney)){
            etSumMoney.setHint("￥0.00");
        }else{
            etSumMoney.setText("￥"+defMoney);
            pending.append(defMoney);
        }

        //取出设置的打印值
        SharedPreferencesUtil sharedPreferencesUtil2 = new SharedPreferencesUtil(this, "printing");
        //取出保存的默认值
        printNum = (String) sharedPreferencesUtil2.getSharedPreference("printNumKey", printNum);
        isDefault = (boolean) sharedPreferencesUtil2.getSharedPreference("isDefaultKey", isDefault);
        Log.e("取出保存的打印值", printNum);
        //取出保存的摄像头参数值
        SharedPreferencesUtil sharedPreferencesUtil3 = new SharedPreferencesUtil(this, "scancamera");
        cameType = (Boolean) sharedPreferencesUtil3.getSharedPreference("cameTypeKey", cameType);
        if(cameType){
            Log.e("当前摄像头打开的是：", "后置");
        }else{
            Log.e("当前摄像头打开的是：", "前置");
        }

    }
    /**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String total) {
//        String text = mInput.getText().toString();
//        // 需要合成的文本text的长度不能超过1024个GBK字节。
//        if (TextUtils.isEmpty(mInput.getText())) {
//            text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
//            mInput.setText(text);
//        }
//        String text = "百度语音，面向广大开发者永久免费开放语音合成技术。";
        //刷卡，微信，支付宝，银联，翼支付，分别顺序对应：040,010,020,030,050
        String payTypeStr = "";
        if(Constants.PAYTYPE_040BANK.equals(payType)){
            payTypeStr = "银行卡收款";
        }else if(Constants.PAYTYPE_010WX.equals(payType)){
            payTypeStr = "微信收款";
        }else if(Constants.PAYTYPE_020ALI.equals(payType)){
            payTypeStr = "支付宝收款";
        }else if(Constants.PAYTYPE_060UNIONPAY.equals(payType)){
            payTypeStr = "银联二维码收款";
        }else if(Constants.PAYTYPE_050BEST.equals(payType)){
            payTypeStr = "翼支付收款";
        }

        String text = payTypeStr+total+"元";
        // 合成前可以修改参数：
        // Map<String, String> params = getParams();
        // synthesizer.setParams(params);
        int result = synthesizer.speak(text);
        checkResult(result, "speak");
    }

    private void checkResult(int result, String method) {
        if (result != 0) {
//            toPrint("error code :" + result + " method:" + method + ", 错误码文档:http://yuyin.baidu.com/docs/tts/122 ");
            Log.e("error code :", result+" method:" + method );
        }
    }


    /**
     * 绑定服务
     */
    public void bindServiceConnection() {
        bindService(new Intent("nld_cloudpos_device_service"), serviceConnection,
                Context.BIND_AUTO_CREATE);
//        showMsgOnTextView("服务绑定");
        Log.e("绑定服务", "绑定服务1");
    }

    private void initPrintService(){
        printRegisterReceiver();
        Intent printIntent = new Intent(/*"com.fuyousf.android.fuious.service.PrintInterface"*/);
        printIntent.setAction("com.fuyousf.android.fuious.service.PrintInterface");
        printIntent.setPackage("com.fuyousf.android.fuious");
        bindService(printIntent, printServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void printRegisterReceiver(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.fuyousf.android.fuious.service.print");
        printReceiver = new PrintReceiver();
        registerReceiver(printReceiver, intentFilter);
    }
    /**
     * 初始化打印设备
     */
    public void getPrinter() {
        Log.i(TAG, "获取打印机设备实例...");
        try {
            aidlPrinter = AidlPrinter.Stub.asInterface(aidlDeviceService.getPrinter());
//            showMsgOnTextView("初始化打印机实例");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化扫码设备
     */
    public void initScanner() {
        try {
            if (aidlScanner == null)
            {
                aidlScanner = AidlScanner.Stub.asInterface(aidlDeviceService.getScanner());
            }

//            showMsgOnTextView("初始化打扫码实例");
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    /**
     * 前置扫码
     */
    public void frontscan(){
        try {
            Log.i(TAG, "-------------scan-----------");
            aidlScanner= AidlScanner
                    .Stub.asInterface(aidlDeviceService.getScanner());
            int time=10;//超时时间
            aidlScanner.startScan(ScanConstant.ScanType.FRONT, time, new AidlScannerListener.Stub() {

                @Override
                public void onScanResult(String[] arg0) throws RemoteException {
//                    showMsgOnTextView("onScanResult-----"+arg0[0]);
                    Log.e(TAG,"onScanResult-----"+arg0[0]);
                    String scanCodeStr = arg0[0];
                    //如果扫描的二维码为空则不执行支付请求
                    if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
                        //auth_no	授权码（及扫描二维码值）
                        String auth_no = scanCodeStr;
                        Log.e("前置扫码值：", auth_no);
                        int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
                        String text = auth_no;
                        sendMessage(msg,text);

                    }else{
                        //清空StringBuilder，EditText恢复初始值
                        //清空EditText
                        pending.delete( 0, pending.length() );
                        if(pending.length()<=0){
                            etSumMoney.setText("￥0.00");
                        }

                        ToastUtil.showText(activity,"扫码失败！",1);
                    }

                }

                @Override
                public void onFinish() throws RemoteException {
                    ToastUtil.showText(activity,"扫码失败！",1);

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
                    ToastUtil.showText(activity,"扫码失败！",1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showText(activity,"扫码失败！",1);
        }
    }
    /**
     * 后置扫码
     */
    public void backscan(){
        try {
            Log.i(TAG, "-------------scan-----------");
            aidlScanner= AidlScanner
                    .Stub.asInterface(aidlDeviceService.getScanner());
            aidlScanner.startScan(ScanConstant.ScanType.BACK, 10, new AidlScannerListener.Stub() {

                @Override
                public void onScanResult(String[] arg0) throws RemoteException {
//					showMsgOnTextView("onScanResult-----"+arg0[0]);
                    Log.w(TAG,"onScanResult-----"+arg0[0]);
                    String scanCodeStr = arg0[0];
                    //如果扫描的二维码为空则不执行支付请求
                    if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
                        //auth_no	授权码（及扫描二维码值）
                        String auth_no = scanCodeStr;
                        Log.e("后置扫码值：", auth_no);

                        int msg = NetworkUtils.MSG_WHAT_ONEHUNDRED;
                        String text = auth_no;
                        sendMessage(msg,text);

                    }else{
                        //清空StringBuilder，EditText恢复初始值
                        //清空EditText
                        pending.delete( 0, pending.length() );
                        if(pending.length()<=0){
                            etSumMoney.setText("￥0.00");
                        }
                        Log.e("后置扫码值：", "为空");
                    }

                }

                @Override
                public void onFinish() throws RemoteException {
                    ToastUtil.showText(activity,"扫码失败！",1);

                }

                @Override
                public void onError(int arg0, String arg1) throws RemoteException {
                    ToastUtil.showText(activity,"扫码失败！",1);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.showText(activity,"扫码失败！",1);
        }
    }





    /**
     * 发起支付第一步
     */
    private void payMethodOne(){
        try {
            String etTotal_fee = pending.toString();
            Log.e("输入框金额值：", etTotal_fee);
            if(Utils.isEmpty(etTotal_fee)){
                ToastUtil.showText(activity,"请输入有效金额！",1);
                return;
            }
            total_feeStr =  DecimalUtil.StringToPrice(etTotal_fee);
            Log.e("金额值转换后：", etTotal_fee);
            //金额是否合法
            int isCorrect = DecimalUtil.isEqual(total_feeStr);
            if(isCorrect != 1){
                ToastUtil.showText(activity,"请输入有效金额！",1);
                return;
            }
            payMethodTwo();


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }
    }
    /**
     * 发起支付第二步
     */
    private void payMethodTwo(){
        if(Constants.PAYTYPE_010WX.equals(payType)){

            Log.e("支付类型：", "微信支付！");
            /** 根据支付通道进行请求服务  */
            if(wxPayServiceType){
                Log.e("支付通道：", "默认");
                if(posProvider.equals(Constants.NEW_LAND)){
                    initScanner();
                    if(cameType){
                        Log.e("扫码调用：", "后置摄像头");
                        backscan();
                    }else{
                        Log.e("扫码调用：", "前置摄像头");
                        frontscan();
                    }
                }else if(posProvider.equals(Constants.FUYOU_SF)){

                    FuyouPosServiceUtil.scanReq(activity);
                }
            }else
            {
                if(posProvider.equals(Constants.NEW_LAND)){
                    Log.e("支付通道：", "星POS");
                    XingPosServicePay(total_feeStr);
                }else if(posProvider.equals(Constants.FUYOU_SF)){
                    Log.e("支付通道：", "富友POS");
                    FuyouPosServicePay(total_feeStr);
                }
            }
        }else if(Constants.PAYTYPE_020ALI.equals(payType)){
            Log.e("支付类型：", "支付宝支付！");
            /** 根据支付通道进行请求服务  */
            if(aliPayServiceType){
                Log.e("支付通道：", "默认");
                if(posProvider.equals(Constants.NEW_LAND)){
                    initScanner();
                    if(cameType){
                        Log.e("扫码调用：", "后置摄像头");
                        backscan();
                    }else{
                        Log.e("扫码调用：", "前置摄像头");
                        frontscan();
                    }
                }else if(posProvider.equals(Constants.FUYOU_SF)){

                    FuyouPosServiceUtil.scanReq(activity);
                }
            }else
            {
                if(posProvider.equals(Constants.NEW_LAND)){
                    Log.e("支付通道：", "星POS");
                    XingPosServicePay(total_feeStr);
                }else if(posProvider.equals(Constants.FUYOU_SF)){
                    Log.e("支付通道：", "富友POS");
                    FuyouPosServicePay(total_feeStr);
                }
            }
        }else if(Constants.PAYTYPE_050BEST.equals(payType)){
            if(posProvider.equals(Constants.NEW_LAND)){
                initScanner();
                if(cameType){
                    Log.e("扫码调用：", "后置摄像头");
                    backscan();
                }else{
                    Log.e("扫码调用：", "前置摄像头");
                    frontscan();
                }
            }else if(posProvider.equals(Constants.FUYOU_SF)){

                FuyouPosServiceUtil.scanReq(activity);
            }
        }else if(Constants.PAYTYPE_040BANK.equals(payType)){
            if(posProvider.equals(Constants.NEW_LAND)){
                Log.e("支付通道：", "星POS");
                XingPosServicePay(total_feeStr);
            }else if(posProvider.equals(Constants.FUYOU_SF)){
                Log.e("支付通道：", "富友POS");
                FuyouPosServicePay(total_feeStr);
            }
        }else if(Constants.PAYTYPE_060UNIONPAY.equals(payType)){
            Log.e("支付类型：", "银联二维码！");
            /** 根据支付通道进行请求服务  */
            if(ylPayServiceType){
                Log.e("支付通道：", "默认");
                if(posProvider.equals(Constants.NEW_LAND)){
                    initScanner();
                    if(cameType){
                        Log.e("扫码调用：", "后置摄像头");
                        backscan();
                    }else{
                        Log.e("扫码调用：", "前置摄像头");
                        frontscan();
                    }
                }else if(posProvider.equals(Constants.FUYOU_SF)){
                    FuyouPosServiceUtil.scanReq(activity);
                }
            }else{
                Log.e("支付通道：", "第三方");
                if(posProvider.equals(Constants.NEW_LAND)){
                    Log.e("支付通道：", "星POS");
                    XingPosServicePay(total_feeStr);
                }else if(posProvider.equals(Constants.FUYOU_SF)){
                    Log.e("支付通道：", "富友POS");
                    FuyouPosServicePay(total_feeStr);
                }
            }
        }else{
            ToastUtil.showText(activity,"请重新选择支付方式！",1);
        }
    }

    /**
     * 支付第三步（API接口支付）
     */
    private void payMethodThree(String auth_no, String total_fee){
        if(Utils.isNotEmpty(auth_no)){
            PosScanpayReqData posBean = PayRequestUtil.payReq(payType, auth_no, total_fee,loginInitData,posProvider);
            payRequestMethood(posBean);
        }else{
            ToastUtil.showText(activity,"扫码结果返回为空！",1);
        }


    }




    /**
     * 发起支付第四步（API发起请求）
     */
    private void payRequestMethood(final PosScanpayReqData posBean){
        showWaitDialog();
        //付款二维码内容(发起支付请求)
        final String url = NitConfig.barcodepayUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("pay_ver",posBean.getPay_ver());
                    userJSON.put("pay_type",posBean.getPay_type());
                    userJSON.put("service_id",posBean.getService_id());
                    userJSON.put("merchant_no",posBean.getMerchant_no());
                    userJSON.put("terminal_id",posBean.getTerminal_id());
                    userJSON.put("terminal_trace",posBean.getTerminal_trace());
                    userJSON.put("terminal_time",posBean.getTerminal_time());
                    userJSON.put("auth_no",posBean.getAuth_no());
                    userJSON.put("total_fee",posBean.getTotal_fee());
                    userJSON.put("order_body",posBean.getOrder_body());
                    userJSON.put("key_sign",posBean.getKey_sign());

                    String content = String.valueOf(userJSON);
                    Log.e("扫码支付发起请求参数：", content);
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("扫码支付返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_TWO;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
    }


    /**
     * 测试环境保存订单到服务器
     * 入参：goodsPrice = 金额，transactionId = 支付返回的UD或9开头的 ，refundCode = order_no(自己生成的订单号)
     */
    private void saveOrderNoToService(final String orderid_scan){
        showWaitDialog();
        final String url = NitConfig.insertChannelIdTestUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("goodsPrice","0.01");
                    userJSON.put("transactionId",orderid_scan);
                    userJSON.put("refundCode",pos_order_noStr);

                    String content = String.valueOf(userJSON);
                    Log.e("保存测试数据发起请求参数：", content);

                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("扫码支付返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_THREE;
                    String text = jsonStr;
                    sendMessage(msg,text);
                } catch (JSONException e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
                }catch (IOException e){
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
                } catch (Exception e) {
                    e.printStackTrace();
                    sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
                }
            }
        }.start();
    }

    private void sendMessage(int what,String text){
        Message msg = new Message();
        msg.what = what;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            String errorJsonText = "";
            switch (msg.what){
                case NetworkUtils.MSG_WHAT_ONEHUNDRED://新大陆扫码返回
                    String auth_no = (String) msg.obj;
                    //total_fee	金额，单位分(金额需乘以100)
                    String total_fee = DecimalUtil.elementToBranch(total_feeStr);
                    payMethodThree(auth_no, total_fee);
                    break;
                case NetworkUtils.MSG_WHAT_TWO:
                    String scanPayJsonStr=(String) msg.obj;
                    scanPayJsonStr(scanPayJsonStr);
                    hideWaitDialog();
                    break;
                case NetworkUtils.MSG_WHAT_THREE:
                    String saveTestJsonStr=(String) msg.obj;
                    //{"isSuccess":true,"errorCode":null,"errorMessage":null,"data":"UD180413A01175300610011747021785"}
                    intentToActivity();
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    break;
                    default:
                        break;
            }
        }
    };



    private void scanPayJsonStr(String jsonStr){
        Gson gjson  =  GsonUtils.getGson();
        PosScanpayResData posResult = gjson.fromJson(jsonStr, PosScanpayResData.class);
        //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        String return_codeStr = posResult.getReturn_code();
        //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        String return_msgStr = posResult.getReturn_msg();
        //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        String result_codeStr = posResult.getResult_code();
        synchronized (CashierActivity.class){
            if("01".equals(return_codeStr)) {
                if("01".equals(result_codeStr)){

                    /**
                     * 下面是调用帮助类将一个对象以序列化的方式保存
                     * 方便我们在其他界面调用，类似于Intent携带数据
                     */
                    try {
                        MySerialize.saveObject("scanPayOrder",getApplicationContext(),MySerialize.serialize(posResult));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    //本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
                    SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "scanPay");
                    Boolean scanPayValue = true;
                    Boolean payServiceType = true;
                    String scanPayTypeValue = "pay";//pay:支付，refund:退款
                    sharedPreferencesUtil.put("scanPayYes", scanPayValue);
                    sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
                    sharedPreferencesUtil.put("payServiceType", payServiceType);

                    //打印小票
                    startPrint(posResult);

                }else if("03".equals(result_codeStr)){
                    Toast.makeText(activity, "支付中!", Toast.LENGTH_LONG).show();
                }else{
                    intentActivity();
                    Toast.makeText(activity, "支付失败!", Toast.LENGTH_LONG).show();
                }
            }else{
                intentActivity();
                Toast.makeText(activity, return_msgStr+"!", Toast.LENGTH_LONG).show();
            }
        }

        //清空EditText
        pending.delete( 0, pending.length() );
        if(pending.length()<=0){
            etSumMoney.setText("￥0.00");
        }
    }

    /** 获取POS机本身的相关信息，获取应用初始化数据 */
    private void appDataInstance(){

        if(Utils.isNotEmpty(posProvider)){
            try {
                if(posProvider.equals(Constants.NEW_LAND)){

                    NewPosServiceUtil.signInReq(activity);

                }else if(posProvider.equals(Constants.FUYOU_SF)){

                    FuyouPosServiceUtil.signInReq(activity);

                }
            } catch(ActivityNotFoundException e) {
                //TODO:
                Log.e("Newland_Exception", "找不到界面");
            } catch(Exception e) {
                //TODO:
                Log.e("Exception：", "异常");
            }
        }else{
            ToastUtil.showText(activity,"POS机初始化参数失败！",1);
        }

    }




    /**
     * 新大陆界面访问成功返回
     */
    private void newlandScanPayResult(Bundle bundle){
        String orderid_scan = "";
        String transamount = "";
        String msgTp = bundle.getString("msg_tp");
        if (TextUtils.equals(msgTp, "0210")) {
            String txndetail = bundle.getString("txndetail");
            Log.e("txndetail支付返回信息：", txndetail);
            try {
                JSONObject job = new JSONObject(txndetail);
                if(!Constants.PAYTYPE_040BANK.equals(payType)){
                    orderid_scan = job.getString("orderid_scan");
                }
                transamount = job.getString("transamount");
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //清空StringBuilder，EditText恢复初始值
            //清空EditText
            pending.delete( 0, pending.length() );
            if(pending.length()<=0){
                etSumMoney.setText("￥0.00");
            }
            //如果是银行卡消费保存消费返回信息
            if(Constants.PAYTYPE_040BANK.equals(payType)){
                Gson gjson  =  GsonUtils.getGson();
                CardPaymentDate posResult = gjson.fromJson(txndetail, CardPaymentDate.class);
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
                SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
                Boolean cardPayValue = true;
                int cardOption = 11;
                sharedPreferencesUtil.put("cardPayYes", cardPayValue);
                sharedPreferencesUtil.put("cardPayType", "pay");
                sharedPreferencesUtil.put("cardOption", cardOption);
            }else{
                Gson gjson  =  GsonUtils.getGson();
                ScanPaymentDate posResult = gjson.fromJson(txndetail, ScanPaymentDate.class);
                //手动录入交易类型微信，支付宝，银联010,020,060赋值微信，支付宝，银联为11,12,13
//                if(payType.equals("010")){
//                    posResult.setPay_tp("11");
//                }else if(payType.equals("020")){
//                    posResult.setPay_tp("12");
//                }else if(payType.equals("030")){
//                    posResult.setPay_tp("13");
//                }
                if(Constants.PAYTYPE_010WX.equals(payType)){
                    posResult.setPay_tp("11");
                }else if(Constants.PAYTYPE_020ALI.equals(payType)){
                    posResult.setPay_tp("12");
                }else if(Constants.PAYTYPE_060UNIONPAY.equals(payType)){
                    posResult.setPay_tp("13");
                }
                /**
                 * 下面是调用帮助类将一个对象以序列化的方式保存
                 * 方便我们在其他界面调用，类似于Intent携带数据
                 */
                try {
                    MySerialize.saveObject("scanPayOrder",activity,MySerialize.serialize(posResult));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
                SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "scanPay");
                Boolean scanPayValue = true;
                Boolean payServiceType = false;
                String scanPayTypeValue = "pay";//pay:支付，refund:退款
                sharedPreferencesUtil.put("scanPayYes", scanPayValue);
                sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
                sharedPreferencesUtil.put("payServiceType", payServiceType);
            }


            //测试环境将订单保存服务器
            //播放语音
            speak(transamount);
            if("test".equals(NitConfig.isTest)){
                Log.e("服务环境：", "测试环境");
                saveOrderNoToService(orderid_scan);
            }else{
                Log.e("服务环境：", "生产环境");
                Log.e("返回的支付金额信息222：", transamount);
                intentToActivity();
            }
        }
    }

    /**
     * 富友界面访问成功返回
     */
    private void fuyouScanPayResult(Bundle bundle){
        //如果是银行卡消费保存消费返回信息
        if(Constants.PAYTYPE_040BANK.equals(payType)){
            String jsonStr = bundle.getString("json");
            String amountStr = bundle.getString("amount");//金额
            String traceNoStr = bundle.getString("traceNo");//凭证号
            String batchNoStr = bundle.getString("batchNo");//批次号
            String referenceNoStr = bundle.getString("referenceNo");//参考号
            String cardNoStr = bundle.getString("cardNo");//卡号
            String typeStr = bundle.getString("type");//卡类型
            String issueStr = bundle.getString("issue");//发卡行
            String dateStr = bundle.getString("date");//日期
            String timeStr = bundle.getString("time");//时间
            String orderNumberStr = bundle.getString("orderNumber");//订单流水号
            String merchantldStr = bundle.getString("merchantld");//商户号
            String terminalldStr = bundle.getString("terminalld");//终端号
            String merchantNameStr = bundle.getString("merchantName");//商户名称
            String transactionTypeStr = bundle.getString("transactionType");//交易类型



            Log.e("返回的支付金额信息：", amountStr);
            String totalStr = DecimalUtil.branchToElement(amountStr);
            //播放语音
            speak(totalStr);

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
            posResult.setTranslocaldate(dateStr);
            posResult.setTranslocaltime(timeStr);
            posResult.setTransamount(totalStr);//此处金额是分转元之后的金额

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
            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "cardPay");
            Boolean cardPayValue = true;
            int cardOption = 11;
            sharedPreferencesUtil.put("cardPayYes", cardPayValue);
            sharedPreferencesUtil.put("cardPayType", "pay");
            sharedPreferencesUtil.put("cardOption", cardOption);

        }else{
            String amountStr = bundle.getString("amount");//金额
            String traceNoStr = bundle.getString("traceNo");//凭证号
            String batchNoStr = bundle.getString("batchNo");//批次号
            String referenceNoStr = bundle.getString("referenceNo");//参考号
            String cardNoStr = bundle.getString("cardNo");//卡号(扫码支付返回订单号)
            String typeStr = bundle.getString("type");//卡类型
            String issueStr = bundle.getString("issue");//发卡行
            String dateStr = bundle.getString("date");//日期
            String timeStr = bundle.getString("time");//时间
            String zfbOrderNumberStr = bundle.getString("zfbOrderNumber");//商户自定义的订单号
            String merchantldStr = bundle.getString("merchantld");//商户号
            String terminalldStr = bundle.getString("terminalld");//终端号
            String merchantNameStr = bundle.getString("merchantName");//商户名称
            String transactionTypeStr = bundle.getString("transactionType");//交易类型

            Log.e("返回的支付金额信息：", amountStr);
            String totalStr = DecimalUtil.branchToElement(amountStr);
            //播放语音
            speak(totalStr);
            //获取系统年份
            Calendar date = Calendar.getInstance();
            String year = String.valueOf(date.get(Calendar.YEAR));
            String dateTimeStr = year + dateStr +  timeStr;
            dateStr = year + dateStr;
            Log.e("手动拼接日期时间：",dateTimeStr);
            Log.e("手动拼接日期：",dateStr);

            ScanPaymentDate posResult = new ScanPaymentDate();
            //手动录入交易类型微信，支付宝，银联010,020,030赋值微信，支付宝，银联为11,12,13
//            if(payType.equals("010")){
//                posResult.setPay_tp("11");
//            }else if(payType.equals("020")){
//                posResult.setPay_tp("12");
//            }else if(payType.equals("030")){
//                posResult.setPay_tp("13");
//            }

            if(Constants.PAYTYPE_010WX.equals(payType)){
                posResult.setPay_tp("11");
            }else if(Constants.PAYTYPE_020ALI.equals(payType)){
                posResult.setPay_tp("12");
            }else if(Constants.PAYTYPE_060UNIONPAY.equals(payType)){
                posResult.setPay_tp("13");
            }

            posResult.setOrderid_scan(cardNoStr);
            posResult.setTranslocaldate(dateStr);
            posResult.setTranslocaltime(timeStr);
            posResult.setTransamount(totalStr);//此处金额是分转元之后的金额

            /**
             * 下面是调用帮助类将一个对象以序列化的方式保存
             * 方便我们在其他界面调用，类似于Intent携带数据
             */
            try {
                MySerialize.saveObject("scanPayOrder",activity,MySerialize.serialize(posResult));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //本地保存扫码支付返回数据（扫码重打印判断是否有支付记录数据以及,使用的支付通道和判断是支付还是退款）
            SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "scanPay");
            Boolean scanPayValue = true;
            Boolean payServiceType = false;
            String scanPayTypeValue = "pay";//pay:支付，refund:退款
            sharedPreferencesUtil.put("scanPayYes", scanPayValue);
            sharedPreferencesUtil.put("scanPayType", scanPayTypeValue);
            sharedPreferencesUtil.put("payServiceType", payServiceType);


        }

        //清空StringBuilder，EditText恢复初始值
        //清空EditText
        pending.delete( 0, pending.length() );
        if(pending.length()<=0){
            etSumMoney.setText("￥0.00");
        }
    }




    /**  打印下一联提示窗口 */
    private void showPrintTwoDialog(final PosScanpayResData payResData,final String totalStr){
        View view = LayoutInflater.from(activity).inflate(R.layout.printtwo_dialog_activity, null);
        Button btok = (Button) view.findViewById(R.id.printtwo_dialog_tvOk);
        final Dialog myDialog = new Dialog(activity,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int index = 2;
                if(posProvider.equals(Constants.NEW_LAND)){
                    NewlandPrintUtil.paySuccessPrintText(activity, aidlPrinter, payResData,loginInitData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                }else if(posProvider.equals(Constants.FUYOU_SF)){
                    FuyouPrintUtil.paySuccessPrintText(activity,printService,payResData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                }

                intentToActivity();
                myDialog.dismiss();

            }
        });
        myDialog.show();
        myDialog.setCanceledOnTouchOutside(false);
    }

    /**
     *  星POS 内置接口支付
     *  payToatl:支付金额
     */
    private void XingPosServicePay(String total_fee){
        String deviceNum = loginInitData.getTrmNo_pos();
        pos_order_noStr = RandomStringGenerator.getNlRandomNum(deviceNum);
        Log.e("生成的订单号：", pos_order_noStr);
        NewPosServiceUtil.payReq(activity, payType, total_fee,pos_order_noStr, loginInitData);
    }

    /**
     * 富友POS 内置支付
     * payTotal:支付金额（最终提交以分为单位）
     */
    private void FuyouPosServicePay(String total_fee){
        String total_feeStr = FieldTypeUtil.makeFieldAmount(total_fee);
        Log.e("SDK提交带规则金额",total_feeStr);
        //设备号
        String deviceNum = loginInitData.getTrmNo_pos();
        pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
        Log.e("生成的订单号：", pos_order_noStr);
        //是否开启前置摄像头
        FuyouPosServiceUtil.payReq(activity, payType, total_feeStr,pos_order_noStr,cameType);
    }

    /** 支付结果跳转目标Activity */
    private void startPrint(PosScanpayResData payResData){
        String totalStr = DecimalUtil.branchToElement(payResData.getTotal_fee());
        //播放语音
        speak(totalStr);
        int index = 1;
        if(posProvider.equals(Constants.NEW_LAND)){
            //初始化打印机
            getPrinter();
            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if("printNumNo".equals(printNum)){

                //不执行打印
                intentToActivity();


            }else if("printNumOne".equals(printNum)){

                //打印一次
                NewlandPrintUtil.paySuccessPrintText(activity, aidlPrinter, payResData,loginInitData,isDefault,NewlandPrintUtil.payPrintRemarks,index);
                intentToActivity();


            }else if("printNumTwo".equals(printNum)){

                //打印两次
                NewlandPrintUtil.paySuccessPrintText(activity, aidlPrinter, payResData,loginInitData,isDefault,NewlandPrintUtil.payPrintRemarks,index);

                try {
                    Thread.sleep(NewlandPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //弹出对话框提示打印下一联
                showPrintTwoDialog(payResData,totalStr);


            }
        }else if(posProvider.equals(Constants.FUYOU_SF)){
            /** 根据打印设置打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联 去打印 */
            if("printNumNo".equals(printNum)){
                //不执行打印
                intentToActivity();
            }else if("printNumOne".equals(printNum)){
                //打印一次
                FuyouPrintUtil.paySuccessPrintText(activity,printService,payResData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                intentToActivity();
            }else if("printNumTwo".equals(printNum)){
                //打印两次
                FuyouPrintUtil.paySuccessPrintText(activity,printService,payResData,loginInitData,isDefault,FuyouPrintUtil.payPrintRemarks,index);
                try {
                    Thread.sleep(FuyouPrintUtil.time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                showPrintTwoDialog(payResData,totalStr);
            }
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bundle bundle = data.getExtras();

        /**
         * 富友POS扫码返回
         */
        if (requestCode == FuyouPosServiceUtil.SCAN_REQUEST_CODE) {
            if(bundle != null){
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        String scanCodeStr = bundle.getString("return_txt");//扫码返回数据
                        Log.e("获取扫描结果：", scanCodeStr);
                        //如果扫描的二维码为空则不执行支付请求
                        if(scanCodeStr!=null&&!"".equals(scanCodeStr)){
                            //auth_no	授权码（及扫描二维码值）
                            String auth_no = scanCodeStr;
                            //total_fee	金额，单位分(金额需乘以100)
                            String etTextStr = pending.toString();
                            Log.e("输入框文本text值：", etTextStr);
                            String total_feeStr = DecimalUtil.elementToBranch(etTextStr);

                            payMethodThree(auth_no,total_feeStr);

                        }else{
                            //清空StringBuilder，EditText恢复初始值
                            //清空EditText
                            pending.delete( 0, pending.length() );
                            if(pending.length()<=0){
                                etSumMoney.setText("￥0.00");
                            }
                            ToastUtil.showText(activity,"扫描结果为空！",1);
                        }


                        break;
                    // 扫码取消
                    case Activity.RESULT_CANCELED:

                        String reason = "扫码取消";
                        if (data != null) {
                            Bundle b = data.getExtras();
                            if (b != null) {
                                reason = (String) b.get("reason");
                            }
                        }
                        if (Utils.isNotEmpty(reason)) {
                            Log.e("reason", reason);
                            ToastUtil.showText(activity,reason,1);
                        }
                        break;
                    default:
                        break;

                }
            }else{
                ToastUtil.showText(activity,"扫码失败！",1);
            }

        }

        /**
         * 支付返回
         */
        if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
            if(bundle != null){
                switch (resultCode) {
                    // 支付成功
                    case Activity.RESULT_OK:
                        if(posProvider.equals(Constants.NEW_LAND)){
                            newlandScanPayResult(bundle);
                        }else if(posProvider.equals(FUYOU_SF)){
                            fuyouScanPayResult(bundle);
                        }
                        break;
                    // 支付取消
                    case Activity.RESULT_CANCELED:
                        String reason = "支付取消";
                        if(posProvider.equals(Constants.NEW_LAND)){
                            reason = bundle.getString("reason");
                        }else if(posProvider.equals(FUYOU_SF)){
                            if (data != null) {
                                Bundle b = data.getExtras();
                                if (b != null) {
                                    reason = (String) b.get("reason");
                                }
                            }

                        }
                        if (reason != null) {
                            Log.d("reason", reason);
                            ToastUtil.showText(activity,reason,1);
                        }
                        //清空StringBuilder，EditText恢复初始值
                        //清空EditText
                        pending.delete( 0, pending.length() );
                        if(pending.length()<=0){
                            etSumMoney.setText("￥0.00");
                        }
                        break;
                    default:
                        break;
                }
            }else{
                ToastUtil.showText(activity,"支付返回失败！",1);
            }

        }


    }

    private void intentToActivity(){
        Intent in = new Intent();
        in.setClass(activity, PaySuccessActivity.class);
        if("test".equals(NitConfig.isTest)){
            in.putExtra("pos_order", pos_order_noStr);
        }
        startActivity(in);
    }

    private void intentActivity(){
        Intent in = new Intent();
        in.setClass(activity, PayErrorActivity.class);
        in.putExtra("optionTypeStr", "010");
        startActivity(in);
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.pay_type_wxPayLayout://微信
                payType = Constants.PAYTYPE_010WX;
                payMethodOne();
                break;
            case R.id.pay_type_aliPayLayout://支付宝
                payType = Constants.PAYTYPE_020ALI;
                payMethodOne();
                break;
            case R.id.pay_type_cardPayLayout://刷卡
                payType = Constants.PAYTYPE_040BANK;
                payMethodOne();
                break;
            case R.id.pay_type_bestPayLayout://翼支付
                payType = Constants.PAYTYPE_050BEST;
                payMethodOne();
                break;
            case R.id.pay_type_unionPayLayout://银联二维码
                payType = Constants.PAYTYPE_060UNIONPAY;
                payMethodOne();
                break;
            case R.id.pay_type_memberPayLayout://会员
                ToastUtil.showText(activity,"暂未开通！",1);
                break;

                default:
                    Log.e("输入框文本text值：", pending.toString());
                    break;
        }
    }
}
