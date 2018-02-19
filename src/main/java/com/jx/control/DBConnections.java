package com.jx.control;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.jx.bean.Connect;

import lombok.extern.slf4j.Slf4j;

/**
 * 负责存储数据库连接。 并且在获取连接时，该类保证连接可用。
 * 
 * @author jx
 *
 */
@Component
@Slf4j
public class DBConnections {
	private Map<String, Connection> connections = new HashMap<String, Connection>(4);

	public void addConnection(String key, Connection connection) {
		connections.put(key, connection);
	}

	public void addConnection(Connect connect) {
		if (connect == null) {
			log.error("连接信息为空!");
			return;
		}
		try {
			Class.forName(connect.getDriverName());
			connections.put(connect.getConnectName(),
					DriverManager.getConnection(connect.getUrl(), connect.getUserName(), connect.getPassWord()));
		} catch (ClassNotFoundException | SQLException e) {
			log.error("添加链接异常，请检查连接字符串!", e);
		}

	}

	public Connection getConnection(String key) {
		return connections.get(key);
	}

	public Connection getMainConnection() {
		if (!connections.isEmpty()) {
			return connections.values().iterator().next();
		} else {
			throw new RuntimeException("当前连接数为0！");
		}
	}
}
