package com.wanding.xingpos.auth.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 
 * @Description 刷卡支付预授权
 * @author liaoxiang
 * 2018年9月5日
 */
public class MicroPayPreAuthRequest extends BaseRequest {

	/**
	 *
	 */
	private static final long serialVersionUID = 7552865623063604261L;

	/**
	* 支付授权码
	*/
	@JSONField(name = "auth_code", format = "UTF-8")
	private String authCode;

	/**
	 * 授权码类型
	 */
	@JSONField(name = "auth_type", format = "UTF-8")
	private String authType;

	/**
	 * 业务订单的简单描述，如预授权冻结
	 */
	@JSONField(name = "order_title", format = "UTF-8")
	private String orderTitle;

	/**
	 * 商户订单号,32个字符以内、可包含字母、数字、下划线；需保证在商户端不重复
	 */
	@JSONField(name = "out_order_no", format = "UTF-8")
	private String outOrderNo;

	/**
	 * 单位为：分（人民币），精确到小数点后两位
	 */
	@JSONField(name = "total_amount", format = "UTF-8")
	private String totalAmount;


	/**
	 * 业务扩展参数，用于特定业务信息的传递，json格式。 
	 */
	@JSONField(name = "extra_param", format = "UTF-8")
	private String extraParam;


	public String getAuthCode() {
		return authCode;
	}


	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}


	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getOrderTitle() {
		return orderTitle;
	}


	public void setOrderTitle(String orderTitle) {
		this.orderTitle = orderTitle;
	}


	public String getOutOrderNo() {
		return outOrderNo;
	}


	public void setOutOrderNo(String outOrderNo) {
		this.outOrderNo = outOrderNo;
	}


	public String getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}


	public String getExtraParam() {
		return extraParam;
	}


	public void setExtraParam(String extraParam) {
		this.extraParam = extraParam;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append("MicroPayPreAuthRequest [authCode=");
		builder.append(authCode);
		builder.append(", authType=");
		builder.append(authType);
		builder.append(", orderTitle=");
		builder.append(orderTitle);
		builder.append(", outOrderNo=");
		builder.append(outOrderNo);
		builder.append(", totalAmount=");
		builder.append(totalAmount);
		builder.append(", extraParam=");
		builder.append(extraParam);
		builder.append("]");
		return builder.toString();
	}

}
