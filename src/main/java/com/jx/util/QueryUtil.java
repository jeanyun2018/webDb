package com.jx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

import com.jx.bean.JobInsert;

/**
 * 运行时匹配工具类 sql语句中的参数，动态的替换到Job的query字段的占位符中
 * 
 * @author jx
 *
 */
public class QueryUtil {
	private static final org.slf4j.Logger debugLogger = LoggerFactory.getLogger(QueryUtil.class);

	private QueryUtil() {
	};

	public static String preparationParameters(String query, Map<String, Object> params) {
		if (params == null) {
			return query;
		}
		if (query.indexOf("#") != -1) {
			debugLogger.info("开始进行运行时匹配");
			debugLogger.info("匹配前结果：{}", query);
			int index = 0;
			for (String str : query.split("#")) {
				index++;
				if (index % 2 == 0) {
					if (params.get(str) != null) {
						query = query.replaceFirst("#" + str + "#", params.get(str).toString());
					}
				}
			}

			debugLogger.info("匹配后结果：{}", query);
		}

		return query;
	}

	public static Object[] preparationParametersList(JobInsert jobInsert, Map<String, Object> params) {
		if (params == null) {
			return null;
		}
		String query = jobInsert.getQuery();
		if (query.indexOf("#") != -1) {
			List<Object> objects = new ArrayList<Object>();
			int index = 0;
			for (String str : query.split("#")) {
				index++;
				if (index % 2 == 0) {
					if (params.get(str) != null) {
						objects.add(params.get("str"));
						query = query.replaceFirst("#" + str + "#", "?");
					}
				}
			}
			jobInsert.setQuery(query);
			return objects.toArray();
		} else
			return null;
	}
}
