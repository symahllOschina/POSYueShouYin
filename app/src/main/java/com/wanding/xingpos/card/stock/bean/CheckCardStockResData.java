package com.wanding.xingpos.card.stock.bean;

import java.io.Serializable;

/**
 * 核销劵
 */
public class CheckCardStockResData implements Serializable {

    //5489 8637 3384
    //{"data":{"code":"057828361099","title":"pos核销劵","subMessage":"核销成功","createTime":1554947998069},"message":"核销成功","status":200}
    private String title;
    private String code;
    private String subMessage;
    private String createTime;

    public CheckCardStockResData() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(String subMessage) {
        this.subMessage = subMessage;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
