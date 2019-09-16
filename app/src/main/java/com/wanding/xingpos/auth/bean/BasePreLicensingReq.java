package com.wanding.xingpos.auth.bean;

public abstract class BasePreLicensingReq {

	private String pay_ver;
	private String pay_type;
	private String merchant_no;
	private String terminal_id;
	private String terminal_trace;
	private String terminal_time;

	private String order_body;
	private String sign_type;
	private String key_sign;

	public String getPay_ver() {
		return pay_ver;
	}

	public void setPay_ver(String pay_ver) {
		this.pay_ver = pay_ver;
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

	public String getOrder_body() {
		return order_body;
	}

	public void setOrder_body(String order_body) {
		this.order_body = order_body;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BasePreLicensingReq [pay_ver=");
		builder.append(pay_ver);
		builder.append(", pay_type=");
		builder.append(pay_type);
		builder.append(", merchant_no=");
		builder.append(merchant_no);
		builder.append(", terminal_id=");
		builder.append(terminal_id);
		builder.append(", terminal_trace=");
		builder.append(terminal_trace);
		builder.append(", terminal_time=");
		builder.append(terminal_time);
		builder.append(", total_fee=");
		builder.append(", order_body=");
		builder.append(order_body);
		builder.append(", sign_type=");
		builder.append(sign_type);
		builder.append(", key_sign=");
		builder.append(key_sign);
		builder.append("]");
		return builder.toString();
	}

}
