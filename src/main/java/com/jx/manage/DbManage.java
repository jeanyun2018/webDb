package com.jx.manage;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.google.common.eventbus.Subscribe;
import com.jx.control.MainContol;
import com.jx.inter.IEventBusManage;
import com.jx.webconfig.ParameterBridge;

import lombok.extern.slf4j.Slf4j;

/**
 * web控制层与DB层之间的管理层 通过eventBus进行解耦 负责Db层的调度
 * 
 * 通过DbBus来接收web层传递的信息
 * 
 * 通过WebBus回传处理的结果
 * 
 * @author jean
 *
 */
@Component
@Slf4j
@Lazy(false)
public class DbManage {

	@Autowired
	private IEventBusManage eventBusManage;
	@Autowired
	private MainContol mainContol;

	@PostConstruct
	public void init() {
		eventBusManage.registerDbBus(this);
	}

	@Subscribe
	public void listenEvent(ParameterBridge parameterBridge) {
		if (StringUtils.hasText(parameterBridge.getFilePath())) {
			mainContol.startWork(parameterBridge.getFilePath());
			answerWeb(parameterBridge);
			log.info("接收到文件路径：{}", parameterBridge.getFilePath());
		} else if (StringUtils.hasText(parameterBridge.getHtml())) {
			mainContol.startHtml(parameterBridge.getHtml());
			answerWeb(parameterBridge);
			log.info("接收到的html：{}", parameterBridge.getHtml());
		} else {
			log.info("接收结果为空！");
		}
	}

	private void answerWeb(ParameterBridge parameterBridge) {
		/* 标记为已经处理 */
		parameterBridge.setDeal(true);
		/* 把已经处理的消息，通过Web消息总线传递给Web层 */
		eventBusManage.postWebBus(parameterBridge);
	}
}
