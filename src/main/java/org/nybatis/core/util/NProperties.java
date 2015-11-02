package org.nybatis.core.util;

import java.io.File;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.worker.WorkerReadLine;

public class NProperties {

	private Map<String, String> properties = new Hashtable<>();

	public NProperties() {}

	public NProperties( File file ) throws IoException {
	    readFrom( file );
	}

	public NProperties( NProperties properties ) {
		append(properties);
	}

    public NProperties readFrom( File file ) throws IoException {

		FileUtil.readFrom( file, new WorkerReadLine() {

			@Override
			public void execute( String readLine ) {

				readLine = readLine.trim();

				if( readLine.startsWith( "#" ) ) return;

				List<String> list = StringUtil.split( readLine, "=" );

				if( list.size() == 0 ) return;

				String key = list.get( 0 ).trim();
				String value = "";

				if( list.size() > 1 ) {

					list.remove( 0 );

					value = StringUtil.join( list, "" ).trim();

					value = StringUtil.unescape( value );

				}

				properties.put( key, value );

			}
		} );

		return this;

	}

	public String get( String key ) {
		return properties.get( key );
	}

	public void set( String key, String value ) {
		properties.put( key, value );
	}

	public String remove( String key ) {
		return properties.remove( key );
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
		return properties.keySet();
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

	public Map<String, String> toMap() {
		return properties;
	}

}
