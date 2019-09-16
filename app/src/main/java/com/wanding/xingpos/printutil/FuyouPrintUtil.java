package com.wanding.xingpos.printutil;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.fuyousf.android.fuious.service.PrintInterface;
import com.wanding.xingpos.auth.bean.PreLicensingResp;
import com.wanding.xingpos.bean.CardPaymentDate;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.bean.PayTypeBean;
import com.wanding.xingpos.bean.PosPayQueryResData;
import com.wanding.xingpos.bean.PosRefundResData;
import com.wanding.xingpos.bean.PosScanpayResData;
import com.wanding.xingpos.bean.ScanPaymentDate;
import com.wanding.xingpos.bean.ShiftResData;
import com.wanding.xingpos.bean.SubReocrdSummaryResData;
import com.wanding.xingpos.bean.SubTimeSummaryResData;
import com.wanding.xingpos.bean.SubTotalSummaryResData;
import com.wanding.xingpos.bean.SummaryResData;
import com.wanding.xingpos.bean.UserLoginResData;
import com.wanding.xingpos.card.stock.bean.CardStockTypeResData;
import com.wanding.xingpos.card.stock.bean.CheckCardStockRecodeDetailResData;
import com.wanding.xingpos.card.stock.bean.CheckCardStockResData;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**  
 * 星POS打印机操作类
 */
public class FuyouPrintUtil {

	public static final int PRINT_REQUEST_CODE = 99;
	/** 打印第二联间隔时间 */
	public static final long time = 1700;

	public static final int ERROR_NONE = 0;  //正常状态，无错误
	public static final int ERROR_PAPERENDED = 240;  //缺纸，不能打印
	public static final int ERROR_HARDERR = 242;     //硬件错误
	public static final int ERROR_OVERHEAT = 243;    //打印头过热
	public static final int ERROR_BUFOVERFLOW = 245; //缓冲模式下所操作的位置超出范围
	public static final int ERROR_LOWVOL = 225;      //低压保护

	/** 统一空格字符串   */
	private static final String twoSpaceStr = "  ";
	private static final String threeSpaceStr = "   ";
	private static final String fiveSpaceStr = "     ";
	private static final String sixSpaceStr = "      ";
	private static final String sevenSpaceStr = "       ";
	private static final String eightSpaceStr = "        ";
	private static final String nineSpaceStr = "         ";


	//刷卡，微信，支付宝，银联，翼支付，银联分别顺序对应：040,010,020,030,050,060
	private static String getPayTypeStr(String payType){
		if("040".equals(payType)){
			return "刷卡支付/BANK PAY";
		}else if("010".equals(payType)){
			return "微信支付/WEIXIN PAY";
		}else if("020".equals(payType)){
			return "支付宝支付/ALI PAY";
		}else if("030".equals(payType)){
			return "银联二维码/UNIONPAY PAY";
		}else if("050".equals(payType)){
			return "翼支付/BEST PAY";
		}else if("060".equals(payType)){
			return "银联二维码支付/UNIONPAY PAY";
		}else if("101".equals(payType)){
			return "扫码预授权/PAY";
		}
		return "";
	}
	
	private static String getQueryPayTypeStr(String payType){
		if("WX".equals(payType)){
			return "微信支付/WEIXIN PAY";
		}else if("ALI".equals(payType)){
			return "支付宝支付/ALI PAY";
		}else if("BEST".equals(payType)){
			return "翼支付/BEST PAY";
		}else if("UNIONPAY".equals(payType)){
			return "银联二维码支付/UNIONPAY PAY";
		}else if("DEBIT".equals(payType) || "CREDIT".equals(payType)){
			//DEBIT= 借记卡       CREDIT=贷记卡 
			return "银行卡支付/BANK PAY";
		}else if("BANK".equals(payType)){
			// BANK = 银行卡
			return "银行卡支付/BANK PAY";
		}
		
		return "";
	}

	private static String getOrderTypeStr(String payType, String orderTypeStr){
		//先判断是支付交易还是退款交易 0正向 ,1退款
		if("0".equals(orderTypeStr)){
			if("WX".equals(payType)){
				return "微信支付/WEIXIN PAY";
			}else if("ALI".equals(payType)){
				return "支付宝支付/ALI PAY";
			}else if("BEST".equals(payType)){
				return "翼支付/BEST PAY";
			}else if("UNIONPAY".equals(payType)){
				return "银联二维码支付/UNIONPAY PAY";
			}else if("DEBIT".equals(payType) || "CREDIT".equals(payType)){
				//DEBIT= 借记卡       CREDIT=贷记卡
				return "银行卡支付/BANK PAY";
			}else if("BANK".equals(payType)){
				// BANK = 银行卡
				return "银行卡支付/BANK PAY";
			}
		}else if("1".equals(orderTypeStr)){
			if("WX".equals(payType)){
				return "微信退款";
			}else if("ALI".equals(payType)){
				return "支付宝退款";
			}else if("BEST".equals(payType)){
				return "翼支付退款";
			}else if("UNIONPAY".equals(payType)){
				return "银联二维码退款";
			}else if("DEBIT".equals(payType) || "CREDIT".equals(payType)){
				//DEBIT= 借记卡       CREDIT=贷记卡
				return "消费撤销";
			}else if("BANK".equals(payType)){
				// BANK = 银行卡
				return "消费撤销";
			}
		}


		return "";
	}
	
	//刷卡，微信，支付宝，银联，分别顺序对应：0,11,12,13
	private static String getPOSPayTypeStr(String payType){
		if(payType!=null&&!"null".equals(payType) &&!"".equals(payType)){
			if("11".equals(payType)){
				return "微信支付/WEIXIN PAY";
			}else if("12".equals(payType)){
				return "支付宝支付/ALI PAY";
			}else if("13".equals(payType)){
				return "银联二维码支付/UNIONPAY PAY";
			}
		}
		
		return "扫码";
	}

	private static String getRefundPayTypeStr(String payType){
		if(Utils.isEmpty(payType)){
			return "扫码退款/REFUND";
		}
		if("WX".equals(payType)){
			return "微信退款";
		}else if("ALI".equals(payType)){
			return "支付宝退款";
		}else if("BEST".equals(payType)){
			return "翼支付退款";
		}else if("UNIONPAY".equals(payType)){
			return "银联二维码退款";
		}


		return "扫码退款/REFUND";
	}


    /**
     * 扫码预授权，预授权撤销，预授权完成
     * payType支付方式：010：微信，020：支付宝
     * auth_type支付类型：1:预授权，2：预授权撤销，3：预授权完成
     *
     */

    private static String getAuthPayTypeStr(String payType, String auth_type){
        if("010".equals(payType)){
            if("1".equals(auth_type)){
                return "微信预授权";
            }else if("2".equals(auth_type)){
                return "微信预授权撤销";
            }else if("3".equals(auth_type)){
                return "微信预授权完成";
            }

        }else if("020".equals(payType)){
            if("1".equals(auth_type)){
                return "支付宝预授权";
            }else if("2".equals(auth_type)){
                return "支付宝预授权撤销";
            }else if("3".equals(auth_type)){
                return "支付宝预授权完成";
            }
        }
        return "";
    }


	public static String getDateTimeFormatStr(String timeStr) {
		//20160325160000
		if(Utils.isNotEmpty(timeStr)){
			if(timeStr.length()>=14){
				String year = timeStr.substring(0,4);
				String month = timeStr.substring(4,6);
				String day = timeStr.substring(6,8);
				String hour = timeStr.substring(8,10);
				String minute = timeStr.substring(10,12);
				String second = timeStr.substring(12);
				Log.e("日期解析：",year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second);
				return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
			}
		}

		return "";
	}

	//	private static String tel = "POS客服电话  029-88445534";
	private static String tel = "悦收银客服电话  400-888-5400";

	public static final String payPrintRemarks = "succ";//正常支付成功备注
	public static final String rePrintRemarks = "cdy";//重打印备注
  
    
    /** 
     *  支付成功打印(包括重打印)
     */
    public static void paySuccessPrintText(Context context, PrintInterface printService, PosScanpayResData payResData,
                                           UserLoginResData posPublicData, boolean isDefault, final String printRemarks, final int index){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型："+getPayTypeStr(payResData.getPay_type()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(getPayTypeStr(payResData.getPay_type()), 1, 3));
			}
			String outTradeNoStr = payResData.getOut_trade_no();
			String outTradeNo = "";
			String outTradeNoSuffix = "";
			if(Utils.isNotEmpty(outTradeNoStr)&&outTradeNoStr.length()>=32){
				outTradeNo = outTradeNoStr.substring(0,24);
				outTradeNoSuffix = outTradeNoStr.substring(24);
				jsonArray.put(PrintUtils.setStringContent("订单号："+outTradeNo, 1, 2));
				jsonArray.put(PrintUtils.setStringContent("        "+outTradeNoSuffix, 1, 2));

			}else{
				outTradeNo = outTradeNoStr;
				jsonArray.put(PrintUtils.setStringContent("订单号："+outTradeNo, 1, 2));
			}

			jsonArray.put(PrintUtils.setStringContent("日期/时间："+getDateTimeFormatStr(payResData.getEnd_time()), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+DecimalUtil.branchToElement(payResData.getTotal_fee()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+DecimalUtil.branchToElement(payResData.getTotal_fee()), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			if(index == 1){
				jsonArray.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
				jsonArray.put(PrintUtils.setStringContent("交易金额不足300.00元，无需签名", 2, 2));
				jsonArray.put(PrintUtils.setStringContent("本人确认以上交易，同意将其记入本卡账户", 1, 2));
			}
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				if(index == 1){
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 2));
					}else{
						jsonArray.put(PrintUtils.setStringContent("商户联", 1, 2));
					}

				}else{
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 2));
					}else{
						jsonArray.put(PrintUtils.setStringContent("客户联", 1, 2));
					}
				}
			}else{
				if(index == 1){
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 3));
					}else{
						jsonArray.put(PrintUtils.setStringContent("商户联", 1, 3));
					}

				}else{
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 3));
					}else{
						jsonArray.put(PrintUtils.setStringContent("客户联", 1, 3));
					}
				}
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    
    /** 
     *  支付成功打印(第二联)
     */
    public static void paySuccessPrintTextTwo(Context context, PrintInterface printService, PosScanpayResData payResData,
                                              UserLoginResData posPublicData, boolean isDefault, final String printNum){

		getPrinterStatus(context,printService);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型："+getPayTypeStr(payResData.getPay_type()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(getPayTypeStr(payResData.getPay_type()), 1, 3));
			}
			//扫码预授权
			if("101".equals(payResData.getPay_type())){
				jsonArray.put(PrintUtils.setStringContent("授 权 码："+payResData.getOut_trade_no(), 1, 2));
				jsonArray.put(PrintUtils.setStringContent("日期/时间："+payResData.getEnd_time(), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("订 单 号："+payResData.getOut_trade_no(), 1, 2));
				jsonArray.put(PrintUtils.setStringContent("日期/时间："+getDateTimeFormatStr(payResData.getEnd_time()), 1, 2));
			}


			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+DecimalUtil.branchToElement(payResData.getTotal_fee()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+DecimalUtil.branchToElement(payResData.getTotal_fee()), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("客户联", 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("客户联", 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

    } 
    /** 
     *  支付成功打印(二维码交易重打印,数据源来自星POS支付或退款返回)
     */
    public static void posPayAndRefundAgainPrintText(Context context, PrintInterface printService, ScanPaymentDate paymentData,
                                                     UserLoginResData posPublicData, String orderType, boolean isDefault, final int index){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if("pay".equals(orderType)){
				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型："+getPOSPayTypeStr(paymentData.getPay_tp()), 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent(getPOSPayTypeStr(paymentData.getPay_tp()), 1, 3));
				}
			}else{

				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型："+getRefundPayTypeStr(paymentData.getPay_tp()), 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent(getRefundPayTypeStr(paymentData.getPay_tp()), 1, 3));
				}
			}
			jsonArray.put(PrintUtils.setStringContent("订 单 号："+paymentData.getOrderid_scan(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+DateTimeUtil.timeStrToDateStr(paymentData.getTranslocaldate()+paymentData.getTranslocaltime()), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+paymentData.getTransamount(), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+paymentData.getTransamount(), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
            if(isDefault){
                if(index == 1){
                    jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 2));
                }else{
                    jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 2));
                }

            }else{
                if(index == 1){
                    jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 3));
                }else{
                    jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 3));
                }
            }
            jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }



	/**
	 *  预授权，预授权撤销，预授权完成交易打印
	 */
	public static String authPaySuccessPrintText(Context context, PrintInterface printService, PreLicensingResp payResData,
                                                 UserLoginResData posPublicData, boolean isDefault, final String printRemarks, final int index){
		getPrinterStatus(context,printService);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型："+getAuthPayTypeStr(payResData.getPay_type(),payResData.getAuth_type()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(getAuthPayTypeStr(payResData.getPay_type(),payResData.getAuth_type()), 1, 3));
			}
			//1:预授权，2：预授权撤销，3：预授权完成
			if("1".equals(payResData.getAuth_type())){
				jsonArray.put(PrintUtils.setStringContent("授 权 号：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(payResData.getCustom_auth_code(), 1, 2));
				jsonArray.put(PrintUtils.setStringContent("第三方流水号：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(payResData.getOut_third_no(), 1, 2));

			}else if("2".equals(payResData.getAuth_type())){
                jsonArray.put(PrintUtils.setStringContent("第三方流水号：", 1, 2));
                jsonArray.put(PrintUtils.setStringContent(payResData.getOut_third_no(), 1, 2));
            }else if("3".equals(payResData.getAuth_type())){
                jsonArray.put(PrintUtils.setStringContent("第三方流水号：", 1, 2));
                jsonArray.put(PrintUtils.setStringContent(payResData.getOut_third_no(), 1, 2));
            }
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+getDateTimeFormatStr(payResData.getEnd_time()), 1, 2));

			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+DecimalUtil.branchToElement(payResData.getTotal_fee()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+DecimalUtil.branchToElement(payResData.getTotal_fee()), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			if(index == 1){
				jsonArray.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
				jsonArray.put(PrintUtils.setStringContent("交易金额不足300.00元，无需签名", 2, 2));
				jsonArray.put(PrintUtils.setStringContent("本人确认以上交易，同意将其记入本卡账户", 1, 2));
			}
            //1:预授权，2：预授权撤销，3：预授权完成
            if("1".equals(payResData.getAuth_type())){
                jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
				jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
				jsonArray.put(PrintUtils.setTwoDimension(payResData.getCustom_auth_code(),2,3));
				jsonArray.put(PrintUtils.setStringContent("预授权完成/撤销请使用POS机扫描二维码!", 1, 2));
            }
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				if(index == 1){
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 2));
					}else{
						jsonArray.put(PrintUtils.setStringContent("商户联", 1, 2));
					}

				}else{
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 2));
					}else{
						jsonArray.put(PrintUtils.setStringContent("客户联", 1, 2));
					}
				}
			}else{
				if(index == 1){
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 3));
					}else{
						jsonArray.put(PrintUtils.setStringContent("商户联", 1, 3));
					}

				}else{
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 3));
					}else{
						jsonArray.put(PrintUtils.setStringContent("客户联", 1, 3));
					}
				}
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();

		}catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

    /** 
     *  银行卡消费打印（重打印）
	 *  option:11银行卡消费 1预授权 2预授权撤销 3预授权完成 4预授权完成撤销
     */
    public static void cardPaySuccessPrintText(Context context, PrintInterface printService, CardPaymentDate cardPayResData,
                                               UserLoginResData posPublicData, int option, boolean isDefault, final int index){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("商户编号："+posPublicData.getMercId_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("卡    号："+cardPayResData.getPriaccount()+" /C", 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("卡号：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(cardPayResData.getPriaccount()+" /C", 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("发 卡 行："+cardPayResData.getIisno(), 1, 2));
			if(option == 11){
				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型：消费/SALE", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent("消费/SALE", 1, 3));
				}
			}else if(option == 1){
				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型：预授权/AUTH", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent("预授权/AUTH", 1, 3));
				}
			}else if(option == 2){
				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型：预授权撤销/CANCEL", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent("预授权撤销/CANCEL", 1, 3));
				}
			}else if(option == 3){
				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型：预授权完成 (请求) /AUTH COMPLETE", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent("预授权完成 (请求) /AUTH COMPLETE", 1, 3));
				}
			}else if(option == 4){
				if(isDefault){
					jsonArray.put(PrintUtils.setStringContent("交易类型：预授权完成撤销/COMPLETE VOID", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
					jsonArray.put(PrintUtils.setStringContent("预授权完成撤销/COMPLETE VOID", 1, 3));
				}
			}
			jsonArray.put(PrintUtils.setStringContent("凭 证 号："+cardPayResData.getSystraceno(), 1, 2));
			if(option == 1||option == 2||option == 3||option == 4){
				jsonArray.put(PrintUtils.setStringContent("授 权 码："+cardPayResData.getAuthcode(), 1, 2));
				jsonArray.put(PrintUtils.setStringContent("参 考 号："+cardPayResData.getRefernumber(), 1, 2));
			}
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+DateTimeUtil.timeStrToDateStr(cardPayResData.getTranslocaldate()+cardPayResData.getTranslocaltime()), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+cardPayResData.getTransamount(), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+cardPayResData.getTransamount(), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			if(index == 1){
				jsonArray.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
				jsonArray.put(PrintUtils.setStringContent("交易金额不足300.00元，无需签名", 2, 2));
				jsonArray.put(PrintUtils.setStringContent("本人确认以上交易，同意将其记入本卡账户", 1, 2));
			}

			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				if(index == 1){
					jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 2));
				}

			}else{
				if(index == 1){
					jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 3));
				}else{
					jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 3));
				}
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    /** 
	 *  银行卡退款打印（重打印）
	 */
	public static void cardRefundSuccessPrintText(Context context, PrintInterface printService, CardPaymentDate cardPayResData,
                                                  UserLoginResData posPublicData, boolean isDefault, final int index){
		getPrinterStatus(context,printService);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("商户编号："+posPublicData.getMercId_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("卡    号："+cardPayResData.getPriaccount()+" /C", 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("卡号：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(cardPayResData.getPriaccount()+" /C", 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("发 卡 行："+cardPayResData.getIisno(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型：消费撤销 /V01D", 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("消费撤销 /V01D", 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("凭 证 号："+cardPayResData.getSystraceno(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+DateTimeUtil.timeStrToDateStr(cardPayResData.getTranslocaldate()+cardPayResData.getTranslocaltime()), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+cardPayResData.getTransamount(), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+cardPayResData.getTransamount(), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			if(index == 1){
				jsonArray.put(PrintUtils.setStringContent("持卡人签名：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
				jsonArray.put(PrintUtils.setStringContent("交易金额不足300.00元，无需签名", 2, 2));
				jsonArray.put(PrintUtils.setStringContent("本人确认以上交易，同意将其记入本卡账户", 1, 2));
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				if(index == 1){
					jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 2));
				}else{
					jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 2));
				}
			}else{
				if(index == 1){
					jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 3));
				}else{
					jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 3));
				}
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} 

    /** 
     *  API二维码退款成功打印（包括重打印）
     */
    public static void refundSuccessPrintText(Context context, PrintInterface printService, PosRefundResData refundResData,
                                              UserLoginResData posPublicData, boolean isDefault, final String printRemarks, final int index){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型："+getRefundPayTypeStr(refundResData.getPay_type()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(getRefundPayTypeStr(refundResData.getPay_type()), 1, 3));
			}


			String outRefundNoStr = refundResData.getOut_refund_no();
			String outRefundNo = "";
			String outRefundNoNoSuffix = "";
			if(Utils.isNotEmpty(outRefundNoStr)&&outRefundNoStr.length()>=32){
				outRefundNo = outRefundNoStr.substring(0,24);
				outRefundNoNoSuffix = outRefundNoStr.substring(24);
				jsonArray.put(PrintUtils.setStringContent("订单号："+outRefundNo, 1, 2));
				jsonArray.put(PrintUtils.setStringContent("        "+outRefundNoNoSuffix, 1, 2));

			}else{
				outRefundNo = outRefundNoStr;
				jsonArray.put(PrintUtils.setStringContent("订单号："+outRefundNo, 1, 2));
			}
			jsonArray.put(PrintUtils.setStringContent("原订单号："+refundResData.getOut_trade_no(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+getDateTimeFormatStr(refundResData.getEnd_time()), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+DecimalUtil.branchToElement(refundResData.getRefund_fee()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+DecimalUtil.branchToElement(refundResData.getRefund_fee()), 1, 3));
			}

			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));

			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				if(index == 1){
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 2));
					}else{
						jsonArray.put(PrintUtils.setStringContent("商户联", 1, 2));
					}

				}else{
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 2));
					}else{
						jsonArray.put(PrintUtils.setStringContent("客户联", 1, 2));
					}

				}

			}else{
				if(index == 1){

					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("商户联 -（重打印）", 1, 3));
					}else{
						jsonArray.put(PrintUtils.setStringContent("商户联", 1, 3));
					}
				}else{
					if(printRemarks.equals(rePrintRemarks)){
						jsonArray.put(PrintUtils.setStringContent("客户联 -（重打印）", 1, 3));
					}else{
						jsonArray.put(PrintUtils.setStringContent("客户联", 1, 3));
					}

				}
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    /** 
     *  查询成功打印
     */
    public static void querySuccessPrintText(Context context, PrintInterface printService, PosPayQueryResData queryResData,
                                             UserLoginResData posPublicData, boolean isDefault, final int index){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));

			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型："+getQueryPayTypeStr(queryResData.getPay_type()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(getQueryPayTypeStr(queryResData.getPay_type()), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("订 单 号："+queryResData.getOut_trade_no(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+getDateTimeFormatStr(queryResData.getEnd_time()), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+DecimalUtil.branchToElement(queryResData.getTotal_fee()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+DecimalUtil.branchToElement(queryResData.getTotal_fee()), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));

			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			if(isDefault){
				if(index == 1){

					jsonArray.put(PrintUtils.setStringContent("商户联 -（扫码查单）", 1, 2));


				}else{

					jsonArray.put(PrintUtils.setStringContent("客户联 -（扫码查单）", 1, 2));


				}

			}else{
				if(index == 1){

					jsonArray.put(PrintUtils.setStringContent("商户联 -（扫码查单）", 1, 3));

				}else{

					jsonArray.put(PrintUtils.setStringContent("客户联 -（扫码查单）", 1, 3));


				}
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    
    /** 
     *  订单详情打印
     */
    public static void orderDetailsPrintText(Context context, PrintInterface printService, OrderDetailData order,
                                             UserLoginResData posPublicData, boolean isDefault, final int index){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));

			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("交易类型："+getOrderTypeStr(order.getPayWay(),order.getOrderType()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交易类型：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent(getOrderTypeStr(order.getPayWay(),order.getOrderType()), 1, 3));
			}
			String orderIdStr = order.getOrderId();
			String orderId = "";
			String orderIdSuffix = "";
			if(Utils.isNotEmpty(orderIdStr)&&orderIdStr.length()>=32){
				orderId = orderIdStr.substring(0,24);
				orderIdSuffix = orderIdStr.substring(24);
				jsonArray.put(PrintUtils.setStringContent("订单号："+orderId, 1, 2));
				jsonArray.put(PrintUtils.setStringContent("        "+orderIdSuffix, 1, 2));

			}else{
				orderId = orderIdStr;
				jsonArray.put(PrintUtils.setStringContent("订单号："+orderId, 1, 2));
			}
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+DateTimeUtil.stampToDate(Long.parseLong(order.getPayTime())), 1, 2));
			if(isDefault){
				jsonArray.put(PrintUtils.setStringContent("金额：RMB "+DecimalUtil.StringToPrice(order.getGoodsPrice()), 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("金额：", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("RMB "+DecimalUtil.StringToPrice(order.getGoodsPrice()), 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));

			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
            if(isDefault){
                if(index == 1){
                    jsonArray.put(PrintUtils.setStringContent("商户联 -（明细补打）", 1, 2));
                }else{
                    jsonArray.put(PrintUtils.setStringContent("客户联 -（明细补打）", 1, 2));
                }

            }else{
                if(index == 1){
                    jsonArray.put(PrintUtils.setStringContent("商户联 -（明细补打）", 1, 3));
                }else{
                    jsonArray.put(PrintUtils.setStringContent("客户联 -（明细补打）", 1, 3));
                }
            }
            jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    

    /** 
     *  结算打印
     */
    public static void SettlementPrintText(Context context, PrintInterface printService, ShiftResData summary, UserLoginResData posPublicData, String staffName){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS收款凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("商户编号："+posPublicData.getMercId_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			//结算时间
			ArrayList<SubTimeSummaryResData> timeList = summary.getTimeList();
			SubTimeSummaryResData subStartTime = null;
			SubTimeSummaryResData subEndTime = null;
			for (int i = 0; i < timeList.size(); i++) {
				subStartTime = timeList.get(0);
				subEndTime = timeList.get(1);
			}
			jsonArray.put(PrintUtils.setStringContent("开始时间："+subStartTime.getType(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("结束时间："+subEndTime.getType(), 1, 2));
			if(Utils.isNotEmpty(staffName)){
				jsonArray.put(PrintUtils.setStringContent("交 班 人："+staffName, 1, 2));
			}else{
				jsonArray.put(PrintUtils.setStringContent("交 班 人："+"", 1, 2));
			}
			jsonArray.put(PrintUtils.setStringContent("类型/TYPE  笔数/SUM  金额/AMOUNT", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));
			//交易明细
			ArrayList<SubReocrdSummaryResData> reocrdList = summary.getReocrdList();
			for (int i = 0; i < reocrdList.size(); i++) {
				SubReocrdSummaryResData reocrd = reocrdList.get(i);
				String mode = reocrd.getMode();
				if("WX".equals(mode)){
					String type = reocrd.getType();
					if("noRefund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("微信"+nineSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}else if("refund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("微信退款"+fiveSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}
				}else if("ALI".equals(mode)){
					String type = reocrd.getType();
					if("noRefund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("支付宝"+sevenSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}else if("refund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("支付宝退款"+threeSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}
				}else if("BEST".equals(mode)){
					String type = reocrd.getType();
					if("noRefund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("翼支付"+sevenSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}else if("refund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("翼支付退款"+threeSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}
				}else if("BANK".equals(mode)){
					String type = reocrd.getType();
					if("noRefund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("银行卡消费"+threeSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}else if("refund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("银行卡退款"+threeSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}
				}else if("UNIONPAY".equals(mode)){
					String type = reocrd.getType();
					if("noRefund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("银联二维码"+threeSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}else if("refund".equals(type)){
						jsonArray.put(PrintUtils.setStringContent("银联二维码退款"+twoSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), 1, 2));
					}
				}
			}
			//总计
			ArrayList<SubTotalSummaryResData> totalList = summary.getTotalList();
			SubTotalSummaryResData total = null;
			for (int i = 0; i < totalList.size(); i++) {
				total = totalList.get(i);
			}
			jsonArray.put(PrintUtils.setStringContent("总计"+nineSpaceStr+total.getTotalCount()+nineSpaceStr+total.getMoney(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    
    /** 
     *  交易汇总打印
     */
    public static void SummaryPrintText(Context context, PrintInterface printService, SummaryResData summary, UserLoginResData posPublicData, String dateStr){
		getPrinterStatus(context,printService);
    	JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS交易汇总", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("商户编号："+posPublicData.getMercId_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("日期/时间："+dateStr, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("类型/TYPE  笔数/SUM  金额/AMOUNT", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));
			//交易明细
			List<PayTypeBean> lsPayType = new ArrayList<PayTypeBean>();
			lsPayType = summary.getOrderSumList();
			//银行卡总金额  = 贷记卡总金额 + 借记卡总金额
			double sumMoney = 0;
			//银行卡总笔数 = 贷记卡总笔数 + 借记卡总笔数
			int sumNum = 0;
			//标示是否有银行卡记录
			boolean isBank = false;
			for (int i = 0; i < lsPayType.size(); i++) {
				PayTypeBean payType = lsPayType.get(i);
				String mode = payType.getPayWay();
				if("WX".equals(mode)){
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();
					jsonArray.put(PrintUtils.setStringContent("微信支付"+fiveSpaceStr+String.valueOf(total_int)+nineSpaceStr+String.valueOf(amount_dou), 1, 2));

				}else if("ALI".equals(mode)){
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();
					jsonArray.put(PrintUtils.setStringContent("支付宝支付"+threeSpaceStr+String.valueOf(total_int)+nineSpaceStr+String.valueOf(amount_dou), 1, 2));

				}else if("BEST".equals(mode)){
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();
					jsonArray.put(PrintUtils.setStringContent("翼支付支付"+threeSpaceStr+String.valueOf(total_int)+nineSpaceStr+String.valueOf(amount_dou), 1, 2));

				}else if("UNIONPAY".equals(mode)){
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();

					double amount_dou = amount.doubleValue();
					int total_int = total.intValue();
					jsonArray.put(PrintUtils.setStringContent("银联二维码"+threeSpaceStr+String.valueOf(total_int)+nineSpaceStr+String.valueOf(amount_dou), 1, 2));

				}else if("DEBIT".equals(mode) || "CREDIT".equals(mode)){
					Double amount = payType.getAmount();
					Integer total = payType.getTotal();



					double amount_dou = amount.doubleValue();
					sumMoney = sumMoney + amount_dou;
					int total_int = total.intValue();
					sumNum = sumNum + total_int;

					isBank = true;


				}
			}
			if(isBank){
				jsonArray.put(PrintUtils.setStringContent("银行卡消费"+threeSpaceStr+String.valueOf(sumNum)+nineSpaceStr+String.valueOf(sumMoney), 1, 2));
			}
			//总笔数
			Integer sumTotal = summary.getSumTotal();
			int sumTotal_int = sumTotal.intValue();
			//总金额
			Double sumAmt = summary.getSumAmt();
			double sumAmt_dou = sumAmt.doubleValue();
			jsonArray.put(PrintUtils.setStringContent("总计"+nineSpaceStr+String.valueOf(sumTotal_int)+nineSpaceStr+String.valueOf(sumAmt_dou), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    } 
    
    /** 
     *  商户信息打印(pos签到成功后)
     */
    public static void businessInfoPrintText(Context context, PrintInterface printService, UserLoginResData posPublicData){
		getPrinterStatus(context,printService);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS商户信息", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("商 户 号："+posPublicData.getMerchant_no(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("款台名称："+posPublicData.getEname(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("设 备 号："+posPublicData.getTerminal_id(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("POS商户号："+posPublicData.getMercId_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("POS设备号："+posPublicData.getTrmNo_pos(), 1, 2));

			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


	/**
	 *  核销成功打印小票
	 */
	public static void checkCardStockPrintText(Context context, PrintInterface printService, CheckCardStockResData checkCardStockResData, UserLoginResData posPublicData, String isMakeUp){
		getPrinterStatus(context,printService);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS卡劵核销凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("卡劵名称："+checkCardStockResData.getTitle(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("核销劵码："+checkCardStockResData.getCode(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("核销状态：使用成功", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("使用时间："+DateTimeUtil.stampToDate(Long.parseLong(checkCardStockResData.getCreateTime())), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("POS设备号："+posPublicData.getTrmNo_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			if("C".equals(isMakeUp)){
				jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
				jsonArray.put(PrintUtils.setStringContent("重打印", 1, 3));
			}
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  核销记录详情打印小票
	 */
	public static void checkCardStockDetailPrintText(Context context, PrintInterface printService, CheckCardStockRecodeDetailResData cardStockRecode, UserLoginResData posPublicData){
		getPrinterStatus(context,printService);
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			jsonArray.put(PrintUtils.setStringContent("POS卡劵核销凭证", 2, 3));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("卡劵名称："+cardStockRecode.getTitle(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("核销劵码："+cardStockRecode.getCode(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("核销状态：使用成功", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("使用时间："+DateTimeUtil.stampToDate(cardStockRecode.getUse_time()), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("POS设备号："+posPublicData.getTrmNo_pos(), 1, 2));
			jsonArray.put(PrintUtils.setStringContent("备注：", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线

			jsonArray.put(PrintUtils.setStringContent("明细补打", 1, 3));

			jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
			jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
			jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
			jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
			jsonArray.put(PrintUtils.setfreeLine("5"));
			jsonObject.put("spos", jsonArray);
			String jsonStr = jsonObject.toString();
			printService.print(jsonStr);
		} catch (JSONException e) {
			e.printStackTrace();

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *  批量制劵---->> 小票
	 */
	public static String batchSecurPrintText(Context context,List<String> list,UserLoginResData posPublicData,CardStockTypeResData cardStockTypeResData){

		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		//打印文字
		try {
			for (int i = 0;i<list.size();i++){


				jsonArray.put(PrintUtils.setStringContent("微信扫一扫领优惠", 2, 3));
				jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
				jsonArray.put(PrintUtils.setStringContent("商户名称："+posPublicData.getMername_pos(), 1, 2));
				jsonArray.put(PrintUtils.setStringContent("终端编号："+posPublicData.getTrmNo_pos(), 1, 2));
				jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线
				jsonArray.put(PrintUtils.setStringContent(cardStockTypeResData.getTitle(), 2, 3));//劵名称
//				jsonArray.put(PrintUtils.setStringContent(list.get(i), 2, 3));//二维码内容
				jsonArray.put(PrintUtils.setTwoDimension(list.get(i),2,3));//二维码内容

				jsonArray.put(PrintUtils.setStringContent("-------------------------------", 1, 2));// 打印虚线

				jsonArray.put(PrintUtils.setStringContent("打印时间："+DateTimeUtil.stampToDate(System.currentTimeMillis()), 1, 2));
				jsonArray.put(PrintUtils.setStringContent(tel, 1, 2));
				jsonArray.put(PrintUtils.setStringContent("----x---------------------x----", 1, 2));
				jsonArray.put(PrintUtils.setStringContent("          ", 2, 1));
				jsonArray.put(PrintUtils.setfreeLine("5"));
				jsonObject.put("spos", jsonArray);
			}
			return jsonObject.toString();

		} catch (JSONException e) {
			e.printStackTrace();

		}catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}



    private int getPrintStatus(PrintInterface printService){
	    int status = 0;
	    if(printService != null){
            try {
                status = printService.getPrinterState();
            }catch (RemoteException e){
                e.printStackTrace();
                status = 1;
            }
        }else{
            status = 1;
        }

	    return status;
    }



	private static void getPrinterStatus(Context context, PrintInterface printService){
		if(printService!=null){
			try {
				int status = printService.getPrinterState();
				Log.e("打印机状态：",status+"");
				if(status != ERROR_NONE){
					if(status == ERROR_PAPERENDED){
						Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
						return;
					}else if(status == ERROR_HARDERR){
						//ERROR_HARDERR = 242;     //硬件错误
						Toast.makeText(context, "设备硬件错误！", Toast.LENGTH_LONG).show();
						return;
					}else if(status == ERROR_LOWVOL){
						//ERROR_LOWVOL = 225;     //低压保护
						Toast.makeText(context, "电量太低，打印机低压保护，打印取消！", Toast.LENGTH_LONG).show();
						return;
					}else if(status == ERROR_OVERHEAT){
						//ERROR_LOWVOL = 225;     //打印头过热
						Toast.makeText(context, "打印头过热，打印取消！", Toast.LENGTH_LONG).show();
						return;
					}else {
						Toast.makeText(context, status+"", Toast.LENGTH_LONG).show();
						return;
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
				Toast.makeText(context, "打印设备状态异常！", Toast.LENGTH_LONG).show();
				return;
			}
		}else {
			Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
			return;
		}
	}

}
