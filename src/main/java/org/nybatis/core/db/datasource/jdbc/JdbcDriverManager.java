package org.nybatis.core.db.datasource.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;

/**
 * DB 연결시 JDBC 드라이버 클래스를 로딩하는 클래스
 * 
 * @author nayasis
 *
 */
public abstract class JdbcDriverManager {
	
	// JDBC 드라이버가 이미 로딩됬는지 판단하기 위한 임시저장소 
	private static Set<String> registeredDriverClassNames = Collections.newSetFromMap( new ConcurrentHashMap<String, Boolean>() );

	/**
	 * JDBC 드라이버를 로딩한다.
	 * 
	 * ( Class.forName("jdbcDriver") 대신 사용 )
	 * 
	 * @param driverClassName JDBC 드라이버 클래스명
	 * 
	 * @return 신규등록여부 (기등록되어있을 경우 false 반환)
	 */
	public synchronized static boolean registerDriver( String driverClassName ) {
		
		if( registeredDriverClassNames.contains(driverClassName) ) return false;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
		try {

			classLoader.loadClass( driverClassName ).newInstance();

		} catch ( InstantiationException | IllegalAccessException | ClassNotFoundException e ) {

			throw new DatabaseConfigurationException( e, "Fail to load JDBC Driver({})", driverClassName );

		}
		
		registeredDriverClassNames.add( driverClassName );
		
		return true;
		
	}
	
	/**
	 * Database에 연결될 때까지 대기할 시간을 설정한다.
	 * 
	 * @param seconds 초
	 */
	public static void setLoginTimeout( int seconds ) {
		DriverManager.setLoginTimeout( seconds );
	}
	
	/**
	 * 지정한 URL로 DB 연결을 시도한다.
	 * 
	 * @param url DB URL (<code>jdbc:<em>subprotocol</em>:<em>subname</em></code>)
	 * @return DB 연결객체
	 */
	public static Connection getConnection( String url ) {
		try {
			return DriverManager.getConnection( url );
		} catch ( SQLException e ) {
			throw new DatabaseConfigurationException( e, "Fail to get connection (URL:{})", url );
		}
	}

	/**
	 * 지정한 URL로 DB 연결을 시도한다.
	 * 
	 * @param url      DB URL (<code>jdbc:<em>subprotocol</em>:<em>subname</em></code>)
	 * @param id       DB 사용자 ID
	 * @param password DB 사용자 PASSWORD
	 * @return DB 연결객체
	 */
	public static Connection getConnection( String url, String id, String password ) {
		try {
			return DriverManager.getConnection( url, id, password );
		} catch ( SQLException e ) {
			throw new DatabaseConfigurationException( e, "Fail to get connection (URL:{}, ID:{}, PW:{})", url, id, password );
		}
	}
	
}
