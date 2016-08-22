package com.ptp.mock.per;

import java.util.List;

/**
 * Created by WANGQIAODONG581 on 2016-06-12.
 */
public interface KeyIndexer {

    void clear(int type);

    // return true if key not exist
    boolean putKey(String key,int type);

    // return true if key exist
    boolean removeKey(String key,int type);

    List<String> findKey(String word, int type);
}
