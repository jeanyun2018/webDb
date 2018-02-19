package com.jx.bean;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询操作实体类。 继承自Job基类。 none字段:查询结果为空时提示信息。 rules字段:查询结果规则匹配列表。
 * 
 * @author jx
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class JobSelect extends Job {
	private String none;
	private Boolean showRes = false;
	private List<RuleSelect> rules;
}
