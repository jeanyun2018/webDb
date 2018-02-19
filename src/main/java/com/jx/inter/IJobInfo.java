package com.jx.inter;

import com.jx.bean.JobInsert;
import com.jx.bean.JobSelect;

/**
 * JobInfo管理接口 实现该接口，负责将解析出的各种标签进行存储。 并且提供根据Job名称，获取Job信息的功能。
 * 
 * @author jx
 *
 */
public interface IJobInfo {
	void addJobSelect(JobSelect job);

	void addJobInsert(JobInsert job);

	JobSelect getSelect(String jobName);

	JobInsert getInsert(String jobName);
	
	void clear();
}
