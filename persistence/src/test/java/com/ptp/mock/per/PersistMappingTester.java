package com.ptp.mock.per;

import com.ptp.mock.per.FileMappingServiceImpl;
import com.ptp.mock.per.mapping.DubboMapping;
import com.ptp.mock.per.mapping.MappingParser;
import com.ptp.mock.per.mapping.MockMapping;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.junit.Assert.assertTrue;

/**
 * Created by WANGQIAODONG581 on 2016-06-08.
 */
public class PersistMappingTester {

    @Test
    public void test()
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("cip_query_cust_info_by_id.json");
        BufferedReader reader = new BufferedReader( new InputStreamReader(inputStream));

        MockMapping mockMapping = MappingParser.parse(reader);

        FileMappingServiceImpl persistMapping = new FileMappingServiceImpl("");
        File f= persistMapping.saveMapping((DubboMapping)mockMapping,"default");

        assertTrue(f != null);

        inputStream = classLoader.getResourceAsStream("op_login.json");
        reader = new BufferedReader( new InputStreamReader(inputStream));

        mockMapping = MappingParser.parse(reader);

        persistMapping = new FileMappingServiceImpl("");
        f= persistMapping.saveMapping(mockMapping,"default");

        assertTrue(f != null);


    }
}
