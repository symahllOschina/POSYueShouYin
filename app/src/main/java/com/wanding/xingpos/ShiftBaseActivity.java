package com.wanding.xingpos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.bean.ShiftResData;
import com.wanding.xingpos.bean.SubReocrdSummaryResData;
import com.wanding.xingpos.bean.SubTimeSummaryResData;
import com.wanding.xingpos.bean.SubTotalSummaryResData;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * 交接班（结算）公共view
 */
@ContentView(R.layout.shift_content_layout)
public class ShiftBaseActivity extends BaseActivity{


    /**
     * 实收金额，日期时间
     */
    @ViewInject(R.id.settlement_content_tvSumMoney)
    TextView tvSumMoney;
    @ViewInject(R.id.settlement_content_tvDateTime)
    TextView tvDateTime;

    @ViewInject(R.id.settlment_content_aliLayout)
    LinearLayout aliLayout;
    @ViewInject(R.id.settlment_content_wxLayout)
    LinearLayout wxLayout;
    @ViewInject(R.id.settlment_content_yzfLayout)
    LinearLayout yzfLayout;
    @ViewInject(R.id.settlment_content_bankLayout)
    LinearLayout bankLayout;
    @ViewInject(R.id.settlment_content_ylLayout)
    LinearLayout ylLayout;


    @ViewInject(R.id.settlment_content_alipaySumNum)
    TextView tvAliPaySumNum;
    @ViewInject(R.id.settlment_content_alipaySumMoney)
    TextView tvAliPaySumMoney;
    @ViewInject(R.id.settlment_content_alirefundSumNum)
    TextView tvAliRefundSumNum;
    @ViewInject(R.id.settlment_content_alirefundSumMoney)
    TextView tvAliRefundSumMoney;

    @ViewInject(R.id.settlment_content_wxpaySumNum)
    TextView tvWXPaySumNum;
    @ViewInject(R.id.settlment_content_wxpaySumMoney)
    TextView tvWXPaySumMoney;
    @ViewInject(R.id.settlment_content_wxrefundSumNum)
    TextView tvWXRefundSumNum;
    @ViewInject(R.id.settlment_content_wxrefundSumMoney)
    TextView tvWXRefundSumMoney;

    @ViewInject(R.id.settlment_content_yzfpaySumNum)
    TextView tvYZFPaySumNum;
    @ViewInject(R.id.settlment_content_yzfpaySumMoney)
    TextView tvYZFPaySumMoney;
    @ViewInject(R.id.settlment_content_yzfrefundSumNum)
    TextView tvYZFRefundSumNum;
    @ViewInject(R.id.settlment_content_yzfrefundSumMoney)
    TextView tvYZFRefundSumMoney;

    @ViewInject(R.id.settlment_content_bankpaySumNum)
    TextView tvBankPaySumNum;
    @ViewInject(R.id.settlment_content_bankpaySumMoney)
    TextView tvBankPaySumMoney;
    @ViewInject(R.id.settlment_content_bankrefundSumNum)
    TextView tvBankRefundSumNum;
    @ViewInject(R.id.settlment_content_bankrefundSumMoney)
    TextView tvBankRefundSumMoney;

    @ViewInject(R.id.settlment_content_ylpaySumNum)
    TextView tvYLPaySumNum;
    @ViewInject(R.id.settlment_content_ylpaySumMoney)
    TextView tvYLPaySumMoney;
    @ViewInject(R.id.settlment_content_ylrefundSumNum)
    TextView tvYLRefundSumNum;
    @ViewInject(R.id.settlment_content_ylrefundSumMoney)
    TextView tvYLRefundSumMoney;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ShiftResData summary = null;
        updateView(summary);
    }

    /**
     * 更新界面数据
     */
    public void updateView(ShiftResData summary){
        aliLayout.setVisibility(View.GONE);
        wxLayout.setVisibility(View.GONE);
        yzfLayout.setVisibility(View.GONE);
        bankLayout.setVisibility(View.GONE);
        ylLayout.setVisibility(View.GONE);
        if(summary != null){
            //结算总金额
            ArrayList<SubTotalSummaryResData> totalList = summary.getTotalList();
            SubTotalSummaryResData total = null;
            for (int i = 0; i < totalList.size(); i++) {
                total = totalList.get(i);
            }
            tvSumMoney.setText("￥"+total.getMoney());
            //结算时间周期
            ArrayList<SubTimeSummaryResData> timeList = summary.getTimeList();
            SubTimeSummaryResData subStartTime = null;
            SubTimeSummaryResData subEndTime = null;
            for (int i = 0; i < timeList.size(); i++) {
                subStartTime = timeList.get(0);
                subEndTime = timeList.get(1);
            }
            String startTimeStr = "";
            String endTimeStr = "";
            startTimeStr = subStartTime.getType();
            endTimeStr = subEndTime.getType();

            if(startTimeStr.contains(".")){
                startTimeStr = startTimeStr.substring(0,startTimeStr.indexOf("."));
            }
            if(endTimeStr.contains(".")){
                endTimeStr = endTimeStr.substring(0,endTimeStr.indexOf("."));
            }


            tvDateTime.setText(startTimeStr+"至"+endTimeStr);
            //交易明细
            ArrayList<SubReocrdSummaryResData> reocrdList = summary.getReocrdList();
            for (int i = 0; i < reocrdList.size(); i++) {
                SubReocrdSummaryResData reocrd = reocrdList.get(i);
                String mode = reocrd.getMode();
                if("WX".equals(mode)){
                    wxLayout.setVisibility(View.VISIBLE);
                    String type = reocrd.getType();
                    if("noRefund".equals(type)){
                        tvWXPaySumNum.setText(reocrd.getTotalCount());
                        tvWXPaySumMoney.setText(reocrd.getMoney());
                    }else if("refund".equals(type)){
                        tvWXRefundSumNum.setText(reocrd.getTotalCount());
                        tvWXRefundSumMoney.setText(reocrd.getMoney());
                    }
                }else if("ALI".equals(mode)){
                    aliLayout.setVisibility(View.VISIBLE);
                    String type = reocrd.getType();
                    if("noRefund".equals(type)){
                        tvAliPaySumNum.setText(reocrd.getTotalCount());
                        tvAliPaySumMoney.setText(reocrd.getMoney());
                    }else if("refund".equals(type)){
                        tvAliRefundSumNum.setText(reocrd.getTotalCount());
                        tvAliRefundSumMoney.setText(reocrd.getMoney());
                    }
                }else if("BEST".equals(mode)){
                    yzfLayout.setVisibility(View.VISIBLE);
                    String type = reocrd.getType();
                    if("noRefund".equals(type)){
                        tvYZFPaySumNum.setText(reocrd.getTotalCount());
                        tvYZFPaySumMoney.setText(reocrd.getMoney());
                    }else if("refund".equals(type)){
                        tvYZFRefundSumNum.setText(reocrd.getTotalCount());
                        tvYZFRefundSumMoney.setText(reocrd.getMoney());
                    }
                }else if("BANK".equals(mode)){
                    bankLayout.setVisibility(View.VISIBLE);
                    String type = reocrd.getType();
                    if("noRefund".equals(type)){
                        tvBankPaySumNum.setText(reocrd.getTotalCount());
                        tvBankPaySumMoney.setText(reocrd.getMoney());
                    }else if("refund".equals(type)){
                        tvBankRefundSumNum.setText(reocrd.getTotalCount());
                        tvBankRefundSumMoney.setText(reocrd.getMoney());
                    }
                }else if("UNIONPAY".equals(mode)){
                    ylLayout.setVisibility(View.VISIBLE);
                    String type = reocrd.getType();
                    if("noRefund".equals(type)){
                        tvYLPaySumNum.setText(reocrd.getTotalCount());
                        tvYLPaySumMoney.setText(reocrd.getMoney());
                    }else if("refund".equals(type)){
                        tvYLRefundSumNum.setText(reocrd.getTotalCount());
                        tvYLRefundSumMoney.setText(reocrd.getMoney());
                    }
                }
            }
        }
    }
}
