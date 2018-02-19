package com.jx.executors;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jx.bean.JobSelect;
import com.jx.bean.RuleSelect;
import com.jx.control.DBConnections;
import com.jx.inter.IJobSelectExecutor;
import com.jx.inter.IShowBackInfo;
import com.jx.job.JobDispatcher;
import com.jx.symbol.SymbolRule;
import com.jx.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 查询操作执行者
 * 
 * @author jx
 *
 */
@Component
@Slf4j
public class JobSelectExecutor implements IJobSelectExecutor {

	@Autowired
	private DBConnections dBConnections;
	private QueryRunner runner = new QueryRunner();
	@Autowired
	private JobDispatcher jobDispatcher;
	@Autowired
	private IShowBackInfo showBackInfo;

	@Override
	public void selectExecutor(JobSelect jobSelect, Map<String, Object> params) {
		// 为空校验
		if (jobSelect == null) {
			return;
		}
		jobSelect.setQuery(QueryUtil.preparationParameters(jobSelect.getQuery(), params));
		showBackInfo.show("查询语句：" + jobSelect.getQuery() + "，查询名称:" + jobSelect.getName());
		log.info("查询语句：{}，查询名称{}", jobSelect.getQuery(), jobSelect.getName());
		try {
			List<Map<String, Object>> results = runner.query(dBConnections.getMainConnection(), jobSelect.getQuery(),
					new MapListHandler());
			if (results.isEmpty()) {
				log.info("查询结果为空！查询名称：{}", jobSelect.getName());
				showBackInfo.show("查询结果为空："+jobSelect.getNone());
			} else {
				// 只匹配一次记录
				List<String> hasFirstRuleOnly = new ArrayList<String>();
				// 是否需要回显查询结果
				if (jobSelect.getShowRes()) {
					showBackInfo.show("查询结果："+results.toString());
				}
				for (Map<String, Object> res : results) {
					List<RuleSelect> rules = jobSelect.getRules();
					// 规则判空
					if (rules != null && !rules.isEmpty())
						for (RuleSelect rule : jobSelect.getRules()) {
							// 遍历规则
							String columname = rule.getColumnName();
							//如果有匹配条件，或者有mustDo属性则开始匹配
							if (StringUtils.hasText(rule.getRule()) || rule.isMustDo()) {
								// 如果该列为只匹配一次
								if (rule.isFirstOnly()) {
									// 假定该列尚未匹配
									boolean isHasAddFist = false;
									// 查询是否有匹配记录，匹配记录不为空则遍历当前所有记录
									if (!hasFirstRuleOnly.isEmpty())
										for (String firstColumnName : hasFirstRuleOnly) {
											// 如果该列名存在于只匹配列表中，则设置匹配标记为已经匹配
											if (firstColumnName.equals(rule.getColumnName())) {
												isHasAddFist = true;
											}
										}
									// 如果该列只匹配一次，并且尚未匹配
									if (!isHasAddFist) {
										// 尚持匹配该规则
										matchingRule(rule, (String) res.get(columname), res);
										// 并且将该列列名加入匹配记录中
										hasFirstRuleOnly.add(rule.getColumnName());
									}

								} else {
									// 如果没有只匹配一次的属性，则直接进行列名匹配
									matchingRule(rule, (String) res.get(columname), res);
								}

							}
							// 打印查询结果日志
							showColumn(columname, (String) res.get(columname));
						}
				}
			}

		} catch (SQLException e) {
			log.error("数据库异常！查询操作异常!", e);
		}
	}

	private void matchingRule(RuleSelect ruleSelect, String src, Map<String, Object> params) {
		String rule = ruleSelect.getRule();
		String next = ruleSelect.getNext();

		if (ruleSelect.isMustDo()) {
			showBackInfo.show("查询到有mustDo属性，开始进行下一个任务！");
			toCallNext(next, params);
			return;
		}
		log.info("尝试匹配{}是否符合规则：{} ", src, rule);
		showBackInfo.show("尝试匹配" + src + "是否符合规则：" + rule);
		// 大于
		if (rule.startsWith(SymbolRule.MORE_THAN)) {
			rule = rule.replace(SymbolRule.MORE_THAN, "");
			try {
				Double numBegin = Double.parseDouble(rule);
				Double numEnd = Double.parseDouble(src);
				if (numBegin > numEnd) {
					showMatchingRule(true);
					toCallNext(next, params);
				} else {
					showMatchingRule(false);
				}
			} catch (Exception e) {
				log.warn("类型转换失败！");
			}
		} // 等于
		else if (rule.startsWith(SymbolRule.EQUAL)) {
			rule = rule.replace(SymbolRule.EQUAL, "");
			if (StringUtils.hasText(src)) {
				if (src.equals(rule)) {
					showMatchingRule(true);
					toCallNext(next, params);
				} else {
					showMatchingRule(false);
				}
			}
		} // 小于
		else if (rule.startsWith(SymbolRule.LESS_THAN)) {
			rule = rule.replace(SymbolRule.LESS_THAN, "");
			try {
				Double numBegin = Double.parseDouble(rule);
				Double numEnd = Double.parseDouble(src);
				if (numBegin < numEnd) {
					showMatchingRule(true);
					toCallNext(next, params);
				} else {
					showMatchingRule(false);
				}
			} catch (Exception e) {
				log.warn("类型转换失败！");
			}
		}

	}

	private void showMatchingRule(boolean flag) {
		if (flag) {
			log.info("规则匹配成功！");
			showBackInfo.show("规则匹配成功！");
		} else {
			log.info("规则匹配失败！");
			showBackInfo.show("规则匹配失败！");
		}
	}

	private void toCallNext(String next, Map<String, Object> params) {
		if (StringUtils.hasText(next)) {
			jobDispatcher.dispatch(next, params);
		}
	}

	private void showColumn(String columnName, String data) {
		log.info("列名：{}    数据：{}", columnName, data);
	}
}
