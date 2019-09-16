package com.wanding.xingpos.activity;

import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.nld.cloudpos.aidl.AidlDeviceService;
import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.printutil.NewlandPrintUtil;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.SharedPreferencesUtil;
import com.wanding.xingpos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**  订单详情界面 */
@ContentView(R.layout.activity_order_details)
public class OrderDetailsActivity extends BaseActivity implements OnClickListener {


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

	/** 商户订单号，渠道订单号，交易时间，交易金额，交易渠道，交易状态 */
	@ViewInject(R.id.order_details_txMerchant_no)
	TextView tvMerchantNo;
	@ViewInject(R.id.order_details_tvTransactionId)
	TextView tvTransactionId;
	@ViewInject(R.id.order_details_txTerminal_time)
	TextView tvTerminalTime;
	@ViewInject(R.id.order_details_txTerminal_total)
	TextView tvTerminalTotal;
	@ViewInject(R.id.order_details_txTerminal_type)
	TextView tvTerminalType;
	@ViewInject(R.id.order_details_txPay_state)
	TextView tvPayState;
	
	/** 完成，重打印  */
	@ViewInject(R.id.order_details_tvOk)
	TextView tvOK;
	@ViewInject(R.id.order_details_tvPrint)
	TextView tvPrint;



	private UserLoginResData loginInitData;
	private OrderDetailData order;//订单对象
	
	AidlDeviceService aidlDeviceService = null;

    AidlPrinter aidlPrinter = null;
    private String TAG = "lyc";


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

	/** 打印联数 printNumNo:不打印，printNumOne:一联，printNumTwo:两联  */
	private String printNum = "printNumNo";
	/**  打印字体大小 isDefault:true默认大小，false即为大字体 */
	private boolean isDefault = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("交易详情");
		posProvider = MainActivity.posProvider;
		Intent intent = getIntent();
		loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
		order = (OrderDetailData) intent.getSerializableExtra("order");

		if(posProvider.equals(Constants.NEW_LAND)){
			//绑定打印机服务
			bindServiceConnection();
		}else if(posProvider.equals(Constants.FUYOU_SF)){
			initPrintService();
		}

		SharedPreferencesUtil sharedPreferencesUtil = new SharedPreferencesUtil(activity, "printing");
		printNum = (String) sharedPreferencesUtil.getSharedPreference("printNumKey", printNum);
		isDefault = (boolean) sharedPreferencesUtil.getSharedPreference("isDefaultKey", isDefault);


		initListener();


		updateViewData();
		
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
	

	
	/** 
	 * 初始化界面控件
	 */
	private void initListener(){

		imgBack.setOnClickListener(this);
		tvOK.setOnClickListener(this);
		tvPrint.setOnClickListener(this);
	}






	

	/** 界面数据初始化 */
    private void updateViewData(){
    	
    	try {
			tvMerchantNo.setText("");
			tvTransactionId.setText("");
			tvTerminalTime.setText("");
			tvTerminalTotal.setText("");
			tvTerminalType.setText("");
			tvPayState.setText("");
			
			if(order!=null){
				//商户订单号
				tvMerchantNo.setText(order.getOrderId());
				//渠道订单号
				String transactionIdStr = order.getTransactionId();
				String transactionId = "";
				if(Utils.isNotEmpty(transactionIdStr)){
					transactionId = transactionIdStr;
				}
				tvTransactionId.setText(transactionId);
				//交易时间
				tvTerminalTime.setText(DateTimeUtil.stampToDate(Long.parseLong(order.getPayTime())));
				//交易金额
				tvTerminalTotal.setText(DecimalUtil.StringToPrice(order.getGoodsPrice()));
				//交易渠道//WX=微信，ALI=支付宝，BEST=翼支付
				String payTypeStr = order.getPayWay();
				String payType = "";
				if("WX".equals(payTypeStr)){
					payType = "微信";
				}else if("ALI".equals(payTypeStr)){
					payType = "支付宝";
				}else if("BEST".equals(payTypeStr)){
					payType = "翼支付";
				}else if("DEBIT".equals(payTypeStr)){
					//DEBIT= 借记卡       CREDIT=贷记卡 
					payType = "银行卡(借记卡)";
				}else if("CREDIT".equals(payTypeStr)){
					//DEBIT= 借记卡       CREDIT=贷记卡
					payType = "银行卡(贷记卡)";
				}else if("UNIONPAY".equals(payTypeStr)){
					//UNIONPAY = 银联二维码
					payType = "银联二维码";
				}else if("BANK".equals(payTypeStr)){
					//BANK = 银行卡
					payType = "银行卡";
				}else{
					payType = "未知";
				}
				tvTerminalType.setText(payType);
				String orderStatus = "未知状态";
				String orderTypeStr = order.getOrderType();
				if(orderTypeStr!=null&&!"".equals(orderTypeStr) &&!"null".equals(orderTypeStr)){
					//先判断是支付交易还是退款交易 0正向 ,1退款
					if("0".equals(orderTypeStr)){
						//判断交易状态状态status 状态为支付、预支付、退款等	0准备支付1支付完成2支付失败3.包括退款5.支付未知
						String statusStr = order.getStatus();
						if(statusStr!=null&&!"null".equals(statusStr) && "1".equals(statusStr)){
							orderStatus = "支付成功";
						}else if(statusStr!=null&&!"null".equals(statusStr) && "3".equals(statusStr)){
							orderStatus = "包含退款";
						}else if(statusStr!=null&&!"null".equals(statusStr) && "4".equals(statusStr)){
							orderStatus = "全部退款";
						}else if(statusStr!=null&&!"null".equals(statusStr) && "5".equals(statusStr)){
							orderStatus = "支付未知";
						}else{
							orderStatus = "支付失败";
						}
					}else if("1".equals(orderTypeStr)){
						//判断退款状态
						String refund_statusStr = order.getStatus();
						if(refund_statusStr!=null&&!"null".equals(refund_statusStr) && "1".equals(refund_statusStr)){
							orderStatus = "退款成功";
						}else{
							orderStatus = "退款失败";
						}
					}
				}
				tvPayState.setText(orderStatus);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	/**  打印下一联提示窗口 */
	private void showPrintTwoDialog(){
		View view = LayoutInflater.from(activity).inflate(R.layout.printtwo_dialog_activity, null);
		TextView btok = (TextView) view.findViewById(R.id.printtwo_dialog_tvOk);
		final Dialog myDialog = new Dialog(activity,R.style.dialog);
		Window dialogWindow = myDialog.getWindow();
		WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
		dialogWindow.setAttributes(params);
		myDialog.setContentView(view);
		btok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int index = 2;
				if(posProvider.equals(Constants.NEW_LAND)){
					NewlandPrintUtil.orderDetailsPrintText(activity, aidlPrinter, order, loginInitData,isDefault,index);
				}else if(posProvider.equals(Constants.FUYOU_SF)){
					FuyouPrintUtil.orderDetailsPrintText(activity, printService, order, loginInitData,isDefault,index);
				}


				myDialog.dismiss();

			}
		});
		myDialog.show();
	}
	

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.order_details_tvOk:
			finish();
			break;
		case R.id.order_details_tvPrint:
			//交易未知不打印小票
			boolean orderStatus = true;
			String orderTypeStr = order.getOrderType();
			if(orderTypeStr!=null&&!"".equals(orderTypeStr) &&!"null".equals(orderTypeStr)){
				//先判断是支付交易还是退款交易 0正向 ,1退款
				if("0".equals(orderTypeStr)){
					//判断交易状态状态status 状态为支付、预支付、退款等	0准备支付1支付完成2支付失败3.包括退款5.支付未知
					String statusStr = order.getStatus();
					if(statusStr!=null&&!"null".equals(statusStr) && "1".equals(statusStr)){

					}else if(statusStr!=null&&!"null".equals(statusStr) && "3".equals(statusStr)){

					}else if(statusStr!=null&&!"null".equals(statusStr) && "4".equals(statusStr)){

					}else if(statusStr!=null&&!"null".equals(statusStr) && "5".equals(statusStr)){
						orderStatus = false;
					}else{
						orderStatus = false;
					}
				}
			}


			String payTypeStr = order.getPayWay();
			if(orderStatus){
				if(!"DEBIT".equals(payTypeStr) &&!"CREDIT".equals(payTypeStr) &&!"BANK".equals(payTypeStr)){
					int index = 1;
					if(posProvider.equals(Constants.NEW_LAND)){
						//初始化打印机
						getPrinter();
						if("printNumNo".equals(printNum)){

						}else if("printNumOne".equals(printNum)){
							//打印
							NewlandPrintUtil.orderDetailsPrintText(activity, aidlPrinter, order, loginInitData,isDefault,index);

						}else if("printNumTwo".equals(printNum)){
							//打印
							NewlandPrintUtil.orderDetailsPrintText(activity, aidlPrinter, order, loginInitData,isDefault,index);
							try {
								Thread.sleep(NewlandPrintUtil.time);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//弹出对话框提示打印下一联

							showPrintTwoDialog();

						}
					}else if(posProvider.equals(Constants.FUYOU_SF)){
						if("printNumNo".equals(printNum)){

						}else if("printNumOne".equals(printNum)){
							//打印
							FuyouPrintUtil.orderDetailsPrintText(activity, printService, order, loginInitData,isDefault,index);

						}else if("printNumTwo".equals(printNum)){
							//打印
							FuyouPrintUtil.orderDetailsPrintText(activity, printService, order, loginInitData,isDefault,index);
							try {
								Thread.sleep(FuyouPrintUtil.time);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							//弹出对话框提示打印下一联

							showPrintTwoDialog();

						}

					}
				}else{
					Toast.makeText(OrderDetailsActivity.this, "银行卡交易不支持明细补打！", Toast.LENGTH_LONG).show();
				}

			}else{
				Toast.makeText(OrderDetailsActivity.this, "该订单状态未知，不打印小票！", Toast.LENGTH_LONG).show();
			}
			break;
			default:
				break;
			
		}
	}
}
