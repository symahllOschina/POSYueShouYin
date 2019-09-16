package com.wanding.xingpos.bean;

import java.io.Serializable;

/**
 * 分期查询订单返回实体
 */
public class InstalmentQueryResData implements Serializable {

    /**
     * contractsState的为1时可以申请退款，提交申请之后，contractsState的值不变化，state的值会发生改变
     * contractsState 为5表示退款已完成  如果当天退款是全额退  返回的可退金额为-1，实际是全额，隔日退款会有手续费
     * state的值会随着提交申请退款后而发生变化，如果退款失败contractsState的值不变，可以继续退款
     */

    private String txnTime;//交易时间"txnTime": "20180507143329",
    private String merchantId;//小票商户号"merchantId": "85712017011L828",
    private String stoeId;//门店号"stoeId": "101110070111176",
    private String txnAmt;//交易金额"txnAmt": "60000",
    private String poundage;//手续金额，单位分
    private String totleAmount;//总金额，单位分
    private String txnterms;//分期数"txnterms": "6",
    private String orderId;//订单号"orderId": "2018050714291762349",
    private String accName;//持卡人姓名"accName": "无语",
    private String accNo;//持卡人信用卡账号"accNo": "3568805952321414",
    private String validity;//信用卡有效期"validity": "1122",
    private String contractsCode;//交易合同号"contractsCode": "HT20180507143328110145",
    private String amount;//首期应还金额，单位分"amount": "10000",
    private String contractsState;//0,1未结清 2,3,4已结清 5已结清（退款操作）"contractsState": "1",
    private String sumTerms;//已还款总期数"sumTerms": "1",
    private String sumAmount;//已还款总金额"sumAmount": "10000",
    private String remainAmount;//剩余还款金额"remainAmount": "50000",
    private String refundId;//退款单号
    private String applyCancelAmount;//申请退款金额
    private String cancelAmount;//商户退款金额（单位分）"cancelAmount": "-1",
    private String cancelInterest;//商户退款利息（单位分） "cancelInterest": "-1",
    private String state;//退款状态：0退款失败，1退款成功，2人工审核，无值或为null时表示没有退款行为"state": null,
    private String merName;//商户名称"merName": "福建新大陆电脑股份有限公司",
    private String vocher;//是否上传退款凭证 1已上传，其余未上传"vocher": null,
    private String respCode;//"respCode": "000000",
    private String respMsg;//"respMsg": "成功"


    public InstalmentQueryResData() {
    }

    public String getTxnTime() {
        return txnTime;
    }

    public void setTxnTime(String txnTime) {
        this.txnTime = txnTime;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getStoeId() {
        return stoeId;
    }

    public void setStoeId(String stoeId) {
        this.stoeId = stoeId;
    }

    public String getTxnAmt() {
        return txnAmt;
    }

    public void setTxnAmt(String txnAmt) {
        this.txnAmt = txnAmt;
    }

    public String getPoundage() {
        return poundage;
    }

    public void setPoundage(String poundage) {
        this.poundage = poundage;
    }

    public String getTotleAmount() {
        return totleAmount;
    }

    public void setTotleAmount(String totleAmount) {
        this.totleAmount = totleAmount;
    }

    public String getTxnterms() {
        return txnterms;
    }

    public void setTxnterms(String txnterms) {
        this.txnterms = txnterms;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getContractsCode() {
        return contractsCode;
    }

    public void setContractsCode(String contractsCode) {
        this.contractsCode = contractsCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getContractsState() {
        return contractsState;
    }

    public void setContractsState(String contractsState) {
        this.contractsState = contractsState;
    }

    public String getSumTerms() {
        return sumTerms;
    }

    public void setSumTerms(String sumTerms) {
        this.sumTerms = sumTerms;
    }

    public String getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(String sumAmount) {
        this.sumAmount = sumAmount;
    }

    public String getRemainAmount() {
        return remainAmount;
    }

    public void setRemainAmount(String remainAmount) {
        this.remainAmount = remainAmount;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    public String getApplyCancelAmount() {
        return applyCancelAmount;
    }

    public void setApplyCancelAmount(String applyCancelAmount) {
        this.applyCancelAmount = applyCancelAmount;
    }

    public String getCancelAmount() {
        return cancelAmount;
    }

    public void setCancelAmount(String cancelAmount) {
        this.cancelAmount = cancelAmount;
    }

    public String getCancelInterest() {
        return cancelInterest;
    }

    public void setCancelInterest(String cancelInterest) {
        this.cancelInterest = cancelInterest;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMerName() {
        return merName;
    }

    public void setMerName(String merName) {
        this.merName = merName;
    }

    public String getVocher() {
        return vocher;
    }

    public void setVocher(String vocher) {
        this.vocher = vocher;
    }

    public String getRespCode() {
        return respCode;
    }

    public void setRespCode(String respCode) {
        this.respCode = respCode;
    }

    public String getRespMsg() {
        return respMsg;
    }

    public void setRespMsg(String respMsg) {
        this.respMsg = respMsg;
    }
}
