package com.wanding.xingpos.auth.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptUtils {

	private static final String DEFAULT_CHARSET = "UTF-8";

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	public static String encryptMD5ToString(final String data) {
		if (data == null || data.length() == 0){
			return "";
		}

		return encryptMD5ToString(data.getBytes());
	}

	public static String encryptMD5ToString(final String data, final String salt) throws UnsupportedEncodingException {
		return encryptMD5ToString(data, salt, DEFAULT_CHARSET);
	}

	public static String encryptMD5ToString(final String data, final String salt, final String charset)
			throws UnsupportedEncodingException {
		if (data == null && salt == null) {
			return "";
		}
		if (salt == null) {
			return bytes2HexString(encryptMD5(StringUtils.isEmpty(charset) ? data.getBytes() : data.getBytes(charset)));
		}
		if (data == null) {
			return bytes2HexString(encryptMD5(StringUtils.isEmpty(charset) ? salt.getBytes() : salt.getBytes(charset)));
		}
		return bytes2HexString(
				encryptMD5(StringUtils.isEmpty(charset) ? (data + salt).getBytes() : (data + salt).getBytes(charset)));
	}

	public static String encryptMD5ToString(final byte[] data, final byte[] salt) {
		if (data == null && salt == null){
			return "";
		}

		if (salt == null){
			return bytes2HexString(encryptMD5(data));
		}

		if (data == null){
			return bytes2HexString(encryptMD5(salt));
		}

		byte[] dataSalt = new byte[data.length + salt.length];
		System.arraycopy(data, 0, dataSalt, 0, data.length);
		System.arraycopy(salt, 0, dataSalt, data.length, salt.length);
		return bytes2HexString(encryptMD5(dataSalt));
	}

	public static String encryptMD5ToString(final byte[] data) {
		return bytes2HexString(encryptMD5(data));
	}

	public static byte[] encryptMD5(final byte[] data) {
		return hashTemplate(data, "MD5");
	}

	private static byte[] hashTemplate(final byte[] data, final String algorithm) {
		if (data == null || data.length <= 0){
			return null;
		}

		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(data);
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String bytes2HexString(final byte[] bytes) {
		if (bytes == null){
			return "";
		}

		int len = bytes.length;
		if (len <= 0){
			return "";
		}

		char[] ret = new char[len << 1];
		for (int i = 0, j = 0; i < len; i++) {
			ret[j++] = HEX_DIGITS[bytes[i] >> 4 & 0x0f];
			ret[j++] = HEX_DIGITS[bytes[i] & 0x0f];
		}
		return new String(ret);
	}

}
