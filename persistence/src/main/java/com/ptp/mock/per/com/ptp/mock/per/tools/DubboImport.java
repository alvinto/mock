package com.ptp.mock.per.com.ptp.mock.per.tools;

import com.ptp.mock.per.FileMappingServiceImpl;
import com.ptp.mock.per.mapping.DubboMapping;
import com.ptp.mock.per.mapping.RestMapping;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangqiaodong581 on 2016-06-21.
 */
public class DubboImport {

    public static void main(String [] args) throws IOException{
        File f= new File("D:\\temp\\MTP-INTEGRATION.log");
        InputStream inputStream = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        List<DubboMapping> mappingList = new ArrayList<DubboMapping>();
        DubboMapping dubboMapping = null;
        Map<String,DubboMapping> context = new HashMap<String,DubboMapping>();
        for(;;){
            //read file
            String line =  br.readLine();
            if( line == null) break;
            if( isReq(line)){
            	dubboMapping = new DubboMapping();
                 String methodName = processRequest(line,dubboMapping);
                 context.put(methodName,dubboMapping);
            }
            if( isRsp(line)) {
            	dubboMapping = processResponse(line,context);
                if( dubboMapping != null ){
                    mappingList.add(dubboMapping);
                }
            }
        }

        String root="";
        FileMappingServiceImpl fileMappingService = new FileMappingServiceImpl(root);
        for(DubboMapping mapping: mappingList){
            fileMappingService.put(mapping,"default");
            System.out.println("import "+mapping.getMethod());
        }
        System.out.println("本次共导入"+mappingList.size()+"条数据");
        br.close();
    	
//    	String ss = "Request from: com.pinganfu.finexchangeinfo.service.facade.SignBankListService, Method: querySignBankList, Detail: {\"id\":\"12312\"}";
//    	String rr = "Response from: com.pinganfu.finexchangeinfo.service.facade.SignBankListService, Method: querySignBankList, Detail: {\"id\":\"12312\"}";
//    	Matcher m = rspPattern.matcher(rr);
//    	
//    	String request = null;
//        String methodName = null;
//        String clazzName = null;
//        while (m.find()){
//        	clazzName = m.group(1);
//            methodName = m.group(2);
//            request = m.group(3);
//        }
//        System.out.println(clazzName + "----" + methodName + "---" + request);
    }

    private static  boolean isRsp(String line){
        return rspPattern.matcher(line).matches();
    }
    
//    final static Pattern reqPattern = Pattern.compile(".*Send\\s(.*)\\s:\\s(.*)\\srequest:\\s(.*)");
//    final static Pattern rspPattern = Pattern.compile(".*Receive\\s(.*)\\s:\\s(.*)\\sresponse:\\s(.*)");
    //Request from: com.pinganfu.finexchangeinfo.service.facade.SignBankListService, Method: querySignBankList, Detail: {"id":"12312"}
    final static Pattern reqPattern = Pattern.compile(".*Request from:\\s(.*), Method:\\s(.*), Detail:\\s(.*)");
    final static Pattern rspPattern = Pattern.compile(".*Response from:\\s(.*), Method:\\s(.*), Detail:\\s(.*)");
    private static boolean isReq(String line){
        return reqPattern.matcher(line).matches();
    }

    private static String processRequest(String reqLine,DubboMapping dubboMapping){
        Matcher m = reqPattern.matcher(reqLine);
        String request = null;
        String methodName = null;
        String clazzName = null;
        while (m.find()){
        	clazzName = m.group(1);
            methodName = m.group(2);
            request = m.group(3);
            
            dubboMapping.setMethod(clazzName+"."+methodName);
            dubboMapping.setRequest(request);
        }
        return clazzName+"."+methodName;
    }

    private static DubboMapping  processResponse(String rspLine, Map<String,DubboMapping> context){
        Matcher m = rspPattern.matcher(rspLine);
        String clazzName = null;
        String methodName = null;
        String rsp = null;
        while (m.find()){
        	clazzName = m.group(1);
            methodName = m.group(2);
            rsp = m.group(3);
            DubboMapping dubboMapping = (DubboMapping) context.get(clazzName+"."+methodName);
            if( dubboMapping != null ) {
            	dubboMapping.setResponse(rsp);
                context.remove(clazzName+"."+methodName);
            }
            return dubboMapping;
        }
        return null;
    }
}
