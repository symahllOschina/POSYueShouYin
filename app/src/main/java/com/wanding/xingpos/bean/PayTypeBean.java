package com.wanding.xingpos.bean;

import java.io.Serializable;

public class PayTypeBean implements Serializable {

	private String payWay;//类型
	private Integer total;//笔数
	private Double amount;//金额
	
	public PayTypeBean() {
		super();
		// TODO Auto-generated constructor stub
	}

	

	public String getPayWay() {
		return payWay;
	}



	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}



	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	
	
}
