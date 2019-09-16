package com.wanding.xingpos.payutil;


import android.util.Log;

public class FieldTypeUtil
{
  public static String getDigitAmount(String value)
  {
    int p = value.indexOf('.');
    if (p == -1) {
      value = "00" + value;
      value = value.substring(0, value.length() - 2) + "." + value.substring(value.length() - 2, value.length());
    }

    return String.format("%.2f", new Object[] { Double.valueOf(value) });
  }

  public static String getNumber(String value)
  {
    for (int i = 0; i < value.length(); i++) {
      if (value.charAt(i) != '0') {
        return value.substring(i);
      }
    }
    return value;
  }

  public static String increaseValue(String value)
  {
    int len = value.length();
    int v = Integer.parseInt(value);
    v++; String newValue = String.valueOf(v);

    StringBuilder str = new StringBuilder();
    for (int i = 0; i < len; i++) {
      str.append('0');
    }
    if (newValue.length() > value.length()) {
      return str.toString();
    }
    str.append(newValue);
    return str.substring(newValue.length());
  }

  public static void logBuffer(String tag, byte[] data)
  {
    if (data == null) {
      Log.d(tag, "null");
      return;
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < data.length; i++) {
      String str = "00" + Integer.toHexString(data[i] & 0xFF);
      builder.append(str.substring(str.length() - 2));
      builder.append(",");

      if (i % 16 == 15) {
        builder.append("\n");
      }
    }

    Log.d(tag, builder.toString());
  }

  /**
   *
   */
  public static String makeFieldAmount(String amount)
  {
    String[] part = amount.split("\\.");
    switch (part.length) {
    case 1:
      return ("0000000000" + amount + "00").substring(amount.length());
    case 2:
      String digit = part[1].length() >= 2 ? part[1].substring(0, 2) : (part[1] + "00").substring(0, 2);
      amount = "000000000000" + part[0] + digit;
      return amount.substring(amount.length() - 12);
      default:
        break;
    }
    System.out.println(part.length);
    return null;
  }

 /* public static String extend(String str)
  {
    try
    {
      byte[] gbk = str.getBytes("GBK");
      return BytesUtil.bytes2HexString(gbk);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String shrink(String str)
  {
    try
    {
      byte[] data = BytesUtil.hexString2Bytes(str);
      return new String(data, "GBK");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String shrinkRaw(String str)
  {
    byte[] data = BytesUtil.hexString2Bytes(str);
    char[] chs = new char[data.length];
    for (int i = 0; i < chs.length; i++) {
      chs[i] = ((char)(data[i] & 0xFF));
    }
    return new String(chs);
  }*/

  public static String addPadding(String src, boolean isLeft, char padding, int fixLen)
  {
    if (src.length() >= fixLen) {
      return src;
    }

    StringBuilder b = new StringBuilder();
    int padLen = fixLen - src.length();
    for (int i = 0; i < padLen; i++) {
      b.append(padding);
    }

    if (isLeft) {
      b.insert(0, src);
    }
    else {
      b.append(src);
    }
    return b.toString();
  }
}
