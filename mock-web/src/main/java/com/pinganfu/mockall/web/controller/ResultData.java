package com.pinganfu.mockall.web.controller;

public class ResultData {
	int code = 0;
	Object resultData;
	
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Object getResultData() {
		return resultData;
	}
	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}
	
	public static ResultData returnResult(Object result){
		ResultData resultData = new ResultData();
		resultData.setResultData(result);
		return resultData;
	}
	
}
