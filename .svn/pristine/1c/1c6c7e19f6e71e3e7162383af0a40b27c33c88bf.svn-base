package com.wanding.xingpos.bean;

import com.wanding.xingpos.utils.MD5;

import java.io.Serializable;

/**
 * 支付查询接口
 * @author fujiarui 
 *
 */
public class PosPayQueryResData implements Serializable {
	
	private String return_code;//响应码
	
	private String return_msg;//返回信息提示
	
	
	private String result_code;//业务结果
	
	private String pay_type;//请求类型
	
	private String merchant_name;//商户名称
	
	private String merchant_no;//商户号
	
	private String terminal_id;//终端号
	
	private String terminal_trace;//终端流水号
	
	private String terminal_time;//终端交易时间
	
	private String total_fee;//金额
	
	private String end_time;//支付完成时间
	
	private String out_trade_no;//唯一订单号
	private String channel_trade_no;//通道订单号，微信订单号、支付宝订单号等，返回时不参与签名
	private String channel_order_no;//银行渠道订单号，微信支付时显示在支付成功页面的条码，可用作扫码查询和扫码退款时匹配
	private String user_id;//付款方用户id，“微信openid”、“支付宝账户”、“qq号”等，返回时不参与签名
	private String attach;//附加数据，原样返回，返回时不参与签名
	private String receipt_fee;//口碑实收金额，pay_type为090时必填
	
//	private String qr_code;//二维码码串
	
	private String key_sign;//签名检验串

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getReturn_msg() {
		return return_msg;
	}

	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

	public String getMerchant_no() {
		return merchant_no;
	}

	public void setMerchant_no(String merchant_no) {
		this.merchant_no = merchant_no;
	}

	public String getTerminal_id() {
		return terminal_id;
	}

	public void setTerminal_id(String terminal_id) {
		this.terminal_id = terminal_id;
	}

	public String getTerminal_trace() {
		return terminal_trace;
	}

	public void setTerminal_trace(String terminal_trace) {
		this.terminal_trace = terminal_trace;
	}

	public String getTerminal_time() {
		return terminal_time;
	}

	public void setTerminal_time(String terminal_time) {
		this.terminal_time = terminal_time;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

/*	public String getQr_code() {
		return qr_code;
	}

	public void setQr_code(String qr_code) {
		this.qr_code = qr_code;
	}*/

	public String getChannel_trade_no() {
		return channel_trade_no;
	}

	public void setChannel_trade_no(String channel_trade_no) {
		this.channel_trade_no = channel_trade_no;
	}

	public String getChannel_order_no() {
		return channel_order_no;
	}

	public void setChannel_order_no(String channel_order_no) {
		this.channel_order_no = channel_order_no;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getReceipt_fee() {
		return receipt_fee;
	}

	public void setReceipt_fee(String receipt_fee) {
		this.receipt_fee = receipt_fee;
	}

	public String getKey_sign() {
		return key_sign;
	}

	public void setKey_sign(String key_sign) {
		this.key_sign = key_sign;
	}
	public String getSignStr(String access_token){
        final StringBuilder sb = new StringBuilder("");
        sb.append("return_code=").append(return_code).append("&");
        sb.append("return_msg=").append(return_msg).append("&");
        sb.append("result_code=").append(result_code).append("&");
        sb.append("pay_type=").append(pay_type).append("&");
        sb.append("merchant_name=").append(merchant_name).append("&");
        sb.append("merchant_no=").append(merchant_no).append("&");
        sb.append("terminal_id=").append(terminal_id).append("&");
        sb.append("terminal_trace=").append(terminal_trace).append("&");
        sb.append("terminal_time=").append(terminal_time).append("&");
        sb.append("total_fee=").append(total_fee).append("&");
        sb.append("end_time=").append(end_time).append("&");
        sb.append("out_trade_no=").append(out_trade_no).append("&");
        sb.append("out_trade_no=").append(out_trade_no).append("&");
        String keySign = MD5.MD5Encode(sb.toString());
//        this.setKey_sign(keySign);
		return keySign;
	}


	

}
