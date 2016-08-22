package com.pinganfu.mockall.web.controller;

public class RestConfigDetail extends ConfigItem{
	
	private String requestHeaders;
	private String responseHeaders;
	private String url;
	private String restMethod;
	public String getRequestHeaders() {
		return requestHeaders;
	}
	public void setRequestHeaders(String requestHeaders) {
		this.requestHeaders = requestHeaders;
	}
	public String getResponseHeaders() {
		return responseHeaders;
	}
	public void setResponseHeaders(String responseHeaders) {
		this.responseHeaders = responseHeaders;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getRestMethod() {
		return restMethod;
	}
	public void setRestMethod(String restMethod) {
		this.restMethod = restMethod;
	}
	
}
