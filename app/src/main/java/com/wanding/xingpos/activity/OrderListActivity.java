package com.wanding.xingpos.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.MainActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.OrderListAdapte;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.bean.OrderListData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 交易明细Activity
 */
@ContentView(R.layout.activity_order_list)
public class OrderListActivity extends BaseActivity implements View.OnClickListener,XListView.IXListViewListener,AdapterView.OnItemClickListener {

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
    @ViewInject(R.id.order_list_mXListView)
    XListView mListView;

    private String posProvider;
    private UserLoginResData loginInitData;

    /**
     * 下拉刷新，上拉加载参数初始化
     */
    private int refreshCount = 1;
    private int pageNum = 1;//默认加载第一页
    private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
    //总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private static final int REFRESH = 100;
    private static final int LOADMORE = 200;
    private static final int NOLOADMORE = 300;
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，

    //交易列表
    private List<OrderDetailData> lsOrder = new ArrayList<OrderDetailData>();
    private OrderListAdapte mAdapter;

    /**
     *（"1"=当日交易）（"2"=本月交易不含今天）（"3" = 昨日交易）
     */
    private String date_typeStr = "1";
    private PopupWindow popupwindow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("交易明细（当天）");
        imgTitleImg.setVisibility(View.VISIBLE);
        tvOption.setVisibility(View.VISIBLE);
        tvOption.setText("汇总");

        posProvider = MainActivity.posProvider;
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initView();

        initListener();

        mAdapter = new OrderListAdapte(this,lsOrder,posProvider);
        mListView.setAdapter(mAdapter);

        getOrderList(pageNum,pageNumCount);

    }

    private void initView(){

        /**
         * ListView初始化
         */
        mListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
        mListView.setPullRefreshEnable(true);//是否可以下拉刷新,默认可以下拉


    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        titleLayout.setOnClickListener(this);
        tvOption.setOnClickListener(this);
        //注册刷新和加载更多接口
        mListView.setXListViewListener(this);
        mListView.setOnItemClickListener(this);
    }

    /**
     * 获取交易明细
     * 入参：mid，eid，date_type（"1"=当日交易）（"2"=本月交易不含今天）
     **/
    private void getOrderList(final int pageNum,final int pageCount){
        if(refreshCount == 1){

            showWaitDialog();
        }
        final String url;
        if("1".equals(date_typeStr) || "3".equals(date_typeStr)){
            url = NitConfig.queryOrderDayListUrl;
        }else{
            url = NitConfig.queryOrderMonListUrl;
        }
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("pageNum",pageNum+"");
                    userJSON.put("numPerPage",pageCount+"");
                    userJSON.put("mid",loginInitData.getMid());
                    userJSON.put("eid",loginInitData.getEid());
                    userJSON.put("date_type",date_typeStr);
                    String content = String.valueOf(userJSON);
                    Log.e("交易明细发起请求参数：", content);
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("交易明细返回JSON值：", jsonStr);
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
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成-->>已没有更多
                    onLoad();
                    break;
                case NetworkUtils.MSG_WHAT_ONE:
                    String orderListJsonStr=(String) msg.obj;
                    orderListJsonStr(orderListJsonStr);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_JSON_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_IO_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                case NetworkUtils.REQUEST_CODE:
                    errorJsonText = (String) msg.obj;
                    ToastUtil.showText(activity,errorJsonText,1);
                    hideWaitDialog();
                    loadMore = "0";
                    break;
                default:
                    break;
            }
        }
    };

    private void orderListJsonStr(String jsonStr){
        try{
            JSONObject job = new JSONObject(jsonStr);
            if("200".equals(job.getString("status"))){
                String dataJson = job.getString("data");
                Gson gjson  =  GsonUtils.getGson();
                java.lang.reflect.Type type = new TypeToken<OrderListData>() {}.getType();
                OrderListData order = gjson.fromJson(dataJson, type);
                //获取总条数
                orderListTotalCount = order.getTotalCount();
                Log.e("总条数：", orderListTotalCount+"");
                List<OrderDetailData> orderList = new ArrayList<OrderDetailData>();
                //获取的list
                orderList = order.getOrderList();
                getMoerNum = orderList.size();
                if(pageNum == 1){
                    lsOrder.clear();
                }
                lsOrder.addAll(orderList);
                Log.e("查询数据：", lsOrder.size()+""+"条");
                //关闭上拉或下拉View，刷新Adapter
                if("0".equals(loadMore)){
                    if(lsOrder.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
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
            }else{
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
                ToastUtil.showText(activity,"查询失败！",1);
            }

        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Message msg1 = new Message();
            msg1.what = NOLOADMORE;
            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            ToastUtil.showText(activity,"查询失败！",1);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Message msg1 = new Message();
            msg1.what = NOLOADMORE;
            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            ToastUtil.showText(activity,"查询失败！",1);
        }
    }

    /**
     * 创建PopupWindow
     */
    private void initmPopupWindowView(){
        View view = getLayoutInflater().inflate(R.layout.order_list_top_popupwindow, null,false);
        // 创建PopupWindow实例,200,150分别是宽度和高度
        popupwindow = new PopupWindow(view, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
//	    popupwindow.setAnimationStyle(R.style.AnimationFade);
        // 自定义view添加触摸事件
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                return false;
            }
        });
        /** 在这里可以实现自定义视图的功能 */
        TextView tvMonth = (TextView) view.findViewById(R.id.order_list_topPup_month);
        TextView tvYesterday = (TextView) view.findViewById(R.id.order_list_topPup_yesterday);
        TextView tvDay = (TextView) view.findViewById(R.id.order_list_topPup_day);
        tvMonth.setOnClickListener(this);
        tvYesterday.setOnClickListener(this);
        tvDay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.menu_title_layout:
                if (popupwindow != null&&popupwindow.isShowing()) {
                    imgTitleImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.open_popupwindow_icon));
                    popupwindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    popupwindow.showAsDropDown(v, 0, 5);
                    imgTitleImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.cloes_popupwindow_icon));
                }
                break;
            case R.id.order_list_topPup_month:
                //当前显示的不是本月的数据时才去查询本月数据
                if(!"2".equals(date_typeStr)){
                    tvTitle.setText("交易明细（本月）");
                    pageNum = 1;
                    date_typeStr = "2";
                    getOrderList(pageNum,pageNumCount);
                }
                imgTitleImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.open_popupwindow_icon));
                if(popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }

//			Toast.makeText(context, "选择当月", Toast.LENGTH_LONG).show();
                break;
            case R.id.order_list_topPup_yesterday:
                if(!"3".equals(date_typeStr)){
                    tvTitle.setText("交易明细（昨天）");
                    pageNum = 1;
                    date_typeStr = "3";
                    getOrderList(pageNum,pageNumCount);
                }
                imgTitleImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.open_popupwindow_icon));
                if(popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                break;
            case R.id.order_list_topPup_day:
                //当前显示的不是当天的数据时才去查询当天数据
                if(!"1".equals(date_typeStr)){
                    tvTitle.setText("交易明细（当天）");
                    pageNum = 1;
                    date_typeStr = "1";
                    getOrderList(pageNum,pageNumCount);
                }
                imgTitleImg.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.open_popupwindow_icon));
                if(popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    popupwindow = null;
                }
                break;
            case R.id.menu_title_tvOption:
                intent.setClass(activity,SummaryActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //这里因为添加了头部，所以数据Item的索引值发生变化，即对应item应为：position-1；
        OrderDetailData order = lsOrder.get(position-1);
        Intent in = new Intent();
        in.setClass(activity, OrderDetailsActivity.class);
        in.putExtra("userLoginData",loginInitData);
        in.putExtra("order", order);
        startActivity(in);
    }

    @Override
    public void onRefresh() {
        refreshCount++;
        loadMore = "1";
        pageNum = 1;
        getOrderList(pageNum,pageNumCount);
    }

    @Override
    public void onLoadMore() {

        refreshCount++;
        loadMore = "2";
        if(lsOrder.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            getOrderList(pageNum,pageNumCount);
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

    private void onLoad() {
        mListView.stopRefresh();
        mListView.stopLoadMore();
        mListView.setRefreshTime(new Date().toLocaleString());
    }
}
