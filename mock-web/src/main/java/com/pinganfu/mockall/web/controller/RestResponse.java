package com.pinganfu.mockall.web.controller;

/**
 * Created by WANGQIAODONG581 on 2016-06-15.
 */
public class RestResponse {
    int code = 0; //success

    Object retObject;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getRetObject() {
        return retObject;
    }

    public void setRetObject(Object retObject) {
        this.retObject = retObject;
    }

    public static RestResponse returnOk(Object retObject){
        RestResponse restResponse = new RestResponse();
        restResponse.setRetObject(retObject);
        return restResponse;
    }
}
