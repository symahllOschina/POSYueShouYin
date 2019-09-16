package com.wanding.xingpos.bean;

import java.io.Serializable;

/**
 * 交易明细返回实体
 */
public class OrderDetailData implements Serializable {
	
	
	private String id;//: 11168002,
	private String orderId;//: "20180420115943334744139079251052",
	private String mid;// 66,
	private String payWay;//: "WX",
	private String payType;// null,
	private String orderType;//: null,
	private String goodsType;//: null,
	private String goodsId;//: null,
	private String goodsName; //: null,
	private String goodsDesc;// null,
	private String goodsPrice;// 0.01,
	private String createTime;//": 1524196783000,
	private String updateTime;//: null,
	private String payTime; //: 1524196788000,
	private String status; // "1",
	private String openId;
	private String transactionId;//渠道订单号
	private String channelOrderId;//终端订单号
	private String channel;
	private String mType;
	private String storeId;
	private String eId;
	private String parentId;
	private String agentId;
	private String refundStatus;
	private String refundAmount;
	private String merchantRemark;
	private String microMchId;//交易单号
	private String notify_url;
	private String factorage;
	private String rate;
	private String surplus;
	private String pay_surplus;
	private String source;
	private String front_url;
	private String discount;
	private String refund_code;
	private String pay_fac;
	
	public OrderDetailData() {
		super();
		// TODO Auto-generated constructor stub
	}



	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getPayWay() {
		return payWay;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getGoodsType() {
		return goodsType;
	}

	public void setGoodsType(String goodsType) {
		this.goodsType = goodsType;
	}

	public String getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(String goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsDesc() {
		return goodsDesc;
	}

	public void setGoodsDesc(String goodsDesc) {
		this.goodsDesc = goodsDesc;
	}

	public String getGoodsPrice() {
		return goodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		this.goodsPrice = goodsPrice;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getPayTime() {
		return payTime;
	}

	public void setPayTime(String payTime) {
		this.payTime = payTime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getChannelOrderId() {
		return channelOrderId;
	}

	public void setChannelOrderId(String channelOrderId) {
		this.channelOrderId = channelOrderId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getmType() {
		return mType;
	}

	public void setmType(String mType) {
		this.mType = mType;
	}

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String geteId() {
		return eId;
	}

	public void seteId(String eId) {
		this.eId = eId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getAgentId() {
		return agentId;
	}

	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}

	public String getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}

	public String getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(String refundAmount) {
		this.refundAmount = refundAmount;
	}

	public String getMerchantRemark() {
		return merchantRemark;
	}

	public void setMerchantRemark(String merchantRemark) {
		this.merchantRemark = merchantRemark;
	}

	public String getMicroMchId() {
		return microMchId;
	}

	public void setMicroMchId(String microMchId) {
		this.microMchId = microMchId;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getFactorage() {
		return factorage;
	}

	public void setFactorage(String factorage) {
		this.factorage = factorage;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getSurplus() {
		return surplus;
	}

	public void setSurplus(String surplus) {
		this.surplus = surplus;
	}

	public String getPay_surplus() {
		return pay_surplus;
	}

	public void setPay_surplus(String pay_surplus) {
		this.pay_surplus = pay_surplus;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getFront_url() {
		return front_url;
	}

	public void setFront_url(String front_url) {
		this.front_url = front_url;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getRefund_code() {
		return refund_code;
	}

	public void setRefund_code(String refund_code) {
		this.refund_code = refund_code;
	}

	public String getPay_fac() {
		return pay_fac;
	}

	public void setPay_fac(String pay_fac) {
		this.pay_fac = pay_fac;
	}
	
	
	
}
