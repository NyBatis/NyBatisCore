package org.nybatis.core.db.configuration.connection;

import org.nybatis.core.util.StringUtil;

public class JdbcConnectionProperties {

	private String  driverName;
	private String  url;
	private String  userName;
	private String  userPassword;
	private boolean autoCommit = false;
	private int     timeout    = 10; // seconds

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName( String driverName ) {
		this.driverName = driverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName( String userId ) {
		if( StringUtil.isEmpty( userId ) ) return;
		this.userName = userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword( String userPassword ) {
		if( StringUtil.isEmpty( userPassword ) ) return;
		this.userPassword = userPassword;
	}

	public boolean isAutoCommit() {
		return autoCommit;
	}

	public void setAutoCommit( boolean autoCommit ) {
		this.autoCommit = autoCommit;
	}

	public void setAutoCommit( String autoCommit ) {
		this.autoCommit = StringUtil.isTrue( autoCommit );
	}

	/**
	 * get timeout
	 *
	 * @return seconds
	 */
	public int getTimeout() {
	    return timeout;
    }

	/**
	 * get timeout
	 *
	 * @return nano time
	 */
	public int getNanoTimeout() {
		return timeout * 1_000_000_000;
	}

	public void setTimeout( int second ) {
	    this.timeout = second;
    }

	public void setTimeout( String second ) {

		try {

			this.timeout = Integer.parseInt( second );

		} catch( NumberFormatException e ) {}

	}

//	/**
//	 * Getter for the connection group (based on driverName + url + userId + userPassword)
//	 *
//	 * @return connection group
//	 */
//	public int getConnectionGroup() {
//		return String.format( "%s-%s-%s-%s", driverName, url, userId, userPassword ).hashCode();
//	}

	public String toString() {
		return String.format( "DriverName : %s, Url : %s, AutoCommit : %s, ConnectionTimeOut : %s", driverName, url, autoCommit, timeout );
	}

}
