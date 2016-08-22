package com.ptp.mock.per.mapping;

import com.alibaba.fastjson.JSON;
import com.ptp.mock.per.MappingService;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by WANGQIAODONG581 on 2016-05-16.
 */
public class RestMapping extends MockMapping{

    Map<String,String> requestHeaders = new HashMap();

    Map<String,String> responseHeaders = new HashMap();

    private String url; //url

    private String restMethod;

    public RestMapping(){
        this.protocol = MappingService.REST_PROTOCOL;
    }

    public void setRequestHeaders(String headers){
        if(StringUtils.isEmpty(headers )) return;
        Map map = JSON.parseObject(headers,Map.class);
        requestHeaders.putAll(map);
    }

    public void setResponseHeaders(String headers){
        if(StringUtils.isEmpty(headers )) return;
        Map map = JSON.parseObject(headers,Map.class);
        responseHeaders.putAll(map);
    }

    public void addRequestHeader(String name,String v){
        this.requestHeaders.put(name,v);
    }

    public void addResponseHeader(String name,String v){
        this.responseHeaders.put(name,v);
    }

    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    @Override
    public String getMethod() {
        return getRestMethod() + " " + this.url;
    }

    public void setMethod(String method) {
        String[] strs = StringUtils.split(method," ");
        if( strs.length > 0) {
            if( !checkRestMethod(strs[0])){
                throw new IllegalArgumentException(strs[0] + " not a rest method");
            }
            this.restMethod = strs[0];
        }
        if( strs.length > 1) this.url = strs[1];
    }

    public static boolean checkRestMethod(String restMethod){
        if(StringUtils.equalsIgnoreCase(restMethod,"get")
                || StringUtils.equalsIgnoreCase(restMethod,"post")
                || StringUtils.equalsIgnoreCase(restMethod,"option")
                ||  StringUtils.equalsIgnoreCase(restMethod,"head")
                ||  StringUtils.equalsIgnoreCase(restMethod,"put")
                ||  StringUtils.equalsIgnoreCase(restMethod,"delete"))
            return true;
        return false;
    }

    public String getRestMethod() {
        return restMethod;
    }

    public void setRestMethod(String restMethod) {
        this.restMethod = restMethod;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
