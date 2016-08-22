package com.pinganfu.mockall.web.controller;

import com.alibaba.fastjson.JSON;
import com.pinganfu.mockall.web.util.RequestUtil;
import com.ptp.mock.per.MappingService;
import com.ptp.mock.per.mapping.DubboMapping;
import com.ptp.mock.per.mapping.MockMapping;
import com.ptp.mock.per.mapping.RestMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author: kevin.
 * @date: 2016/5/27.
 * @package: com.pinganfu.mockall.web.controller.
 * @version: 1.0.0.
 * @description: 约定 /conf 请求为添加、查询接口请求；/rest、/dubbo为外部调用请求
 */
@Controller
public class IndexController {
	Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private MappingService mappingService;

    @RequestMapping(method = RequestMethod.GET,value = "/conf")
    public String index(){
        return "index";
    }
    
//    @ResponseBody
//    @RequestMapping(method = RequestMethod.POST,value = "/conf/login")
//    public ResultData login(HttpSession session,@RequestBody String username){
//    	session.setAttribute("username", username);
//    	return ResultData.returnResult("ok");
//    }
    
//    @ResponseBody
//    @RequestMapping(method = RequestMethod.GET,value = "/conf/lgout")
//    public ResultData loginOut(HttpSession session,@RequestBody String username){
//    	session.setAttribute("username", null);
//    	return ResultData.returnResult("ok");
//    }
    
    /**
     * 新增 或者 修改一个dubbo接口配置     *
     * @param params
     * methodName  方法名称
     * className  类全名称
     * requestData 请求数据
     * responseData 返回数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value="/conf/put_dubbo")
    public ResultData putDubbo (@RequestBody Map<String,Object> params){
    	DubboMapping dubboMapping = (DubboMapping)RequestUtil.parse(params, MappingService.DUBBO_TYPE);
    	String oldUrl = (String)params.get("oldUrl");
        mappingService.update(dubboMapping,oldUrl,(String)params.get("username"));
        return ResultData.returnResult("ok");
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT,value="/dubbo/{username}/{facade}/{methodName}")
    public ResultData editDubbo (@RequestBody Map<String,String> params,@PathVariable String username,@PathVariable String facade,@PathVariable String methodName){
		String method = facade +"."+ methodName;
		String res = JSON.toJSONString(params);
		DubboMapping dubboMapping = new DubboMapping();
		dubboMapping.setMethod(method);
		dubboMapping.setResponse(res);
		mappingService.update(dubboMapping,null,username);
        return ResultData.returnResult("ok");
    }
    
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT,value="/dubbo/{facade}/{methodName}")
    public ResultData editDubboDefault (@RequestBody Map<String,String> params,@PathVariable String facade,@PathVariable String methodName){
		String method = facade +"."+ methodName;
		String res = JSON.toJSONString(params);
		DubboMapping dubboMapping = new DubboMapping();
		dubboMapping.setMethod(method);
		dubboMapping.setResponse(res);
		mappingService.update(dubboMapping,null,"default");
        return ResultData.returnResult("ok");
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.POST,value="/conf/remove_dubbo")
    public ResultData removeDubbo(@RequestBody Map<String,Object> params){
    	DubboMapping dubboMapping = new DubboMapping();
    	dubboMapping.setMethod(params.get("facadeName") + "." + params.get("methodName"));
    	boolean result = mappingService.remove(dubboMapping,(String)params.get("username"));//默认为default 登录功能添加后，此处需要获取session中用户信息
        return ResultData.returnResult(result ? "ok" : "error");
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE,value="/dubbo/{username}/{facadeClassName}/{methodName}")
    public ResultData deleteDubbo(@PathVariable String username,@PathVariable String facadeClassName,@PathVariable String methodName){
		DubboMapping dubboMapping = mappingService.getDubboMapping(facadeClassName, methodName,username);
		mappingService.remove(dubboMapping, username);
    	return ResultData.returnResult("删除成功");
    }
    
    
    @ResponseBody
    @RequestMapping(method=RequestMethod.POST ,value="/dubbo/{username}/{facadeClassName}/{methodName}")
    public String dubboMock(HttpServletResponse response,@PathVariable String username,@PathVariable String facadeClassName,@PathVariable String methodName){
		DubboMapping dubboMapping = mappingService.getDubboMapping(facadeClassName, methodName,username);
		if(dubboMapping == null){
			dubboMapping = mappingService.getDubboMapping(facadeClassName, methodName,"default");
			if(dubboMapping == null){
				response.setStatus(404);
				return null;
			}
		}
		return dubboMapping.getResponse();
    }
    
    @ResponseBody
    @RequestMapping(method=RequestMethod.POST ,value="/dubbo/{facadeClassName}/{methodName}")
    public String dubboMockDefault(HttpServletResponse response,@PathVariable String facadeClassName,@PathVariable String methodName){
		DubboMapping dubboMapping = mappingService.getDubboMapping(facadeClassName, methodName,"default");
		if(dubboMapping == null){
			response.setStatus(404);
			return null;
		}
		return dubboMapping.getResponse();
    }
    
    /**
     * 新增 或者 修改一个rest接口配置     *
     * @param params
     * requestHeaders  请求头
     * restMethod  请求类型
     * url 资源路径
     * responseHeaders 响应头
     * response 返回数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value="/conf/put_rest")
    public ResultData putRest (@RequestBody Map<String,Object> params){
        String oldUrl = params.get("oldUrl") == null ? "" : (String)params.get("oldUrl");
        RestMapping restMapping = (RestMapping)RequestUtil.parse(params, MappingService.REST_TYPE);
        mappingService.update(restMapping,oldUrl,(String)params.get("username"));//默认为default 登录功能添加后，此处需要获取session中用户信息
        return ResultData.returnResult("ok");
    }
    
    /**
     * 
     * mock-web/rest/{username}/methodName/resourceName
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.PUT,value="/rest/**")
    public ResultData editRest (HttpServletRequest request,@RequestBody Map<String,String> params){
    	Map<String,String> map = RequestUtil.parseUrl(true, request);
    	String method =map.get("methodName") +" "+ map.get("resourceName");
		RestMapping restMapping = new RestMapping();
		restMapping.setMethod(method);
		String requestHeaders = (String) params.get("requestHeaders");
        String req = (String)params.get("request");
        String res = (String)params.get("response");
        String responseHeaders = (String)params.get("responseHeaders");
		restMapping.setResponse(res);
		restMapping.setRequest(req);
		restMapping.setRequestHeaders(requestHeaders);
		restMapping.setResponseHeaders(responseHeaders);
		mappingService.update(restMapping,null,map.get("username"));
        return ResultData.returnResult("ok");
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.POST,value="/conf/remove_rest")
    public ResultData removeRest(@RequestBody Map<String,Object> params){
    	RestMapping restMapping = new RestMapping();
        restMapping.setMethod(params.get("restMethod") + " " + params.get("url"));
    	boolean result = mappingService.remove(restMapping,(String)params.get("username"));//默认为default 登录功能添加后，此处需要获取session中用户信息
        return ResultData.returnResult(result ? "ok" : "error");
    }
    
    @ResponseBody
    @RequestMapping(method= RequestMethod.DELETE,value="/rest/**")
    public ResultData deleteRest(HttpServletRequest request){
    	Map<String,String> map = RequestUtil.parseUrl(true, request);
		RestMapping restMapping = mappingService.getRestMapping(map.get("methodName"), map.get("resourceName"),map.get("username"));
		mappingService.remove(restMapping, map.get("username"));
    	return ResultData.returnResult("删除成功");
    }
    /**
     * rest mock
     * 请求格式是 “/mock/username/resourceName”
     */
    @ResponseBody
    @RequestMapping(value="/mock/**")
    public String restMock(HttpServletRequest request,HttpServletResponse response){
    	Map<String,String> map = RequestUtil.parseUrl(false, request);
    	String methodName = request.getMethod();
    	String userName = map.get("username");
        RestMapping restMapping = mappingService.getRestMapping(methodName, map.get("resourceName"),userName);
        if(restMapping == null){
        	if(!"default".equals(userName)){
        		restMapping = mappingService.getRestMapping(methodName, map.get("resourceName"),"default");
        		if(restMapping == null){
        			return "error,未找到对应的数据！";
        		}
        	}else{
        		return "error,未找到对应的数据！";
        	}
        }
        Map<String,String> headerMap = restMapping.getResponseHeaders();
        for(Map.Entry<String, String> entry : headerMap.entrySet()){
        	response.addHeader(entry.getKey(), entry.getValue());
        }
        return restMapping.getResponse();
    }
    
    /**
     * 查找dubbo接口,根据方法名称     *
     * @param params 包含的列表
     * name: 包含此关键字的数据, 为空时查询所有数据
     * pageSize:  页面大小
     * pageNum:  页面号, 从1开始
     * @return
     */
    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value="/conf/query_list_by_type")
    public PageData<ConfigItem> findMock(@RequestBody Map<String,String> params){
        String keyword = params.get("keyword");
        PageData<ConfigItem> pageData = new PageData<ConfigItem>();
        keyword = keyword == null ? "" : keyword;
        int pageSize = 10;
        int pageNum = 1;
        try{
        	pageNum = Integer.parseInt(params.get("pageNum"));
            pageSize = Integer.parseInt(params.get("pageSize"));
        }catch (NumberFormatException e){}

        int type = Integer.parseInt(params.get("type"));
        List<MockMapping> lst = mappingService.queryList(keyword,Integer.parseInt(params.get("type")),(String)params.get("username"));//默认为default 登录功能添加后，此处需要获取session中用户信息
        pageData.setTotalCnt(lst.size());
    	pageData.setPageSize(pageSize);
    	pageData.setPageNum(pageNum);
    	List<MockMapping> list = lst.subList(pageSize*(pageNum-1), (pageSize*pageNum)>lst.size() ? lst.size() : (pageSize*pageNum));
    	List<ConfigItem> items = new ArrayList<ConfigItem>();
        if(type == MappingService.DUBBO_TYPE){
        	for(MockMapping dubboMapping:list){
        		DubboMapping dubbo = (DubboMapping)dubboMapping;
        		if(dubbo != null){
        			DubboConfigDetail item = new DubboConfigDetail();
        			item.setMethodName(dubbo.getMethodName());
        			item.setFacadeName(dubbo.getFacadeName());
        			item.setRequest(dubbo.getRequest());
        			item.setResponse(dubbo.getResponse());
        			items.add(item);
        		}
        	}
        }else if(type == MappingService.REST_TYPE){
        	
        	for(MockMapping restMapping:list){
        		RestMapping rest = (RestMapping)restMapping;
        		RestConfigDetail item = new RestConfigDetail();
        		if(rest != null){
        			item.setRequest(rest.getRequest());
        			item.setRequestHeaders(JSON.toJSONString(rest.getRequestHeaders()));
        			item.setResponse(rest.getResponse());
        			item.setResponseHeaders(JSON.toJSONString(rest.getResponseHeaders()));
        			item.setRestMethod(rest.getRestMethod());
        			item.setUrl(rest.getUrl());
        			items.add(item);
        		}
        	}
        }
        pageData.setData(items);
        return pageData;
    }
    
    
    @RequestMapping(method = RequestMethod.POST,value="/conf/download")
    public ResponseEntity<byte[]> download(@RequestBody Map<String,Object> params,HttpSession session){
    	int type = (Integer)params.get("type");
    	String path = "";
    	if(MappingService.REST_TYPE == type){
    		String restMethod = (String)params.get("restMethod");
            String url = (String)params.get("url");
            path = restMethod + " " + url;
    	}else if(MappingService.DUBBO_TYPE == type){
    		String className = (String) params.get("facadeName");
            String methodName = (String) params.get("methodName");
            path = className + "." + methodName;
    	}
    	File file = mappingService.getFile(path,type,(String)session.getAttribute("username"));//默认为default 登录功能添加后，此处需要获取session中用户信息
    	
    	HttpHeaders headers = new HttpHeaders(); 
    	headers.setContentType(MediaType.APPLICATION_OCTET_STREAM); 
    	headers.setContentDispositionFormData("attachment",path.replace(" ", "~~"));
    	return new ResponseEntity<byte[]>(RequestUtil.getBytesFromFile(file),headers,HttpStatus.CREATED);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST,value="/conf/upload")
    public ResultData uploadFile(@RequestParam("file") MultipartFile file,HttpServletRequest request,HttpSession session){
    	if(!file.isEmpty()){
    		try {
				MockMapping mockMapping = mappingService.getMockMapping(file.getInputStream());
				String protocol = mockMapping.getProtocol();
				if(MappingService.DUBBO_PROTOCOL.equals(protocol)){
					mappingService.update((DubboMapping)mockMapping,null,(String)session.getAttribute("username"));//默认为default 登录功能添加后，此处需要获取session中用户信息
				}
				if(MappingService.REST_PROTOCOL.equals(protocol)){
					
				}
			} catch (IOException e) {
				logger.error("upload file failure",e);
			}
    	}
    	return ResultData.returnResult("ok");
    }
}
