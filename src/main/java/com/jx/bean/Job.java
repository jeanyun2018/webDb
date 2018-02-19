package com.jx.bean;

import lombok.Data;

/**
 * job实体类 增删改查操作基础类 XMLReader的解析粒度控制类，解析到job标签后，将job节点发送给IJobTagParser接口的实现
 * 
 * @author jx
 *
 */
@Data
public class Job {
	private String name;
	private String query;
	private String type;
}
