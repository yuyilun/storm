package com.ibeifeng.hadoop.senior.hdfs;

import java.io.File;
import java.io.FileInputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class HdfsApp {
	
	
	public static FileSystem getFileSystem()throws Exception {
		//core-site.xml core-default.xml hdfs-site.xml hdfs-default.xml
		Configuration config = new Configuration();
		FileSystem fileSystem = FileSystem.get(config);
		return fileSystem;
	}
	
	public static void read(String fileName) throws Exception {
		FileSystem fileSystem = getFileSystem();
		System.out.println(fileSystem);
		
		//String fileName = "/user/beifeng/mapreduce/wordcount/input/wc.input";
		Path path = new Path(fileName);
		FSDataInputStream inputStream = fileSystem.open(path);
		
		try{
			IOUtils.copyBytes(inputStream, System.out, 4096, false);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IOUtils.closeStream(inputStream);
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {
		
		//String fileName = "/user/beifeng/mapreduce/wordcount/input/wc.input";
		//read(fileName);
		
		FileInputStream inputStream = new FileInputStream(new File("/opt/modules/hadoop-2.5.0/wc.input"));
		
		FileSystem fileSystem = getFileSystem();
		String fileName = "/user/beifeng/mapreduce/wordcount/input/wc2.input";
		Path path = new Path(fileName);
		FSDataOutputStream outputStream = fileSystem.create(path);
		
		try{
			IOUtils.copyBytes(inputStream, outputStream, 4096, false);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			IOUtils.closeStream(inputStream);
			IOUtils.closeStream(outputStream);
		}
		
		
		
	}

}
