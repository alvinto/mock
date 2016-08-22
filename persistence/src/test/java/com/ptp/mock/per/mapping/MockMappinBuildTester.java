package com.ptp.mock.per.mapping;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by WANGQIAODONG581 on 2016-06-07.
 */
public class MockMappinBuildTester {

    @Test
    public void test(){

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("op_login.json");
        BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream));

        MockMapping mockMapping = MappingParser.parse(reader);
        assertTrue(mockMapping instanceof RestMapping);
        RestMapping restMapping = (RestMapping) mockMapping;

        assertEquals(restMapping.getRestMethod(),"post");
        assertEquals(restMapping.getUrl(),"p1/login.json");
        assertEquals(restMapping.getRequestHeaders().get("osName"),"ios");

        String str = MappingSerializer.toString(restMapping);
        assert (str != null);

        MockMapping mockMapping2 = MappingParser.parse(new BufferedReader(new StringReader(str)));
        assertTrue(mockMapping2 instanceof RestMapping);
        RestMapping restMapping2 = (RestMapping) mockMapping2;
        assertEquals(restMapping2.getRestMethod(),"post");
        assertEquals(restMapping2.getUrl(),"p1/login.json");
        assertEquals(restMapping2.getRequestHeaders().get("osName"),"ios");
    }




}
