package com.zk.servlet;

import java.io.IOException;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.google.gson.Gson;
import com.zk.dao.ZkHelper;


public class ZkServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ZkHelper zk = new ZkHelper();
	private static String connectString="";
	private static String time="";
	private static String theme="blue";
	private Logger logger = Logger.getLogger(ZkServlet.class);
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String op = request.getParameter("op");
		
		try {
			if("login".equals(op)){
				login(request,response);
			}else if("getTree".equals(op)){
				getTreeOp(request,response);
			}else if("getNodeInfo".equals(op)){
				getNodeInfo(request,response);
			}else if("getTheme".equals(op)){
				getTheme(request,response);
			}else if("setTheme".equals(op)){
				setTheme(request,response);
			}
			
		} catch (Exception e) {
			response.sendRedirect("500.html");
			logger.info("表单提交错误");
			e.printStackTrace();
		}
	}

	private void setTheme(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String theme = request.getParameter("theme");
		this.theme=theme;
		Map<String,String> m = new HashMap<String,String>();
		m.put("theme", "1");
		response.getWriter().append(new Gson().toJson(m));
	}

	private void getTheme(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		Map<String,String> m = new HashMap<String,String>();
		
		m.put("theme", theme);
		
		response.getWriter().append(new Gson().toJson(m));
		
	}

	private ZooKeeper getZK(HttpServletRequest request,HttpServletResponse response) throws IOException {
		ZooKeeper zkCli = (ZooKeeper) request.getSession().getAttribute("zkCli");
		
		if(zkCli==null||zkCli.getState()!=ZooKeeper.States.CONNECTED){
			try {
				ZooKeeper newZK =  zk.connect(connectString,Integer.parseInt(time));
				request.getSession().setAttribute("zkCli", newZK);
				return newZK;
			} catch (Exception e) {
				e.printStackTrace();
				logger.info("尝试重连ZK时出错.....");
				logger.info("连接名:"+connectString);
				logger.info("时间参数:"+time);
			}
		}
		return zkCli;
	}
	private void getNodeInfo(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String path = request.getParameter("path");
		ZooKeeper zkCli = getZK(request, response);
		DateFormat df  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Stat stat = new Stat();
		try {
			byte[] bytes = zkCli.getData(path, false, stat); 
			Map<String,String> node = new LinkedHashMap<String,String>();
			String strs="null";
			if(bytes!=null){
				strs = new String(bytes,"utf-8");
			}
			node.put("nodeContent", strs);
			node.put("czxid", stat.getCzxid()+"");
			node.put("ctime", df.format(new Timestamp(stat.getCtime())));
			node.put("mzxid", stat.getMzxid()+"");
			node.put("mtime", df.format(new Timestamp(stat.getMtime())));
			node.put("pzxid", stat.getPzxid()+"");
			node.put("cversion", stat.getCversion()+"");
			node.put("dataVersion", stat.getVersion()+"");
			node.put("aclVersion", stat.getAversion()+"");
			if(stat.getEphemeralOwner()==0){
				node.put("nodeType", "持久节点");
			}else{
				node.put("nodeType", "临时节点");
				node.put("clientId", stat.getEphemeralOwner()+"");
			}
			node.put("dataLongth", stat.getDataLength()+"");
			node.put("children", stat.getNumChildren()+"");
			response.getWriter().append(new Gson().toJson(node));
		} catch (Exception e) {
			logger.info("无法查询节点"+path+"的信息");
			Map<String,String> node = new LinkedHashMap<String,String>();
			response.getWriter().append(new Gson().toJson(node));
		}
		
	}

	private void getTreeOp(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = request.getParameter("path");
		
		ZooKeeper zkCli = getZK(request, response);
		
		List<Map<String,Object>> nodes = getNodes(zkCli, path);
		
		response.getWriter().append(new Gson().toJson(nodes));
	}
	public List<Map<String,Object>> getNodes(ZooKeeper zkCli, String path) throws Exception {
		List<Map<String,Object>> nodes = new ArrayList<Map<String,Object>>();
		if(!getChild(zkCli,path)){
			return nodes;
		}
        List<String> list = zkCli.getChildren(path,true);
        String realpath = path;
        for(String s : list){
            Map<String,Object> m = new LinkedHashMap<String,Object>();
            m.put("text", s+"");
            if(path.equals("/")){
            	path = "/"+s;
            }else{
            	path = path+"/"+s;
            }
            if(getChild(zkCli,path)){
            	m.put("nodes", getNodes(zkCli, path));
            }
            path=realpath;
            nodes.add(m);
        }
        return nodes;
    }
	
	public boolean getChild(ZooKeeper zkCli,  String path) throws Exception {
		List<String> list =new ArrayList<String>();
        try {
			list=zkCli.getChildren(path,true);
		} catch (Exception e) {
			list=null;
		}
        //判断是否有子节点
        if(list == null){
            return false;
        }
        if(list.isEmpty()){
        	return false;
        }
        return true;
	}
	
	private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException, ServletException {
		connectString = request.getParameter("connectString");
		time = request.getParameter("time");
		
		try {
			logger.info("进入登录程序...开始连接zookeeper");
			ZooKeeper zkCli =  zk.connect(connectString,Integer.parseInt(time));
			request.getSession().setAttribute("zkCli", zkCli);
			response.sendRedirect("back/show.html");
		} catch (Exception e) {
			response.sendRedirect("500.html");
			logger.info("连接异常");
			logger.info(e.getMessage());
			e.printStackTrace();
		}
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
