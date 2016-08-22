package com.pinganfu.mobile.dubboplus.mock;

import com.alibaba.dubbo.common.json.JSONObject;
import com.alibaba.dubbo.rpc.*;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by wangqiaodong581 on 2016-07-14.
 */
public class RemoteMockInvoker<T extends Object> implements Invoker<T> {
    private String mockServer;
    private Invoker invoker;
    private Logger logger = LoggerFactory.getLogger("CircuitBreaker");

    RemoteMockInvoker(Invoker invoker){
        //read from env to
        mockServer = System.getProperty("dubboMockServer");
        this.invoker = invoker;
        if( mockServer !=  null ){
            logger.info(" Mock for " + invoker.getInterface().getName() + " loaded ");
        }
    }

    private String executePost(String path) {
        if( mockServer == null || "".equals(mockServer)) return null;
        try {
            URL url = new URL(mockServer + "/" + path) ;
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestProperty("Content-type", "application/json");
            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write("{}".getBytes("UTF-8"));
            outputStream.flush();

            return streamToStr(urlConnection.getInputStream());
        } catch (IOException e) {

        }
        return null;
    }

    private String streamToStr(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[100];
        int len = 0;
        while (inputStream != null && (len = inputStream.read(data)) != -1) {
            byteArrayOutputStream.write(data, 0, len);
        }
        return new String(byteArrayOutputStream.toByteArray(), "UTF-8");
    }

    @Override
    public Class<T> getInterface() {
        return invoker.getInterface();
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        String methodPath = invoker.getInterface().getName() + "/" + invocation.getMethodName();
        String result = executePost(methodPath);
        if(result != null) {
            try {
                Method m = invoker.getInterface().getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                //Class returnType = m.getReturnType();
                Type returnType = m.getGenericReturnType();
                logger.info("Invoke " + methodPath + " returns : " + result);
                Object object = buildObjectUsingFast(returnType,result);
                return new RpcResult(object);
                //return new RpcResult(JSON.parseObject(result, returnType));
            } catch (NoSuchMethodException e) {
                throw new RpcException("not found method " + invocation.getMethodName(), e);
            }
        }
        return invoker.invoke(invocation);
    }

    private Object buildObjectUsingGson(Type returnType, String result){
        Gson gson =  new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Object object = gson.fromJson(result, TypeToken.get(returnType).getType());
        return object;
    }

    private Object buildObjectUsingFast(Type returnType, String result){
        return JSON.parseObject(result,returnType);
    }

    @Override
    public com.alibaba.dubbo.common.URL getUrl() {
        return invoker.getUrl();
    }

    @Override
    public boolean isAvailable() {
        return invoker.isAvailable();
    }

    @Override
    public void destroy() {
        invoker.destroy();
    }
}