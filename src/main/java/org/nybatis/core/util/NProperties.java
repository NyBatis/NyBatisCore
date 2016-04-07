package org.nybatis.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.worker.WorkerReadLine;

/**
 * Properties wrapper
 *
 */
public class NProperties {

	private Properties properties = new Properties();

	public NProperties() {}

	public NProperties( String filePath ) throws IoException {
		this( filePath, StandardCharsets.UTF_8.toString() );
	}

	public NProperties( String filePath, String charset ) throws IoException {
		readFrom( new File(Const.profile.getFileName(filePath)), charset );
	}

	public NProperties( File file ) throws IoException {
		this( file,  StandardCharsets.UTF_8.toString() );
	}

	public NProperties( File file, String charset ) throws IoException {
	    readFrom( file, charset );
	}

	public NProperties( NProperties properties ) {
		append(properties);
	}

	public NProperties readFrom( File file ) throws IoException {
		return readFrom( file, StandardCharsets.UTF_8.toString() );
	}

    public NProperties readFrom( File file, String charset ) throws IoException {

		try {
			properties.load( new BufferedReader( new InputStreamReader( new FileInputStream( file ), charset ) ));
		} catch( IOException e ) {
			throw new IoException( e );
		}

		return this;

	}

	public String get( String key ) {
		return properties.getProperty( key );
	}

	public NProperties set( String key, String value ) {
		properties.put( key, value );
		return this;
	}

	public <T> T getObject( String key ) {
		Object val = properties.get( key );
		return val == null ? null : (T) val;
	}

	public NProperties setObject( String key, Object value ) {
		properties.put( key, value );
		return this;
	}

	public NProperties remove( String key ) {
		properties.remove( key );
		return this;
	}

	public boolean hasKey( String key ) {
		return properties.containsKey(key);
	}

	public boolean hasValue( String value ) {
		return properties.containsValue(value);
	}

	public boolean isEmpty() {
		return properties.isEmpty();
	}

	public Set<String> keySet() {
		return properties.keySet().stream().map( StringUtil::nvl ).collect( Collectors.toSet() );
	}

	public NProperties append( NProperties otherProperties ) {
		properties.putAll( otherProperties.properties );
		return this;
	}

	public String toString() {
		return properties.toString();
	}

	public int hashCode() {
		return properties.hashCode();
	}

	public Properties getUnwrappedProperties() {
		return properties;
	}

}
