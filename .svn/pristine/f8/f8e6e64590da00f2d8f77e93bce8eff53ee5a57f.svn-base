package com.wanding.xingpos.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditTextUtils {
	/**
	 * EditText输入框内容输入变化监听
	 * （主要用于金额输入）
	 * 使用方法：
	 * EditText et = findviewById(R.id.etNum);
	 * MoneyEditText.setPricePoint(et);
	 */
	public static void setPricePoint(final EditText editText) {
		editText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//如果输入内容包含小数点,
				if (s.toString().contains(".")) {
					//小数点后位数只能输入两位
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        editText.setText(s);
                        editText.setSelection(s.length());
                    }
                }
                if (".".equals(s.toString().trim().substring(0))) {
                    s = "0" + s;
                    editText.setText(s);
                    editText.setSelection(2);
                }
                if (s.toString().startsWith("0") && s.toString().trim().length() > 1) {
                    if (!".".equals(s.toString().substring(1, 2))) {
                        editText.setText(s.subSequence(0, 1));
                        editText.setSelection(1);
                        return;
                    }
                }
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	
	/**
	 * 禁止EditText输入空格和换行符
	 *
	 * @param editText EditText输入框
	 */
	public static void setEditTextInputSpace(EditText editText) {
	    InputFilter filter = new InputFilter() {
	       

			@Override
			public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
				if (" ".equals(source) || source.toString().contentEquals("\n")) {
	                return "";
	            } else {
	                return source;
	            }
			}
	    };
	    editText.setFilters(new InputFilter[]{filter});
	}
	
	/**
	 * 禁止EditText输入特殊字符(这里只匹配了了一部分特殊字符)
	 * end>7||dend>7：表示最大输入的字符数，从0下标开始计算
	 *
	 * @param editText EditText输入框
	 */
	public static void setEditTextInputSpeChat(EditText editText) {
	    InputFilter filter = new InputFilter() {
	        @Override
	        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
	            String speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
	            Pattern pattern = Pattern.compile(speChat);
	            Matcher matcher = pattern.matcher(source.toString());
	            if (matcher.find()||end>7||dend>7) {
	                return "";
	            } else {
	                return source;
	            }
	            
	           
	        }
	    };
	    editText.setFilters(new InputFilter[]{filter});
	    
	   
	}
	
}
