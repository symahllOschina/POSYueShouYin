package com.wanding.xingpos.payutil;

import com.wanding.xingpos.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 支付帮助类 */
public class PayUtils {

	/**
	 *  支付宝支付二维码规则
	 *  https://docs.open.alipay.com/194/105322/
	 * 由于业务发展需要，支付宝将会在2017年9月底对支付宝的用户付款码做升级处理。付款码将由原来的28开头扩充到25-30开头，
	 * 长度由原来的16-18位扩充到16-24位。未来随移动支付产业的发展，用户付款码可能会有所加长，建议开发者做好设计预留工作。
	 */
	private static final Pattern aliPayAuthCodeReg = Pattern.compile("^(((2[5-9])|(30))\\d{14,22})$");

	/**
	 * 微信支付二维码规则
	 * https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=5_1
	 * 用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头
	 */
	private static final Pattern weixinPayAuthCodeReg = Pattern.compile("^1[0-5]\\d{16}$");

	/**
	 * 翼支付二维码规则
	 * 用户刷卡条形码规则：51开始 18位
	 */
	private static final Pattern bestPayAuthCodeReg = Pattern.compile("^51\\d{16}$");
	/**
	 * 直接返回业务码：
	 * 银行卡，微信，支付宝，银联，翼支付分别顺序对应：040,010,020,030,050
	 */
	public static String getPayType(String auth_no){
		String payType = "";
		if(weixinPayAuthCodeReg.matcher(auth_no).matches()){
			payType = Constants.PAYTYPE_010WX;
		}else if(aliPayAuthCodeReg.matcher(auth_no).matches()){
			payType = Constants.PAYTYPE_020ALI;
		}else if(bestPayAuthCodeReg.matcher(auth_no).matches()){
			payType = Constants.PAYTYPE_050BEST;
		}
		return payType;

	}


	/**
	 微信默认支付生成的退款码：		20180718100229508013728246503209   32位
	 								20180718104006076010243646865935
	 翼支付支付生成的退款商户号：		20180718104248491025636044878377
	 支付宝默认支付生成的退款码：		104809457722118071810264204411	   30位
	 								104809455022118071810341305221

	 微信星POS支付生成的退款码：		20180718100713261413	20位
	 								20180718103845292266

	 支付宝星POS支付生成的退款码：	91531880647269346951
	 								91531881435733411693

	 银联二维码付款凭证号：			20180718310584592997
	 *
	 */


	/**  该表达式表示字符串包含UD开头字符，后面字符串长度为30位或者以数字9开头后面字符串长度为19位 */
	public static Pattern pattern = Pattern.compile("^UD[A-Za-z0-9]{30}$|^9[A-Za-z0-9]{19}$|[A-Za-z0-9]{12}$");//^NL\\d{25,30}
	/** 根据扫描的二维码结果码判断该二维码是星POS服务交易的还是自己的服务交易 */
	public static boolean getPayService(String auth_no){
		
		Matcher match = pattern.matcher(auth_no);
		
		return match.matches();
		
	}
	
	
	
}
