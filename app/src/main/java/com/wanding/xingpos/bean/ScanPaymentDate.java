package com.wanding.xingpos.bean;

import java.io.Serializable;

/** 星pos服务扫码支付返回JSON实体  */
public class ScanPaymentDate implements Serializable {

	private String mername;// 	String 	商户名称 	交易类和查询类交易返回
	private String merid;// 	String 	商户号 	交易类和查询类交易返回
	private String termid;// 	String 	终端号 	交易类和查询类交易返回
	private String acqno;// 	String 	收单行 	
	private String iisno;// 	String 	发卡行 	
	private String expdate;// 	String 	有效期 	
	private String batchno;// 	String 	批次号 	
	private String systraceno;// 	String 	凭证号 	
	private String authcode;// 	String 	授权号 	
	private String orderid_scan;// 	String 	订单号 	
	private String translocaltime;// 	String 	交易时间 	
	private String translocaldate;// 	String 	交易日期 	
	private String transamount;// 	String 	交易金额 	
	private String priaccount;// 	String 	卡号 	
	private String refernumber;// 	String 	系统参考号 	
	private String pay_tp;// 	String 	交易类型 	查询类交易返回
	
	
	public ScanPaymentDate() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getMername() {
		return mername;
	}


	public void setMername(String mername) {
		this.mername = mername;
	}


	public String getMerid() {
		return merid;
	}


	public void setMerid(String merid) {
		this.merid = merid;
	}


	public String getTermid() {
		return termid;
	}


	public void setTermid(String termid) {
		this.termid = termid;
	}


	public String getAcqno() {
		return acqno;
	}


	public void setAcqno(String acqno) {
		this.acqno = acqno;
	}


	public String getIisno() {
		return iisno;
	}


	public void setIisno(String iisno) {
		this.iisno = iisno;
	}


	public String getExpdate() {
		return expdate;
	}


	public void setExpdate(String expdate) {
		this.expdate = expdate;
	}


	public String getBatchno() {
		return batchno;
	}


	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}


	public String getSystraceno() {
		return systraceno;
	}


	public void setSystraceno(String systraceno) {
		this.systraceno = systraceno;
	}


	public String getAuthcode() {
		return authcode;
	}


	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}


	public String getOrderid_scan() {
		return orderid_scan;
	}


	public void setOrderid_scan(String orderid_scan) {
		this.orderid_scan = orderid_scan;
	}


	public String getTranslocaltime() {
		return translocaltime;
	}


	public void setTranslocaltime(String translocaltime) {
		this.translocaltime = translocaltime;
	}


	public String getTranslocaldate() {
		return translocaldate;
	}


	public void setTranslocaldate(String translocaldate) {
		this.translocaldate = translocaldate;
	}


	public String getTransamount() {
		return transamount;
	}


	public void setTransamount(String transamount) {
		this.transamount = transamount;
	}


	public String getPriaccount() {
		return priaccount;
	}


	public void setPriaccount(String priaccount) {
		this.priaccount = priaccount;
	}


	public String getRefernumber() {
		return refernumber;
	}


	public void setRefernumber(String refernumber) {
		this.refernumber = refernumber;
	}


	public String getPay_tp() {
		return pay_tp;
	}


	public void setPay_tp(String pay_tp) {
		this.pay_tp = pay_tp;
	}
	
	
	
}
