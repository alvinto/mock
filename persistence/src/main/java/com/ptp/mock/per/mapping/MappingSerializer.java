package com.ptp.mock.per.mapping;

import com.alibaba.fastjson.JSON;

/**
 * Created by WANGQIAODONG581 on 2016-06-07.
 */
public abstract class MappingSerializer {
    public final static String SECTION_PROTOCOL="#protocol";
    public final static String SECTION_METHOD="#method";
    public final static String SECTION_REQUEST_HEADER="#request header";
    public final static String SECTION_REQUEST="#request";
    public final static String SECTION_RESPONSE_HEADER="#response header";
    public final static String SECTION_RESPONSE="#response";

    public static String toString(MockMapping mapping){
        StringBuilder sb = new StringBuilder();
        if( mapping == null) return null;
        String protocol = mapping.getProtocol();
        sb.append(SECTION_PROTOCOL); sb.append("\n");
        sb.append(protocol); sb.append("\n");
        sb.append(SECTION_METHOD); sb.append("\n");
    	sb.append(mapping.getMethod()); sb.append("\n");
        if( mapping instanceof RestMapping) {
            sb.append(SECTION_REQUEST_HEADER);  sb.append("\n");
            sb.append(JSON.toJSONString(((RestMapping) mapping).getRequestHeaders())); sb.append("\n");
        }
        sb.append(SECTION_REQUEST); sb.append("\n");
        sb.append(mapping.getRequest());sb.append("\n");
        if( mapping instanceof RestMapping) {
            sb.append(SECTION_RESPONSE_HEADER);          sb.append("\n");
            sb.append(JSON.toJSONString(((RestMapping) mapping).getResponseHeaders()));            sb.append("\n");
        }
        sb.append(SECTION_RESPONSE); sb.append("\n");
        sb.append(mapping.getResponse());sb.append("\n");
        return sb.toString();
    }
}
