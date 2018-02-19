package com.jx.inter;

import org.jsoup.nodes.Element;

import com.jx.bean.Job;

/**
 * 任务标签解析接口。 XMLReader将解析到的Job标签节点发送给该接口。 该接口负责具体区分是何种Job标签。
 * 
 * @author jx
 *
 */
public interface IJobTagParser {
	void parse(Element element, Job job);
}
