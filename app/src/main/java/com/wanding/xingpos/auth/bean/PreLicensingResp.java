package com.wanding.xingpos.auth.bean;

import java.io.Serializable;

public class PreLicensingResp implements Serializable {
	
	private String code;
	private String msg;
	private String sub_code;
	private String Sub_msg;
	private String pay_type;
	private String merchant_no;
	private String terminal_id;
	private String terminal_trace;
	private String terminal_time;
	private String total_fee;//预授权金额
	private String finish_amt;//预授权完成金额
	private String end_time;
	private String out_trade_no;//商户操作交易流水号
	private String out_third_no;//第三方交易渠道流水号
	private String out_third_authno;//第三方返回授权号
	private String custom_auth_code;//商户自定义授权号30位12开头
	private String sign_type;
	private String key_sign;

	private String auth_type;//1:预授权，2：预授权撤销，3：预授权完成

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSub_code() {
		return sub_code;
	}
	public void setSub_code(String sub_code) {
		this.sub_code = sub_code;
	}
	public String getSub_msg() {
		return Sub_msg;
	}
	public void setSub_msg(String sub_msg) {
		Sub_msg = sub_msg;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	public String getMerchant_no() {
		return merchant_no;
	}
	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}
	public String getTerminal_id() {
		return terminal_id;
	}
	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}
	public String getTerminal_trace() {
		return terminal_trace;
	}
	public void setTerminal_trace(String terminal_trace) {
		this.terminal_trace = terminal_trace;
	}
	public String getTerminal_time() {
		return terminal_time;
	}
	public void setTerminal_time(String terminal_time) {
		this.terminal_time = terminal_time;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getFinish_amt() {
		return finish_amt;
	}

	public void setFinish_amt(String finish_amt) {
		this.finish_amt = finish_amt;
	}

	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getOut_third_no() {
		return out_third_no;
	}
	public void setOut_third_no(String out_third_no) {
		this.out_third_no = out_third_no;
	}
	public String getOut_third_authno() {
		return out_third_authno;
	}
	public void setOut_third_authno(String out_third_authno) {
		this.out_third_authno = out_third_authno;
	}
	public String getCustom_auth_code() {
		return custom_auth_code;
	}
	public void setCustom_auth_code(String custom_auth_code) {
		this.custom_auth_code = custom_auth_code;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getKey_sign() {
		return key_sign;
	}
	public void setKey_sign(String key_sign) {
		this.key_sign = key_sign;
	}

	public String getAuth_type() {
		return auth_type;
	}

	public void setAuth_type(String auth_type) {
		this.auth_type = auth_type;
	}

	@Override
	public String toString() {
		return "PreLicensingResp [code=" + code + ", msg=" + msg + ", sub_code=" + sub_code + ", Sub_msg=" + Sub_msg
				+ ", pay_type=" + pay_type + ", merchant_no=" + merchant_no + ", terminal_id=" + terminal_id
				+ ", terminal_trace=" + terminal_trace + ", terminal_time=" + terminal_time + ", total_fee=" + total_fee
				+ ", end_time=" + end_time + ", out_trade_no=" + out_trade_no + ", out_third_no=" + out_third_no
				+ ", out_third_authno=" + out_third_authno + ", custom_auth_code=" + custom_auth_code + ", sign_type="
				+ sign_type + ", key_sign=" + key_sign + "]";
	}

	
	
	
}
