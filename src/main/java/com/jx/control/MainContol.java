package com.jx.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jx.inter.IJobInfo;
import com.jx.job.JobDispatcher;

/**
 * 主流程控制类
 * 
 * @author jx
 *
 */
@Component
public class MainContol {
	@Autowired
	private XMLReader xmlReader;
	@Autowired
	private IJobInfo jobInfo;
	@Autowired
	private DBConnections dBConnections;
	@Autowired
	private JobDispatcher jobDispatcher;

	public void startWork(String configPath) {
		// 文件路径方式初始化
		xmlReader.init(configPath);
		beginWork();
	}

	public void startHtml(String html) {
		// 字符串方式初始化
		xmlReader.initHtml(html);
		beginWork();
	}
	

	private void beginWork() {
		jobInfo.clear();
		// 解析dom文件
		xmlReader.read();
		// 将解析的数据库信息连接数据库
		dBConnections.addConnection(xmlReader.getConnect());
		// 从第一个任务开始调度
		jobDispatcher.dispatch(xmlReader.getFirstJob().getName(), xmlReader.getFirstJob().getType(), null);
	}

}
