package com.jx.job;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jx.bean.JobInsert;
import com.jx.bean.JobSelect;
import com.jx.inter.IJobInfo;

/**
 * 负责存储JobParser解析到的Job信息。 并且对外提供获取和存储操作功能。
 * 
 * @author jx
 *
 */
@Component
public class JobInfos implements IJobInfo {
	private Map<String, JobSelect> jobSelects = new HashMap<String, JobSelect>(10);
	private Map<String, JobInsert> jobInserts = new HashMap<String, JobInsert>(10);

	@Override
	public JobSelect getSelect(String jobName) {
		return jobSelects.get(jobName);
	}

	@Override
	public JobInsert getInsert(String jobName) {
		return jobInserts.get(jobName);
	}

	@Override
	public void addJobSelect(JobSelect job) {
		jobSelects.put(job.getName(), job);
	}

	@Override
	public void addJobInsert(JobInsert job) {
		jobInserts.put(job.getName(), job);
	}

	@Override
	public void clear() {
		if (!jobSelects.isEmpty()) {
			jobSelects.clear();
		}
		if (!jobInserts.isEmpty()) {
			jobInserts.clear();
		}
	}

}
