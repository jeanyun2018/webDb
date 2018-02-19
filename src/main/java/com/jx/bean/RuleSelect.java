package com.jx.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询时匹配规则实体类。 字段columnName:列名 字段rule:规则名 字段next:成功匹配时，需要执行的下一条job
 * 字段firstOnly:在该查询中，该规则是否只匹配一次。默认为false；
 * 
 * 
 * 例如查询结果为三条:res:1,3,3 条件为res=3，firstOnly为true。 则只会执行一次next的Job。
 * firstOnly为false则next的Job执行两次。
 * 
 * @author jx
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class RuleSelect {
	private String columnName;
	private String rule;
	private String next;
	private boolean firstOnly = false;
	private boolean mustDo = false;
}
