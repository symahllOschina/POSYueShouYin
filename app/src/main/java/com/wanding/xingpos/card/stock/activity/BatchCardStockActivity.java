package com.wanding.xingpos.card.stock.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.NitConfig;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.card.stock.bean.CardStockTypeResData;
import com.wanding.xingpos.http.util.HttpURLConnectionUtil;
import com.wanding.xingpos.http.util.NetworkUtils;
import com.wanding.xingpos.printutil.FuyouPrintUtil;
import com.wanding.xingpos.utils.ToastUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量制劵
 */
@ContentView(R.layout.activtiy_batch_card_stock)
public class BatchCardStockActivity extends BaseActivity implements View.OnClickListener {

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

    @ViewInject(R.id.batch_card_stock_tvCardStockType)
    private TextView tvCardStockType;
    @ViewInject(R.id.batch_card_stock_etPrintNum)
    private EditText etPrintNum;

    @ViewInject(R.id.batch_card_stock_tvOk)
    private TextView tvOk;



    private int REQUEST_CODE = 1;

    private UserLoginResData loginInitData;
    CardStockTypeResData cardStockType = null;
    /**
     * list:服务端返回的全部数据
     * partList：打印数据大于10条，取出的前10条内容
     * afterList：打印数据大于10条，取出的后面内容
     */
    List<String> list = null;
    List<String> frontList = new ArrayList<String>();
    List<String> afterList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
        tvTitle.setText("批量制劵");
        imgTitleImg.setVisibility(View.GONE);
        tvOption.setVisibility(View.GONE);

        Intent intent = getIntent();
        loginInitData = (UserLoginResData) intent.getSerializableExtra("userLoginData");

        initView();
        initListener();
    }

    /**
     * 初始化界面控件
     */
    private void initView(){

        //设置输入款默认的光标位置
        etPrintNum.setSelection(etPrintNum.getText().toString().length());

    }

    private void initListener(){
        imgBack.setOnClickListener(this);
        tvCardStockType.setOnClickListener(this);
        tvOk.setOnClickListener(this);
    }

    /**
     * 获取打印劵内容
     */
    private void getCouponPrintList(final int num){
        showWaitDialog();
        final String url = NitConfig.qRCodeUrl;
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject userJSON = new JSONObject();
                    userJSON.put("total",num+"");
                    userJSON.put("cardId",cardStockType.getWxcard_id());
                    userJSON.put("mid",loginInitData.getMid());
                    String content = String.valueOf(userJSON);
                    Log.e("发起请求参数：", content);
                    String jsonStr = HttpURLConnectionUtil.doPos(url,content);
                    Log.e("返回JSON值：", jsonStr);
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
                case 1:
                    String couponPrintListJsonStr=(String) msg.obj;
                    couponPrintListJsonStr(couponPrintListJsonStr);
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

    private void couponPrintListJsonStr(String jsonStr){
        try {
            JSONObject job = new JSONObject(jsonStr);
            String status = job.getString("status");
            String message = job.getString("message");
            if("200".equals(status)){
                String dataJson = job.getString("data");
                JSONObject dataJob = new JSONObject(dataJson);
                String urlListStr = dataJob.getString("urlList");
                list = com.alibaba.fastjson.JSONObject.parseObject(urlListStr,List.class);
//                for (int i = 0;i<list.size();i++){
//                    Log.e(TAG,list.get(i));
//                }
                //打印劵
                printCouponText(list);
            }else{
                if(Utils.isNotEmpty(message)){
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(activity, "获取卡劵信息失败！", Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(activity, "获取卡劵信息失败！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "获取卡劵信息失败！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开始打印
     */
    private void printCouponText(List<String> list){
        String printTextStr = "";
        if(list.size()>10){
            //先取出10条打印，打印完继续打印剩余的内容
            frontList.clear();
            afterList.clear();
            for(int i = 0;i<10;i++){
                String textStr = list.get(i);
                Log.e(TAG,textStr);
                frontList.add(textStr);
            }
            for (int j = 10;j<list.size();j++){
                String textStr = list.get(j);
                Log.e(TAG,textStr);
                afterList.add(textStr);
            }
            printTextStr = FuyouPrintUtil.batchSecurPrintText(activity,frontList,loginInitData,cardStockType);
        }else{
            printTextStr = FuyouPrintUtil.batchSecurPrintText(activity,list,loginInitData,cardStockType);
        }
        Intent intent=new Intent();
        intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
        intent.putExtra("data", printTextStr);
        intent.putExtra("isPrintTicket", "true");
        startActivityForResult(intent, FuyouPrintUtil.PRINT_REQUEST_CODE);
    }

    /**  重打印提示窗口 */
    private void showPrintTwoDialog(final int printNum){
        View view = LayoutInflater.from(activity).inflate(R.layout.option_hint_dialog, null);
        TextView hintNumMsg = (TextView) view.findViewById(R.id.option_hint_dialog_hintNumMsg);
        hintNumMsg.setVisibility(View.VISIBLE);
        hintNumMsg.setText("当前打印数量"+" "+printNum+""+"份");
        TextView tvHintMsg = (TextView) view.findViewById(R.id.option_hint_dialog_hintMsg);
        tvHintMsg.setText("是否确认打印？");
        TextView btCancel = (TextView) view.findViewById(R.id.option_hint_tvCancel);
        TextView btok = (TextView) view.findViewById(R.id.option_hint_tvOk);
        final Dialog myDialog = new Dialog(activity,R.style.dialog);
        Window dialogWindow = myDialog.getWindow();
        WindowManager.LayoutParams params = myDialog.getWindow().getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setAttributes(params);
        myDialog.setContentView(view);
        btCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                myDialog.dismiss();

            }
        });
        btok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                getCouponPrintList(printNum);
                myDialog.dismiss();

            }
        });
        myDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /**
         * 选择劵类型返回
         */
        if(requestCode == REQUEST_CODE){
            if(resultCode == CardStockTypeListActivity.RESULT_CODE){
                Bundle bundle = data.getExtras();
                cardStockType = (CardStockTypeResData) bundle.getSerializable("cardStockType");
                if(cardStockType!=null){
                    tvCardStockType.setText(cardStockType.getTitle());
                }
            }
        }
        /**
         * 打印返回
         */
        if(requestCode == FuyouPrintUtil.PRINT_REQUEST_CODE){
            Log.e(TAG,resultCode+"");
            switch (resultCode) {
                case Activity.RESULT_CANCELED:
                    String reason = "";
                    String traceNo = "";
                    String batchNo = "";
                    String ordernumber = "";
                    if (data != null) {
                        Bundle b = data.getExtras();
                        if (b != null) {
                            reason = (String) b.get("reason");
                            traceNo = (String)b.getString("traceNo");
                            batchNo = (String)b.getString("batchNo");
                            ordernumber = (String)b.getString("ordernumber");
                        }
                    }
                    if (reason != null) {
                        Log.e("reason", reason);
                        if(FuyouPrintUtil.ERROR_NONE == Integer.valueOf(reason)){
                            //打印正常
                            if(afterList!=null){
                                if(afterList.size()>0){
                                    String printTextStr = FuyouPrintUtil.batchSecurPrintText(activity,afterList,loginInitData,cardStockType);
                                    Intent intent=new Intent();
                                    intent.setComponent(new ComponentName("com.fuyousf.android.fuious","com.fuyousf.android.fuious.CustomPrinterActivity"));
                                    intent.putExtra("data", printTextStr);
                                    intent.putExtra("isPrintTicket", "true");
                                    startActivityForResult(intent, FuyouPrintUtil.PRINT_REQUEST_CODE);
                                    afterList.clear();
                                }
                            }
                        }else if(FuyouPrintUtil.ERROR_PAPERENDED == Integer.valueOf(reason)){
                            //缺纸，不能打印
                            ToastUtil.showText(activity,"打印机缺纸，打印中断！",1);
                        }else {
                            ToastUtil.showText(activity,"打印机出现故障错误码为："+reason,1);
                        }

                    }
                    Log.w("TAG", "失败返回值--reason--返回值："+reason+"/n 凭证号："+traceNo+"/n 批次号："+batchNo+"/n 订单号："+ordernumber);



                    break;
                case Activity.RESULT_OK:
                    //Activity.RESULT_OK:经过测试发现该分支只返回支付相关业务


                    break;

            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.menu_title_imageView:
                finish();
                break;
            case R.id.batch_card_stock_tvCardStockType:
                intent.setClass(activity,CardStockTypeListActivity.class);
                intent.putExtra("userLoginData",loginInitData);
                intent.putExtra("cardStockType",cardStockType);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.batch_card_stock_tvOk:
                if(Utils.isFastClick()){
                    return;
                }
                String etPrintNumStr = etPrintNum.getText().toString().trim();
                int printNum = 0;
                if(cardStockType == null){
                    ToastUtil.showText(activity,"请选择劵类型！",1);
                    return;
                }
                if(Utils.isEmpty(etPrintNumStr)){
                    ToastUtil.showText(activity,"请输入打印数量！",1);
                    return;
                }
                printNum = Integer.valueOf(etPrintNumStr);
                if(printNum == 0){
                    ToastUtil.showText(activity,"打印数量最小为1 ！",1);
                    return;
                }
                if(printNum>20){
                    ToastUtil.showText(activity,"打印数量不能超过20条！",1);
                    return;
                }
                showPrintTwoDialog(printNum);

                break;
                default:
                    break;
        }
    }
}
