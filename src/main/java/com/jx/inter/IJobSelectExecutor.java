package com.jx.inter;

import java.util.Map;

import com.jx.bean.JobSelect;

/**
 * 查询操作执行接口。 由任务调度器来进行调度。
 * 
 * @author jx
 *
 */
public interface IJobSelectExecutor {
	void selectExecutor(JobSelect jobSelect, Map<String, Object> params);
}
