package com.ptp.mock.per.mapping;

/**
 * Created by WANGQIAODONG581 on 2016-05-16.
 */
public class DubboMapping extends MockMapping{

    private String method ;//类名+“.”+方法名
    
    private String facadeName;//类名
    
    private String methodName;//方法名

    @Override
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
        int temp = method.lastIndexOf(".");
        if(temp != -1){
        	this.facadeName = method.substring(0, temp);
        	this.methodName = method.substring(temp+1);
        }
    }

    public DubboMapping(){
        this.protocol = "dubbo/1.1";
    }

	public String getFacadeName() {
		return facadeName;
	}

	public void setFacadeName(String facadeName) {
		this.facadeName = facadeName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
}
