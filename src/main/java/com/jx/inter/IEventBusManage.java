package com.jx.inter;

public interface IEventBusManage {

	public void registerWebBus(Object obj);

	public void unRegisterWebBus(Object obj);

	public void postWebBus(Object obj);

	public void registerDbBus(Object obj);

	public void unRegisterDbBus(Object obj);

	public void postDbBus(Object obj);
}
