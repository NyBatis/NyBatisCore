package org.nybatis.core.util;

import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.validation.Validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Properties wrapper
 *
 */
public class NProperties {

	private Properties properties = new Properties();

	public NProperties() {}

	public NProperties( String filePath ) throws UncheckedIOException {
		this( filePath, StandardCharsets.UTF_8.toString() );
	}

	public NProperties( String filePath, String charset ) throws UncheckedIOException {
		readFrom( filePath, charset );
	}

	public NProperties( File file ) throws UncheckedIOException {
		this( file,  StandardCharsets.UTF_8.toString() );
	}

	public NProperties( File file, String charset ) throws UncheckedIOException {
	    readFrom( file, charset );
	}

	public NProperties( NProperties properties ) {
		append(properties);
	}

	public NProperties readFrom( String file ) throws UncheckedIOException {
		return readFrom( file, StandardCharsets.UTF_8.toString() );
	}

    public NProperties readFrom( String file, String charset ) throws UncheckedIOException {

		try {
			InputStream stream = FileUtil.getResourceAsStream( file );
			if( Validator.isNotEmpty(stream) ) {
				properties.load( new BufferedReader( new InputStreamReader( stream, charset ) ));
			}
		} catch( IOException e ) {
			throw new UncheckedIOException( e );
		}

		return this;

	}

	public NProperties readFrom( File file ) throws UncheckedIOException {
		return readFrom( file, StandardCharsets.UTF_8.toString() );
	}

	public NProperties readFrom( File file, String charset ) throws UncheckedIOException {
		if( FileUtil.isFile( file ) ) {
			readFrom( file.getPath(), charset );
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

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		properties.forEach( ( key, val ) -> map.put( StringUtil.nvl(key), val ) );
		return map;
	}

}
