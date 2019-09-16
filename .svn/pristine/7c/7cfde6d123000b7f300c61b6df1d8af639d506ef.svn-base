package com.wanding.xingpos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.ShiftRecordResData;
import com.wanding.xingpos.utils.DateTimeUtil;
import com.wanding.xingpos.utils.Utils;

import java.util.List;

public class ShiftRecordAdapte extends BaseAdapter {
	
	private Context context;
	private List<ShiftRecordResData> lsRecord;
	private LayoutInflater inflater;
	
	public ShiftRecordAdapte(Context context, List<ShiftRecordResData> lsRecord) {
		super();
		this.context = context;
		this.lsRecord = lsRecord;
		inflater = LayoutInflater.from(context);
	}
	
	/**
	 * 定义Item查看按钮的触发事件，并将接口暴露出去
	 */
	public interface OnSetRecordDetail{
		public void getDetail(ShiftRecordResData record);
	}
	
	private OnSetRecordDetail onSetRecordDetail;
	
	public void ItemOnSetRecordDetail(OnSetRecordDetail onSetRecordDetail){
		this.onSetRecordDetail = onSetRecordDetail;
	}

	@Override
	public int getCount() {
		return lsRecord.size();
	}

	@Override
	public Object getItem(int position) {
		return lsRecord.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class ViewHolder{
		TextView tvStartTime;//开始时间
		TextView tvEndTime;//结束时间
		TextView tvName;//交班人
		TextView tvOption;//操作：查看
	}

	@Override
	public View getView(int position, View subView, ViewGroup parent) {
		final ShiftRecordResData order = lsRecord.get(position);
		ViewHolder vh = null;
		if(subView == null){
			subView = inflater.inflate(R.layout.shift_record_item, null);
			vh = new ViewHolder();
			vh.tvStartTime = (TextView) subView.findViewById(R.id.settlement_record_item_tvStartTime);
			vh.tvEndTime = (TextView) subView.findViewById(R.id.settlement_record_item_tvEndTime);
			vh.tvName = (TextView) subView.findViewById(R.id.settlement_record_item_tvName);
			vh.tvOption = (TextView) subView.findViewById(R.id.settlement_record_item_tvOption);
			subView.setTag(vh);
		}else{
			vh = (ViewHolder) subView.getTag();
		}
		//开始时间
		Long startTimeStr = order.getStartTime();
		String startTime = "";
		if(startTimeStr!=null){
			startTime = DateTimeUtil.stampToFormatDate(startTimeStr, "yyyy-MM-dd HH:mm:ss");
		}
		vh.tvStartTime.setText(startTime);
		//开始时间
		Long endTimeStr = order.getEndTime();
		String endTime = "";
		if(endTimeStr!=null){
			endTime = DateTimeUtil.stampToFormatDate(endTimeStr, "yyyy-MM-dd HH:mm:ss");
		}
		vh.tvEndTime.setText(endTime);
		//交班人
		String staffNameStr = order.getReserve1();
		String staffName = "";
		if(Utils.isNotEmpty(staffNameStr)){
			staffName = staffNameStr;
		}
		vh.tvName.setText(staffName);
		//查看
		vh.tvOption.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onSetRecordDetail.getDetail(order);
			}
		});
		return subView;
	}

}
