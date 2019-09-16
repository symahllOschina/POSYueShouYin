package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.ShiftRecordAdapte;
import com.wanding.xingpos.adapter.ShiftRecordAdapte.OnSetRecordDetail;
import com.wanding.xingpos.bean.ShiftRecordListResData;
import com.wanding.xingpos.bean.ShiftRecordResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.date.picker.TimeSelector;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.summary.util.SettlementDateUtil;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 结算、交接班Activity
 */
@ContentView(R.layout.activity_shift_record)
public class ShiftRecordActivity extends BaseActivity implements OnClickListener,OnSetRecordDetail,XListView.IXListViewListener {
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




	@ViewInject(R.id.select_datetime_tvStartTime)
	TextView tvStartDateTime;
	@ViewInject(R.id.select_datetime_tvEndTime)
	TextView tvEndDateTime;
	@ViewInject(R.id.select_datetime_tvOk)
	TextView tvOk;
	@ViewInject(R.id.settlement_record_listView)
	XListView mListView;





	private String pickerStartDateTime,pickerEndDateTime,pickerSeleteDateTime;//日期选择控件的选择范围，起始日期和结束日期,以及选择的日期时间
	private String startDateTimeStr,endDateTimeStr,dateTimeStr,dateStr,timeStr;//
	
	private UserLoginResData posPublicData;
	
	private List<ShiftRecordResData> lsRecord = new ArrayList<ShiftRecordResData>();
	private ShiftRecordAdapte mAdapter;
	
	private String startTime = "";
	private String endTime = "";
	
	private int pageNo = 1;//默认加载第一页
	private static final int pageSize = 20;//默认一页加载xx条数据（死值不变）
	
	private static final int REFRESH = 100;  
    private static final int LOADMORE = 200;  
    private static final int NOLOADMORE = 300;  
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，
    //交易总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("交班记录");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");


		initListener();
		initData();
		

	}
	
	private void initData(){
		Intent intent = getIntent();
		posPublicData = (UserLoginResData) intent.getSerializableExtra("userLoginData");
		//起始日期为一个月前
		pickerStartDateTime = DateTimeUtil.getAMonthDateStr(-1, "yyyy-MM-dd HH:mm");
		//初始化日期时间（即系统默认时间）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        dateTimeStr = sdf.format(new Date());
        pickerEndDateTime = dateTimeStr;
        pickerSeleteDateTime = dateTimeStr;
        dateStr = dateTimeStr.split(" ")[0];
        timeStr = dateTimeStr.split(" ")[1];
        //初始化显示的开始时间，结束时间
        String startTimeStr = "00:00";
        startDateTimeStr = dateStr + " "+startTimeStr;
        endDateTimeStr = dateTimeStr;
        
        tvStartDateTime.setText(startDateTimeStr);
        tvEndDateTime.setText(endDateTimeStr);


		//查询交接班记录参数设置
		setQueryRecordParams();
	}


	
	private void initListener() {
		imgBack.setOnClickListener(this);
		tvStartDateTime.setOnClickListener(this);
		tvEndDateTime.setOnClickListener(this);
		tvOk.setOnClickListener(this);
		
		mListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
		mListView.setPullRefreshEnable(false);//是否可以下拉刷新,默认可以下拉
		mListView.setXListViewListener(this);//注册刷新和加载更多接口
	}
	
	private void setQueryRecordParams(){
		String startTimeStr = tvStartDateTime.getText().toString();
		String endTimeStr = tvEndDateTime.getText().toString();
		Log.e("起始时间：", startTimeStr);
		Log.e("结束时间：", endTimeStr);
		startTime = SettlementDateUtil.getStartTimeStampTo(startTimeStr);
		endTime = SettlementDateUtil.getEndTimeStampTo(endTimeStr);
		if(Long.parseLong(startTime)<=Long.parseLong(endTime)){
			
			queryRecord(startTime,endTime,pageNo,pageSize);
		}else{
			Toast.makeText(ShiftRecordActivity.this, "开始时间不能大于结束时间！", Toast.LENGTH_LONG).show();
		}
		
		
	}
	
	/**
	 *  查询交接班记录
	 */
	private void queryRecord(final String startTime, final String endTime, final int pageNo, final int pageSize){
		showWaitDialog();
		final String url = NitConfig.settlementRecordUrl;
		new Thread(){
			@Override
			public void run() {
				try {
					JSONObject userJSON = new JSONObject();
					userJSON.put("mid",posPublicData.getMid());
					userJSON.put("eid",posPublicData.getEid());
					userJSON.put("startTime",startTime);
					userJSON.put("endTime",endTime);
					userJSON.put("pageNo",pageNo+"");
					userJSON.put("pageSize",pageSize+"");
					String content = String.valueOf(userJSON);
					Log.e("结算记录发起请求参数：", content);
					String jsonStr = HttpURLConnectionUtil.doPos(url,content);
					Log.e("结算记录返回JSON值：", jsonStr);
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
			case REFRESH:
				mAdapter.notifyDataSetChanged();
                //更新完毕
				onLoad();
				break;
			case LOADMORE:
				mListView.setPullLoadEnable(true);//是否可以上拉加载更多
				mAdapter.notifyDataSetChanged();
                // 加载更多完成
				onLoad();
				break;
			case NOLOADMORE:
				mListView.setPullLoadEnable(false);//是否可以上拉加载更多
				// 加载更多完成-->>已没有更多
				onLoad();
				break;
			case NetworkUtils.MSG_WHAT_ONE:
				String jsonStr;
				try {
					jsonStr = (String) msg.obj;
					JSONObject job = new JSONObject(jsonStr);
					if(job.getBoolean("isSuccess")){
						String dataJson = job.getString("data");
						settlementRecordJson(dataJson);
					}else{
						ToastUtil.showText(activity,"查询失败！",1);
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
	
	/**
	 * 交班记录成功返回
	 */
	private void settlementRecordJson(String dataJson){
		
		Gson gjson  =  GsonUtils.getGson();
		java.lang.reflect.Type type = new TypeToken<ShiftRecordListResData>() {}.getType();
		ShiftRecordListResData order = gjson.fromJson(dataJson, type);
		//获取总条数
		orderListTotalCount = order.getTotalCount();
		Log.e("总条数：", orderListTotalCount+"");
		List<ShiftRecordResData> orderList = new ArrayList<ShiftRecordResData>();
		//获取的list
		orderList = order.getOrderList();
		getMoerNum = orderList.size();
		Log.e("每次获取条数：", getMoerNum+"");
		if(pageNo == 1){
			lsRecord.clear();
		}
		lsRecord.addAll(orderList); 
		Log.e("查询数据：", lsRecord.size()+""+"条");
		//关闭上拉或下拉View，刷新Adapter
		if("0".equals(loadMore)){
			mAdapter = new ShiftRecordAdapte(this, lsRecord);
			mAdapter.ItemOnSetRecordDetail(this);
			mListView.setAdapter(mAdapter);
			if(lsRecord.size()<=orderListTotalCount&&getMoerNum==pageSize){
				Message msg1 = new Message();
                msg1.what = LOADMORE;  
                handler.sendEmptyMessageDelayed(LOADMORE, 0);
			}else{
				Message msg1 = new Message();
                msg1.what = NOLOADMORE;
				handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
			}
		}else if("1".equals(loadMore)){
			Message msg1 = new Message();
            msg1.what = REFRESH;
			handler.sendEmptyMessageDelayed(REFRESH, 2000);
		}else if("2".equals(loadMore)){
			Message msg1 = new Message();
            msg1.what = LOADMORE;
			handler.sendEmptyMessageDelayed(LOADMORE, 2000);
		}
		
		
	}

	/**
	 *  显示日期控件
	 */
	private void setQueryDateText(final TextView tvText){

		TimeSelector timeSelector = new TimeSelector(this, new TimeSelector.ResultHandler() {
			@Override
			public void handle(String time) {
				tvText.setText(time);
			}
		},pickerStartDateTime,pickerEndDateTime);
		timeSelector.setMode(TimeSelector.MODE.YMDHM);//显示 年月日时分（默认）；
//                timeSelector.setMode(TimeSelector.MODE.YMD);//只显示 年月日
		timeSelector.setIsLoop(false);//不设置时为true，即循环显示
		timeSelector.show();
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_title_imageView:
			finish();
			break;
		case R.id.select_datetime_tvStartTime://开始时间
			setQueryDateText(tvStartDateTime);
			break;
		case R.id.select_datetime_tvEndTime://结束时间
			setQueryDateText(tvEndDateTime);
			break;
		case R.id.select_datetime_tvOk://确定
			//查询交接班记录参数设置
			pageNo = 1;
			setQueryRecordParams();
			break;
			default:
				break;
		}
	}

	/**  Adapter的Item暴露的查看按钮实现接口  */
	@Override
	public void getDetail(ShiftRecordResData record) {
		Intent in = new Intent();
		in.setClass(this, ShiftRecordDetailActivity.class);
		in.putExtra("record", record);
		in.putExtra("userLoginData",posPublicData);
		startActivity(in);
	}
	
	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(new Date().toLocaleString());
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLoadMore() {
		loadMore = "2";
		if(lsRecord.size()<=orderListTotalCount&&getMoerNum==pageSize){
        	//已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
        	if(Long.parseLong(startTime)<=Long.parseLong(endTime)){
        		pageNo = pageNo + 1;
    			queryRecord(startTime,endTime,pageNo,pageSize);
    		}else{
    			Toast.makeText(ShiftRecordActivity.this, "开始时间不能大于结束时间！", Toast.LENGTH_LONG).show();
    		}
        }else{
        	//没有数据执行两秒关闭view
			handler.postDelayed(new Runnable() {
                @Override
                public void run() {  
                	Message msg = new Message();
                    msg.what = NOLOADMORE;
					handler.sendMessage(msg);
                }  
            }, 1000); 
        	
        }
	}
	
}
