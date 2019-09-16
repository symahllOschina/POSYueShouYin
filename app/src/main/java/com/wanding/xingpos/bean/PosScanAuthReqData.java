package com.wanding.xingpos.bean;

import java.io.Serializable;

/**
 * 富友POS（微信，支付宝）扫码预授权请求参数公共类
 */
public class PosScanAuthReqData implements Serializable {
    //{"type":"5","machineCode":"61271182","auth_code":"134531378197658753","amount":"0.01","desc":"测试预授权"}
    //{"type":"5","machineCode":"61271182","orig_order_no":"120120180903160333770014587402","amount":"0.01","desc":"测试撤销"}
    //{"type":"5","machineCode":"61271182","orig_order_no":"120120180903160333770014587402","finish_amt":"0.01","desc":"测试预授权完成"}
    //公共加签参数：sign、sign_type、random_str

    private String type;
    private String machineCode;
    private String auth_code;
    private String orig_order_no;
    private String amount;
    private String finish_amt;
    private String desc;
    private String sign;
    private String sign_type;
    private String random_str;

    public PosScanAuthReqData() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getAuth_code() {
        return auth_code;
    }

    public void setAuth_code(String auth_code) {
        this.auth_code = auth_code;
    }

    public String getOrig_order_no() {
        return orig_order_no;
    }

    public void setOrig_order_no(String orig_order_no) {
        this.orig_order_no = orig_order_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getFinish_amt() {
        return finish_amt;
    }

    public void setFinish_amt(String finish_amt) {
        this.finish_amt = finish_amt;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getRandom_str() {
        return random_str;
    }

    public void setRandom_str(String random_str) {
        this.random_str = random_str;
    }
}
