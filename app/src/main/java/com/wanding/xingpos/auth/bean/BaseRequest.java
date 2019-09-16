package com.wanding.xingpos.auth.bean;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 *  预授权请求参数实体
 */
public abstract class BaseRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8086493384575667398L;

	/**
	 * 商户号
	 */
	@JSONField(name = "mch_no", format = "UTF-8")
	private String mchNo;

	/**
	 * 终端号
	 */
	@JSONField(name = "term_no", format = "UTF-8")
	private String termNo;

	/**
	 * 请求使用的编码格式，默认utf-8，如utf-8,gbk,gb2312等，
	 */
	@JSONField(name = "charset", format = "UTF-8")
	private String charset;

	/**
	 * 签名字符串所使用的签名算法类型，默认MD5
	 */
	@JSONField(name = "sign_type", format = "UTF-8")
	private String signType;

	/**
	 * 请求参数的签名串
	 */
	@JSONField(name = "sign", format = "UTF-8")
	private String sign;

	/**
	 * 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
	 */
	@JSONField(name = "timestamp", format = "UTF-8")
	private String timestamp;

	/**
	 * 随机字符串，长度要求在32位以内。
	 */
	@JSONField(name = "nonce_str", format = "UTF-8")
	private String nonceStr;

	/**
	 * 接口版本，固定为：1.0
	 */
	@JSONField(name = "version", format = "UTF-8")
	private String version;

	public String getMchNo() {
		return mchNo;
	}

	public void setMchNo(String mchNo) {
		this.mchNo = mchNo;
	}

	public String getTermNo() {
		return termNo;
	}

	public void setTermNo(String termNo) {
		this.termNo = termNo;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
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

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("BaseRequest [mchNo=");
		builder.append(mchNo);
		builder.append(", termNo=");
		builder.append(termNo);
		builder.append(", charset=");
		builder.append(charset);
		builder.append(", signType=");
		builder.append(signType);
		builder.append(", sign=");
		builder.append(sign);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append(", nonceStr=");
		builder.append(nonceStr);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}

}
