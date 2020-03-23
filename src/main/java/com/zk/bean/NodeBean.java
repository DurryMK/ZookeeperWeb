package com.zk.bean;

import java.io.Serializable;
import java.util.List;

public class NodeBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8650314133920428743L;
	
	private String text;
	private List<NodeBean> nodes;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public List<NodeBean> getNodes() {
		return nodes;
	}
	public void setNodes(List<NodeBean> nodes) {
		this.nodes = nodes;
	}
	@Override
	public String toString() {
		return "NodeBean [text=" + text + ", nodes=" + nodes + "]";
	}
	
}
