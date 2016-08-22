package com.ptp.mock.per.mapping;

import org.apache.commons.lang.StringUtils;

/**
 * Created by WANGQIAODONG581 on 2016-06-07.
 */
public class MockMappingBuild {
    private static final String PROTOCOL_DUBBO = "dubbo";
    private static final String PROTOCOL_REST = "rest";

    private final static String  PROTOCOL_SEPARATOR="/";

    private String mappingVersion;
    private MockMapping mockMapping;

    public String getMappingVersion() {
        return mappingVersion;
    }

    public void setMappingVersion(String mappingVersion) {
        this.mappingVersion= mappingVersion;
        String[] str =  StringUtils.split(mappingVersion,PROTOCOL_SEPARATOR);
        if( str != null && StringUtils.equals(str[0],PROTOCOL_REST)){
            mockMapping = new RestMapping();
            mockMapping.setProtocol(mappingVersion);
        }else if( str != null && StringUtils.equals(str[0],PROTOCOL_DUBBO)){
            mockMapping = new DubboMapping();
            mockMapping.setProtocol(mappingVersion);
        }
    }

    public void setMappingMethod(String method){
        mockMapping.setMethod(method);
    }

    public void setRequestHeader(String headers){
        ((RestMapping)mockMapping).setRequestHeaders(headers);
    }

    public void setRequestBody(String body){
        mockMapping.setRequest(body);
    }

    public void setResponseBody(String body){
        mockMapping.setResponse(body);
    }

    public void setResponseHeader(String headers){
        ((RestMapping)mockMapping).setResponseHeaders(headers);
    }

    public boolean isBuildingDubbo(){
        return mockMapping instanceof DubboMapping;
    }

    public boolean isBuildingRest(){
         return mockMapping instanceof RestMapping;
    }

    public RestMapping getRestMapping(){
        if (!isBuildingRest()) return null;
        return(RestMapping) mockMapping;
    }

    public DubboMapping getDubboMapping(){
        if(!isBuildingDubbo()) return null;
        return(DubboMapping) mockMapping;
    }


}
