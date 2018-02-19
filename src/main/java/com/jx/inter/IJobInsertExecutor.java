package com.jx.inter;

import java.util.Map;

import com.jx.bean.JobInsert;

/*
 * 插入操作执行接口。
 * 插入操作执行类，需实现该接口，会被Job调度器进行调用。
 */
public interface IJobInsertExecutor {
	void insertExecutor(JobInsert jobInsert, Map<String, Object> params);
}
