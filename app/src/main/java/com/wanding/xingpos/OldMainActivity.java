package com.wanding.xingpos;

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
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.tts.chainofresponsibility.logger.LoggerProxy;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.nld.cloudpos.aidl.scan.AidlScannerListener;
import com.nld.cloudpos.data.ScanConstant;
import com.wanding.xingpos.activity.AgainPrintActivity;
import com.wanding.xingpos.activity.OrderListActivity;
import com.wanding.xingpos.activity.PayErrorActivity;
import com.wanding.xingpos.activity.PaySuccessActivity;
import com.wanding.xingpos.activity.RefundManageActivity;
import com.wanding.xingpos.activity.SettingActivity;
import com.wanding.xingpos.activity.ShiftActivity;
import com.wanding.xingpos.activity.StaffListActivity;
import com.wanding.xingpos.auth.activity.AuthManageActivity;
import com.wanding.xingpos.baidu.tts.util.AutoCheck;
import com.wanding.xingpos.baidu.tts.util.InitConfig;
import com.wanding.xingpos.baidu.tts.util.MainHandlerConstant;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
import com.wanding.xingpos.baidu.tts.util.NonBlockSyntherizer;
import com.wanding.xingpos.baidu.tts.util.OfflineResource;
import com.wanding.xingpos.baidu.tts.util.UiMessageListener;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.PosInitData;
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
import java.util.HashMap;
import java.util.Map;

import static com.wanding.xingpos.Constants.FUYOU_SF;

/**
 * 悦收银 主界面
 * https://www.cnblogs.com/wjtaigwh/p/6210534.html
 */
@ContentView(R.layout.old_activity_main)
public class OldMainActivity extends MainBaseActivity implements View.OnClickListener,MainHandlerConstant{

    /**
     * 左侧DrawerLayout
     */
    @ViewInject(R.id.activity_main_drawerLayout)
    DrawerLayout mDrawerLayout;
    @ViewInject(R.id.menu_title_imageView)
    ImageView imgMenu;
    @ViewInject(R.id.activity_main_nav)
    NavigationView mNavigationView;

    /**
     * 底部支付类型
     * 刷卡，微信，支付宝，银联，翼支付
     */
    @ViewInject(R.id.main_bottom_paySK)
    LinearLayout paySK;
    @ViewInject(R.id.main_bottom_payWX)
    LinearLayout payWX;
    @ViewInject(R.id.main_bottom_payAli)
    LinearLayout payAL;
    @ViewInject(R.id.main_bottom_payYL)
    LinearLayout payYL;
    @ViewInject(R.id.main_bottom_payDX)
    LinearLayout payDX;

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
    private PosInitData posInitData;
    private UserLoginResData loginInitData;

    // ================== 初始化参数设置开始 ==========================
    /**
     * 发布时请替换成自己申请的appId appKey 和 secretKey。注意如果需要离线合成功能,请在您申请的应用中填写包名。
     *
     */
    protected String appId = "11072721";

    protected String appKey = "eZGGWmPXBYCbTBrcxZWkGX7B";

    protected String secretKey = "a336b0c83f57cc5a878489f372ecfe9a";

    // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
    protected TtsMode ttsMode = TtsMode.MIX;

    // 离线发音选择，VOICE_FEMALE即为离线女声发音。
    // assets目录下bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat为离线男声模型；
    // assets目录下bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat为离线女声模型
    protected String offlineVoice = OfflineResource.VOICE_MALE;

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================

    // 主控制类，所有合成控制方法从这个类开始
    public static MySyntherizer synthesizer;
    protected Handler mainHandler;

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
        App.getInstance().addActivity(activity);
        mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }

        };

        //初始化百度语音引擎
        initialTts();

        initListener();

        //menu选项事件
        setMenuEvents();

        /**
         * POS机提供厂商标识
         */
        SharedPreferencesUtil sharedPreferencesUtil_posInit = new SharedPreferencesUtil(this, "posInit");
        if(sharedPreferencesUtil_posInit.contain("posProvider")){
            posProvider = (String) sharedPreferencesUtil_posInit.getSharedPreference("posProvider", "");
            if(Utils.isEmpty(posProvider)){
                //取默认值为新大陆
                posProvider = Constants.NEW_LAND;
            }
        }else{
            //取默认值为新大陆
            posProvider = Constants.NEW_LAND;
        }

        try {
            posInitData =(PosInitData) MySerialize.deSerialization(MySerialize.getObject("PosInitData", this));
            //签到登录
            getLoginData();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

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
        synthesizer.release();
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
        imgMenu.setVisibility(View.VISIBLE);
        imgMenu.setOnClickListener(this);

        paySK.setOnClickListener(this);
        payWX.setOnClickListener(this);
        payAL.setOnClickListener(this);
        payYL.setOnClickListener(this);
        payDX.setOnClickListener(this);
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
     * 初始化引擎，需要的参数均在InitConfig类里
     * <p>
     * DEMO中提供了3个SpeechSynthesizerListener的实现
     * MessageListener 仅仅用log.i记录日志，在logcat中可以看见
     * UiMessageListener 在MessageListener的基础上，对handler发送消息，实现UI的文字更新
     * FileSaveListener 在UiMessageListener的基础上，使用 onSynthesizeDataArrived回调，获取音频流
     */
    protected void initialTts() {
        try {
            LoggerProxy.printable(true); // 日志打印在logcat中
            // 设置初始化参数
            // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
            SpeechSynthesizerListener listener = new UiMessageListener(mainHandler);

            Map<String, String> params = getParams();


            // appId appKey secretKey 网站上您申请的应用获取。注意使用离线合成功能的话，需要应用中填写您app的包名。包名在build.gradle中获取。
            InitConfig initConfig = new InitConfig(appId, appKey, secretKey, ttsMode, params, listener);

            // 如果您集成中出错，请将下面一段代码放在和demo中相同的位置，并复制InitConfig 和 AutoCheck到您的项目中
            // 上线时请删除AutoCheck的调用
            AutoCheck.getInstance(getApplicationContext()).check(initConfig, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 100) {
                        AutoCheck autoCheck = (AutoCheck) msg.obj;
                        synchronized (autoCheck) {
                            String message = autoCheck.obtainDebugMessage();
//                        toPrint(message); // 可以用下面一行替代，在logcat中查看代码
                            Log.e("AutoCheckMessage", message);
                        }
                    }
                }

            });
            // 此处可以改为MySyntherizer 了解调用过程
            synthesizer = new NonBlockSyntherizer(this, initConfig, mainHandler);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    /**
     * 合成的参数，可以初始化时填写，也可以在合成前设置。
     *
     * @return
     */
    protected Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");

        params.put(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线

        // 离线资源文件， 从assets目录中复制到临时目录，需要在initTTs方法前完成
        OfflineResource offlineResource = createOfflineResource(offlineVoice);
        // 声学模型文件路径 (离线引擎使用), 请确认下面两个文件存在
        params.put(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, offlineResource.getTextFilename());
        params.put(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE,
                offlineResource.getModelFilename());
        return params;

    }

    protected OfflineResource createOfflineResource(String voiceType) {
        OfflineResource offlineResource = null;
        try {
            offlineResource = new OfflineResource(this, voiceType);
        } catch (IOException e) {
            // IO 错误自行处理
            e.printStackTrace();
//            toPrint("【error】:copy files from assets failed." + e.getMessage());
            // 可以用下面一行替代，在logcat中查看代码
            Log.e("【error】:", e.getMessage());
        }
        return offlineResource;
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
        //微信支付
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

        }
        else if(Constants.PAYTYPE_020ALI.equals(payType)){

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

        }
        else if(Constants.PAYTYPE_060UNIONPAY.equals(payType)){
            /** 根据支付通道进行请求服务  */
            if(ylPayServiceType){
                Log.e("支付通道：", "默认");

                if(posProvider.equals(Constants.NEW_LAND)){
                    if(cameType){
                        Log.e("扫码调用：", "后置摄像头");
//							Intent in = new Intent();
//			    			in.setClass(getActivity(), CaptureActivity.class);
//			    			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			    			startActivityForResult(in, REQUEST_CODE);
                        initScanner();
                        backscan();
                    }else{
                        Log.e("扫码调用：", "前置摄像头");
                        initScanner();
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
        }
        else if(Constants.PAYTYPE_040BANK.equals(payType)){
            if(posProvider.equals(Constants.NEW_LAND)){
                Log.e("支付通道：", "星POS");
                XingPosServicePay(total_feeStr);
            }else if(posProvider.equals(Constants.FUYOU_SF)){
                Log.e("支付通道：", "富友POS");
                FuyouPosServicePay(total_feeStr);
            }
        }
        else if(Constants.PAYTYPE_050BEST.equals(payType)){
            Log.e("支付类型：", "翼支付！");

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
            ToastUtil.showText(activity,"请选择支付类型！",1);
        }
    }

    /**
     * 发起支付第三步
     */
    private void payMethodThree(String auth_no, String total_fee){
        PosScanpayReqData posBean = PayRequestUtil.payReq(payType, auth_no, total_fee,loginInitData,posProvider);
        payRequestMethood(posBean);
    }


    /** 获取登录信息（签到）  */
    private void getLoginData(){
        showWaitDialog();
        final String url = NitConfig.loginUrl;
        new Thread(){
            @Override
            public void run() {
                JSONObject userJSON = new JSONObject();
                try {
                    userJSON.put("thirdMid",posInitData.getMercId_pos());   //商户号
                    userJSON.put("terminal_id",posInitData.getTrmNo_pos()); //  设备号，终端号
                    if(posProvider.equals(FUYOU_SF)){
                        userJSON.put("type","5"); //设备识别类型：5表示POS机为富友厂商
                    }
                    String content = String.valueOf(userJSON);
                    Log.e("签到发起请求参数：", content);
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("签到发起返回字符串结果：", jsonStr);
                    int msg = NetworkUtils.MSG_WHAT_ONE;
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
                case NetworkUtils.MSG_WHAT_ONE:
                    String loginJsonStr=(String) msg.obj;
                    loginJsonStr(loginJsonStr);
                    hideWaitDialog();
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

    private void loginJsonStr(String jsonStr){
        if(Utils.isNotEmpty(jsonStr)){
            try {
                JSONObject job = new JSONObject(jsonStr);
                String statusStr = job.getString("status");
                if("200".equals(statusStr)){
                    Log.e("签到查询状态：", "成功！");
                    String dataJsonStr = job.getString("data");
                    JSONObject dataJob = new JSONObject(dataJsonStr);
                    UserLoginResData logResData = new UserLoginResData();
                    //json返回信息
                    logResData.setAccess_token(dataJob.getString("accessToken"));
                    logResData.setMerchant_no(dataJob.getString("merchant_no"));
                    logResData.setTerminal_id(dataJob.getString("terminal_id"));
                    logResData.setMid(dataJob.getString("mid"));
                    logResData.setEid(dataJob.getString("eid"));

//						logResData.setAccess_token("wwh88pdhkqps1xvhxb0fcqa61bs7awyz");
//						logResData.setMerchant_no("1000179");
//						logResData.setTerminal_id("10108");
//						logResData.setMid("344");
//						logResData.setEid("132");

                    logResData.setEname(dataJob.getString("ename"));
                    //pos初始化信息
                    logResData.setMercId_pos(posInitData.getMercId_pos());
                    logResData.setTrmNo_pos(posInitData.getTrmNo_pos());
                    logResData.setMername_pos(posInitData.getMername_pos());
                    logResData.setBatchno_pos(posInitData.getBatchno_pos());

                    /**
                     * 下面是调用帮助类将一个对象以序列化的方式保存
                     * 方便我们在其他界面调用，类似于Intent携带数据
                     */
                    try {
                        MySerialize.saveObject("UserLoginResData",activity,MySerialize.serialize(logResData));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }



                    try {
                        loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    ToastUtil.showText(activity,"签到成功！",1);
                    //打印商户信息
                   /* if(posProvider.equals(Constants.NEW_LAND)){
                        //初始化打印机
                        getPrinter();
                        //打印
                        NewlandPrintUtil.businessInfoPrintText(activity, aidlPrinter,loginInitData);
                    }else if(posProvider.equals(FUYOU_SF)){
                        //打印
                        FuyouPrintUtil.businessInfoPrintText(activity,printService,loginInitData);
                    }*/

                }else{
                    UserLoginResData logResData = new UserLoginResData();
                    //json返回信息
                    logResData.setAccess_token("");
                    logResData.setMerchant_no("");
                    logResData.setTerminal_id("");
                    logResData.setMid("");
                    logResData.setEid("");
                    logResData.setEname("");
                    //pos初始化信息
                    logResData.setMercId_pos(posInitData.getMercId_pos());
                    logResData.setTrmNo_pos(posInitData.getTrmNo_pos());
                    logResData.setMername_pos(posInitData.getMername_pos());
                    logResData.setBatchno_pos(posInitData.getBatchno_pos());

                    /**
                     * 下面是调用帮助类将一个对象以序列化的方式保存
                     * 方便我们在其他界面调用，类似于Intent携带数据
                     */
                    try {
                        MySerialize.saveObject("UserLoginResData",activity,MySerialize.serialize(logResData));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        loginInitData=(UserLoginResData) MySerialize.deSerialization(MySerialize.getObject("UserLoginResData", activity));
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    ToastUtil.showText(activity,"签到失败！",1);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            ToastUtil.showText(activity,NetworkUtils.REQUEST_TEXT,1);
        }
    }

    private void scanPayJsonStr(String jsonStr){
        Gson gjson  =  GsonUtils.getGson();
        PosScanpayResData posResult = gjson.fromJson(jsonStr, PosScanpayResData.class);
        //return_code	响应码：“01”成功 ，02”失败，请求成功不代表业务处理成功
        String return_codeStr = posResult.getReturn_code();
        //return_msg	返回信息提示，“支付成功”，“支付中”，“请求受限”等
        String return_msgStr = posResult.getReturn_msg();
        //result_code	业务结果：“01”成功 ，02”失败 ，“03”支付中
        String result_codeStr = posResult.getResult_code();
        synchronized (OldMainActivity.class){
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
    private void newlandSignResult(Bundle bundle){
        String msgTp = bundle.getString("msg_tp");
        if (TextUtils.equals(msgTp, "0310")) {
            String txndetail = bundle.getString("txndetail");
            Log.e("txndetail获取设备商户信息：", txndetail);
            try {
                JSONObject job = new JSONObject(txndetail);
                posInitData = new PosInitData();
                posInitData.setMercId_pos(job.getString("merid"));
                posInitData.setTrmNo_pos(job.getString("termid"));
                posInitData.setMername_pos(job.getString("mername"));
                posInitData.setBatchno_pos(job.getString("batchno"));
                //将需要的参数传入支付请求公共类保存在本地
                saveDataLocal(posInitData);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                ToastUtil.showText(activity,"POS签到失败！请联系技术！",1);
            }

        }
    }

    /**
     * 富友界面访问成功返回
     */
    private void fuyouSignResult(Bundle bundle){

        String merchantIdStr = bundle.getString("merchantId");//商户号
        String terminalIdStr = bundle.getString("terminalId");//终端号
        String merchantNameStr = bundle.getString("merchantName");//商户名
        String batchNoStr = "";//批次号（富友签到时不返回该字段）

        posInitData = new PosInitData();
        posInitData.setPosProvider(posProvider);
        posInitData.setMercId_pos(merchantIdStr);
        posInitData.setTrmNo_pos(terminalIdStr);
        posInitData.setMername_pos(merchantNameStr);
        posInitData.setBatchno_pos(batchNoStr);
        //将需要的参数传入支付请求公共类保存在本地
        saveDataLocal(posInitData);
    }

    private void saveDataLocal(PosInitData data){

        /**
         * 下面是调用帮助类将一个对象以序列化的方式保存
         * 方便我们在其他界面调用，类似于Intent携带数据
         */
        try {
            MySerialize.saveObject("PosInitData",activity,MySerialize.serialize(data));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        Log.e("保存POS机初始化信息：","成功！");
        //将POS初始化信息保存在本地
        SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "posInit");
        sharedPreferencesUtil.put("posMercId", posInitData.getMercId_pos());
        sharedPreferencesUtil.put("posTrmNo", posInitData.getTrmNo_pos());
        sharedPreferencesUtil.put("posMername", posInitData.getMername_pos());
        sharedPreferencesUtil.put("posBatchno", posInitData.getBatchno_pos());
        //执行登陆功能
        getLoginData();


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

    /**
     * 菜单选项item事件
     */
    private void setMenuEvents(){
        //设置NavigationView Item图标不能显示图片原始颜色的问题
        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = new Intent();
                switch(item.getItemId()){
                    case R.id.sign:
//                        Toast.makeText(MainActivity.this, "签到", Toast.LENGTH_SHORT).show();
                        //应用初始化（获取POS机商户信息）
                        appDataInstance();
                        break;
                    case R.id.orderlist:
                        Toast.makeText(OldMainActivity.this, "交易明细", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity,OrderListActivity.class);
                        intent.putExtra("userLoginData",loginInitData);
                        startActivity(intent);
                        break;
                    case R.id.auth:
                        Toast.makeText(OldMainActivity.this, "预授权", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity,AuthManageActivity.class);
                        intent.putExtra("userLoginData",loginInitData);
                        startActivity(intent);
                        break;
                    case R.id.shift:
                        Toast.makeText(OldMainActivity.this, "交接班", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity,ShiftActivity.class);
                        intent.putExtra("userLoginData",loginInitData);
                        startActivity(intent);
                        break;
                    case R.id.again_print:
                        Toast.makeText(OldMainActivity.this, "重打印", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity,AgainPrintActivity.class);
                        intent.putExtra("userLoginData",loginInitData);
                        startActivity(intent);
                        break;
                    case R.id.refund:
                        Toast.makeText(OldMainActivity.this, "退款", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity, RefundManageActivity.class);
                        intent.putExtra("userLoginData",loginInitData);
                        startActivity(intent);
                        break;
                     case R.id.scan_query_order:
                        Toast.makeText(OldMainActivity.this, "扫码查单", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.stafflist:
                        Toast.makeText(OldMainActivity.this, "员工", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity, StaffListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.setting:
                        Toast.makeText(OldMainActivity.this, "设置", Toast.LENGTH_SHORT).show();
                        intent.setClass(activity, SettingActivity.class);
                        intent.putExtra("userLoginData",loginInitData);
                        startActivity(intent);
                        break;
                        default:
                            break;

                }
                //关闭侧滑菜单
//                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    /**  退出应用提示窗口 */
    private void showColseAPPDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.close_dialog_activity, null);
        TextView btok = (TextView) view.findViewById(R.id.close_dialog_tvOk);
        TextView btCancel = (TextView) view.findViewById(R.id.close_dialog_tvCancel);
        final Dialog myDialog = new Dialog(this,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //关闭应用
                finish();
                myDialog.dismiss();

            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                myDialog.dismiss();
            }
        });
        myDialog.show();
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
         *  签到返回
         */
        if (requestCode == FuyouPosServiceUtil.SIGN_REQUEST_CODE) {
            if(bundle != null){
                switch (resultCode) {
                    // 请求成功
                    case Activity.RESULT_OK:
                        if(posProvider.equals(Constants.NEW_LAND)){
                            newlandSignResult(bundle);
                        }else if(posProvider.equals(Constants.FUYOU_SF)){
                            fuyouSignResult(bundle);
                        }
                        break;
                    // 请求取消
                    case Activity.RESULT_CANCELED:
                        String reason = bundle.getString("reason");
                        if (reason != null) {
                            ToastUtil.showText(activity,"POS机初始化参数失败！",1);
                        }
                        break;
                    default:
                        break;
                }
            }

        }

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
                        if (reason != null) {
                            Log.d("reason", reason);
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
            case R.id.menu_title_imageView://左上角菜单
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.main_bottom_payWX:
                payType = Constants.PAYTYPE_010WX;
                payMethodOne();
                break;
            case R.id.main_bottom_payAli:
                payType = Constants.PAYTYPE_020ALI;
                payMethodOne();
                break;
            case R.id.main_bottom_payYL://银联二维码
                payType = Constants.PAYTYPE_060UNIONPAY;
                payMethodOne();
                break;
            case R.id.main_bottom_paySK://刷卡
                payType = Constants.PAYTYPE_040BANK;
                payMethodOne();
                break;
            case R.id.main_bottom_payDX://翼支付
                payType = Constants.PAYTYPE_050BEST;
                payMethodOne();
                break;

                default:
                    Log.e("输入框文本text值：", pending.toString());
                    break;
        }
    }


    //拦截/屏蔽返回键、MENU键实现代码
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            showColseAPPDialog();
        }
        else if(keyCode == KeyEvent.KEYCODE_MENU) {//MENU键
            //监控/拦截菜单键
            return true;
        }
        else if (keyCode == KeyEvent. KEYCODE_HOME) {
            //(屏蔽HOME键3)

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
