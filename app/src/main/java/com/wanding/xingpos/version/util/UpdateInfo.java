package com.wanding.xingpos.version.util;

import java.io.Serializable;

/**
 * APP版本更新实体类
 * */
public class UpdateInfo implements Serializable {
	
	private static final long serialVersionUID = 8272820444876450398L;
	private String version;//版本号
    private String description;//新版本更新描述
    private String url;//新版本APK更新下载地址
	
    public UpdateInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
    
    

}
