package com.wanding.xingpos;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wanding.xingpos.utils.EditTextUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.Arrays;

/**
 * 主界面金额输入公共Activity
 */
@ContentView(R.layout.content_layout)
public class MainBaseActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.content_layout_etSumMoney)
    public EditText etSumMoney;
    @ViewInject(R.id.content_layout_imagEliminate)
    ImageButton imagEliminate;
    @ViewInject(R.id.content_layout_tvOne)
    TextView tvOne;
    @ViewInject(R.id.content_layout_tvTwo)
    TextView tvTwo;
    @ViewInject(R.id.content_layout_tvThree)
    TextView tvThree;
    @ViewInject(R.id.content_layout_tvFour)
    TextView tvFour;
    @ViewInject(R.id.content_layout_tvFive)
    TextView tvFive;
    @ViewInject(R.id.content_layout_tvSix)
    TextView tvSix;
    @ViewInject(R.id.content_layout_tvSeven)
    TextView tvSeven;
    @ViewInject(R.id.content_layout_tvEight)
    TextView tvEight;
    @ViewInject(R.id.content_layout_tvNine)
    TextView tvNine;
    @ViewInject(R.id.content_layout_tvZero)
    TextView tvZero;
    @ViewInject(R.id.content_layout_tvSpot)
    TextView tvSpot;


    /**
     * 金额拼接
     */
    public StringBuilder pending = new StringBuilder();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initListener();
    }

    /** 初始化View  */
    private void initView(){

        //设置金额框输入规则
        EditTextUtils.setPricePoint(etSumMoney);
        //强制隐藏Android输入法窗体
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        //EditText始终不弹出软件键盘
        etSumMoney.setInputType(InputType.TYPE_NULL);
        imm.hideSoftInputFromWindow(etSumMoney.getWindowToken(),0);
    }

    /** 注册所有按钮点击事件  */
    private void initListener(){
        tvOne.setOnClickListener(this);
        tvTwo.setOnClickListener(this);
        tvThree.setOnClickListener(this);
        tvFour.setOnClickListener(this);
        tvFive.setOnClickListener(this);
        tvSix.setOnClickListener(this);
        tvSeven.setOnClickListener(this);
        tvEight.setOnClickListener(this);
        tvNine.setOnClickListener(this);
        tvZero.setOnClickListener(this);
        tvSpot.setOnClickListener(this);
        imagEliminate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.content_layout_tvOne:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("1");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvTwo:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("2");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvThree:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("3");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvFour:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("4");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvFive:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("5");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvSix:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("6");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvSeven:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("7");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvEight:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("8");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvNine:
                if (!pending.toString().contains(".")) {
                    if(pending.toString().length()>=5){
                        return;
                    }
                }
                if(pending.toString().length()>0){
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                }
                pending = pending.append("9");
                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvZero:
                if("0.0".equals(pending.toString())){
                    return;
                }
                pending = pending.append("0");

                if (pending.toString().contains(".")) {
                    if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                        //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                        pending = pending.deleteCharAt(pending.length()-1);
                    }
                }
                //输入内容头为0的情况下，只能输入小数点
                if (pending.toString().startsWith("0") && pending.toString().trim().length() > 1) {
                    if (!".".equals(pending.toString().substring(1, 2))) {
                        pending = pending.deleteCharAt(pending.length()-1);
                        return;
                    }
                }
                etSumMoney.setText("￥"+pending);
                break;
            case R.id.content_layout_tvSpot:
                if(pending.length()>0){
                    if (judje1()) {
                        pending = pending.append(".");
                        if (pending.toString().contains(".")) {
                            if (pending.length() - 1 - pending.toString().indexOf(".") > 2) {
                                //如果内容包含小数点，小数点后大于两位，删除最后最后一个字符
                                pending = pending.deleteCharAt(pending.length()-1);
                            }
                        }
                        etSumMoney.setText("￥"+pending);
                    }
                }

                break;
            case R.id.content_layout_imagEliminate:
                //删除
                if (pending.length() != 0) {
                    pending = pending.delete(pending.length() - 1, pending.length());
                    etSumMoney.setText("￥"+pending);
                    if("0".equals(pending.toString())){
                        //清空pending
                        pending.delete( 0, pending.length() );
                    }
                    if(pending.length()<=0){
                        etSumMoney.setText("￥0.00");
                    }
                }
                break;
            default:

                break;
        }
    }

    private boolean judje1() {
        String a = "+-*/.";
        int[] b = new int[a.length()];
        int max;
        for (int i = 0; i < a.length(); i++) {
            String c = "" + a.charAt(i);
            b[i] = pending.lastIndexOf(c);
        }
        Arrays.sort(b);
        if (b[a.length() - 1] == -1) {
            max = 0;
        } else {
            max = b[a.length() - 1];
        }
        if (pending.indexOf(".", max) == -1) {
            return true;
        } else {
            return false;
        }
    }
}
