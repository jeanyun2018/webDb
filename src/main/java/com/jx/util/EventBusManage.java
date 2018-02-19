package com.jx.util;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.jx.inter.IEventBusManage;

/**
 * 消息总线控制层 提供两条消息总线，web层组件绑定在webBus中，DB层组件绑定在dbBus中。
 * 通过EventBusManage进行相互通信。通过ParameterBridge，参数桥传递数据。
 * 
 * @author jean
 *
 */
@Component
public class EventBusManage implements IEventBusManage {

	private EventBus webBus = new EventBus("webBus");
	private EventBus dbBus = new EventBus("dbBus");

	@Override
	public void registerWebBus(Object obj) {
		webBus.register(obj);
	}

	@Override
	public void unRegisterWebBus(Object obj) {
		webBus.unregister(obj);
	}

	@Override
	public void postWebBus(Object obj) {
		webBus.post(obj);
	}

	@Override
	public void registerDbBus(Object obj) {
		dbBus.register(obj);
	}

	@Override
	public void unRegisterDbBus(Object obj) {
		dbBus.unregister(obj);
	}

	@Override
	public void postDbBus(Object obj) {
		dbBus.post(obj);
	}

}
