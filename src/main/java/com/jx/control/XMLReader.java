package com.jx.control;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jx.bean.Connect;
import com.jx.bean.Job;
import com.jx.inter.IJobTagParser;

import lombok.extern.slf4j.Slf4j;

/**
 * XML文件读取类 负责将xml文件中的数据库连接信息封装到Connect实体类中。 将全局变量进行替换。
 * 解析替换完成的结果，并且将解析出的Job节点，委托给IJobTagParser的实现进行解析。
 * 单一职责原则，不负责Job具体标签的解析。由IJobTagParser，进行解析具体为何种Job节点。
 * 
 * @author jx
 *
 */
@Component
@Slf4j
public class XMLReader {
	@Autowired
	private IJobTagParser parser;
	private Job firstJob = new Job();
	private Document doc;
	private Connect connect;
	private Map<String, String> configs = new HashMap<String, String>(10);

	public XMLReader() {
	}

	public void init(String filePath) {
		try {
			doc = Jsoup.parse(new File(filePath), "utf-8");
		} catch (IOException e) {
			log.error("读取文件异常!", e);
		}
	}

	public void initHtml(String html) {
		doc = Jsoup.parse(html);
	}

	/**
	 * 解析数据库连接信息 并且封装到Connect对象中 解析出的标签名一律为小写
	 * 
	 * @return
	 */
	private Connect findSqlInfo() {
		if (doc.getElementsByTag("sql").isEmpty()) {
			return null;
		}
		Elements tags = doc.getElementsByTag("sql").get(0).getAllElements();
		Connect connect = new Connect();
		for (Element tag : tags) {
			switch (tag.tagName()) {
			case "connectname":
				connect.setConnectName(tag.text());
				break;
			case "username":
				connect.setUserName(tag.text());
				break;
			case "password":
				connect.setPassWord(tag.text());
				break;
			case "url":
				connect.setUrl(tag.text());
				break;
			case "drivername":
				connect.setDriverName(tag.text());
				break;
			}
		}
		return connect;
	}

	/**
	 * config查询文件中的config标签，将标签内容优先加载 在config中配置全局变量
	 */
	private void findConfig() {
		SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		configs.put("time", time.format(new Date()));
		if (doc.getElementsByTag("config").isEmpty()) {
			return;
		}
		Elements tags = doc.getElementsByTag("config").get(0).getAllElements();
		for (Element tag : tags) {
			configs.put(tag.tagName(), tag.text());
		}
	}

	/* 获取job信息，以及数据库连接信息 */
	public void read() {
		// 获取数据库连接信息
		connect = findSqlInfo();
		// 查询全局配置
		findConfig();
		// 替换全局配置并且重新解析
		doc = Jsoup.parse(findAndPeplace(doc.html()));

		// 获取job信息，并且分发处理
		findJobInfo();
	}

	/* 获取数据库连接信息，以及配置信息 */
	public void getSqlAndConfig() {
		// 获取数据库连接信息
		connect = findSqlInfo();
		// 查询全局配置
		findConfig();
	}

	public Job getFirstJob() {
		return firstJob;
	}

	// 查询所有任务信息
	private void findJobInfo() {
		Elements jobs = doc.getElementsByTag("job");
		boolean isFirstJob = true;
		for (Element job : jobs) {
			Job dbjob = new Job();
			dbjob.setName(job.getElementsByTag("name").get(0).text());
			dbjob.setType(job.getElementsByTag("type").get(0).text());
			dbjob.setQuery(job.getElementsByTag("query").get(0).text());
			// 记录第一个任务信息
			if (isFirstJob) {
				firstJob = dbjob;
				isFirstJob = false;
			}
			parser.parse(job, dbjob);
		}
	}

	/**
	 * 替换全局变量
	 * 
	 * @param src
	 * @return
	 */
	private String findAndPeplace(String src) {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(src.getBytes())));
		StringBuilder builder = new StringBuilder();
		String line;
		try {
			while ((line = br.readLine()) != null) {
				int begin = line.indexOf("@");
				if (begin != -1) {
					int index = 0;
					for (String str : line.split("@")) {
						index++;
						if (index % 2 == 0) {
							if (configs.get(str.toLowerCase()) != null)
								line = line.replaceFirst("@" + str + "@", configs.get(str.toLowerCase()));
						}
					}
				}
				builder.append(line);
			}
		} catch (IOException e) {
			log.error("替换全局变量异常!", e);
		}
		return builder.toString();
	}

	public Connect getConnect() {
		return connect;
	}

	public Map<String, String> getConfigs() {
		return configs;
	}
}
