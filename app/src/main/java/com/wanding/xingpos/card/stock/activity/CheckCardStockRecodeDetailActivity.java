package com.wanding.xingpos.card.stock.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.scan.AidlScanner;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.card.stock.bean.CheckCardStockRecodeDetailResData;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * 核销记录详情界面
 */
@ContentView(R.layout.activity_check_card_stock_recode_details)
public class CheckCardStockRecodeDetailActivity extends BaseActivity implements View.OnClickListener {

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
     * 商户名称，卡劵名称，核销代码，使用状态，使用时间，POS设备号
     */
    @ViewInject(R.id.card_stock_recode_details_tvMerName)
    TextView tvMerName;
    @ViewInject(R.id.card_stock_recode_details_tvCaedStockName)
    TextView tvCaedStockName;
    @ViewInject(R.id.card_stock_recode_details_tvCardStockCode)
    TextView tvCardStockCode;
    @ViewInject(R.id.card_stock_recode_details_tvCardStockState)
    TextView tvCardStockState;
    @ViewInject(R.id.card_stock_recode_details_tvCardStockCreateTime)
    TextView tvCardStockCreateTime;
    @ViewInject(R.id.card_stock_recode_details_tvDeviceNum)
    TextView tvDeviceNum;

    @ViewInject(R.id.card_stock_recode_details_tvOk)
    TextView tvOk;
    @ViewInject(R.id.card_stock_recode_details_tvPrint)
    TextView tvPrint;


    /**
     * 签到商户信息
     * 核销详情
     */
    UserLoginResData loginInitData;
    CheckCardStockRecodeDetailResData cardStockRecode;


    private String posProvider;

    AidlDeviceService aidlDeviceService = null;
    AidlPrinter aidlPrinter = null;
    public AidlScanner aidlScanner=null;

    /**
     * 新大陆(打印机，扫码摄像头)AIDL服务
     */
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
    private PrintInterface printService = null;
    private PrintReceiver printReceiver;
    private ServiceConnection printServiceConnection = new ServiceConnection() {

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
        tvTitle.setText("核销单详情");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);

        posProvider = MainActivity.posProvider;
        if(posProvider.equals(Constants.NEW_LAND)){
            //绑定打印机服务
            bindServiceConnection();
        }else if(posProvider.equals(Constants.FUYOU_SF)){
            initPrintService();
        }
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
        cardStockRecode = (CheckCardStockRecodeDetailResData) intent.getSerializableExtra("writeOffRecode");


        initListener();
        updateView();


    }

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



    private void initListener(){
        imgBack.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        tvPrint.setOnClickListener(this);
    }

    private void updateView(){
        try{
            tvMerName.setText(loginInitData.getMername_pos());
            tvCaedStockName.setText(cardStockRecode.getTitle());
            tvCardStockCode.setText(cardStockRecode.getCode());
            tvCardStockState.setText("使用成功");
            tvCardStockCreateTime.setText(DateTimeUtil.stampToDate(cardStockRecode.getUse_time()));
            tvDeviceNum.setText(loginInitData.getTrmNo_pos());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.card_stock_recode_details_tvOk:
                finish();
                break;
            case R.id.card_stock_recode_details_tvPrint:
                if(Utils.isFastClick()){
                    return;
                }
                FuyouPrintUtil.checkCardStockDetailPrintText(activity,printService,cardStockRecode,loginInitData);
                break;
                default:
                    break;
        }
    }
}
