package com.wanding.xingpos.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 汇总返回实体
 */
public class SummaryResData implements Serializable {

	private Integer sumTotal;//总笔数
	private Double sumAmt;//总金额
	private String isHistory;//N:表示当天，Y:表示昨天历史
	private List<PayTypeBean> orderSumList;
	
	
	public SummaryResData() {
		super();
		// TODO Auto-generated constructor stub
	}


	public Integer getSumTotal() {
		return sumTotal;
	}


	public void setSumTotal(Integer sumTotal) {
		this.sumTotal = sumTotal;
	}


	public Double getSumAmt() {
		return sumAmt;
	}


	public void setSumAmt(Double sumAmt) {
		this.sumAmt = sumAmt;
	}


	public String getIsHistory() {
		return isHistory;
	}


	public void setIsHistory(String isHistory) {
		this.isHistory = isHistory;
	}


	public List<PayTypeBean> getOrderSumList() {
		return orderSumList;
	}


	public void setOrderSumList(List<PayTypeBean> orderSumList) {
		this.orderSumList = orderSumList;
	}



	
	
	
	
}
