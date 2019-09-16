package com.wanding.xingpos.activity;

import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.ShiftBaseActivity;
import com.wanding.xingpos.adapter.StaffListAdapter;
import com.wanding.xingpos.bean.ShiftResData;
import com.wanding.xingpos.bean.StaffData;
import com.wanding.xingpos.bean.SubReocrdSummaryResData;
import com.wanding.xingpos.bean.SubTimeSummaryResData;
import com.wanding.xingpos.bean.SubTotalSummaryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 结算、交接班Activity
 */
@ContentView(R.layout.activity_shift)
public class ShiftActivity extends ShiftBaseActivity implements OnClickListener {
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



	@ViewInject(R.id.settlement_activity_tvPrint)
	TextView tvPrint;
	@ViewInject(R.id.settlement_activity_tvRecord)
	TextView tvRecord;


	
	private UserLoginResData posPublicData;
	
	AidlDeviceService aidlDeviceService = null;
    AidlPrinter aidlPrinter = null;
    
    private SharedPreferencesUtil sharedPreferencesUtil;
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
	
    private List<StaffData> lsStaff;
    private TextView tvStaffName;
    private String staffName;

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
		tvTitle.setText("交接班");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.VISIBLE);
		tvOption.setText("员工管理");

		posProvider = MainActivity.posProvider;
		Intent intent = getIntent();
		posPublicData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

		if(posProvider.equals(Constants.NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(Constants.FUYOU_SF)){
			initPrintService();
		}

		initListener();
		
		SummaryOrder();
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
	

	


	
	private void initListener() {
		imgBack.setOnClickListener(this);
		tvOption.setOnClickListener(this);
		tvPrint.setOnClickListener(this);
		tvRecord.setOnClickListener(this);
	}
	


	
	/** 结算请求  */
	private void SummaryOrder(){
		showWaitDialog();
		final String url = NitConfig.settlementOrderUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("mid",posPublicData.getMid());
					userJSON.put("eid",posPublicData.getEid());

					String content = String.valueOf(userJSON);
					Log.e("结算发起请求参数：", content);

					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e("结算返回JSON值：", jsonStr);
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
				
			};
		}.start();
	}
	
	/** 结算请求  */
	private void SummaryExitOrder(){
		showWaitDialog();
		final String url = NitConfig.summaryOrderUrl;
		new Thread(){
			@Override
			public void run() {

				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("mid",posPublicData.getMid());
					userJSON.put("eid",posPublicData.getEid());
					userJSON.put("name",staffName);

					String content = String.valueOf(userJSON);
					Log.e("交班退出发起请求参数：", content);

					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e("交班退出返回JSON值：", jsonStr);
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
					String jsonStr = (String) msg.obj;
					try {
						JSONObject job = new JSONObject(jsonStr);
						if(job.getBoolean("isSuccess")){
							summaryJson(jsonStr);
						}else{
							String errorStr = job.getString("errorMessage");
							if(Utils.isNotEmpty(errorStr)){
								ToastUtil.showText(activity,errorStr,1);
							}else{
								ToastUtil.showText(activity,"查询失败！",1);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ToastUtil.showText(activity,"查询失败！",1);
					}
					hideWaitDialog();
					break;
				case NetworkUtils.MSG_WHAT_TWO:
					String record_jsonStr = (String) msg.obj;
					try {
						JSONObject job = new JSONObject(record_jsonStr);
						if(job.getBoolean("isSuccess")){
							summaryRecordJson(record_jsonStr);
						}else{
							String errorStr = job.getString("errorMessage");
							if(Utils.isNotEmpty(errorStr)){
								ToastUtil.showText(activity,errorStr,1);
							}else{
								ToastUtil.showText(activity,"查询失败！",1);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ToastUtil.showText(activity,"查询失败！",1);
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
	
	/** 结算请求返回JSON数据处理 */
	private void summaryJson(String jsonStr){
		ShiftResData summary = null;
		try {
			JSONObject job = new JSONObject(jsonStr);
			summary = new ShiftResData();
			JSONObject obj = job.getJSONObject("data");
			JSONArray timeArray = obj.getJSONArray("time");
			ArrayList<SubTimeSummaryResData> timeList = new ArrayList<SubTimeSummaryResData>();
			for (int i = 0; i < timeArray.length(); i++) {
				JSONObject time_obj = timeArray.optJSONObject(i);
				SubTimeSummaryResData time = new SubTimeSummaryResData();
				
				String time_typeStr = time_obj.getString("type");
				Log.e("time_typeStr:", time_typeStr);
				if(time_typeStr==null|| "null".equals(time_typeStr)){
					time.setType("");
				}else{
					time.setType(time_typeStr);
				}
				Log.e("timeType:", time.getType());
				
				String time_totalCountStr = time_obj.getString("totalCount");
				Log.e("time_totalCountStr:", time_totalCountStr);
				if(time_totalCountStr==null|| "null".equals(time_totalCountStr)){
					time.setTotalCount("");
				}else{
					time.setTotalCount(time_totalCountStr);
					
				}
				Log.e("timeTotalCount:", time.getTotalCount());
				
				String time_moneyStr = time_obj.getString("money");
				if(time_moneyStr==null|| "null".equals(time_moneyStr)){
					time.setMoney("");
				}else{
					
					time.setMoney(time_moneyStr);
				}
				Log.e("timeMoney:", time.getMoney());
				
				String time_modeStr = time_obj.getString("mode");
				if(time_modeStr==null|| "null".equals(time_modeStr)){
					
					time.setMode("");
					
				}else{

					time.setMode(time_modeStr);
				}
				Log.e("timeMode:", time.getMode());
				timeList.add(time);
			}
			summary.setTimeList(timeList);
			
			JSONArray reocrdArray = obj.getJSONArray("reocrd");
			ArrayList<SubReocrdSummaryResData> reocrdList = new ArrayList<SubReocrdSummaryResData>();
			for (int i = 0; i < reocrdArray.length(); i++) {
				JSONObject reocrd_obj = reocrdArray.optJSONObject(i);
				SubReocrdSummaryResData reocrd = new SubReocrdSummaryResData();
				
				String reocrd_typeStr = reocrd_obj.getString("type");
				if(reocrd_typeStr==null|| "null".equals(reocrd_typeStr)){
					
					reocrd.setType("");
					
				}else{
					reocrd.setType(reocrd_typeStr);
				}
				
				String reocrd_totalCountStr = reocrd_obj.getString("totalCount");
				if(reocrd_totalCountStr==null|| "null".equals(reocrd_totalCountStr)){
					reocrd.setTotalCount("0");
				}else{
					reocrd.setTotalCount(reocrd_totalCountStr);
					
				}
				
				String reocrd_moneyStr = reocrd_obj.getString("money");
				if(reocrd_moneyStr ==null|| "null".equals(reocrd_moneyStr)){
					reocrd.setMoney("0.00");
				}else{
					
					reocrd.setMoney(reocrd_moneyStr);
				}
				
				String reocrd_modeStr = reocrd_obj.getString("mode");
				if(reocrd_modeStr==null|| "null".equals(reocrd_modeStr)){
					reocrd.setMode("");
				}else{
					reocrd.setMode(reocrd_modeStr);
				}
				reocrdList.add(reocrd);
			}
			summary.setReocrdList(reocrdList);
			
			JSONArray totalArray = obj.getJSONArray("total");
			ArrayList<SubTotalSummaryResData> totalList = new ArrayList<SubTotalSummaryResData>();
			for (int i = 0; i < totalArray.length(); i++) {
				JSONObject total_obj = totalArray.optJSONObject(i);
				SubTotalSummaryResData total = new SubTotalSummaryResData();
				
				
				String total_typeStr = total_obj.getString("type");
				if(total_typeStr==null|| "null".equals(total_typeStr)){
					total.setType("");
				}else{
					
					total.setType(total_typeStr);
				}
				
				String total_totalCountStr = total_obj.getString("totalCount");
				if(total_totalCountStr==null|| "null".equals(total_totalCountStr)){
					total.setTotalCount("0");
				}else{
					
					total.setTotalCount(total_totalCountStr);
				}
				
				String total_moneyStr = total_obj.getString("money");
				if(total_moneyStr==null|| "null".equals(total_moneyStr)){
					total.setMoney("0.00");
				}else{
					
					total.setMoney(total_moneyStr);
				}
				
				String total_modeStr = total_obj.getString("mode");
				if(total_modeStr==null|| "null".equals(total_modeStr)){
					total.setMode("total");
				}else{
					
					total.setMode(total_modeStr);
				}
				totalList.add(total);
			}
			summary.setTotalList(totalList);
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		updateView(summary);
		
	}
	/** 交接班退出请求返回JSON数据处理 */
	private void summaryRecordJson(String jsonStr){
		ShiftResData summary = null;
		try {
			JSONObject job = new JSONObject(jsonStr);
			summary = new ShiftResData();
			JSONObject obj = job.getJSONObject("data");
			JSONArray timeArray = obj.getJSONArray("time");
			ArrayList<SubTimeSummaryResData> timeList = new ArrayList<SubTimeSummaryResData>();
			for (int i = 0; i < timeArray.length(); i++) {
				JSONObject time_obj = timeArray.optJSONObject(i);
				SubTimeSummaryResData time = new SubTimeSummaryResData();
				
				String time_typeStr = time_obj.getString("type");
				Log.e("time_typeStr:", time_typeStr);
				if(time_typeStr==null|| "null".equals(time_typeStr)){
					time.setType("");
				}else{
					time.setType(time_typeStr);
				}
				Log.e("timeType:", time.getType());
				
				String time_totalCountStr = time_obj.getString("totalCount");
				Log.e("time_totalCountStr:", time_totalCountStr);
				if(time_totalCountStr==null|| "null".equals(time_totalCountStr)){
					time.setTotalCount("");
				}else{
					time.setTotalCount(time_totalCountStr);
					
				}
				Log.e("timeTotalCount:", time.getTotalCount());
				
				String time_moneyStr = time_obj.getString("money");
				if(time_moneyStr==null|| "null".equals(time_moneyStr)){
					time.setMoney("");
				}else{
					
					time.setMoney(time_moneyStr);
				}
				Log.e("timeMoney:", time.getMoney());
				
				String time_modeStr = time_obj.getString("mode");
				if(time_modeStr==null|| "null".equals(time_modeStr)){
					
					time.setMode("");
					
				}else{
					
					time.setMode(time_modeStr);
				}
				Log.e("timeMode:", time.getMode());
				timeList.add(time);
			}
			summary.setTimeList(timeList);
			
			JSONArray reocrdArray = obj.getJSONArray("reocrd");
			ArrayList<SubReocrdSummaryResData> reocrdList = new ArrayList<SubReocrdSummaryResData>();
			for (int i = 0; i < reocrdArray.length(); i++) {
				JSONObject reocrd_obj = reocrdArray.optJSONObject(i);
				SubReocrdSummaryResData reocrd = new SubReocrdSummaryResData();
				
				String reocrd_typeStr = reocrd_obj.getString("type");
				if(reocrd_typeStr==null|| "null".equals(reocrd_typeStr)){
					
					reocrd.setType("");
					
				}else{
					reocrd.setType(reocrd_typeStr);
				}
				
				String reocrd_totalCountStr = reocrd_obj.getString("totalCount");
				if(reocrd_totalCountStr==null|| "null".equals(reocrd_totalCountStr)){
					reocrd.setTotalCount("0");
				}else{
					reocrd.setTotalCount(reocrd_totalCountStr);
					
				}
				
				String reocrd_moneyStr = reocrd_obj.getString("money");
				if(reocrd_moneyStr ==null|| "null".equals(reocrd_moneyStr)){
					reocrd.setMoney("0.00");
				}else{
					
					reocrd.setMoney(reocrd_moneyStr);
				}
				
				String reocrd_modeStr = reocrd_obj.getString("mode");
				if(reocrd_modeStr==null|| "null".equals(reocrd_modeStr)){
					reocrd.setMode("");
				}else{
					reocrd.setMode(reocrd_modeStr);
				}
				reocrdList.add(reocrd);
			}
			summary.setReocrdList(reocrdList);
			
			JSONArray totalArray = obj.getJSONArray("total");
			ArrayList<SubTotalSummaryResData> totalList = new ArrayList<SubTotalSummaryResData>();
			for (int i = 0; i < totalArray.length(); i++) {
				JSONObject total_obj = totalArray.optJSONObject(i);
				SubTotalSummaryResData total = new SubTotalSummaryResData();
				
				
				String total_typeStr = total_obj.getString("type");
				if(total_typeStr==null|| "null".equals(total_typeStr)){
					total.setType("");
				}else{
					
					total.setType(total_typeStr);
				}
				
				String total_totalCountStr = total_obj.getString("totalCount");
				if(total_totalCountStr==null|| "null".equals(total_totalCountStr)){
					total.setTotalCount("0");
				}else{
					
					total.setTotalCount(total_totalCountStr);
				}
				
				String total_moneyStr = total_obj.getString("money");
				if(total_moneyStr==null|| "null".equals(total_moneyStr)){
					total.setMoney("0.00");
				}else{
					
					total.setMoney(total_moneyStr);
				}
				
				String total_modeStr = total_obj.getString("mode");
				if(total_modeStr==null|| "null".equals(total_modeStr)){
					total.setMode("total");
				}else{
					
					total.setMode(total_modeStr);
				}
				totalList.add(total);
			}
			summary.setTotalList(totalList);
			
			
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(posProvider.equals(Constants.NEW_LAND)){
			//初始化打印机
			getPrinter();
			//打印
			NewlandPrintUtil.SettlementPrintText(activity, aidlPrinter, summary,posPublicData,staffName);
		}else if(posProvider.equals(Constants.FUYOU_SF)){
			//打印
			FuyouPrintUtil.SettlementPrintText(activity,printService,summary,posPublicData,staffName);
		}

		
		//重新查询数据刷新界面
		SummaryOrder();
		
	}
	
	/**
	 * 显示员工列表选择框
	 */
	private void showStaffListDialog(){
		String isDelete = "2";
		View view = LayoutInflater.from(this).inflate(R.layout.staff_select_dialog, null);
		ListView listView = (ListView) view.findViewById(R.id.staff_select_listView);
		StaffListAdapter adapter = new StaffListAdapter(ShiftActivity.this, lsStaff, isDelete);
		listView.setAdapter(adapter);
		final Dialog myDialog = new Dialog(this,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				StaffData staff = lsStaff.get(position);
				String staffNameStr = staff.getName();
				tvStaffName.setText(staffNameStr);
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}
	
	/**
	 * 交班退出操作提示框
	 */
	private void showOptionHintDialog(){
		View view = LayoutInflater.from(this).inflate(R.layout.shift_hint_dialog, null);
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.shift_hint_seleteStaffLayout);
		tvStaffName = (TextView) view.findViewById(R.id.shift_hint_tvStaffName);
		tvStaffName.setText(posPublicData.getEname());
		TextView btok = (TextView) view.findViewById(R.id.shift_hint_tvOk);
		TextView btCancel = (TextView) view.findViewById(R.id.shift_hint_tvCancel);
		final Dialog myDialog = new Dialog(this,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		layout.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void onClick(View v) {
				String defaultNameStr = posPublicData.getEname();
				boolean isShow = true;
				//取出员工集合
				try {
					String staffStr = MySerialize.getObject("staff", ShiftActivity.this);
					if(Utils.isNotEmpty(staffStr)){
						lsStaff = (List<StaffData>) MySerialize.deSerialization(staffStr);
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(lsStaff==null||lsStaff.size()<=0){
					lsStaff = new ArrayList<StaffData>();
				}else{
					for (int i = 0; i < lsStaff.size(); i++) {
						StaffData data = lsStaff.get(i);
						String name = data.getName();
						if(defaultNameStr.equals(name)){
							isShow = false;
						}
					}
				}
				if(isShow){
					//显示之前给员工集合加上默认值
					StaffData staff = new StaffData();
					staff.setName(defaultNameStr);
					lsStaff.add(0, staff);
				}
				
				//显示列表选择框
				showStaffListDialog();
				
			}
		});
		btok.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				staffName = tvStaffName.getText().toString();
				SummaryExitOrder();
				//关闭应用
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
		myDialog.setCancelable(false);
	}
	

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.menu_title_tvOption:
			intent.setClass(activity, StaffListActivity.class);
			startActivity(intent);
			break;
		case R.id.settlement_activity_tvPrint://交班退出
			
			showOptionHintDialog();

			
			break;
		case R.id.settlement_activity_tvRecord://交班记录
			intent = new Intent();
			intent.setClass(activity, ShiftRecordActivity.class);
			intent.putExtra("userLoginData",posPublicData);
			startActivity(intent);
			break;
			default:
				break;
		}
	}
	
}
