package com.jx.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 插入操作实体类。 继承Job实体类。 fail字段:失败提示信息。 next字段:表明执行成功后，需要调用的下一条job。
 * success字段:成功提示信息。
 * 
 * @author jx
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class JobInsert extends Job {
	private String fail;
	private String next;
	private String success;
}
