package com.zk.dao;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.apache.log4j.Logger;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;

public class ZkHelper {
	
	private static ZooKeeper zkClient;
	private CountDownLatch connectedSignal = new CountDownLatch(1);
	private Logger logger = Logger.getLogger(ZkHelper.class);

	public ZooKeeper connect(String connectString,int sessionTimeout) throws IOException, InterruptedException {
		logger.info("zk客户端初始化中....");
		zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			public void process(WatchedEvent event) {
				logger.info("事件信息:" + event.getType() + ",路径" + event.getPath() + ",状态:" + event.getState());
				if (event.getState() == KeeperState.SyncConnected) { // 只有建立了联接才让主线程继续运行
					logger.info("zk客户端与服务器的联接建立成功....");
					connectedSignal.countDown();
				}				
			}
		});
		connectedSignal.await(); // 阻塞主线程
		return zkClient;
	}

	

	public void close() {
		logger.info("关闭zookeeper联接...");
		if (zkClient != null && zkClient.getState() == States.CONNECTED) {
			try {
				zkClient.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public ZkHelper() {
		super();
	}

	public ZkHelper(CountDownLatch connectedSignal, Logger logger) {
		super();
		this.connectedSignal = connectedSignal;
		this.logger = logger;
	}

}