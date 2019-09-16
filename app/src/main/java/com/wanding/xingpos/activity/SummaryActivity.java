package com.wanding.xingpos.activity;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.PayTypeBean;
import com.wanding.xingpos.bean.SummaryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date.picker.TimeSelector;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.summary.util.SummaryDateUtil;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;

/**
 * 汇总Actiivty
 */
@ContentView(R.layout.activity_summary)
public class SummaryActivity extends BaseActivity implements OnClickListener {

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

	@ViewInject(R.id.summary_tvSumMoney)
	TextView tvSumMoney;
	@ViewInject(R.id.summary_tvSumNum)
	TextView tvSumNum;
	@ViewInject(R.id.summary_tvAliLayout)
	LinearLayout aliLayout;
	@ViewInject(R.id.summary_tvWeixinLayout)
	LinearLayout weixinLayout;
	@ViewInject(R.id.summary_tvYizhifuLayout)
	LinearLayout yizhifuLayout;
	@ViewInject(R.id.summary_tvYHKLayout)
	LinearLayout yhkLayout;
	@ViewInject(R.id.summary_tvUNIONPAYLayout)
	LinearLayout ylLayout;
	@ViewInject(R.id.summary_view1)
	View view1;
	@ViewInject(R.id.summary_view2)
	View view2;
	@ViewInject(R.id.summary_view3)
	View view3;
	@ViewInject(R.id.summary_view4)
	View view4;
	@ViewInject(R.id.summary_view5)
	View view5;
	@ViewInject(R.id.summary_tvAliSumMoney)
	TextView tvAliSumMoney;
	@ViewInject(R.id.summary_tvAliSumNum)
	TextView tvAliSumNum;
	@ViewInject(R.id.summary_tvWeixinSumMoney)
	TextView tvWeixinSumMoney;
	@ViewInject(R.id.summary_tvWeixinSumNum)
	TextView tvWeixinSumNum;
	@ViewInject(R.id.summary_tvYizhifuSumMoney)
	TextView tvYizhifuSumMoney;
	@ViewInject(R.id.summary_tvYizhifuSumNum)
	TextView tvYizhifuSumNum;
	@ViewInject(R.id.summary_tvYHKSumMoney)
	TextView tvYHKSumMoney;
	@ViewInject(R.id.summary_tvYHKSumNum)
	TextView tvYHKSumNum;
	@ViewInject(R.id.summary_tvUNIONPAYSumMoney)
	TextView tvYLSumMoney;
	@ViewInject(R.id.summary_tvUNIONPAYSumNum)
	TextView tvYLSumNum;

	
	private UserLoginResData loginInitData;
	private SummaryResData summary;

	private String startDateTime,endDateTime,endDate,seleteDate;//
	
	AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
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
	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private String posProvider;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("选择时间");
		imgTitleImg.setVisibility(View.VISIBLE);
		tvOption.setVisibility(View.VISIBLE);
		tvOption.setText("打印");




		posProvider = MainActivity.posProvider;
		Intent intent = getIntent();
		loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

		//起始日期为一个月前
		startDateTime = DateTimeUtil.getAMonthDateStr(-1, "yyyy-MM-dd HH:mm");
		//初始化日期时间（即系统默认时间）
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
		endDateTime = sdf.format(new Date());
		endDate = endDateTime.split(" ")[0];
		seleteDate = endDate;
		tvTitle.setText(endDate);

		if(posProvider.equals(Constants.NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(Constants.FUYOU_SF)){
			initPrintService();
		}

		initListener();

		String dataJson = "";
		updateView(dataJson);

		//查询汇总
		querySummary(endDate);
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
		if(posProvider.equals(Constants.NEW_LAND)){
			unbindService(serviceConnection);
			aidlPrinter=null;
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
	 * 注册按钮事件
	 */
	private void initListener(){

		imgBack.setOnClickListener(this);
		titleLayout.setOnClickListener(this);
		tvOption.setOnClickListener(this);
	}

	
	/**
	 * 更新界面数据
	 */
	private void updateView(String dataJson){
		aliLayout.setVisibility(View.GONE);
		view1.setVisibility(View.GONE);
		weixinLayout.setVisibility(View.GONE);
		view2.setVisibility(View.GONE);
		yizhifuLayout.setVisibility(View.GONE);
		view3.setVisibility(View.GONE);
		yhkLayout.setVisibility(View.GONE);
		view4.setVisibility(View.GONE);
		ylLayout.setVisibility(View.GONE);
		view5.setVisibility(View.GONE);
		
		if(Utils.isNotEmpty(dataJson)){
			Gson gson = GsonUtils.getGson();
			java.lang.reflect.Type type = new TypeToken<SummaryResData>() {}.getType();
			summary = gson.fromJson(dataJson, type);

			Integer sumTotal = summary.getSumTotal();
			Double sumAmt = summary.getSumAmt();
			//总笔数
			if(sumTotal!=null){
				int sumTotal_int = sumTotal.intValue();
				tvSumNum.setText(String.valueOf(sumTotal_int));
			}
			//总金额
			if(sumAmt!=null){
				double sumAmt_dou = sumAmt.doubleValue();
				tvSumMoney.setText(String.valueOf(sumAmt_dou));
			}



			List<PayTypeBean> lsPayType = new ArrayList<PayTypeBean>();
			lsPayType = summary.getOrderSumList();
			//银行卡总金额  = 贷记卡总金额 + 借记卡总金额
			double sumMoney = 0;
			//银行卡总笔数 = 贷记卡总笔数 + 借记卡总笔数
			int sumNum = 0;
			for (int i = 0; i < lsPayType.size(); i++) {
				PayTypeBean payType = lsPayType.get(i);
				String payWayStr = payType.getPayWay();
				//支付宝
				if("ALI".equals(payWayStr)){
					aliLayout.setVisibility(View.VISIBLE);
					view1.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvAliSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvAliSumNum.setText(String.valueOf(total_int));
				}
				//微信
				if("WX".equals(payWayStr)){
					weixinLayout.setVisibility(View.VISIBLE);
					view2.setVisibility(View.VISIBLE);

					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvWeixinSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvWeixinSumNum.setText(String.valueOf(total_int));
				}
				//翼支付
				if("BEST".equals(payWayStr)){
					yizhifuLayout.setVisibility(View.VISIBLE);
					view3.setVisibility(View.VISIBLE);

					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvYizhifuSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvYizhifuSumNum.setText(String.valueOf(total_int));
				}
				//银行卡：DEBIT= 借记卡
				if("DEBIT".equals(payWayStr)){
					yhkLayout.setVisibility(View.VISIBLE);
					view4.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Log.e("借记卡金额1",amount+"");
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					Log.e("借记卡金额2",amount_dou+"");
					Log.e("总金额1",sumMoney+"");
					sumMoney = sumMoney + amount_dou;
					Log.e("总金额2",sumMoney+"");
					int total_int = total.intValue();
					sumNum = sumNum + total_int;
					Log.e("总笔数",sumNum+"");
					tvYHKSumMoney.setText(DecimalUtil.doubletoString(sumMoney));
					tvYHKSumNum.setText(String.valueOf(sumNum));
				}
				//银行卡：    CREDIT=贷记卡
				if("CREDIT".equals(payWayStr)){
					yhkLayout.setVisibility(View.VISIBLE);
					view4.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Log.e("贷记卡金额1",amount+"");
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					Log.e("贷记卡金额2",amount_dou+"");
					Log.e("总金额1",sumMoney+"");
					sumMoney = sumMoney + amount_dou;
					Log.e("总金额2",sumMoney+"");
					int total_int = total.intValue();
					sumNum = sumNum + total_int;
					Log.e("总笔数",sumNum+"");
					tvYHKSumMoney.setText(DecimalUtil.doubletoString(sumMoney));
					tvYHKSumNum.setText(String.valueOf(sumNum));
				}
				//UNIONPAY = 银联二维码
				if("UNIONPAY".equals(payWayStr)){
					ylLayout.setVisibility(View.VISIBLE);
					view5.setVisibility(View.VISIBLE);
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();

					tvYLSumMoney.setText(DecimalUtil.doubletoString(amount_dou));
					tvYLSumNum.setText(String.valueOf(total_int));
				}

			}
		}

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
	 * 查询汇总
	 */
	private void querySummary(final String dateStr){
		showWaitDialog();
		final String url;
		if(dateStr.equals(endDate)){
			Log.e("查询日期：", "当天");
			url = NitConfig.querySummaryUrl;
		}else{
			Log.e("查询日期：", "历史");
			url = NitConfig.queryHistorySummaryUrl;
		}
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("eid",loginInitData.getEid());
					userJSON.put("mid",loginInitData.getMid());
					userJSON.put("startTime",SummaryDateUtil.getStartTimeStampTo(dateStr));
					userJSON.put("endTime",SummaryDateUtil.getEndTimeStampTo(dateStr));
					String content = String.valueOf(userJSON);
					Log.e("汇总查询发起请求参数：", content);

					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e("汇总查询返回JSON值：", jsonStr);
					int msg = NetworkUtils.MSG_WHAT_ONE;
					String text = jsonStr;
					sendMessage(msg,text);
				}catch (JSONException e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_JSON_CODE,NetworkUtils.REQUEST_JSON_TEXT);
				}catch (IOException e){
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_IO_CODE,NetworkUtils.REQUEST_IO_TEXT);
				} catch (Exception e) {
					e.printStackTrace();
					sendMessage(NetworkUtils.REQUEST_CODE,NetworkUtils.REQUEST_TEXT);
				}
				
			};
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
			switch (msg.what) {
			case NetworkUtils.MSG_WHAT_ONE:
				try {
					String jsonStr=(String) msg.obj;
					JSONObject job = new JSONObject(jsonStr);
					String status = job.getString("status");
					String message = job.getString("message");
					String dataJson = job.getString("data");
					if("200".equals(status)){
						updateView(dataJson);
					}else{
						Toast.makeText(SummaryActivity.this, message, Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				hideWaitDialog();
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
		};
	};
	
	/**
	 *  显示日期控件
	 */
	private void setQueryDateText(){
		TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
			@Override
			public void handle(String time) {
				seleteDate = time.split(" ")[0];
            	tvTitle.setText(seleteDate);
            	querySummary(seleteDate);
			}
		},startDateTime,endDateTime);
//		timeSelector.setMode(TimeSelector.MODE.YMDHM);//显示 年月日时分（默认）；
                timeSelector.setMode(TimeSelector.MODE.YMD);//只显示 年月日
		timeSelector.setIsLoop(false);//不设置时为true，即循环显示
		timeSelector.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.menu_title_layout:
			setQueryDateText();
			break;
		case R.id.menu_title_tvOption:
			if(summary!=null){
				if(summary.getOrderSumList().size()>0){
					if(posProvider.equals(Constants.NEW_LAND)){
						//初始化打印机
						getPrinter();
						//打印
						NewlandPrintUtil.SummaryPrintText(activity, aidlPrinter, summary,loginInitData,seleteDate);
					}else if(posProvider.equals(Constants.FUYOU_SF)){
						FuyouPrintUtil.SummaryPrintText(activity, printService, summary,loginInitData,seleteDate);
					}

				}else{
					ToastUtil.showText(activity,"暂无汇总信息！",1);
				}
			}else{
				ToastUtil.showText(activity,"暂无汇总信息！",1);
			}
			break;
			default:
				break;
		}
	}
}
