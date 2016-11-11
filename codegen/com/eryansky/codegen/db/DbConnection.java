package com.eryansky.codegen.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.eryansky.codegen.util.Resources;

/**
 * 数据库连接
 */
public class DbConnection {
	
	public Connection getConn() {
		Connection conn = null;
		try {
			Class.forName(Resources.DRIVER).newInstance();
			conn = DriverManager.getConnection(Resources.URL, Resources.USERNAME, Resources.PASSWORD);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return conn;
	}

}
