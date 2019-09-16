package com.wanding.xingpos.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wanding.xingpos.BaseActivity;
import com.wanding.xingpos.R;
import com.wanding.xingpos.bean.StaffData;
import com.wanding.xingpos.utils.EditTextUtils;
import com.wanding.xingpos.utils.MySerialize;
import com.wanding.xingpos.utils.Utils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *  员工添加界面
 */
@ContentView(R.layout.activity_staff_add)
public class StaffAddActivity extends BaseActivity implements OnClickListener {

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

	@ViewInject(R.id.add_staff_etName)
	EditText etName;
	@ViewInject(R.id.add_staff_tvSaveStaff)
	TextView tvSaveStaff;//保存员工


	private List<StaffData> lsStaff;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		imgBack.setVisibility(View.VISIBLE);
		imgBack.setImageDrawable(ContextCompat.getDrawable(activity,R.drawable.back_icon));
		tvTitle.setText("员工添加");
		imgTitleImg.setVisibility(View.GONE);
		tvOption.setVisibility(View.GONE);
		tvOption.setText("");

		initView();
		initData();
		initListener();
	}
	
	private void initView(){


		etName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
		EditTextUtils.setEditTextInputSpeChat(etName);
		
		
		
	}
	
	private void initListener(){
		imgBack.setOnClickListener(this);
		tvSaveStaff.setOnClickListener(this);
	}
	
	private void initData(){
		try {
			String staffStr = MySerialize.getObject("staff", activity);
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
		
		if(lsStaff==null||lsStaff.size()<=0){
			lsStaff = new ArrayList<StaffData>();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.menu_title_imageView://返回
			finish();
			break;
		case R.id.add_staff_tvSaveStaff://保存员工
			String staffNameStr = etName.getText().toString().trim();
			if(Utils.isEmpty(staffNameStr)){
				Toast.makeText(activity, "名称不能为空！", Toast.LENGTH_LONG).show();
				return;
			}
			if(lsStaff.size()>0){
				for (int i = 0; i < lsStaff.size(); i++) {
					StaffData data = lsStaff.get(i);
					String name = data.getName();
					if(staffNameStr.equals(name)){
						Toast.makeText(activity, "该名称已存在！", Toast.LENGTH_LONG).show();
						return;
					}
				}
			}
			if(lsStaff.size()>=20){
				Toast.makeText(activity, "录入名称已达上限！！", Toast.LENGTH_LONG).show();
				return;
			}
			//保存名称
			StaffData staff = new StaffData();
			staff.setName(staffNameStr);
			lsStaff.add(staff);
			//保存
	        try {
	            String listStr = MySerialize.serialize(lsStaff);
	            MySerialize.saveObject("staff",activity,listStr);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        Toast.makeText(activity, "保存成功！", Toast.LENGTH_LONG).show();
	        finish();
			break;
			default:
				break;
		}
	}

}
