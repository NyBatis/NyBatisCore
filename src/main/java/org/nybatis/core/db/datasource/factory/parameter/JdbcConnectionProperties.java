package org.nybatis.core.db.datasource.factory.parameter;

import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.Encryptor;
import org.nybatis.core.util.StringUtil;

/**
 * JDBC connection properties
 */
public class JdbcConnectionProperties {

	private String  driverName;
	private String  url;
	private String  userName;
	private String  userPassword;
	private boolean autoCommit     = false;
	private int     timeoutSeconds = 10;

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
	    return timeoutSeconds;
    }

	/**
	 * get timeout
	 *
	 * @return nano time
	 */
	public long getNanoTimeout() {
		return (long) timeoutSeconds * 1_000_000_000;
	}

	/**
	 * set timeout
	 *
	 * @param seconds seconds
	 */
	public void setTimeout( int seconds ) {
	    this.timeoutSeconds = seconds;
    }

	/**
	 * set timeout
	 * @param seconds seconds
	 */
	public void setTimeout( String seconds ) {
		try {
			this.timeoutSeconds = Integer.parseInt( seconds );
		} catch( NumberFormatException e ) {}
	}

	/**
	 * set secret key for password decryption
	 *
	 * @param secretKey	secret key
	 */
	public void setPasswordSecretKey( String secretKey ) {

		if( StringUtil.isBlank(secretKey) ) return;

		if( FileUtil.isResourceExisted(secretKey) ) {
			secretKey = FileUtil.readResourceFrom(secretKey);
		}

		secretKey = StringUtil.trim( secretKey );

		if( StringUtil.isEmpty(secretKey) ) {
			NLogger.warn( "There is no secret key for password decryption" );
			return;
		}

		Encryptor encryptor = new Encryptor();
		userPassword = encryptor.decrypt( userPassword, secretKey );

	}

	public String toString() {
		return String.format( "DriverName : %s, Url : %s, AutoCommit : %s, ConnectionTimeOut : %s", driverName, url, autoCommit, timeoutSeconds );
	}

}
