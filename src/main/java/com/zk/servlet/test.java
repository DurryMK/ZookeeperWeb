package com.zk.servlet;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.ZooKeeper;

public class test {
	private static String str="";
	public static void main(String[] args) {
		
		
		String s = "asdasda";
		
		String[] ss = s.split("c");
		
		System.out.println(ss[1]);
		}
	public void getChild(ZooKeeper zkCli,  String path) throws Exception {
        
        List<String> list =new ArrayList<String>();
        try {
			list=zkCli.getChildren(path,true);
		} catch (Exception e) {
			list=null;
		}
        //判断是否有子节点
        if(list == null){
            return;
        }
        if(list.isEmpty()){
        	return;
        }
        for(String s : list){
            //判断是否为根目录
            if(path.equals("/")){
            	getChild(zkCli,path + s);
            }else {
            	getChild(zkCli,path +"/" + s);
            }
        }
    }
}
