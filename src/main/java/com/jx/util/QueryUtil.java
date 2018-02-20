package com.jx.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;

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

	/**
	 * 将需要执行的sql语句进行运行时匹配 将语句中的#包裹的参数，进行替换。
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	public static String preparationParameters(String query, Map<String, Object> params) {
		if (params == null) {
			return query;
		}
		String runquery = query;
		if (runquery.indexOf("#") != -1) {
			debugLogger.info("开始进行运行时匹配");
			debugLogger.info("匹配前结果：{}", runquery);
			int index = 0;
			for (String str : runquery.split("#")) {
				index++;
				if (index % 2 == 0) {
					if (params.get(str) != null) {
						runquery = runquery.replaceFirst("#" + str + "#", params.get(str).toString());
					}
				}
			}
			debugLogger.info("匹配后结果：{}", runquery);
		}
		return runquery;
	}

	/**
	 * 将语句中用!包裹的参数，尝试去参数params匹配。 如果匹配成功则，将!包裹的内容用占位符？进行替换。 返回Object列表
	 * 
	 * @param query
	 * @param params
	 * @return
	 */
	public static Object[] preparationParametersList(String query, Map<String, Object> params) {
		if (params == null) {
			return null;
		}
		if (query.indexOf("!") != -1) {
			List<Object> objects = new ArrayList<Object>();
			int index = 0;
			for (String str : query.split("!")) {
				index++;
				if (index % 2 == 0) {
					if (params.get(str) != null) {
						objects.add(params.get(str));
						query = query.replaceFirst("!" + str + "!", "?");
					}
				}
			}
			return objects.toArray();
		} else
			return null;
	}
}
