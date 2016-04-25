package org.nybatis.core.db.configuration.builder;

import java.io.File;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.NProperties;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;
import org.nybatis.core.xml.node.Node;

public class PropertyResolver {

	private NProperties properties = new NProperties();

	public PropertyResolver() {
		addDefaultProperties();
	}

	public PropertyResolver( Node properties ) {
		addDefaultProperties();
		setProperties(properties);

	}

	private void addDefaultProperties() {
		properties.set( "profile", Const.profile.get() );
	}

	private void setProperties( Node properties ) {

		String path = getPropValue( properties.getAttrIgnoreCase( "path" ) );

		if( Validator.isNotEmpty(path) ) {
			try {
	            this.properties.readFrom( new File( Const.path.getConfigDatabase() + "/" + path) );
            } catch( UncheckedIOException e ) {
            	NLogger.error( e );
            }
		}

		// set default properties
		NProperties defaultProperties = new NProperties();
		defaultProperties.set( "default.root", Const.path.getRoot() );
		defaultProperties.set( "default.base", Const.path.getBase() );

		for( String key : this.properties.keySet() ) {
			this.properties.set( key, getPropValue(this.properties.get(key), defaultProperties) );
		}

		// merge default properties
		this.properties.append( defaultProperties );

		// prepare properties
		for( Node element : properties.getChildElements("property") ) {

			String key   = element.getAttrIgnoreCase( "key"   );
			String value = element.getAttrIgnoreCase( "value" );

			this.properties.set( key, getPropValue(value) );

		}

	}

	public String getPropValue( String value ) {
		return getPropValue( value, properties );
	}

	private String getPropValue( String value, NProperties properties ) {

		value = StringUtil.nvl( value );

		for( String key : StringUtil.capturePatterns( value, "[#|$]\\{(.+?)\\}" ) ) {
			if( ! properties.hasKey( key ) ) continue;
			value = value.replaceAll( String.format("[#|$]\\{%s\\}", key), properties.get(key) );
		}

		return value;

	}

	public String getChildValue( Node node, String childName ) {
		return getPropValue( node.getChildElement(childName).getValue() );
	}

	public String getAttrVal( Node node, String attrubuteName ) {
		return getPropValue( node.getAttrIgnoreCase(attrubuteName) );
	}

	public boolean hasAttr( Node node, String key ) {
		if( node.isNull() ) return false;
		return node.hasAttrIgnoreCase( key );
	}

	public String getValue( Node node, String key ) {

		String val = getChildValue( node, key );

		if( StringUtil.isBlank(val) ) val = getAttrVal( node, key );

		return StringUtil.isBlank(val) ? null : val;

	}
}
