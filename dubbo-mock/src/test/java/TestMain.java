import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by wangqiaodong581 on 2016-08-04.
 */
public class TestMain {

    static public void  main(String[] args) throws UnsupportedEncodingException {
        String t = URLEncoder.encode("http://www.baidu.com/ads%g","UTF-8");



        System.out.println(t);
    }
}
