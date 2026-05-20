package com.ureca.pos.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	//jdk 11버전부터 mysql-connector-java-8.0.13이상 연결시 useSSL=true로 되어 있어 close 시 오류 발생 
	//useSSL=false로 설정해야 close시 오류가 발생하지 않는다.
	static final String URL = "jdbc:mysql://localhost:3306/pos_db?serverTimezone=UTC&useUniCode=yes&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true";
	static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String ID="ureca";
	static final String PW="ureca";
	private static DBUtil instance = new DBUtil();
	private DBUtil() {
		try {
			Class.forName(DRIVER);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static DBUtil getInstance() {
		return instance;
	}
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, ID, PW);
	}
	public void close(AutoCloseable ... acs) {
		for (AutoCloseable  c: acs) {
			if(c != null) {
				try {
					c.close();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}
}





