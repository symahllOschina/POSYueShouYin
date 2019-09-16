package com.wanding.xingpos.auth.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class MicroPayPreAuthResponse extends BaseResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8116428585633870092L;

	/**
	 * 授权订单号
	 */
	@JSONField(name = "auth_no", format = "UTF-8")
	private String authNo;

	/**
	 * 订单号
	 */
	@JSONField(name = "trade_no", format = "UTF-8")
	private String tradeNo;

	/**
	 * 第三方商户订单号
	 */
	@JSONField(name = "out_trade_no", format = "UTF-8")
	private String outTradeNo;

	/**
	 * 单位为：元（人民币），精确到小数点后两位
	 */
	@JSONField(name = "total_amount", format = "UTF-8")
	private String totalAmount;

	/**
	 * 预授权交易成功时间，格式需要为"yyyy-MM-dd HH:mm:ss"
	 */
	@JSONField(name = "gmt_trans", format = "UTF-8")
	private String gmtTrans;

	/**
	 * 预授权交易状态
	 */
	@JSONField(name = "status", format = "UTF-8")
	private String status;

	public String getAuthNo() {
		return authNo;
	}

	public void setAuthNo(String authNo) {
		this.authNo = authNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getGmtTrans() {
		return gmtTrans;
	}

	public void setGmtTrans(String gmtTrans) {
		this.gmtTrans = gmtTrans;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("MicroPayPreAuthResponse [authNo=");
		builder.append(authNo);
		builder.append(", tradeNo=");
		builder.append(tradeNo);
		builder.append(", outTradeNo=");
		builder.append(outTradeNo);
		builder.append(", totalAmount=");
		builder.append(totalAmount);
		builder.append(", gmtTrans=");
		builder.append(gmtTrans);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}


}
