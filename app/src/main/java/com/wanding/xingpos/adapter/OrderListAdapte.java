package com.wanding.xingpos.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.OrderDetailData;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.DecimalUtil;
import com.wanding.xingpos.utils.TextStyleUtil;
import com.wanding.xingpos.utils.Utils;

import java.util.List;

public class OrderListAdapte extends BaseAdapter {
	
	private Context context;
	private List<OrderDetailData> lsOrder;
	private LayoutInflater inflater;

	/**
	 * posProvider：MainActivity里定义为公共参数，区分pos提供厂商
	 * 值为 newland 表示新大陆
	 * 值为 fuyousf 表示富友
	 */
	private String posProvider;

	public OrderListAdapte(Context context, List<OrderDetailData> lsOrder, String posProvider) {
		super();
		this.context = context;
		this.lsOrder = lsOrder;
		this.posProvider = posProvider;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return lsOrder.size();
	}

	@Override
	public Object getItem(int position) {
		return lsOrder.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class ViewHolder{
		TextView tvOrderId;//
		TextView tvOrderIdSuffix;//订单号后六位
		LinearLayout layoutTransactionId;
		TextView tvTransactionId;//交易号
		TextView tvOrderTime;
		TextView tvOrderTotal;
		TextView tvOrderPayStatus;//交易状态
	}

	@Override
	public View getView(int position, View subView, ViewGroup parent) {
		OrderDetailData order = lsOrder.get(position);
		ViewHolder vh = null;
		if(subView == null){
			subView = inflater.inflate(R.layout.order_list_item, null);
			vh = new ViewHolder();
			vh.tvOrderId = (TextView) subView.findViewById(R.id.order_list_item_tvOrderId);
			vh.tvOrderIdSuffix = (TextView) subView.findViewById(R.id.order_list_item_tvOrderIdSuffix);
			vh.layoutTransactionId = subView.findViewById(R.id.order_list_item_layoutTransactionId);
			vh.tvTransactionId = (TextView) subView.findViewById(R.id.order_list_item_tvTransactionId);
			vh.tvOrderTime = (TextView) subView.findViewById(R.id.order_list_item_tvOrderTime);
			vh.tvOrderTotal = (TextView) subView.findViewById(R.id.order_list_item_tvOrderTotal);
			vh.tvOrderPayStatus = (TextView) subView.findViewById(R.id.order_list_item_tvOrderPayStatus);
			subView.setTag(vh);
		}else{
			vh = (ViewHolder) subView.getTag();
		}
		//订单号,订单号后六位
		/*String orderIdStr = order.getOrderId();
		String orderId = "";
		String orderIdSuffix = "";
		if(Utils.isNotEmpty(orderIdStr)&&orderIdStr.length()>=32){
			orderId = orderIdStr.substring(0,24);
			orderIdSuffix = orderIdStr.substring(24);
		}else{
			orderId = orderIdStr;
		}
		vh.tvOrderId.setText(orderId);
		vh.tvOrderIdSuffix.setText(orderIdSuffix);*/
		//订单号
		String orderIdStr = order.getOrderId();
		String orderId = "";
		if(Utils.isNotEmpty(orderIdStr)){
			orderId = orderIdStr;
		}
		vh.tvOrderId.setText(orderId);

		String orderIdText = vh.tvOrderId.getText().toString();
		if(Utils.isNotEmpty(orderIdText)&&orderIdText.length()>=32){
			SpannableStringBuilder style1 = TextStyleUtil.changeStyle(orderIdText, 24, orderIdText.length());
			vh.tvOrderId.setText(style1);
		}


		//渠道号
		String transactionIdStr = order.getTransactionId();
		String transactionId = "";
		if(Utils.isNotEmpty(transactionIdStr)){
			transactionId = transactionIdStr;
		}
		vh.tvTransactionId.setText(transactionId);



		//订单交易时间
		String orderTimeStr = order.getPayTime();
		String orderPayTime = "";
		if(orderTimeStr!=null&&!"".equals(orderTimeStr) &&!"null".equals(orderTimeStr)){
			orderPayTime = DateTimeUtil.stampToFormatDate(Long.parseLong(orderTimeStr), "MM月dd日 HH:mm:ss");
		}
		vh.tvOrderTime.setText(orderPayTime);
		//订单交易金额
		String orderTotalStr = order.getGoodsPrice();
		String orderTotal = "";
		if(orderTotalStr!=null&&!"".equals(orderTotalStr) &&!"null".equals(orderTotalStr)){
			orderTotal = DecimalUtil.StringToPrice(orderTotalStr);
//			orderTotal = orderTotalStr;
		}
		vh.tvOrderTotal.setText("￥"+orderTotal);
		//交易状态
		String orderStatus = "未知状态";
		int color = ContextCompat.getColor(context,R.color.green_006400);
		String orderTypeStr = order.getOrderType();
		if(orderTypeStr!=null&&!"".equals(orderTypeStr) &&!"null".equals(orderTypeStr)){
			//先判断是支付交易还是退款交易 0正向 ,1退款
			if("0".equals(orderTypeStr)){
				//判断交易状态状态status 状态为支付、预支付、退款等	0准备支付1支付完成2支付失败3.包括退款5.支付未知
				String statusStr = order.getStatus();
				if(statusStr!=null&&!"null".equals(statusStr) && "1".equals(statusStr)){
					orderStatus = "支付成功";
					color = ContextCompat.getColor(context,R.color.green_006400);
				}else if(statusStr!=null&&!"null".equals(statusStr) && "3".equals(statusStr)){
					orderStatus = "包含退款";
					color = ContextCompat.getColor(context,R.color.red_d05450);
				}else if(statusStr!=null&&!"null".equals(statusStr) && "4".equals(statusStr)){
					orderStatus = "全部退款";
					color = ContextCompat.getColor(context,R.color.red_d05450);
				}else if(statusStr!=null&&!"null".equals(statusStr) && "5".equals(statusStr)){
					orderStatus = "支付未知";
					color = ContextCompat.getColor(context,R.color.red_d05450);
				}else{
					orderStatus = "支付失败";
					color = ContextCompat.getColor(context,R.color.red_d05450);
				}
			}else if("1".equals(orderTypeStr)){
				//判断退款状态
				String refund_statusStr = order.getStatus();
				if(refund_statusStr!=null&&!"null".equals(refund_statusStr) && "1".equals(refund_statusStr)){
					orderStatus = "退款成功";
					color = ContextCompat.getColor(context,R.color.green_006400);
				}else{
					orderStatus = "退款失败";
					color = ContextCompat.getColor(context,R.color.red_d05450);
				}
			}
		}
		vh.tvOrderPayStatus.setText(orderStatus);
		vh.tvOrderPayStatus.setTextColor(color);
		
		return subView;
	}

}
