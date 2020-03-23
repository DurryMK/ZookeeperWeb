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
		logger.info("zk�ͻ��˳�ʼ����....");
		zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
			public void process(WatchedEvent event) {
				logger.info("�¼���Ϣ:" + event.getType() + ",·��" + event.getPath() + ",״̬:" + event.getState());
				if (event.getState() == KeeperState.SyncConnected) { // ֻ�н��������Ӳ������̼߳�������
					logger.info("zk�ͻ���������������ӽ����ɹ�....");
					connectedSignal.countDown();
				}				
			}
		});
		connectedSignal.await(); // �������߳�
		return zkClient;
	}

	

	public void close() {
		logger.info("�ر�zookeeper����...");
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