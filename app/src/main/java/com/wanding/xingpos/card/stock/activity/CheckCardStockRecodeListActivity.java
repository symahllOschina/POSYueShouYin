package com.wanding.xingpos.card.stock.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.card.stock.adapter.CheckCardStockRecodeListAdapter;
import com.wanding.xingpos.card.stock.bean.CheckCardStockRecodeDetailResData;
import com.wanding.xingpos.card.stock.bean.CheckCardStockRecodeListResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.utils.GsonUtils;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;
import com.wanding.xingpos.view.ClearEditText;
import com.wanding.xingpos.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 核销记录
 */
@ContentView(R.layout.activity_check_card_stock_recode_list)
public class CheckCardStockRecodeListActivity extends BaseActivity implements View.OnClickListener,
                                                    XListView.IXListViewListener,
                                                    AdapterView.OnItemClickListener {


    @ViewInject(R.id.search_header_titleLayout)
    private LinearLayout titleLayout;
    @ViewInject(R.id.search_header_tvTitle)
    private TextView tvTitle;

    /**
     * 搜索框
     */
    @ViewInject(R.id.search_header_etSearch)
    private ClearEditText etSearch;
    @ViewInject(R.id.search_header_tvSearch)
    private TextView tvSearch;

    @ViewInject(R.id.check_card_stock_recode_xListView)
    private XListView xListView;

    private int pageNum = 1;//默认加载第一页
    private static final int pageNumCount = 20;//默认一页加载xx条数据（死值不变）
    //总条数
    private int orderListTotalCount = 0;
    //每次上拉获取的条数
    private int getMoerNum = 0;
    private static final int REFRESH = 100;
    private static final int LOADMORE = 200;
    private static final int NOLOADMORE = 300;
    private int refreshCount = 1;
    private String loadMore = "0";//loadMore为1表示刷新操作  2为加载更多操作，

    private UserLoginResData loginInitData;

    String etSearchStr = "";
    private List<CheckCardStockRecodeDetailResData> list = new ArrayList<CheckCardStockRecodeDetailResData>();
    private BaseAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvTitle.setText("核销记录");
        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initView();
        initListener();
        mAdapter = new CheckCardStockRecodeListAdapter(activity,list);
        xListView.setAdapter(mAdapter);


        getWriteOffRecodeList(pageNum,pageNumCount);
    }

    /**
     * 初始化界面控件
     */
    private void initView(){
        /**
         * ListView初始化
         */
        xListView.setPullLoadEnable(true);//是否可以上拉加载更多,默认可以上拉
        xListView.setPullRefreshEnable(true);//是否可以下拉刷新,默认可以下拉

    }

    private void initListener(){
        titleLayout.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
        //注册刷新和加载更多接口
        xListView.setXListViewListener(this);
        xListView.setOnItemClickListener(this);
    }

    /**
     * 查询核销记录
     */
    private void getWriteOffRecodeList(final int pageNum,final int pageCount){
        if(refreshCount == 1){

            showWaitDialog();
        }
        final String url = NitConfig.writeOffRecodeUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("pageNum",pageNum+"");
                    userJSON.put("mid",loginInitData.getMid());
                    userJSON.put("code",etSearchStr);
                    String content = String.valueOf(userJSON);
                    Log.e("发起请求参数：", content);
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
                    xListView.setPullLoadEnable(true);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成
                    onLoad();
                    break;
                case NOLOADMORE:
                    xListView.setPullLoadEnable(false);//是否可以上拉加载更多
                    mAdapter.notifyDataSetChanged();
                    // 加载更多完成-->>已没有更多
                    onLoad();
                    break;
                case NetworkUtils.MSG_WHAT_ONE:
                    String batchSecurListJsonStr=(String) msg.obj;
                    batchSecurListJsonStr(batchSecurListJsonStr);
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
        };
    };

    private void batchSecurListJsonStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");
                Gson gjson  =  GsonUtils.getGson();
                java.lang.reflect.Type type = new TypeToken<CheckCardStockRecodeListResData>() {}.getType();
                CheckCardStockRecodeListResData checkCardStockRecodeListResData = gjson.fromJson(dataJson, type);
                //获取总条数
                orderListTotalCount = checkCardStockRecodeListResData.getTotal();
                Log.e("总条数：", orderListTotalCount+"");
                List<CheckCardStockRecodeDetailResData> recodeList = new ArrayList<CheckCardStockRecodeDetailResData>();
                //获取的list
                recodeList = checkCardStockRecodeListResData.getCouponList();
                getMoerNum = recodeList.size();
                if(pageNum == 1){
                    list.clear();
                }
                list.addAll(recodeList);
                Log.e("查询数据：", list.size()+""+"条");
                //关闭上拉或下拉View，刷新Adapter
                if("0".equals(loadMore)){
                    if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
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
                    handler.sendEmptyMessageDelayed(REFRESH, 0);
                }else if("2".equals(loadMore)){
                    Message msg1 = new Message();
                    msg1.what = LOADMORE;
                    handler.sendEmptyMessageDelayed(LOADMORE, 0);
                }

            }else{
                if(Utils.isNotEmpty(message)){
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
                }
                Message msg1 = new Message();
                msg1.what = NOLOADMORE;
                handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Message msg1 = new Message();
            msg1.what = NOLOADMORE;
            handler.sendEmptyMessageDelayed(NOLOADMORE, 0);
            Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "查询失败！", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.search_header_titleLayout:
                finish();
                break;
            case R.id.search_header_tvSearch:
                etSearchStr = etSearch.getText().toString().trim();
                if(Utils.isFastClick()){
                    return;
                }
                refreshCount = 1;
                loadMore = "0";
                pageNum = 1;
                getWriteOffRecodeList(pageNum,pageNumCount);
                break;
            default:
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckCardStockRecodeDetailResData checkCardStockRecodeDetailResData = list.get(position - 1);
        Intent intent = new Intent();
        intent.setClass(activity,CheckCardStockRecodeDetailActivity.class);
        intent.putExtra("userLoginData",loginInitData);
        intent.putExtra("cardStockRecode",checkCardStockRecodeDetailResData);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        etSearchStr = etSearch.getText().toString().trim();
        refreshCount++;
        loadMore = "1";
        pageNum = 1;
        getWriteOffRecodeList(pageNum,pageNumCount);

    }

    @Override
    public void onLoadMore() {
        etSearchStr = etSearch.getText().toString().trim();
        refreshCount++;
        loadMore = "2";
        if(list.size()<=orderListTotalCount&&getMoerNum==pageNumCount){
            //已取出数据条数<=服务器端总条数&&上一次上拉取出的条数 == 规定的每页取出条数时代表还有数据库还有数据没取完
            pageNum = pageNum + 1;
            getWriteOffRecodeList(pageNum,pageNumCount);
        }else{
            //没有数据执行两秒关闭view
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Message msg = new Message();
                    msg.what = NOLOADMORE;
                    handler.sendMessage(msg);
                }
            }, 0);

        }
    }

    private void onLoad() {
        xListView.stopRefresh();
        xListView.stopLoadMore();
        xListView.setRefreshTime(new Date().toLocaleString());
    }
}
