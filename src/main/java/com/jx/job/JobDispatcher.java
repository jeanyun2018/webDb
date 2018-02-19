package com.jx.job;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jx.inter.IJobDispatcher;
import com.jx.inter.IJobInfo;
import com.jx.inter.IJobInsertExecutor;
import com.jx.inter.IJobSelectExecutor;
import com.jx.symbol.SymbolRule;

import lombok.extern.slf4j.Slf4j;

/**
 * Job调度器。 通过IJobInfo接口提供的方法获取Job信息。 将获取的Job信息根据类型不同，交由不同的执行者去执行。
 * 
 * @author jx
 *
 */
@Component
@Slf4j
public class JobDispatcher implements IJobDispatcher {
	@Autowired
	private IJobSelectExecutor jobSelectExecutor;
	@Autowired
	private IJobInsertExecutor jobInsertExecutor;
	@Autowired
	private IJobInfo jobInfos;

	@Override
	public void dispatch(String name, String type, Map<String, Object> params) {
		String baseType = type.toLowerCase();
		switch (baseType) {
		case "select":
			log.info("尝试调度查询Job：{}", name);
			jobSelectExecutor.selectExecutor(jobInfos.getSelect(name), params);
			break;
		case "insert":
			log.info("尝试调度新增Job：{}", name);
			jobInsertExecutor.insertExecutor(jobInfos.getInsert(name), params);
			break;

		}

	}

	@Override
	public void dispatch(String next, Map<String, Object> params) {
		if (next.startsWith(SymbolRule.SELECT)) {
			next = next.replace(SymbolRule.SELECT, "");
			dispatch(next, SymbolRule.SELECT.replace(":", ""), params);
		} else if (next.startsWith(SymbolRule.INSERT)) {
			next = next.replace(SymbolRule.INSERT, "");
			dispatch(next, SymbolRule.INSERT.replace(":", ""), params);
		}
	}

}
