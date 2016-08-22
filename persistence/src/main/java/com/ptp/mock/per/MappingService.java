package com.ptp.mock.per;

import com.ptp.mock.per.mapping.DubboMapping;
import com.ptp.mock.per.mapping.MockMapping;
import com.ptp.mock.per.mapping.RestMapping;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Created by WANGQIAODONG581 on 2016-06-08.
 */
public interface MappingService{

    int DUBBO_TYPE = 0;
    int REST_TYPE=1;
    int ALL_TYPE=2;
    
    /**
     * 备份标志 在源文件名后加该标志表示备份文件
     */
    String BAK_FLAG = ".bak";
    /**
     * rest协议
     */
    String REST_PROTOCOL = "rest/1.1";
    /**
     * dubbo协议
     */
	String DUBBO_PROTOCOL = "dubbo/1.1";

    //return true if dubboMapping not exist
    boolean put(DubboMapping dubboMapping,String username);

    //return true if dubboMapping not exist
    boolean put(RestMapping restMapping,String username);
    /**
     * 更新文件
     * @param mockMapping
     * @return
     */
    boolean update(MockMapping mockMapping,String oldUrl,String username);

    DubboMapping getDubboMapping(String className, String methodName,String username);

    RestMapping getRestMapping(String action,String url,String username);

    //return full methodName contains the keyword
    List<String> query(String keyword,int type);
    /**
     * 查询所有的dubbo请求或rest请求
     * @param keyword
     * @param type
     * @return
     */
    List<MockMapping> queryList(String keyword,int type,String username);
    /**
     * 删除接口
     * @param mockMapping
     * @return
     */
    boolean remove(MockMapping mockMapping,String username);
    /**
     * 
     * @param path
     * @param type 0:dubbo;1:rest
     * @param flag true:备份为--bak；false：恢复为default
     * @return
     */
    boolean move(String path,boolean flag,int type,String username);
    
    File getFile(String path,int type,String username);
    
    /**
     * 输入流转化为MockMapping对象
     * @param buffer 输入流
     * @return
     */
    MockMapping getMockMapping(InputStream buffer);
    
    /**
     * 是否已存在配置
     * @param path 资源路径
     * @param type 0：dubbo；1：rest
     * @param username:用户名
     * @return
     */
    boolean hasExist(String path,int type,String username);
}
