package com.wanding.xingpos.auth.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 *  预授权结果
 */
public abstract class BaseResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3367095476727090753L;

	/**
	 * 
	 */
	@JSONField(name = "code", format = "UTF-8")
	private String code;

	@JSONField(name = "msg", format = "UTF-8")
	private String msg;

	/**
	 * 业务结果
	 */
	@JSONField(name = "result_code", format = "UTF-8")
	private String resultCode;

	@JSONField(name = "result_msg", format = "UTF-8")
	private String resultMsg;

	@JSONField(name = "sign", format = "UTF-8")
	private String sign;

	/**
	 * 请求完成时间，格式"yyyy-MM-dd HH:mm:ss"
	 */
	@JSONField(name = "timestamp", format = "UTF-8")
	private String timestamp;

	/**
	 * 随机字符串，长度要求在32位以内。
	 */
	@JSONField(name = "nonce_str", format = "UTF-8")
	private String nonceStr;

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

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseResponse [code=");
		builder.append(code);
		builder.append(", msg=");
		builder.append(msg);
		builder.append(", resultCode=");
		builder.append(resultCode);
		builder.append(", resultMsg=");
		builder.append(resultMsg);
		builder.append(", sign=");
		builder.append(sign);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", nonceStr=");
		builder.append(nonceStr);
		builder.append("]");
		return builder.toString();
	}


}
