package com.jx.webcontrol;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.eventbus.Subscribe;
import com.jx.inter.IEventBusManage;
import com.jx.inter.IShowBackInfo;
import com.jx.webconfig.ParameterBridge;

import lombok.extern.slf4j.Slf4j;

/**
 * 提供web层调用接口，并且通过消息总线，对处理结果进行回显。
 * 
 * @author jean
 *
 */
@Controller
@Slf4j
public class InfoController implements IShowBackInfo {
	@Autowired
	private IEventBusManage eventBusManage;
	private StringBuilder builder = new StringBuilder();

	private int times = 0;

	/**
	 * 绑定到WebBus，接收DB层的消息
	 */
	@PostConstruct
	public void init() {
		eventBusManage.registerWebBus(this);
	}

	@Subscribe
	public void getEvent(ParameterBridge parameterBridge) {
		if (parameterBridge != null && parameterBridge.isDeal()) {
			if (StringUtils.hasText(parameterBridge.getFilePath())) {
				log.info("路径：{}已经处理", parameterBridge.getFilePath());
			} else {
				log.info("Html已经处理:{}", parameterBridge.getHtml());
			}

		}
	}

	@ResponseBody
	@RequestMapping("/info")
	public String getInfo() {
		return builder.toString();
	}

	@Override
	public void show(String info) {
		if (StringUtils.hasText(info)) {
			times++;
			// 清空过长内容
			if (times > 50) {
				builder.setLength(0);
			}
			builder.append(info + "<br/>");
		}
	}

	@ResponseBody
	@RequestMapping("/postHtml")
	public String postHtml(String html) {
		if (StringUtils.hasText(html)) {
			ParameterBridge pb = new ParameterBridge();
			pb.setHtml(html);
			eventBusManage.postDbBus(pb);
		}
		return html;
	}

	@ResponseBody
	@RequestMapping("/clear")
	public String clear() {
		builder.setLength(0);
		return "OK";
	}

	@RequestMapping("/")
	public String home() {
		return "index";
	}
}
