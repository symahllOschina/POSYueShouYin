package com.wanding.xingpos;


/**
 * 服务地址管理类
 */
public class NitConfig {
	
	/**  打包前必看：
	 * 1，替换正式域名前缀，
	 * 2,支付通道为星POS服务通道时，保存订单前判断是否为测试环境isTest = "test"
	 * 3，升级版本号
	 * 4，SignInActivity签到界面参数posProvider默认值必须区分开富友或新大陆
	 * 5,新大陆POS机交易设置界面隐藏银联二维码设置
	 */
	public static final String isTest = "test";//测试为 isTest = "test"正式为isTest = "true"

	//获取最新版本号
	public static String getNEWLANDUrl = "http://download.weupay.com/download/downloadVersion/newland/xml";
	public static String getFUYOUUrl = "http://download.weupay.com/download/downloadVersion/fuiou/xml";

	/** 测试服务前缀 */						  // test.weupay.com/pay/api/qmp/100/1/barcodepay
	//支付链接前缀									 
	public static final String basePath =  "http://dev.weupay.com/pay/api/qmp/100";
	//查询业务链接前缀
	public static final String queryBasePath = "http://dev.weupay.com/pay/api/qmp/200";//test:dev
	//交易明细查询（历史）
	public static final String querySumHistoryPath = "http://test.weupay.com:8081/download/api/qmp/200";//test:dev
	//核销业务
	public static final String writeOffBasePath = "http://dev.weupay.com/admin/api/qmp/200";//

	//富友业务（扫码预授权）
	public static final String authBasePath = "http://192.168.1.134:8081/pay";


	/** 正式服务器 */
	//支付链接前缀
	public static final String basePath1 =      "http://weixin.weupay.com/pay/api/qmp/100";
	//查询业务链接前缀
	public static final String queryBasePath1 = "http://weixin.weupay.com/pay/api/qmp/200";
	//交易明细查询（历史）
	public static final String querySumHistoryPath1 = "http://download.weupay.com/download/api/qmp/200";//test:dev
	//核销业务，批量制劵
	public static final String writeOffBasePath1 = "http://weixin.weupay.com/admin/api/qmp/200";

	/**
	 * 分期请求地址
	 */
	//测试地址
	public static final String instalmentServiceUrl1 = "http://sandbox.starpos.com.cn/installment";
	//生产地址
	public static final String instalmentServiceUrl = "http://bystages-server.starpos.com.cn:8485/installment";
	
	/**
	 * 微信，支付宝（条码），刷卡支付请求
	 */																				 
	public static final String barcodepayUrl = basePath +"/1/barcodepay";
	
	/**
	 * 微信，支付宝（条码），刷卡退款请求
	 */																				 
	public static final String refundUrl = basePath +"/1/refund";
	
	/**
	 * 微信，支付宝（条码），刷卡支付查询请求   mer/queryOrderDetail
	 */																				 
	public static final String queryUrl = basePath +"/1/query";
	
	
	/**
	 * 正式服务器图片前缀 
	 */
	public static final String imgUrl="";
	public static final String doLoginUrl = "";
	
	/**
	 * 本地服务器图片前缀 
	 */
	public static final String imgUrls="";
	
	//签到
	public static final String loginUrl = queryBasePath +"/1/indexLogin";
	//POS接口退款和查询时获取orderId
	public static final String getPosPayOrderId =  queryBasePath +  "/1/queryChannelId";
	//保存测试数据
	public static final String insertChannelIdTestUrl = queryBasePath + "/1/insertChannelIdTest";
	//结算(交接班退出)
	public static final String summaryOrderUrl = queryBasePath +        "/1/handOver";
	//结算(交接班数据查询)
	public static final String settlementOrderUrl = queryBasePath +        "/1/getWorkOverRecord";
	//结算记录，交接班记录(查询)
	public static final String settlementRecordUrl = queryBasePath +        "/1/getWorkOverRecordInterval";
	//结算记录，交接班记录详情(查询)
	public static final String settlementRecordDetailUrl = queryBasePath +        "/1/getWorkOverRecordHistory";
	//交易明细：(查当天)入参：当前页数：pageNum 一页数量：numPerPage mid，eid，date_type（"1"=当日交易）
	public static final String queryOrderDayListUrl = queryBasePath +   "/1/queryOrder";
	//交易明细：(查当月)入参：当前页数：pageNum 一页数量：numPerPage mid，eid，date_type（"2"=本月交易不含今天）
	public static final String queryOrderMonListUrl = querySumHistoryPath+"/1/queryOrderByMonth";
	
	/**
	 * 当天汇总查询
	 * 入参：eid，mid，startTime，endTime
	 */
	public static final String querySummaryUrl = queryBasePath +   "/1/queryOrderSum";

	/**
	 * 历史汇总查询
	 * 入参：eid，mid，startTime，endTime
	 */																			    	
	public static final String queryHistorySummaryUrl = querySumHistoryPath +   "/1/queryOrderSumHistory";

	/**
	 * 扫码预授权
	 *
	 */
	public static final String authUrl = authBasePath + "/wp/prelicen/preadd";

	/**
	 * 预授权撤销
	 *
	 */
	public static final String authCancelyUrl = authBasePath + "/wp/prelicen/preCancel";

	/**
	 * 富友POS：扫码预授权完成
	 *
	 */
	public static final String authFinishUrl = authBasePath + "/wp/prelicen/preFinish";


	/**
	 *  核销劵码查询
	 *  参数：mid = 商户id,code = 核销码;
	 */
	public static final String writeOffQueryCodeUrl = writeOffBasePath + "/queryCode";
	/**
	 *  核销
	 *  参数：mid = 商户id,code = 核销码,couponId;
	 */
	public static final String writeOffConsumeCodeUrl = writeOffBasePath + "/consumeCode";

	/**
	 *  核销记录
	 *  参数：mid = 商户id,pagNum = 页数,code = 核销代码
	 */
	public static final String writeOffRecodeUrl = writeOffBasePath + "/queryConsumeList";
	/**
	 * 查询劵列表：/api/qmp/200/queryCouponList
	 * mid,pageNum
	 */
	public static final String queryCouponListUrl = writeOffBasePath + "/queryCouponList";
	/**
	 *	批量制劵：/api/qmp/200/qRCode
	 * total，cardId，mid
	 */
	public static final String qRCodeUrl = writeOffBasePath + "/qRCode";
	
	
	
	
	
	
	
	
	
	
	
}
