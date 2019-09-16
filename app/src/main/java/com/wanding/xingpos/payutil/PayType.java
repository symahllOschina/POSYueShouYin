package com.wanding.xingpos.payutil;

/**
 * @author 支付类型
 *
 */
public enum PayType {

	 WX("010","微信支付"),
	 ALI("020","支付宝支付"),
	 DEBIT("030","借记卡"),
	 CREDIT("040","贷记卡"),
	 BEST("050","翼支付"),
	 UNIONPAY("060","银联二维码");
	private String payType = "";
	private String desc = "";
	private PayType(String payType, String desc){
		this.payType = payType;
		this.desc = desc;
	}
	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	
}
