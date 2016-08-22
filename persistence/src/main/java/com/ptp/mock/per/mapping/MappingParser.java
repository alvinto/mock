package com.ptp.mock.per.mapping;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WANGQIAODONG581 on 2016-06-06.
 */
public  abstract class MappingParser {

    static  public MockMapping parse(BufferedReader br) {
        try {
            PushBackBufferReader pushBackBufferReader = new PushBackBufferReader(br);
            if (br == null) return null;
            MockMappingBuild mockMappingBuild = new MockMappingBuild();
            mockMappingBuild.setMappingVersion(readProtocol(pushBackBufferReader));
            mockMappingBuild.setMappingMethod(readMethod(pushBackBufferReader));

            if (mockMappingBuild.isBuildingRest()) {
                mockMappingBuild.setRequestHeader(readContent(pushBackBufferReader, MappingSerializer.SECTION_REQUEST_HEADER));
            }
            mockMappingBuild.setRequestBody(readContent(pushBackBufferReader,MappingSerializer.SECTION_REQUEST));
            if (mockMappingBuild.isBuildingRest()) {
                mockMappingBuild.setResponseHeader(readContent(pushBackBufferReader, MappingSerializer.SECTION_RESPONSE_HEADER));
            }
            mockMappingBuild.setResponseBody(readContent(pushBackBufferReader,MappingSerializer.SECTION_RESPONSE));
            if (mockMappingBuild.isBuildingRest()) {
                return mockMappingBuild.getRestMapping();
            }else if (mockMappingBuild.isBuildingDubbo()) {
                return mockMappingBuild.getDubboMapping();
            } else
                throw new IllegalArgumentException("can not parse mapping version : " + mockMappingBuild.getMappingVersion());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static private String readMethod(PushBackBufferReader br) {
        try {
            return readContent(br,MappingSerializer.SECTION_METHOD);
        }catch (IOException e) {
            throw new RuntimeException("can not parse mapping url");
        }
    }

    static private String readProtocol(PushBackBufferReader br){
        try {
            return readContent(br,MappingSerializer.SECTION_PROTOCOL);
        }catch (IOException e) {
            throw new RuntimeException("can not parse mapping version");
        }
    }

    //获取指定section的数据
    static private String readContent(PushBackBufferReader br, String section) throws IOException {
        List<String> buffer = new ArrayList<String>();

        //read section start tag
        String line  = br.readLine();
        if( !StringUtils.startsWith(line,section)){
            br.pushBack(line);
            return null;
        }
        //read data
        for (; ; ) {
            line  = br.readLine();
            //end file
            if (line == null) {
                return StringUtils.join(buffer,"");
            }
            if (StringUtils.startsWith(line, "#")) {
                br.pushBack(line);
                return StringUtils.join(buffer,"");
            } else {
                buffer.add(line);
            }
        }
        //read section end tag

    }

    private static class PushBackBufferReader{
        private BufferedReader br;
        private String line;

        public PushBackBufferReader(BufferedReader br){
            this.br = br;
        }

        public String readLine() throws IOException{
            if( line != null) {
                String ret = line;
                line = null;
                return ret;
            }
            return br.readLine();
        }

        public void pushBack(String line){
            this.line = line;
        }

    }

}
