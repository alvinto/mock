package com.ptp.mock.per;

import com.ptp.mock.per.mapping.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WANGQIAODONG581 on 2016-05-16.
 */
@Service("mappingService")
public class FileMappingServiceImpl implements MappingService{
	Logger logger = LoggerFactory.getLogger(FileMappingServiceImpl.class);
    FileStoreIndexer keyIndexer;
    private String root;

    public FileMappingServiceImpl(String root){
        this.root = root;
        keyIndexer = new FileStoreIndexer(root);
    }

    public KeyIndexer getKeyIndexer() {
        return keyIndexer;
    }

    @Override
    public boolean put(DubboMapping dubboMapping,String username) {
        String key = dubboMapping.getMethod();
        boolean notExist = keyIndexer.putKey(key,MappingService.DUBBO_TYPE);
        saveMapping(dubboMapping,username);
        return notExist;
    }

    @Override
    public boolean put(RestMapping restMapping,String username) {
        String key = restMapping.getMethod();
        boolean notExist = keyIndexer.putKey(key,MappingService.REST_TYPE);
        saveMapping(restMapping,username);
        return notExist;
    }
    
    @Override
    public boolean update(MockMapping mockMapping,String oldUrl,String username){
    	boolean result = false;
    	if(MappingService.DUBBO_PROTOCOL.equals(mockMapping.getProtocol())){
    		DubboMapping dubboMapping = (DubboMapping)mockMapping;
    		String method = getDubboStorePath(dubboMapping.getMethod(), username);
        	//编辑时修改了facade或method，原配置需要删除
        	if(StringUtils.isNotEmpty(oldUrl) && hasExist(oldUrl,MappingService.DUBBO_TYPE,username) && !method.equals(oldUrl)){
            	DubboMapping dubbo = new DubboMapping();
            	dubbo.setMethod(oldUrl);
            	remove(dubbo,username);
        	}
        	result = put(dubboMapping,username);
    	}
    	if(MappingService.REST_PROTOCOL.equals(mockMapping.getProtocol())){
    		RestMapping restMapping = (RestMapping)mockMapping;
    		String path = restMapping.getRestMethod() + " " + restMapping.getUrl();
    		if(StringUtils.isNotEmpty(oldUrl) && hasExist(oldUrl,MappingService.REST_TYPE,username) && !path.equals(oldUrl)){
				RestMapping rest = new RestMapping();
				rest.setMethod(oldUrl);
				remove(rest,username);
    		}
        	result = put(restMapping,username);
    	}
    	
    	return result;
    }
    
    @Override
	public boolean remove(MockMapping mockMapping,String username) {
    	String key = mockMapping.getMethod();
    	int type = 0;
    	String path = "";
    	if(MappingService.REST_PROTOCOL.equals(mockMapping.getProtocol())){
    		type = MappingService.REST_TYPE;
    		path = getRestStorePath(key,username);
    	}
    	if(MappingService.DUBBO_PROTOCOL.equals(mockMapping.getProtocol())){
    		type = MappingService.DUBBO_TYPE;
    		path = getDubboStorePath(key,username);
    	}
		keyIndexer.removeKey(key,type);
    	if( path == null) return false;
        File file = new File(path);
        if(file.exists()){
    		file.getAbsoluteFile().delete();
        }
		return true;
	}
    
    @Override
	public boolean move(String key,boolean flag,int type,String username) {
    	boolean result = true;
    	String path = "";
    	if(MappingService.DUBBO_TYPE == type){
    		path = getDubboStorePath(key,username);
    	}
    	if(MappingService.REST_TYPE == type){
    		path = getRestStorePath(key,username);
    	}
    	if( path == null) return false;
    	moveFile(path,flag);
		return result;
	}
    
    private boolean moveFile(String path,boolean flag){
    	File file = new File(path);
        if(file.exists()){
        	if(flag){//备份
        		path = path + MappingService.BAK_FLAG;
        	}else{//恢复
        		path = path.replace(MappingService.BAK_FLAG, "");
        		File ff = new File(path);
        		if(ff.exists()){
        			ff.getAbsoluteFile().delete();
        		}
        	}
        }
		return file.renameTo(new File(path));
    }
    
    private String insertPrefix( String preFix, String filePath){
        if( !StringUtils.isEmpty(preFix)){
            return preFix + File.separator + filePath;
        }
        return filePath;
    }

    //从methodName 中提取路径
    private String getRestStorePath(String fullMethodName,String username){
        String[] paths = StringUtils.split(fullMethodName," ");
        String restMethod = paths[0] + "." + username;
        String fullPath = "";
        if( paths.length > 1){
        	fullPath = paths[1];
        	//处理 带参数 url
            fullPath = paths[1].replace("?", "/") + File.separator + restMethod;
        }else if( paths.length >0 ){
            fullPath = restMethod;
        }
        fullPath = insertPrefix("rest",fullPath);
        fullPath = insertPrefix(root,fullPath);
        return fullPath;
    }

    private String getDubboStorePath(String fullMethodName,String username)
    {
        String fullName= fullMethodName;
        String fullPath = StringUtils.replace(fullName, ".", File.separator);
    	fullPath = fullPath + "." + username;
        fullPath = insertPrefix("dubbo",fullPath);
        fullPath = insertPrefix(root,fullPath);
        return fullPath;
    }

    private String getFile(MockMapping mockMapping,String username)
    {
        if( mockMapping instanceof DubboMapping) {
            String methodName = mockMapping.getMethod();
            return getDubboStorePath(methodName,username);
        }
        if( mockMapping instanceof RestMapping){
            String methodName = mockMapping.getMethod();
            return getRestStorePath(methodName,username);
        }
        return null;
    }

    File saveMapping(MockMapping mockMapping,String username)
    {
        if( mockMapping == null) return null;
        String path = getFile(mockMapping,username);
        if( path == null) return null;

        File file = new File(path);
        File parentDir = new File(file.getParent());
        parentDir.mkdirs();
        RandomAccessFile fileOutputStream = null;
        try {
            fileOutputStream = new RandomAccessFile(file,"rw");
            if(file.exists()){
            	fileOutputStream.setLength(0);
            }
            String str = MappingSerializer.toString(mockMapping);
            fileOutputStream.write(str.getBytes("UTF-8"));
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            close(fileOutputStream);
        }
        return file;
    }

    //从文件中获取存储的mapping
    @Override
    public DubboMapping getDubboMapping(String clazzName, String methodName,String username){
        String fullMethodName= clazzName + "." + methodName;
        String fullPath = getDubboStorePath(fullMethodName,username);
        return (DubboMapping) getMockMapping(new File(fullPath));
    }

    @Override
    public RestMapping getRestMapping(String action, String url,String username)
    {
        String fullMethodName= action.toLowerCase() + " " + url;
        String fullPath = getRestStorePath(fullMethodName,username);
        File f = new File(fullPath);
        return (RestMapping) getMockMapping(f);
    }
    /**
     * 根据“请求方法 资源路径”查询RestMapping
     * @param path
     * @return
     */
    private RestMapping getRestMapping(String path,String username){
    	String fullPath = getRestStorePath(path,username);
        File f = new File(fullPath);
        return (RestMapping) getMockMapping(f);
    }

    @Override
    public List<String> query(String keyword,int type) {
        return keyIndexer.findKey(keyword,type);
    }

    private void close(Closeable closeable){
        if(closeable != null) {
            try {
                closeable.close();
            } catch (IOException var2) {

            }
        }
    }

    private void close(Reader reader){
        if( reader != null){
            try{
                reader.close();
            }catch (IOException e){

            }
        }
    }
    
    private MockMapping getMockMapping(File mappingFile){
        if(!mappingFile.exists()) return null;
        	
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(mappingFile)));
            return MappingParser.parse(br);
        }catch (FileNotFoundException e){
            throw new RuntimeException("mapping file " + mappingFile.getPath() + " not found");
        }finally {
            close(br);
        }
    }
    
    public MockMapping getMockMapping(InputStream buffer){
        return MappingParser.parse(new BufferedReader(new InputStreamReader(buffer)));
    }

	@Override
	public List<MockMapping> queryList(String keyword,int type,String username) {
		List<MockMapping> list = new ArrayList<MockMapping>();
		//rest
		if(MappingService.REST_TYPE == type){
			List<String> restList = new ArrayList<String>();
			restList.addAll(query(keyword,MappingService.REST_TYPE));
			for(String path : restList){
				if(getRestMapping(path,username) != null){
					list.add(getRestMapping(path,username));
				}
			}
		}else if(MappingService.DUBBO_TYPE == type){//dubbo
			List<String> dubboList = new ArrayList<String>();
			dubboList.addAll(query(keyword,MappingService.DUBBO_TYPE));
			for(String path : dubboList){
				DubboMapping dubbo = getDubboMapping(StringUtils.substringBeforeLast(path,"."),StringUtils.substringAfterLast(path,"."),username);
				if(dubbo != null){
					list.add(dubbo);
				}
			}
		}
		return list;
	}

	@Override
	public File getFile(String pathFlag, int type,String username) {
		String path = "";
		if(MappingService.DUBBO_TYPE ==  type) {
			path = getDubboStorePath(pathFlag,username);
        }
        if(MappingService.REST_TYPE ==  type){
        	path = getRestStorePath(pathFlag,username);
        }
        if(StringUtils.isNotEmpty(path)){
        	return new File(path);
        }
		return null;
	}

	@Override
	public boolean hasExist(String path,int type,String username){
		File file = getFile(path,type,username);
		return file.exists();
	}
}
