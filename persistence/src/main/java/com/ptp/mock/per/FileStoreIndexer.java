package com.ptp.mock.per;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by WANGQIAODONG581 on 2016-06-12.
 */
public class FileStoreIndexer implements KeyIndexer{
    private String root;
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();

    public FileStoreIndexer(){

    }

    public FileStoreIndexer(String root){
        this.root = root;
    }

    /**
     * 获取index文件目录
     * @param type 1：rest；2：dubbo
     * @return
     */
    private String getIndexFile(int type){
    	String fileName = MappingService.REST_TYPE == type ? "rest.index" : "dubbo.index";
        if(!StringUtils.isEmpty(root)){
            return root + File.separator + fileName;
        }
        else return fileName;
    }


    @Override
    public void clear(int type) {
        String f= getIndexFile(type);
        RandomAccessFile indexFile = null;
        try {
            writeLock.lock();
            indexFile =  new RandomAccessFile(f,"rw");
            indexFile.setLength(0);
        }catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            close(indexFile);
            writeLock.unlock();
        }
    }

    @SuppressWarnings("resource")
	@Override
    public boolean putKey(String key,int type) {
    	if(hasKey(key,type)){
    		return false;
    	}
        String f= getIndexFile(type);
        RandomAccessFile indexFile = null;
        List<Pos> poses = new ArrayList<Pos>();
        try {
            writeLock.lock();
            indexFile = new RandomAccessFile(f,"rw");
            for(;;) {
                String line = indexFile.readLine();
                if (StringUtils.startsWith(line, "#") && line.length() >= key.length()) {
                    long fp = indexFile.getFilePointer();
                    poses.add(new Pos(fp - line.length()-1, line.length()-key.length()));
                    break;
                }else if( StringUtils.equals(line,key)){
                	return false;
                }
                else if( line == null){ //end file
                    break;
                }
            }
            //todo getBest Pos
            Pos pos = getBestPos(poses);
            if( pos == null) {
                indexFile.seek(indexFile.length());
                indexFile.writeBytes(key + "\n");
            }else {
                indexFile.seek(pos.pointer);
                indexFile.writeBytes(key+"\n");
            }
            return true;
        }catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            close(indexFile);
            writeLock.unlock();
        }
    }

    private boolean hasKey(String key,int type){
    	List<String> list = findKey(key, type);
    	for(String s : list){
    		if(s.equals(key)){
    			return true;
    		}
    	}
    	return false;
    }
    //todo
    private Pos getBestPos(List<Pos> poses) {
        if( poses != null && poses.size() >0)
            return poses.get(0);
        return null;
    }

    @SuppressWarnings("resource")
	@Override
    public boolean removeKey(String key,int type) {
        String f= getIndexFile(type);
        RandomAccessFile indexFile = null;
        try {
            writeLock.lock();
            indexFile = new RandomAccessFile(f,"rw");
            //comment an key
            for (; ; ) {
                String line = indexFile.readLine();
                if( line == null) return false;
                if (StringUtils.equals(key,line)) {
                    long fp = indexFile.getFilePointer();
                    indexFile.seek(fp - line.length()-1);
                    indexFile.writeBytes(StringUtils.repeat("#",key.length()));
                    return true;
                }
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            close(indexFile);
            writeLock.unlock();
        }
    }

    @SuppressWarnings("finally")
	@Override
    public List<String> findKey(String word, int type) {
        String f= getIndexFile(type);
        List<String> keys = new ArrayList<String>();
        RandomAccessFile indexFile = null;
        try {
            readLock.lock();
            indexFile = new RandomAccessFile(f,"rw");
            for (; ; ) {
                String line = indexFile.readLine();
                if (line == null) break;
            	if (StringUtils.contains(line.toLowerCase(), word.toLowerCase()) && !StringUtils.startsWith(line,"#")) {
            		keys.add(line);
            	}
            }
        }catch (IOException e){
            throw new RuntimeException(e);
        }finally {
            close(indexFile);
            readLock.unlock();
            return keys;
        }
    }

    protected void close(Closeable closeable){
        if( closeable != null){
            try {
                closeable.close();
            }catch (IOException e){

            }
        }
    }

    private static class Pos {
        long pointer; //位置指针
		int length; //位置长度

        public Pos(long l, int length) {
            this.pointer = l;
            this.length = length;
        }
    }
}
