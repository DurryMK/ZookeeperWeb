package com.zk.listeners;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;

import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.apache.zookeeper.ZooKeeper;

/**
 * Application Lifecycle Listener implementation class ZkListener
 *
 */
@WebListener
public class ZkListener implements HttpSessionListener {
	
	private Logger logger = Logger.getLogger(ZkListener.class);
	
    public ZkListener() {
    }

    public void sessionCreated(HttpSessionEvent se)  {
    	
    }

    public void sessionDestroyed(HttpSessionEvent se)  { 
         
         ZooKeeper zkClis = (ZooKeeper) se.getSession().getAttribute("zkCli");
         
         if(zkClis!=null&&zkClis.getState()==ZooKeeper.States.CONNECTED){
        	 try {
     			zkClis.close();
     			logger.info("ZKÁ¬½Ó¹Ø±Õ.....");
     		} catch (InterruptedException e) {
     			e.printStackTrace();
     		}
         }
    }
	
}
