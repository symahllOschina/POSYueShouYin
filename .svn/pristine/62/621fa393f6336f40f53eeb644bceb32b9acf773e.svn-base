package com.wanding.xingpos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.StaffData;

import java.util.List;

public class StaffListAdapter extends BaseAdapter {

	private Context context;
	private List<StaffData> lsStaff;
	private String isDelete;//值分别为1,2 1表示显示删除图标，2则不显示
	private LayoutInflater inflater;
	
	
	
	public StaffListAdapter(Context context, List<StaffData> lsStaff, String isDelete) {
		super();
		this.context = context;
		this.lsStaff = lsStaff;
		this.isDelete = isDelete;
		inflater = LayoutInflater.from(context);
	}
	


	public interface OnItemDeleteListener{
		public void onDelete(int position);
	}
	
	private OnItemDeleteListener onItemDeleteListener;
	
	public void setOnItemDeleteListener(OnItemDeleteListener onItemDeleteListener){
		this.onItemDeleteListener = onItemDeleteListener;
	}
	
	@Override
	public int getCount() {
		return lsStaff.size();
	}

	@Override
	public Object getItem(int position) {
		return lsStaff.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public class ViewHolder{
		TextView tvName;
		ImageView imgDelete;
	}

	@Override
	public View getView(final int position, View subView, ViewGroup parent) {
		StaffData staff = lsStaff.get(position);
		ViewHolder vh;
		if(subView == null){
			vh = new ViewHolder();
			subView = inflater.inflate(R.layout.staff_list_item, null);
			vh.tvName = (TextView) subView.findViewById(R.id.staff_liet_item_tvName);
			vh.imgDelete = (ImageView) subView.findViewById(R.id.staff_liet_item_imgDelete);
			if("1".equals(isDelete)){
				vh.imgDelete.setVisibility(View.VISIBLE);
			}else if("2".equals(isDelete)){
				vh.imgDelete.setVisibility(View.GONE);
			}
			subView.setTag(vh);
		}else {
			vh = (ViewHolder) subView.getTag();
		}
		
		//赋值
		String nameStr = staff.getName();
		vh.tvName.setText(nameStr);
		vh.imgDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onItemDeleteListener.onDelete(position);
			}
		});
		return subView;
	}

}
