package com.ptp.mock.per.mapping;

import com.ptp.mock.per.FileStoreIndexer;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by WANGQIAODONG581 on 2016-06-12.
 */
public class FileStoreIndexerTester {

    @Test
    public void test(){
        FileStoreIndexer store = new FileStoreIndexer("");
        store.clear(0);
        store.putKey("com.pinganfu.cip.QueryCust",0);
        store.putKey("com.pinganfu.cip.QueryInfo",0);
        store.putKey("post /p2/query",1);
        store.putKey("get /p1/login.json",1);
        store.removeKey("post /p2/query",1);
        store.putKey("post /p2/query/bug",1);
        store.removeKey("post /p2/query",1);

        List<String> keys = store.findKey("query",2);
        assertTrue(keys.size() ==1 );
        assertEquals(keys.get(0),"post /p2/query/bug");
    }

    @Test
    public void testStr(){
        assertTrue(StringUtils.contains("abcdadsf", ""));
    }
}
