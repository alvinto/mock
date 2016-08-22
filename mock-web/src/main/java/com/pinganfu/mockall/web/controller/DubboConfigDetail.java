package com.pinganfu.mockall.web.controller;

/**
 * Created by WANGQIAODONG581 on 2016-06-14.
 */
public class DubboConfigDetail extends ConfigItem {
	private String facadeName;

    private String methodName;


    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

	public String getFacadeName() {
		return facadeName;
	}

	public void setFacadeName(String facadeName) {
		this.facadeName = facadeName;
	}
}
