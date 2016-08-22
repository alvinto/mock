package com.ptp.mock.per.mapping;
/**
 * Created by WANGQIAODONG581 on 2016-06-07.
 */
public abstract class MockMapping {

    String protocol; // [dubbo|rest]

    private String request;

    private String response;

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public abstract String getMethod();

    public abstract void setMethod(String method);
}
