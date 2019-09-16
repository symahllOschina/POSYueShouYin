package com.wanding.xingpos.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.Constants;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.baidu.tts.util.MySyntherizer;
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
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.Calendar;

import static com.wanding.xingpos.Constants.FUYOU_SF;

/** 银行卡退款界面 */
@ContentView(R.layout.activity_card_refund)
public class CardRefundActivity extends BaseActivity implements OnClickListener {

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


	@ViewInject(R.id.card_refund_etOrderId)
	private EditText etOrderId;
	@ViewInject(R.id.card_refund_tvOk)
	private TextView tvOk;

	


	private UserLoginResData posPublicData;
	
	// 主控制类，所有合成控制方法从这个类开始
    protected MySyntherizer synthesizer;


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
		tvTitle.setText("消费撤销");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		synthesizer = MainActivity.synthesizer;
		posProvider = MainActivity.posProvider;
		Intent intent = getIntent();
		posPublicData = (UserLoginResData) intent.getSerializableExtra("userLoginData");


		initListener();
	}
	

	
	/** 
	 * 初始化界面控件
	 */
	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOk.setOnClickListener(this);
	}
	
	/**
     * speak 实际上是调用 synthesize后，获取音频流，然后播放。
     * 获取音频流的方式见SaveFileActivity及FileSaveListener
     * 需要合成的文本text的长度不能超过1024个GBK字节。
     */
    private void speak(String total) {
    	String payTypeStr = "银行卡退款";
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

	
	/** 退款 */
	private void refundStepOne(){
		String orderTextStr = etOrderId.getText().toString().trim();
		if(Utils.isEmpty(orderTextStr)){
			ToastUtil.showText(activity,"凭证号不能为空！",1);
			return;
		}
		if(Constants.NEW_LAND.equals(posProvider)){
			Log.e("支付通道：", "星POS");
			NewPosServiceUtil.cardRefundReq(CardRefundActivity.this, orderTextStr);
		}else if(Constants.FUYOU_SF.equals(posProvider)){
			Log.e("支付通道：", "富友POS");
			//设备号
			String deviceNum = posPublicData.getTrmNo_pos();
			String pos_order_noStr = RandomStringGenerator.getFURandomNum(deviceNum);
			Log.e("富友退款流水号",pos_order_noStr);
			FuyouPosServiceUtil.cardRefundReq(CardRefundActivity.this, orderTextStr,pos_order_noStr);
		}

	}
	/**
	 * 新大陆界面访问成功返回
	 */
	private void newlandResult(Bundle bundle){
		String msgTp = bundle.getString("msg_tp");
		if (TextUtils.equals(msgTp, "0210")) {
			String txndetail = bundle.getString("txndetail");
			Log.e("txndetail退款成功返回信息：", txndetail);
			try {
				JSONObject job = new JSONObject(txndetail);
				String transamount = job.getString("transamount");

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
				sharedPreferencesUtil.put("cardPayType", "refund");
				sharedPreferencesUtil.put("cardOption", cardOption);

				speak(transamount);
//							finish();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			    	   Toast.makeText(context, "退款成功！", Toast.LENGTH_LONG).show();
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
		String orderNumberStr = bundle.getString("orderNumber");//订单流水号
		String merchantldStr = bundle.getString("merchantld");//商户号
		String terminalldStr = bundle.getString("terminalld");//终端号
		String merchantNameStr = bundle.getString("merchantName");//商户名称
		String transactionTypeStr = bundle.getString("transactionType");//交易类型

		Log.e("返回的支付金额信息：", amountStr);
		Log.e("交易参考号：", referenceNoStr);
		Log.e("返回的卡类型：", typeStr);
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
		sharedPreferencesUtil.put("cardPayType", "refund");
		sharedPreferencesUtil.put("cardOption", cardOption);
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
			case R.id.menu_title_imageView:
				finish();
				break;
			case R.id.card_refund_tvOk:
				refundStepOne();
				break;
				default:
					break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle bundle=data.getExtras();
		/**
		 * 银行卡退款返回
		 */
		if (requestCode == FuyouPosServiceUtil.PAY_REQUEST_CODE) {
			if(bundle != null){
				switch (resultCode) {
					// 退款成功
					case Activity.RESULT_OK:
						if(Constants.NEW_LAND.equals(posProvider)){
							newlandResult(bundle);
						}else if(Constants.FUYOU_SF.equals(posProvider)){
							fuyouResult(bundle);
						}
						break;
					// 支付取消
					case Activity.RESULT_CANCELED:
						String reason = "退款取消";
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
						break;
					default:
						break;
				}
			}else{
				ToastUtil.showText(activity,"支付返回失败！",1);
			}

		}
	}
}
