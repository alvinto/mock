package com.pinganfu.mock.calc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WANGQIAODONG581 on 2016-05-16.
 */
public class CalcCenter {


    ExecutorService executorService = Executors.newSingleThreadExecutor();

    // submit calc task
    public CalcResult submit(CalcTask calcTask){
        assert ( calcTask != null);

        if( calcTask instanceof MultiTask){


        }

        else {
            executorService.submit(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return null;
                }
            });
        }
        return null;
    }

    private class CallableTask implements Callable {

        private CalcTask calcTask;

        @Override
        public Object call() throws Exception {
            return null;
        }

    }

}
