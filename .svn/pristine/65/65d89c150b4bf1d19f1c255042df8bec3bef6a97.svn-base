package com.wanding.xingpos.auth.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 扫码类型辨别（正则表达式）
 * WXPA
 * ALPA
 */
public class ScanDisUtil {
    /**
     *  * https://docs.open.alipay.com/194/105322/
     * 由于业务发展需要，支付宝将会在2017年9月底对支付宝的用户付款码做升级处理。
     * 付款码将由原来的28开头扩充到25-30开头，长度由原来的16-18位扩充到16-24位。
     * 未来随移动支付产业的发展，用户付款码可能会有所加长，建议开发者做好设计预留工作。
     */
    private static final Pattern aliPayAuthCodeReg = Pattern.compile("^(((2[5-9])|(30))\\d{14,22})$");

    /**
     *  * https://pay.weixin.qq.com/wiki/doc/api/micropay.php?chapter=5_1
     *
     * 用户刷卡条形码规则：18位纯数字，以10、11、12、13、14、15开头
     */
    private static final Pattern weixinPayAuthCodeReg = Pattern.compile("^1[0-5]\\d{16}$");

    public static boolean isAliQRCode(String auth_code){
        Matcher match = aliPayAuthCodeReg.matcher(auth_code);
        return match.matches();
    }

    public static boolean isWxQRCode(String auth_code){
        Matcher match = weixinPayAuthCodeReg.matcher(auth_code);
        return match.matches();
    }





//
//    private void isE(){
//        if (StringUtils.isNotEmpty(auth_code)) {
//            Matcher matcher = weixinPayAuthCodeReg.matcher(auth_code);
//            boolean matches = matcher.matches();
//            payWay = PayWayEnum.WX_MICRO.getChannel();
//            String payType = PayWayEnum.WX_MICRO.getDesc();
//            mPayType = PayType.WX.getPayType();
//            if (!matches) {
//                Matcher aliMatcher = aliPayAuthCodeReg.matcher(auth_code);
//                boolean aliMatches = aliMatcher.matches();
//                if (aliMatches) {
//                    preType = "ALPA";
//                    payWay = PayWayEnum.ALI_MICRO.getChannel();
//                    payType = PayWayEnum.ALI_MICRO.getDesc();
//                    mPayType = PayType.ALI.getPayType();
//                } else {
//                    LOG.warn("暂不支持其他预授权类型");
//                }
//            } else {
//                preType = "WXPA";
//            }
//        }
//    }

}
