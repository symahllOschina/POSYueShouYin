package com.wanding.xingpos.auth.util;

import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * @Description 
 * @author liaoxiang
 * 2018年9月5日
 */
public class SignUtils {

	public static final String CHARSET_UTF8 = "UTF-8";

	public static final String SIGN_TYPE_MD5 = "MD5";

	public static final String SIGN = "sign";

	public static final String SIGN_TYPE = "sign_type";

	public static Map<String, String> getSortedMap(Map<String, String> params) {
		Map<String, String> sortedParams = new TreeMap<String, String>();
		if (params != null && params.size() > 0) {
			sortedParams.putAll(params);
		}
		return sortedParams;
	}

	public static String getSignContentV1(Map<String, String> sortedParams) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(sortedParams.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = sortedParams.get(key);
			if (StringUtils.isNoneBlank(key, value)) {
				content.append((i == 0 ? "" : "&") + key + "=" + value);
			}
		}
		return content.toString();
	}

	public static String getSignContentV3(Map<String, String> params) {
		if (params == null) {
			return null;
		}
		params.remove(SIGN);
		params.remove(SIGN_TYPE);

		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}

		return content.toString();
	}

	public static String getSignContentV2(Map<String, String> params) {
		if (params == null) {
			return null;
		}

		params.remove(SIGN);

		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}

		return content.toString();
	}

	/**
	 * 加签
	 * @param params
	 * @param salt
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String md5Sign(Map<String, String> params, String salt, String charset)
			throws UnsupportedEncodingException {
		String signContent = getSignContentV3(params);
		Log.e("加签前：",signContent);
		return EncryptUtils.encryptMD5ToString(signContent, salt, charset);
	}

	private static String md5Sign(String content, String salt, String charset) throws UnsupportedEncodingException {
		return EncryptUtils.encryptMD5ToString(content, salt, charset);
	}

	/**
	 * 验签
	 * @param salt
	 * @param charset
	 * @param signType
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static boolean checkSignV1(Map<String, String> params, String salt, String charset, String signType)
			throws UnsupportedEncodingException {
		String sign = params.get("sign");
		String content = getSignContentV3(params);

		return checkSign(content, sign, salt, charset, signType);
	}

	/**
	 * 验签
	 * @param salt
	 * @param charset
	 * @param signType
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static boolean checkSignV2(Map<String, String> params, String salt, String charset, String signType)
			throws UnsupportedEncodingException {
		String sign = params.get("sign");
		String content = getSignContentV2(params);

		return checkSign(content, sign, salt, charset, signType);
	}


	private static boolean checkSign(String content, String sign, String salt, String charset, String signType)
			throws UnsupportedEncodingException {

		if (SIGN_TYPE_MD5.equals(signType)) {

			return md5SignVerify(content, salt, charset, sign);

		} else {

			throw new RuntimeException("Sign Type is Not Support : signType=" + signType);
		}

	}

	private static boolean md5SignVerify(String content, String salt, String charset, final String sign)
			throws UnsupportedEncodingException {
		String md5Sign = md5Sign(content, salt, charset);
		return md5Sign.equals(sign);
	}

}
