package com.wanding.xingpos.bean;

import java.io.Serializable;

/** POS机初次激活获取的设备信息实体对象  */
public class PosInitData implements Serializable {

	/**
	 * posProvider：表示pos机厂商（提供者），默认情况下为新大陆newland
	 * 				当调用新大陆SDK签到提示找不到界面时posProvider的值发生变化，改为 posProvider = "fuyousf"
	 */
	private String posProvider;

	private String mercId_pos=""; //商户号（pos机商户识别号（POS接口使用））
	
	private String trmNo_pos=""; //设备号 进件生成的终端号（POS接口使用）;
	
	
	private String mername_pos = "";//商户名称例如"新大陆行业部测试",
	private String batchno_pos = "";//批次号
	
	
	public PosInitData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getPosProvider() {
		return posProvider;
	}

	public void setPosProvider(String posProvider) {
		this.posProvider = posProvider;
	}

	public String getMercId_pos() {
		return mercId_pos;
	}


	public void setMercId_pos(String mercId_pos) {
		this.mercId_pos = mercId_pos;
	}


	public String getTrmNo_pos() {
		return trmNo_pos;
	}


	public void setTrmNo_pos(String trmNo_pos) {
		this.trmNo_pos = trmNo_pos;
	}


	public String getMername_pos() {
		return mername_pos;
	}


	public void setMername_pos(String mername_pos) {
		this.mername_pos = mername_pos;
	}


	public String getBatchno_pos() {
		return batchno_pos;
	}


	public void setBatchno_pos(String batchno_pos) {
		this.batchno_pos = batchno_pos;
	}


	
	
}
