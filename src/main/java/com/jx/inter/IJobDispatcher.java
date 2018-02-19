package com.jx.inter;

import java.util.Map;

/**
 * Job调度器接口 调度器实现该接口，由其他类，通过该接口，提供Job名，Job类型，该Job所执行参数来进行调度。
 * 
 * @author jx
 *
 */
public interface IJobDispatcher {
	void dispatch(String name, String type, Map<String, Object> params);

	void dispatch(String next, Map<String, Object> params);
}
