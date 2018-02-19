package com.jx.job;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.jx.bean.Job;
import com.jx.bean.JobInsert;
import com.jx.bean.JobSelect;
import com.jx.bean.RuleSelect;
import com.jx.inter.IJobInfo;
import com.jx.inter.IJobTagParser;

/**
 * 负责将Job节点继续解析。 并且将解析到的结果存入IJobInfo中。
 * 
 * @author jx
 *
 */
@Component
public class JobParser implements IJobTagParser {
	@Autowired
	private IJobInfo jobInfo;

	@Override
	public void parse(Element element, Job job) {
		String jobType = job.getType().toLowerCase();
		switch (jobType) {
		case "select":
			putSelectInfo(element, job);
			break;
		case "insert":
			putInsertInfo(element, job);
			break;
		}

	}

	private String tryToGetTagText(Element e, String tagName) {
		if (e.getElementsByTag(tagName).size() > 0) {
			String text = e.getElementsByTag(tagName).get(0).text();
			if (StringUtils.hasText(text)) {
				return text;
			}
		}
		return null;
	}

	private void putInsertInfo(Element element, Job job) {
		JobInsert jobInsert = new JobInsert();
		jobInsert.setName(job.getName());
		jobInsert.setQuery(job.getQuery());
		jobInsert.setType(job.getType());
		/**
		 * 解析独立的字段
		 */

		jobInsert.setFail(tryToGetTagText(element, "fail"));
		jobInsert.setNext(tryToGetTagText(element, "next"));
		jobInsert.setSuccess(tryToGetTagText(element, "success"));
		jobInfo.addJobInsert(jobInsert);

	}

	private void putSelectInfo(Element element, Job job) {
		JobSelect jobSelect = new JobSelect();
		jobSelect.setName(job.getName());
		jobSelect.setQuery(job.getQuery());
		jobSelect.setType(job.getType());
		/**
		 * 解析独立字符串
		 */
		// 如果由showRes属性
		if (element.hasAttr("showRes")) {
			// 如果该属性字段为真
			if ("true".equals(element.attr("showRes").toLowerCase())) {
				jobSelect.setShowRes(true);
			}
		}
		jobSelect.setNone(tryToGetTagText(element, "none"));
		findRules(element, jobSelect);
		jobInfo.addJobSelect(jobSelect);
	}

	private void findRules(Element element, JobSelect job) {
		if (element.getElementsByTag("rules").size() < 1) {
			return;
		}
		List<RuleSelect> rules = new ArrayList<RuleSelect>();

		for (Element rule : element.getElementsByTag("rule")) {
			RuleSelect select = new RuleSelect();
			if (rule.hasAttr("mustDo")) {
				select.setMustDo(true);
			}
			if (rule.hasAttr("rule")) {
				select.setRule(rule.attr("rule"));
			}
			if (rule.hasAttr("next")) {
				select.setNext(rule.attr("next"));
			}
			if (rule.hasAttr("firstOnly")) {
				select.setFirstOnly(true);
			}
			select.setColumnName(rule.text());
			rules.add(select);
		}
		job.setRules(rules);
	}

}
