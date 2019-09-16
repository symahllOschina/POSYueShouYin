package com.wanding.xingpos.printutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.nld.cloudpos.aidl.printer.AidlPrinter;
import com.nld.cloudpos.aidl.printer.AidlPrinterListener;
import com.nld.cloudpos.aidl.printer.PrintItemObj;
import com.nld.cloudpos.data.PrinterConstant;
import com.nld.cloudpos.data.PrinterConstant.PrinterState;
import com.wanding.xingpos.R;
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
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 星POS打印机操作类
 */
public class NewlandPrintUtil {

    /** 打印第二联间隔时间 */
    public static final long time = 2200;


    /**
     * 统一空格字符串
     */
    private static final String twoSpaceStr = "  ";
    private static final String threeSpaceStr = "   ";
    private static final String fiveSpaceStr = "     ";
    private static final String sixSpaceStr = "      ";
    private static final String sevenSpaceStr = "       ";
    private static final String eightSpaceStr = "        ";
    private static final String nineSpaceStr = "         ";

    private static String TAG = "lyc";

    //刷卡，微信，支付宝，银联，翼支付，银联分别顺序对应：040,010,020,030,050,060
    private static String getPayTypeStr(String payType) {
        if ("040".equals(payType)) {
            return "刷卡支付/BANK PAY";
        } else if ("010".equals(payType)) {
            return "微信支付/WEIXIN PAY";
        } else if ("020".equals(payType)) {
            return "支付宝支付/ALI PAY";
        } else if ("030".equals(payType)) {
            return "银联二维码/UNIONPAY PAY";
        } else if ("050".equals(payType)) {
            return "翼支付/BEST PAY";
        } else if ("060".equals(payType)) {
            return "银联二维码支付/UNIONPAY PAY";
        }
        return "";
    }

    private static String getQueryPayTypeStr(String payType) {
        if ("WX".equals(payType)) {
            return "微信支付/WEIXIN PAY";
        } else if ("ALI".equals(payType)) {
            return "支付宝支付/ALI PAY";
        } else if ("BEST".equals(payType)) {
            return "翼支付/BEST PAY";
        } else if ("UNIONPAY".equals(payType)) {
            return "银联二维码支付/UNIONPAY PAY";
        } else if ("DEBIT".equals(payType) || "CREDIT".equals(payType)) {
            //DEBIT= 借记卡       CREDIT=贷记卡
            return "银行卡支付/BANK PAY";
        } else if ("BANK".equals(payType)) {
            // BANK = 银行卡
            return "银行卡支付/BANK PAY";
        }

        return "";
    }

    private static String getOrderTypeStr(String payType, String orderTypeStr) {
        //先判断是支付交易还是退款交易 0正向 ,1退款
        if ("0".equals(orderTypeStr)) {
            if ("WX".equals(payType)) {
                return "微信支付/WEIXIN PAY";
            } else if ("ALI".equals(payType)) {
                return "支付宝支付/ALI PAY";
            } else if ("BEST".equals(payType)) {
                return "翼支付/BEST PAY";
            } else if ("UNIONPAY".equals(payType)) {
                return "银联二维码支付/UNIONPAY PAY";
            } else if ("DEBIT".equals(payType) || "CREDIT".equals(payType)) {
                //DEBIT= 借记卡       CREDIT=贷记卡
                return "银行卡支付/BANK PAY";
            } else if ("BANK".equals(payType)) {
                // BANK = 银行卡
                return "银行卡支付/BANK PAY";
            }
        } else if ("1".equals(orderTypeStr)) {
            if ("WX".equals(payType)) {
                return "扫码退款/REFUND";
            } else if ("ALI".equals(payType)) {
                return "扫码退款/REFUND";
            } else if ("BEST".equals(payType)) {
                return "扫码退款/REFUND";
            } else if ("UNIONPAY".equals(payType)) {
                return "扫码退款/REFUND";
            } else if ("DEBIT".equals(payType) || "CREDIT".equals(payType)) {
                //DEBIT= 借记卡       CREDIT=贷记卡
                return "消费撤销";
            } else if ("BANK".equals(payType)) {
                // BANK = 银行卡
                return "消费撤销";
            }
        }


        return "";
    }

    //刷卡，微信，支付宝，银联，分别顺序对应：0,11,12,13
    private static String getPOSPayTypeStr(String payType) {
        if (payType != null && !"null".equals(payType) && !"".equals(payType)) {
            if ("11".equals(payType)) {
                return "微信支付/WEIXIN PAY";
            } else if ("12".equals(payType)) {
                return "支付宝支付/ALI PAY";
            } else if ("13".equals(payType)) {
                return "银联二维码支付/UNIONPAY PAY";
            }
        }

        return "扫码";
    }

    public static String getDateTimeFormatStr(String timeStr) {
        //20160325160000
        if (Utils.isNotEmpty(timeStr)) {
            if (timeStr.length() >= 14) {
                String year = timeStr.substring(0, 4);
                String month = timeStr.substring(4, 6);
                String day = timeStr.substring(6, 8);
                String hour = timeStr.substring(8, 10);
                String minute = timeStr.substring(10, 12);
                String second = timeStr.substring(12);
                Log.e("日期解析：", year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second);
                return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
            }
        }

        return "";
    }


    //	private static String tel = "POS客服电话  029-88445534";
    private static String tel = "悦收银客服电话  400-888-5400";


    public static final String payPrintRemarks = "succ";//正常支付成功备注
    public static final String rePrintRemarks = "cdy";//重打印备注


    /**
     * 支付成功打印(第一联)
     */
    public static void paySuccessPrintText(Context context, final AidlPrinter aidlPrinter, PosScanpayResData payResData,
                                           UserLoginResData posPublicData, boolean isDefault, final String printRemarks, final int index) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("交易类型：" + getPayTypeStr(payResData.getPay_type()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj(getPayTypeStr(payResData.getPay_type()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    String outTradeNoStr = payResData.getOut_trade_no();
                    String outTradeNo = "";
                    String outTradeNoSuffix = "";
                    if(Utils.isNotEmpty(outTradeNoStr)&&outTradeNoStr.length()>=32){
                        outTradeNo = outTradeNoStr.substring(0,24);
                        outTradeNoSuffix = outTradeNoStr.substring(24);
                        data.add(new PrintItemObj("订单号：" + outTradeNo, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("        " + outTradeNoSuffix, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }else{
                        outTradeNo = outTradeNoStr;
                        data.add(new PrintItemObj("订单号：" + outTradeNo, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    }

                    data.add(new PrintItemObj("日期/时间：" + getDateTimeFormatStr(payResData.getEnd_time()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + DecimalUtil.branchToElement(payResData.getTotal_fee()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + DecimalUtil.branchToElement(payResData.getTotal_fee()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }

                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    if (index == 1) {
                        data.add(new PrintItemObj("持卡人签名：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("\r"));
                        data.add(new PrintItemObj("交易金额不足300.00元，无需签名", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                        data.add(new PrintItemObj("\r"));
                        data.add(new PrintItemObj("本人确认以上交易，同意将其记入本卡账户", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("----x---------------------x----"));
                    if (isDefault) {
                        if (index == 1) {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("商户联", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        } else {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("客户联", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        }

                    } else {
                        if (index == 1) {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("商户联", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        } else {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("客户联", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        }

                    }


                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 支付成功打印（第二联）
     */
    public static void paySuccessPrintTextTwo(Context context, final AidlPrinter aidlPrinter, PosScanpayResData payResData,
                                              UserLoginResData posPublicData, boolean isDefault) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("交易类型：" + getPayTypeStr(payResData.getPay_type()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj(getPayTypeStr(payResData.getPay_type()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("订 单 号：" + payResData.getOut_trade_no(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("日期/时间：" + getDateTimeFormatStr(payResData.getEnd_time()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + DecimalUtil.branchToElement(payResData.getTotal_fee()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + DecimalUtil.branchToElement(payResData.getTotal_fee()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("持卡人签名：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
                    if (isDefault) {
                        data.add(new PrintItemObj("客户联", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("客户联", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 支付成功打印(二维码交易重打印,数据源来自星POS支付或退款返回)
     */
    public static void posPayAndRefundAgainPrintText(Context context, final AidlPrinter aidlPrinter, ScanPaymentDate paymentData,
                                                     UserLoginResData posPublicData, String orderType, boolean isDefault, final int index) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if ("pay".equals(orderType)) {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：" + getPOSPayTypeStr(paymentData.getPay_tp()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj(getPOSPayTypeStr(paymentData.getPay_tp()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    } else {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：扫码退款/REFUND", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj("扫码退款/REFUND", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("订 单 号：" + paymentData.getOrderid_scan(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("日期/时间：" + DateTimeUtil.timeStrToDateStr(paymentData.getTranslocaldate() + paymentData.getTranslocaltime()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + paymentData.getTransamount(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + paymentData.getTransamount(), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }


                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("-------------------------------"));
                    if (isDefault) {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        }

                    } else {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }

                    }
                    data.add(new PrintItemObj("----x---------------------x----"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 银行卡消费打印（重打印）
     * option:11银行卡消费 1预授权 2预授权撤销 3预授权完成 4预授权完成撤销
     */
    public static void cardPaySuccessPrintText(Context context, final AidlPrinter aidlPrinter, CardPaymentDate cardPayResData, UserLoginResData posPublicData, int option, boolean isDefault, final int index) {

        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("商户编号：" + posPublicData.getMercId_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("卡    号：" + cardPayResData.getPriaccount() + " /C", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("卡号：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj(cardPayResData.getPriaccount() + " /C", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }

                    data.add(new PrintItemObj("收 单 行：" + cardPayResData.getAcqno() + " 发卡行：" + cardPayResData.getIisno(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (option == 11) {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：消费/SALE", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj("消费/SALE", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    } else if (option == 1) {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：预授权/AUTH", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj("预授权/AUTH", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    } else if (option == 2) {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：预授权撤销/CANCEL", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj("预授权撤销/CANCEL", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    } else if (option == 3) {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：预授权完成 (请求) /AUTH COMPLETE", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj("预授权完成 (请求) /AUTH COMPLETE", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    } else if (option == 4) {
                        if (isDefault) {
                            data.add(new PrintItemObj("交易类型：预授权完成撤销/COMPLETE VOID", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            data.add(new PrintItemObj("预授权完成撤销/COMPLETE VOID", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }

                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos() + " 凭证号：" + cardPayResData.getSystraceno(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (option == 1 || option == 2 || option == 3 || option == 4) {
                        data.add(new PrintItemObj("授 权 码：" + cardPayResData.getAuthcode(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("参 考 号：" + cardPayResData.getRefernumber(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("日期/时间：" + DateTimeUtil.timeStrToDateStr(cardPayResData.getTranslocaldate() + cardPayResData.getTranslocaltime()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + cardPayResData.getTransamount(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + cardPayResData.getTransamount(), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    if (index == 1) {
                        data.add(new PrintItemObj("持卡人签名：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("\r"));
                        data.add(new PrintItemObj("交易金额不足300.00元，无需签名", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                        data.add(new PrintItemObj("\r"));
                        data.add(new PrintItemObj("本人确认以上交易，同意将其记入本卡账户", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("----x---------------------x----"));
                    if (isDefault) {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        }

                    } else {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    }
                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 银行卡退款打印（重打印）
     */
    public static void cardRefundSuccessPrintText(Context context, final AidlPrinter aidlPrinter, CardPaymentDate cardPayResData, UserLoginResData posPublicData, boolean isDefault, final int index) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//				PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("商户编号：" + posPublicData.getMercId_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("卡    号：" + cardPayResData.getPriaccount() + " /C", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("卡号：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj(cardPayResData.getPriaccount() + " /C", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("收 单 行：" + cardPayResData.getAcqno() + " 发卡行：" + cardPayResData.getIisno(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("交易类型：消费撤销 /V01D", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("消费撤销 /V01D", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos() + " 凭证号：" + cardPayResData.getSystraceno(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("日期/时间：" + DateTimeUtil.timeStrToDateStr(cardPayResData.getTranslocaldate() + cardPayResData.getTranslocaltime()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + cardPayResData.getTransamount(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + cardPayResData.getTransamount(), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (index == 1) {
                        data.add(new PrintItemObj("持卡人签名：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("\r"));
                        data.add(new PrintItemObj("交易金额不足300.00元，无需签名", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                        data.add(new PrintItemObj("\r"));
                        data.add(new PrintItemObj("本人确认以上交易，同意将其记入本卡账户", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("-------------------------------"));
                    if (isDefault) {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        }

                    } else {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    }

                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//                data.add(new PrintItemObj("\r"));
//                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//	                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//	                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 退款成功打印
     */
    public static void refundSuccessPrintText(Context context, final AidlPrinter aidlPrinter, PosRefundResData refundResData,
                                              UserLoginResData posPublicData, boolean isDefault, final String printRemarks, final int index) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("交易类型：扫码退款/REFUND", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("扫码退款/REFUND", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    String outRefundNoStr = refundResData.getOut_refund_no();
                    String outRefundNo = "";
                    String outRefundNoNoSuffix = "";
                    if(Utils.isNotEmpty(outRefundNoStr)&&outRefundNoStr.length()>=32){
                        outRefundNo = outRefundNoStr.substring(0,24);
                        outRefundNoNoSuffix = outRefundNoStr.substring(24);
                        data.add(new PrintItemObj("订单号：" + outRefundNo, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("        " + outRefundNoNoSuffix, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    }else{
                        outRefundNo = outRefundNoStr;
                        data.add(new PrintItemObj("订单号：" + outRefundNo, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    }
                    data.add(new PrintItemObj("原订单号：" + refundResData.getOut_trade_no(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));



                     data.add(new PrintItemObj("日期/时间：" + getDateTimeFormatStr(refundResData.getEnd_time()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + DecimalUtil.branchToElement(refundResData.getRefund_fee()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + DecimalUtil.branchToElement(refundResData.getRefund_fee()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }

                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    data.add(new PrintItemObj("-------------------------------"));
                    if (isDefault) {
                        if (index == 1) {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("商户联", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        } else {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("客户联", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        }

                    } else {
                        if (index == 1) {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("商户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("商户联", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        } else {
                            if (printRemarks.equals(rePrintRemarks)) {
                                data.add(new PrintItemObj("客户联 -（重打印）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else {
                                data.add(new PrintItemObj("客户联", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                            }

                        }
                    }
                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 查询成功打印
     */
    public static void querySuccessPrintText(Context context, final AidlPrinter aidlPrinter, PosPayQueryResData queryResData, UserLoginResData posPublicData, boolean isDefault, final int index) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("交易类型：" + getQueryPayTypeStr(queryResData.getPay_type()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj(getQueryPayTypeStr(queryResData.getPay_type()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("订 单 号：" + queryResData.getOut_trade_no(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("日期/时间：" + getDateTimeFormatStr(queryResData.getEnd_time()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + DecimalUtil.branchToElement(queryResData.getTotal_fee()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + DecimalUtil.branchToElement(queryResData.getTotal_fee()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("-------------------------------"));
                    if (isDefault) {
                        if (index == 1) {

                            data.add(new PrintItemObj("商户联 - (扫码查单)", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));


                        } else {

                            data.add(new PrintItemObj("客户联 - (扫码查单)", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));


                        }

                    } else {
                        if (index == 1) {

                            data.add(new PrintItemObj("商户联 -（扫码查单）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));


                        } else {

                            data.add(new PrintItemObj("客户联 -（扫码查单）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));


                        }
                    }
                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 订单详情打印
     */
    public static void orderDetailsPrintText(Context context, final AidlPrinter aidlPrinter, OrderDetailData order,
                                             UserLoginResData posPublicData, boolean isDefault, final int index) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS收款凭证", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("交易类型：" + getOrderTypeStr(order.getPayWay(), order.getOrderType()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交易类型：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj(getOrderTypeStr(order.getPayWay(), order.getOrderType()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    String orderIdStr = order.getOrderId();
                    String orderId = "";
                    String orderIdSuffix = "";
                    if(Utils.isNotEmpty(orderIdStr)&&orderIdStr.length()>=32){
                        orderId = orderIdStr.substring(0,24);
                        orderIdSuffix = orderIdStr.substring(24);
                        data.add(new PrintItemObj("订单号：" + orderId, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("        " + orderIdSuffix, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }else{
                        orderId = orderIdStr;
                        data.add(new PrintItemObj("订单号：" + orderId, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("日期/时间：" + DateTimeUtil.stampToDate(Long.parseLong(order.getPayTime())), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (isDefault) {
                        data.add(new PrintItemObj("金额：RMB " + DecimalUtil.StringToPrice(order.getGoodsPrice()), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("金额：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        data.add(new PrintItemObj("RMB " + DecimalUtil.StringToPrice(order.getGoodsPrice()), PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("备注：", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("-------------------------------"));
                    if (isDefault) {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（明细补打）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（明细补打）", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    } else {
                        if (index == 1) {
                            data.add(new PrintItemObj("商户联 -（明细补打）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        } else {
                            data.add(new PrintItemObj("客户联 -（明细补打）", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.LEFT, false, 6));
                        }
                    }
                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();


        }
    }

//    /** 
//     *  银行卡交易重打印
//     */
//    public static void cardPayPrintText(Context context,AidlPrinter aidlPrinter){
//    	
//    } 

    /**
     * 结算打印
     */
    public static void SettlementPrintText(Context context, final AidlPrinter aidlPrinter, ShiftResData summary, UserLoginResData posPublicData, String staffName) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS结算总计单", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("商户编号：" + posPublicData.getMercId_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    //结算时间
                    ArrayList<SubTimeSummaryResData> timeList = summary.getTimeList();
                    SubTimeSummaryResData subStartTime = null;
                    SubTimeSummaryResData subEndTime = null;
                    for (int i = 0; i < timeList.size(); i++) {
                        subStartTime = timeList.get(0);
                        subEndTime = timeList.get(1);

                    }
                    data.add(new PrintItemObj("开始时间：" + subStartTime.getType(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("结束时间：" + subEndTime.getType(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    if (Utils.isNotEmpty(staffName)) {
                        data.add(new PrintItemObj("交 班 人：" + staffName, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    } else {
                        data.add(new PrintItemObj("交 班 人：" + "", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }

                    data.add(new PrintItemObj("类型/TYPE  笔数/SUM  金额/AMOUNT", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("-------------------------------"));
//                	data.add(new PrintItemObj("银行卡对账平", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
//                	data.add(new PrintItemObj("银行卡入账总计", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    //交易明细
                    ArrayList<SubReocrdSummaryResData> reocrdList = summary.getReocrdList();
//	                for (int i = 0; i < reocrdList.size(); i++) {
//	                	SubReocrdSummaryResData reocrd = reocrdList.get(i);
//	                	String mode = reocrd.getMode();
//	                	if(mode.equals("BANK")){
//	                		String type = reocrd.getType();
//	                		if(type.equals("noRefund")){
//	                			data.add(new PrintItemObj("消费"+nineSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
//	                		}else if(type.equals("refund")){
//	                			data.add(new PrintItemObj("消费撤销"+fiveSpaceStr+reocrd.getTotalCount()+nineSpaceStr+reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
//	                		}
//	                		
//	                	}
//					}
                    for (int i = 0; i < reocrdList.size(); i++) {
                        SubReocrdSummaryResData reocrd = reocrdList.get(i);
                        String mode = reocrd.getMode();
                        if ("WX".equals(mode)) {
                            String type = reocrd.getType();
                            if ("noRefund".equals(type)) {
                                data.add(new PrintItemObj("微信" + nineSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else if ("refund".equals(type)) {
                                data.add(new PrintItemObj("微信退款" + fiveSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }
                        } else if ("ALI".equals(mode)) {
                            String type = reocrd.getType();
                            if ("noRefund".equals(type)) {
                                data.add(new PrintItemObj("支付宝" + sevenSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else if ("refund".equals(type)) {
                                data.add(new PrintItemObj("支付宝退款" + threeSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }
                        } else if ("BEST".equals(mode)) {
                            String type = reocrd.getType();
                            if ("noRefund".equals(type)) {
                                data.add(new PrintItemObj("翼支付" + sevenSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else if ("refund".equals(type)) {
                                data.add(new PrintItemObj("翼支付退款" + threeSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }
                        } else if ("BANK".equals(mode)) {
                            String type = reocrd.getType();
                            if ("noRefund".equals(type)) {
                                data.add(new PrintItemObj("银行卡消费" + threeSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else if ("refund".equals(type)) {
                                data.add(new PrintItemObj("银行卡退款" + threeSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }
                        } else if ("UNIONPAY".equals(mode)) {
                            String type = reocrd.getType();
                            if ("noRefund".equals(type)) {
                                data.add(new PrintItemObj("银联二维码" + threeSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            } else if ("refund".equals(type)) {
                                data.add(new PrintItemObj("银联二维码退款" + twoSpaceStr + reocrd.getTotalCount() + nineSpaceStr + reocrd.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                            }
                        }
                    }
                    data.add(new PrintItemObj("\r"));
                    //总计
                    ArrayList<SubTotalSummaryResData> totalList = summary.getTotalList();

                    SubTotalSummaryResData total = null;
                    for (int i = 0; i < totalList.size(); i++) {
                        total = totalList.get(i);
                    }
                    data.add(new PrintItemObj("总计" + nineSpaceStr + total.getTotalCount() + nineSpaceStr + total.getMoney(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 交易汇总打印
     */
    public static void SummaryPrintText(Context context, final AidlPrinter aidlPrinter, SummaryResData summary, UserLoginResData posPublicData, String dateStr) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS交易汇总", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("商户编号：" + posPublicData.getMercId_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("终端编号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    //时间
                    data.add(new PrintItemObj("日期/时间：" + dateStr, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    data.add(new PrintItemObj("类型/TYPE  笔数/SUM  金额/AMOUNT", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("-------------------------------"));
//                	data.add(new PrintItemObj("银行卡对账平", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
//                	data.add(new PrintItemObj("银行卡入账总计", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
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
                        if ("WX".equals(mode)) {
                            Double amount = payType.getAmount();
                            Integer total = payType.getTotal();

                            double amount_dou = amount.doubleValue();
                            int total_int = total.intValue();

                            data.add(new PrintItemObj("微信支付" + fiveSpaceStr + String.valueOf(total_int) + nineSpaceStr + String.valueOf(amount_dou), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                        } else if ("ALI".equals(mode)) {
                            Double amount = payType.getAmount();
                            Integer total = payType.getTotal();

                            double amount_dou = amount.doubleValue();
                            int total_int = total.intValue();
                            data.add(new PrintItemObj("支付宝支付" + threeSpaceStr + String.valueOf(total_int) + nineSpaceStr + String.valueOf(amount_dou), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                        } else if ("BEST".equals(mode)) {
                            Double amount = payType.getAmount();
                            Integer total = payType.getTotal();

                            double amount_dou = amount.doubleValue();
                            int total_int = total.intValue();
                            data.add(new PrintItemObj("翼支付支付" + threeSpaceStr + String.valueOf(total_int) + nineSpaceStr + String.valueOf(amount_dou), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                        } else if ("UNIONPAY".equals(mode)) {
                            Double amount = payType.getAmount();
                            Integer total = payType.getTotal();

                            double amount_dou = amount.doubleValue();
                            int total_int = total.intValue();

                            data.add(new PrintItemObj("银联二维码" + threeSpaceStr + String.valueOf(total_int) + nineSpaceStr + String.valueOf(amount_dou), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                        } else if ("DEBIT".equals(mode) || "CREDIT".equals(mode)) {
                            Double amount = payType.getAmount();
                            Integer total = payType.getTotal();


                            double amount_dou = amount.doubleValue();
                            sumMoney = sumMoney + amount_dou;
                            int total_int = total.intValue();
                            sumNum = sumNum + total_int;

                            isBank = true;


                        }
                    }
                    if (isBank) {
                        data.add(new PrintItemObj("银行卡消费" + threeSpaceStr + String.valueOf(sumNum) + nineSpaceStr + String.valueOf(sumMoney), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    }
                    data.add(new PrintItemObj("\r"));
                    //总笔数
                    Integer sumTotal = summary.getSumTotal();
                    int sumTotal_int = sumTotal.intValue();
                    //总金额
                    Double sumAmt = summary.getSumAmt();
                    double sumAmt_dou = sumAmt.doubleValue();
                    data.add(new PrintItemObj("总计" + nineSpaceStr + String.valueOf(sumTotal_int) + nineSpaceStr + String.valueOf(sumAmt_dou), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 商户信息打印(pos签到成功后)
     */
    public static void businessInfoPrintText(Context context, final AidlPrinter aidlPrinter, UserLoginResData posPublicData) {
        if (aidlPrinter != null) {
            //先判断打印机状态
            try {
                int printState = aidlPrinter.getPrinterState();
                if (printState == PrinterState.PRINTER_STATE_NORMAL) {
                    Log.e("打印机状态", "正常");
                    /** 正常 */
//					PrintUtil.printText();//打印格式等实例代码
                    //文本内容
                    final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                    //标题（星POS收款凭证）
                    data.add(new PrintItemObj("POS商户信息", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("-------------------------------"));
//	                data.add(new PrintItemObj("\r"));
                    data.add(new PrintItemObj("商户名称：" + posPublicData.getMername_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("商 户 号：" + posPublicData.getMerchant_no(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("款台名称：" + posPublicData.getEname(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("设 备 号：" + posPublicData.getTerminal_id(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("POS商户号：" + posPublicData.getMercId_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("POS设备号：" + posPublicData.getTrmNo_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("批 次 号：" + posPublicData.getBatchno_pos(), PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));

                    data.add(new PrintItemObj("-------------------------------"));
                    data.add(new PrintItemObj(tel, PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.LEFT, false, 6));
                    data.add(new PrintItemObj("----x---------------------x----"));
//	                data.add(new PrintItemObj("\r"));
//	                data.add(new PrintItemObj("\r"));

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (aidlPrinter != null) {
                                try {
                                    boolean flag = aidlPrinter.open();
                                    //打印文本
                                    aidlPrinter.printText(data);

                                    aidlPrinter.start(new AidlPrinterListener.Stub() {

                                        @Override
                                        public void onPrintFinish() throws RemoteException {
                                            Log.e(TAG, "打印结束");
                                            /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                            aidlPrinter.paperSkip(2);
//		                                        showMsgOnTextView("打印结束");
                                        }

                                        @Override
                                        public void onError(int errorCode) throws RemoteException {
                                            Log.e(TAG, "打印异常");
//		                                        showMsgOnTextView( "打印异常码:" + errorCode);
                                        }
                                    });

                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                    aidlPrinter.printText(data);

                } else if (printState == PrinterState.PRINTER_STATE_NOPAPER) {
                    /** 缺纸 */
                    Toast.makeText(context, "请放入足够纸卷！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_NOT_OPEN) {
                    /** 设备未打开*/
                    Toast.makeText(context, "设备未打开！", Toast.LENGTH_LONG).show();
                } else if (printState == PrinterState.PRINTER_STATE_DEV_ERROR) {
                    /** 设备通讯异常*/
                    Toast.makeText(context, "设备通讯异常！", Toast.LENGTH_LONG).show();
                }
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "找不到打印设备！", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * 打印文本
     *
     * @throws RemoteException
     */
    public static void printText(final Context context, final AidlPrinter aidlPrinter) {

        try {

            if (null != aidlPrinter) {
                //文本内容
                final List<PrintItemObj> data = new ArrayList<PrintItemObj>();
                data.add(new PrintItemObj("商户号", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("交易时间", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体3", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体4", PrinterConstant.FontScale.FONTSCALE_DW_H, PrinterConstant.FontType.FONTTYPE_N, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体5", PrinterConstant.FontScale.FONTSCALE_W_H, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体6", PrinterConstant.FontScale.FONTSCALE_DW_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体7", PrinterConstant.FontScale.FONTSCALE_W_DH, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体8", PrinterConstant.FontScale.FONTSCALE_DW_H, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, false, 6));
                data.add(new PrintItemObj("文本打印测试Test 字号5  非粗体9", PrinterConstant.FontScale.FONTSCALE_DW_H, PrinterConstant.FontType.FONTTYPE_S, PrintItemObj.ALIGN.CENTER, true, 6));
                data.add(new PrintItemObj("\r"));
                data.add(new PrintItemObj("\r"));
                data.add(new PrintItemObj("-------------------------------"));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (aidlPrinter != null) {
                            try {
                                boolean flag = aidlPrinter.open();
                                //打印文本
                                aidlPrinter.printText(data);
                                //打印图片
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                                aidlPrinter.printImage(PrinterConstant.Align.ALIGN_CENTER, bitmap);
                                aidlPrinter.printQrCode(PrinterConstant.Align.ALIGN_RIGHT, 100, "12345");
                                aidlPrinter.printBarCode(PrinterConstant.Align.ALIGN_CENTER, 4, 64, "12345");

                                aidlPrinter.start(new AidlPrinterListener.Stub() {

                                    @Override
                                    public void onPrintFinish() throws RemoteException {
                                        Log.e(TAG, "打印结束");
                                        /**如果出现纸撕下部分有未输出的内容释放下面代码**/
                                        aidlPrinter.paperSkip(2);
//                                        showMsgOnTextView("打印结束");
                                    }

                                    @Override
                                    public void onError(int errorCode) throws RemoteException {
                                        Log.e(TAG, "打印异常");
//                                        showMsgOnTextView( "打印异常码:" + errorCode);

                                    }
                                });

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();


                aidlPrinter.printText(data);
            } else {
//                showMsgOnTextView("请检查打印数据data和打印机状况");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
