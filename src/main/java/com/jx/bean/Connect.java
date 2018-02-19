package com.jx.bean;

import lombok.Data;

/**
 * 连接信息实体类，负责封装jdbc连接信息
 * 
 * @author jx
 *
 */
@Data
public class Connect {
	private String userName;
	private String passWord;
	private String driverName;
	private String url;
	private String connectName;

	public Connect(String userName, String passWord, String driverName, String url, String connectName) {
		this.connectName = connectName;
		this.driverName = driverName;
		this.url = url;
		this.passWord = passWord;
		this.userName = userName;
	}

	public Connect() {
	}

}
