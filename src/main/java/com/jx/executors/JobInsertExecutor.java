package com.jx.executors;

import java.sql.SQLException;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jx.bean.JobInsert;
import com.jx.control.DBConnections;
import com.jx.inter.IJobInsertExecutor;
import com.jx.inter.IShowBackInfo;
import com.jx.job.JobDispatcher;
import com.jx.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 插入操作执行者
 * 
 * @author jx
 *
 */
@Component
@Slf4j
public class JobInsertExecutor implements IJobInsertExecutor {

	@Autowired
	private DBConnections dBConnections;
	QueryRunner runner = new QueryRunner();
	@Autowired
	private IShowBackInfo showBackInfo;
	@Autowired
	private JobDispatcher jobDispatcher;

	@Override
	public void insertExecutor(JobInsert jobInsert, Map<String, Object> params) {
		// 为空校验
		if (jobInsert == null) {
			return;
		}
		String insertQuery = QueryUtil.preparationParameters(jobInsert.getQuery(), params);
		Object[] args = QueryUtil.preparationParametersList(insertQuery, params);
		showBackInfo.show("执行新增语句：" + jobInsert.getName() + "SQL:" + insertQuery);
		log.info("执行新增语句： {}，参数：{}", insertQuery, args);
		try {
			int res = runner.update(dBConnections.getMainConnection(), insertQuery, args);
			if (res > 0) {
				showInfoRes(true, jobInsert);
				toCallNext(jobInsert.getNext());
			} else {
				showInfoRes(false, jobInsert);
			}
		} catch (SQLException e) {
			log.error("数据库异常！", e);
			showBackInfo.show(jobInsert.getFail());
		}
	}

	private void toCallNext(String next) {
		if (StringUtils.hasText(next)) {
			jobDispatcher.dispatch(next, null);
		}
	}

	private void showInfoRes(boolean flag, JobInsert insert) {
		if (flag) {
			showBackInfo.show(insert.getSuccess());
			log.info("新增数据成功！{}", insert.getSuccess());
		} else {
			showBackInfo.show(insert.getFail());
			log.info("新增数据失败！{}", insert.getFail());
		}
	}
}
