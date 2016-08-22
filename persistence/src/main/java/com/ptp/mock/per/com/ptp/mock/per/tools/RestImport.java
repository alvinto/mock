package com.ptp.mock.per.com.ptp.mock.per.tools;

import com.ptp.mock.per.FileMappingServiceImpl;
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
public class RestImport {

    public static void main(String [] args) throws IOException{
        File f= new File("D:\\MTP-WEB.log");
        InputStream inputStream = new FileInputStream(f);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
        List<RestMapping> mappingList = new ArrayList<RestMapping>();
        RestMapping restMapping = null;
        Map context = new HashMap();
        for(;;){
            //read file
            String line =  br.readLine();
            if( line == null) break;
            if( isReq(line)){
                 restMapping = new RestMapping();
                 String tno = processRequest(line,restMapping);
                 context.put(tno,restMapping);
            }
            if( isRsp(line)) {
                restMapping = processResponse(line,context);
                if( restMapping != null ){
                    mappingList.add(restMapping);
                }
            }
        }

        String root="";
        FileMappingServiceImpl fileMappingService = new FileMappingServiceImpl(root);
        for(RestMapping mapping: mappingList){
            fileMappingService.put(mapping,"default");
        }
    }

    private static  boolean isRsp(String line){
        return rspPattern.matcher(line).matches();
    }

    final static Pattern reqPattern = Pattern.compile(".*\\s\\[clientId=.*,\\sop=(.*),\\scid=.*,\\smp=.*,\\stno=(.*),\\stm=.*\\]REQ:\\s(.*)");
    final static Pattern rspPattern = Pattern.compile(".*\\s\\[clientId=.*,\\sop=(.*),\\scid=.*,\\smp=.*,\\stno=(.*),\\stm=.*\\]RSP:\\s(.*)");
    //2016-06-21 17:07:09.400 [http--0.0.0.0-18080-1] INFO
    // mtp.web - [clientId=3abbc144d4d756e8f71f2499a5da9e0912127dc8100001,
    // op=/p2/op_gen_token_with_url.json, cid=1000010003071252, mp=15133660004,\
    // tno=68099113, tm=none, ver=iOS_4.2.21]REQ: {"appId":"100001","appVersion":null,"clientId":"3abbc144d4d756e8f71f2499a5da9e0912127dc8","deviceId":"3abbc144d4d756e8f71f2499a5da9e0912127dc8100001",
    // "operationType":"p2/op_gen_token_with_url","pluginBusinessId":null,"sessionId":null,"url":"http://lpms-mobile-p5-stg.wanlitong.com/wap/mall/index.shtml#/detail?productId=4069&repositoryId=12503&sellerMerId=P6000082&from=http%3A%2F%2Flpms-mobile-p5-stg.wanlitong.com%2Fwap%2Fmall%2Findex.shtml%23%2Fhome%3FfromApp%3Dclient_yiqianbao_ios","urlType":"wlt"}
    private static boolean isReq(String line){
        return reqPattern.matcher(line).matches();
    }

    private static String processRequest(String reqLine,RestMapping restMapping){
        Matcher m = reqPattern.matcher(reqLine);
        String tno = null;
        while (m.find()){
            String op = m.group(1);
            tno = m.group(2);
            String req = m.group(3);
            restMapping.setUrl(op);
            restMapping.setMethod("post");
            restMapping.setRequest(req);
        }
        return tno;
    }

    private static RestMapping  processResponse(String rspLine, Map context){
        Matcher m = rspPattern.matcher(rspLine);
        while (m.find()){
            String tno = m.group(2);
            String rsp = m.group(3);
            RestMapping restMapping = (RestMapping) context.get(tno);
            if( restMapping != null ) {
                restMapping.setResponse(rsp);
                context.remove(tno);
            }
            return restMapping;
        }
        return null;
    }
}
