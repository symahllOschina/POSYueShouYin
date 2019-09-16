package com.wanding.xingpos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.adapter.StaffListAdapter;
import com.wanding.xingpos.adapter.StaffListAdapter.OnItemDeleteListener;
import com.wanding.xingpos.bean.StaffData;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.List;

/**
 *  员工列表界面
 */
@ContentView(R.layout.activity_staff_list)
public class StaffListActivity extends BaseActivity implements OnClickListener,OnItemDeleteListener {

	@ViewInject(R.id.menu_title_imageView)
	ImageView imgBack;
	@ViewInject(R.id.menu_title_layout)
	LinearLayout titleLayout;
	@ViewInject(R.id.menu_title_tvTitle)
	TextView tvTitle;
	@ViewInject(R.id.menu_title_imgTitleImg)
	ImageView imgTitleImg;
	@ViewInject(R.id.menu_title_tvOption)
	TextView tvOption;
	@ViewInject(R.id.staff_list_listView)
	private ListView mListView;


	private StaffListAdapter mAdapter;
	private List<StaffData> lsStaff;
	
	private boolean OnResume = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("员工管理");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.VISIBLE);
		tvOption.setText("添加");
		

		initListener();
		initData();
		OnResume = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(OnResume){
			initData();
		}
	}
	

	
	private void initListener(){
		imgBack.setOnClickListener(this);
		tvOption.setOnClickListener(this);
	}
	
	@SuppressWarnings("unchecked")
	private void initData(){
		try {
			String staffStr = MySerialize.getObject("staff", StaffListActivity.this);
			if(Utils.isNotEmpty(staffStr)){
				lsStaff = (List<StaffData>) MySerialize.deSerialization(staffStr);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if(lsStaff!=null){
			if(lsStaff.size()>0){
				String isDelete = "1";
				mAdapter = new StaffListAdapter(StaffListActivity.this, lsStaff,isDelete);
				mListView.setAdapter(mAdapter);
				mAdapter.setOnItemDeleteListener(this);
			}else{
				Log.e("TAG", "lsStaff集合长度为0");
			}
		}else{
			Log.e("TAG", "lsStaff集合为null");
		}
       
	}

	@Override
	public void onClick(View v) {
		Intent in = null;
		switch (v.getId()) {
		case R.id.menu_title_imageView://返回
			finish();
			break;
		case R.id.menu_title_tvOption://添加
			OnResume = true;
			
			in = new Intent();
			in.setClass(StaffListActivity.this, StaffAddActivity.class);
			startActivity(in);
			break;
			default:
				break;
		}
	}

	@Override
	public void onDelete(int position) {
		StaffData staff = lsStaff.get(position);
		lsStaff.remove(position);
		//保存
        try {
            String listStr = MySerialize.serialize(lsStaff);
            MySerialize.saveObject("staff",StaffListActivity.this,listStr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initData();
        mAdapter.notifyDataSetChanged();
	}

}
