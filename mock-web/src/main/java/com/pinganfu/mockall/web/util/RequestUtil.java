package com.pinganfu.mockall.web.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ptp.mock.per.MappingService;
import com.ptp.mock.per.mapping.DubboMapping;
import com.ptp.mock.per.mapping.MockMapping;
import com.ptp.mock.per.mapping.RestMapping;

public class RequestUtil {
	private static final Logger logger = LoggerFactory.getLogger(RequestUtil.class);
	public static final byte[] readBytes(InputStream is, int contentLen) {
		if (contentLen > 0) {
			int readLen = 0;

			int readLengthThisTime = 0;

			byte[] message = new byte[contentLen];

			try {

				while (readLen != contentLen) {

					readLengthThisTime = is.read(message, readLen, contentLen
							- readLen);

					if (readLengthThisTime == -1) {// Should not happen.
						break;
					}

					readLen += readLengthThisTime;
				}

				return message;
			} catch (IOException e) {
				// Ignore
				// e.printStackTrace();
			}
		}

		return new byte[] {};
	}

	public static byte[] getBytesFromFile(File f){
        if (f == null){
            return null;
        }
        try{
            FileInputStream stream = new FileInputStream(f);
            ByteArrayOutputStream out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            for (int n;(n = stream.read(b)) != -1;) {
		        out.write(b, 0, n);
		    }

            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e){
        	logger.error("transform file to byte[] failure ",e);
        }
        return null;
    }
	/**
	 * 根据参数装配实体
	 * @param params
	 * @param type 0:dubboMapping;1:restMapping
	 * @return
	 */
	public static MockMapping parse(Map<String,Object> params,int type){
		MockMapping mockMapping = null;
		if(MappingService.DUBBO_TYPE == type){
			String className = (String)params.get("facadeName");
			String methodName = (String)params.get("methodName");
			String request = (String)params.get("request");
			String response = (String)params.get("response");
			String method = className+"."+methodName;
			DubboMapping dubboMapping = new DubboMapping();
			dubboMapping.setMethod(method);
			dubboMapping.setRequest(request);
			dubboMapping.setResponse(response);
			mockMapping = dubboMapping;
		}
		if(MappingService.REST_TYPE == type){
			String requestHeaders = (String) params.get("requestHeaders");
	        String restMethod = (String)params.get("restMethod");
	        String url = (String)params.get("url");
	        String request = (String)params.get("request");
	        String response = (String)params.get("response");
	        String responseHeaders = (String)params.get("responseHeaders");
	        RestMapping restMapping = new RestMapping();
	        
	        restMapping.setMethod(restMethod + " " + url);
	        restMapping.setRequestHeaders(requestHeaders);
	        restMapping.setRequest(request);
	        restMapping.setResponseHeaders(responseHeaders);
	        restMapping.setResponse(response);
	        
	        mockMapping = restMapping;
		}
		return mockMapping;
	}

	/**
	 *
	 * @param flag true: 解析/rest/username/methodName/resourceName ; <br>
	 * false:解析 /mock/username/resourceName
	 * @param request
	 * @return Map<String,String> key: username,methodName,resourceName<br>
	 * flag是true时 username为null时默认返回“default”;
	 * flag是false时，不返回methodName
	 */
	public static Map<String,String> parseUrl(boolean flag,HttpServletRequest request){
		Map<String,String> map = new HashMap<String, String>();
		String mark = flag ? "/rest/" : "/mock/";
		String url = request.getRequestURI();
		String params = request.getQueryString();
		String result = null;
    	if(StringUtils.isNotEmpty(url) && url.length() > mark.length()){
    		result = url.substring(url.indexOf(mark)+mark.length());
    	}
    	if(StringUtils.isNotEmpty(params)){
    		result = result + "/" + params;
    	}
    	
    	String[] results = result.split("/");
    	if(flag){
    		if(RestMapping.checkRestMethod(results[0])){
    			map.put("username", "default");
    			map.put("methodName", results[0]);
    			map.put("resourceName", StringUtils.substringAfter(result, results[0]));
    		}else{
    			map.put("username", results[0]);
    			map.put("methodName", results[1]);
    			map.put("resourceName", StringUtils.substringAfter(result, results[0]));
    		}
    	}else{
    		map.put("username", results[0]);
    		map.put("resourceName", StringUtils.substringAfter(result, results[0]));
    	}
    	
    	return map;
	}
	
}
