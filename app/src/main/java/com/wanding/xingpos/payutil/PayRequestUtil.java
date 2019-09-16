package com.wanding.xingpos.payutil;

import com.wanding.xingpos.bean.PosPayQueryReqData;
import com.wanding.xingpos.bean.PosRefundReqData;
import com.wanding.xingpos.bean.PosScanpayReqData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.utils.DateFormatUtils;
import com.wanding.xingpos.utils.RandomStringGenerator;

import java.util.Date;


/**
 * 二维码支付,退款,查询交易请求参数公共类
 */
public class PayRequestUtil {

	private static String pay_ver = "100";


    /**
     * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
     * 值为 newland 表示新大陆
     * 值为 fuyousf 表示富友
     */
    private static final String NEW_LAND = "newland";
    private static final String FUYOU_SF= "fuyousf";
	
	/**
	 * pay_ver	版本号，当前版本“100”
	 * pay_type	请求类型，“010”微信，“020”支付宝，“060”qq钱包
	 * service_id	接口类型，当前类型“010”
	 * merchant_no	商户号
	 * terminal_id	终端号
	 * terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
	 * terminal_time	终端交易时间，yyyyMMddHHmmss，全局统一时间格式
	 * auth_no	授权码(二维码号)
	 * total_fee	金额，单位分
	 * order_body	订单描述
	 * key_sign	签名检验串,拼装所有必传参数+令牌，32位md5加密转换
	 */
	public static PosScanpayReqData payReq(String pay_type, String auth_no, String total_fee, UserLoginResData loginInitData, String posProvider){
		
		PosScanpayReqData posBean = new PosScanpayReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "010";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000177";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "10107";

        String terminal_traceStr = "";
    	if(posProvider.equals(NEW_LAND)){
            //terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
            terminal_traceStr = RandomStringGenerator.getAWRandomNum();
        }else if(posProvider.equals(FUYOU_SF)){
            terminal_traceStr = RandomStringGenerator.getAFRandomNum();
        }
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String auth_noStr = auth_no;
		String total_feeStr = total_fee;
		String order_bodyStr = "";
		
		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setAuth_no(auth_noStr);
		posBean.setTotal_fee(total_feeStr);
		posBean.setOrder_body(order_bodyStr);
		
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));


		
		return posBean;
	}
	
	/**
	 * pay_ver	版本号，当前版本“100”
	 * pay_type	请求类型，“010”微信，“020”支付宝，“060”qq钱包
	 * service_id	接口类型，当前类型“030”
	 * merchant_no	商户号
	 * terminal_id	终端号
	 * terminal_trace	终端退款流水号（socket协议：长度为6位，Http协议：长度为32位）
	 * terminal_time	终端交易时间，yyyyMMddHHmmss，全局统一时间格式
	 * refund_fee	退款金额，单位分
	 * out_trade_no	订单号，查询凭据，万鼎订单号、微信订单号、支付宝订单号任意一个
	 * operator_id	操作员号
	 * key_sign	签名检验串,拼装所有必传参数+令牌，32位md5加密转换
	 */
	public static PosRefundReqData refundReq(String refund_fee, String out_trade_no, UserLoginResData loginInitData, String posProvider){
		
	
		
		PosRefundReqData posBean = new PosRefundReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = "";
		String service_idStr = "030";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000177";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "10107";

		String terminal_traceStr = "";
		if(posProvider.equals(NEW_LAND)){
			//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
			terminal_traceStr = RandomStringGenerator.getAWRandomNum();
		}else if(posProvider.equals(FUYOU_SF)){
			terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		}
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String refund_feeStr = refund_fee;
		String out_trade_noStr = out_trade_no;
		//operator_id 操作员号	否	String	2
		String operator_idStr = "";
		
		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setRefund_fee(refund_feeStr);
		posBean.setOut_trade_no(out_trade_noStr);
		posBean.setOperator_id(operator_idStr);
		
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
		
		
		
		return posBean;
	}
	
	/**
	 * pay_ver	版本号，当前版本“100”
	 * pay_type	请求类型，“010”微信，“020”支付宝，“060”qq钱包
	 * service_id	接口类型，当前类型“020”
	 * merchant_no	商户号
	 * terminal_id	终端号
	 * terminal_trace	终端退款流水号（socket协议：长度为6位，Http协议：长度为32位）
	 * terminal_time	终端交易时间，yyyyMMddHHmmss，全局统一时间格式
	 * refund_fee	退款金额，单位分
	 * out_trade_no	订单号，查询凭据，万鼎订单号、微信订单号、支付宝订单号任意一个
	 * operator_id	操作员号
	 * key_sign	签名检验串,拼装所有必传参数+令牌，32位md5加密转换
	 */
	public static PosPayQueryReqData queryReq(String pay_type, String out_trade_no, UserLoginResData loginInitData, String posProvider){
		
		PosPayQueryReqData posBean = new PosPayQueryReqData();
		String pay_verStr = pay_ver;
		String pay_typeStr = pay_type;
		String service_idStr = "020";

		//merchant_no	商户号
		String merchant_noStr = loginInitData.getMerchant_no();
//		String merchant_noStr = "1000177";
		//terminal_id	终端号
		String terminal_idStr = loginInitData.getTerminal_id();
//		String terminal_idStr = "10107";

		String terminal_traceStr = "";
		if(posProvider.equals(NEW_LAND)){
			//terminal_trace	终端流水号（socket协议：长度为6位，Http协议：长度为32位）
			terminal_traceStr = RandomStringGenerator.getAWRandomNum();
		}else if(posProvider.equals(FUYOU_SF)){
			terminal_traceStr = RandomStringGenerator.getAFRandomNum();
		}
		String terminal_timeStr = DateFormatUtils.ISO_DATETIME_SS.format(new Date());
		String out_trade_noStr = out_trade_no;
		//pay_trace	当前支付终端流水号，与pay_time同时传递	否	String	6
		String pay_traceStr = "";
		//pay_time	当前支付终端交易时间，yyyyMMddHHmmss，全局统一时间格式	否	String	14
		String pay_timeStr = "";
		//operator_id 操作员号	否	String	2
		String operator_idStr = "";
		
		posBean.setPay_ver(pay_verStr);
		posBean.setPay_type(pay_typeStr);
		posBean.setService_id(service_idStr);
		posBean.setMerchant_no(merchant_noStr);
		posBean.setTerminal_id(terminal_idStr);
		posBean.setTerminal_trace(terminal_traceStr);
		posBean.setTerminal_time(terminal_timeStr);
		posBean.setOut_trade_no(out_trade_noStr);
		posBean.setPay_trace(pay_traceStr);
		posBean.setPay_time(pay_timeStr);
		posBean.setOperator_id(operator_idStr);
		posBean.setKey_sign(posBean.getSignStr(loginInitData.getAccess_token()));
//		posBean.setKey_sign(posBean.getSignStr("u3n38fwe6u7ic86dps4v21oqr8s0l53p"));
		return posBean;
	}
}
