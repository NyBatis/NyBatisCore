package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.db.datasource.factory.parameter.JdbcConnectionProperties;
import org.nybatis.core.db.datasource.factory.parameter.JndiConnectionProperties;
import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.DatasourceFactory;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.factory.jdbc.JdbcDataSourceFactory;
import org.nybatis.core.db.datasource.factory.jndi.JndiDatasourceFactory;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.node.Node;

/**
 * Database connection Environment builder
 */
public class EnvironmentBuilder {

	private String           environmentId;
	private PropertyResolver prop = new PropertyResolver();

	public EnvironmentBuilder( String environmentId ) {
		this.environmentId = environmentId;
	}

	public EnvironmentBuilder( Node environment, PropertyResolver propertyResolver ) {

		prop          = propertyResolver;
		environmentId = environment.getAttrIgnoreCase( "id" );

		if( StringUtil.isEmpty( environmentId ) ) {
			NLogger.warn( "Environment element of database configuration has no id.\n\n{}", environment );
			return;
		}

		if( isDefault( environment ) ) {
			setDefault();
		}

		NLogger.trace( "configurate datasource (id:{})", environmentId );

		setJdbcDatasource( environment );
		setUnpooledJdbcDatasource( environment );
		setJndiDatasource( environment );

	}

	public void setDefault() {
		new DatasourceManager().setDefaultDatasourceId( environmentId );
	}

	private boolean isDefault( Node environment ) {
		return StringUtil.isTrue( environment.getAttrIgnoreCase( "default" ) );
	}

	private void setJdbcDatasource( Node environment ) {

		Node datasource = environment.getChildElement( "datasourceJdbc" );

		if( datasource.isNull() ) return;

		JdbcConnectionProperties connectionProperties = new JdbcConnectionProperties();

		connectionProperties.setDriverName(   prop.getValue(  datasource, "driver"     ) );
		connectionProperties.setUrl(          prop.getValue(  datasource, "url"        ) );
		connectionProperties.setUserName(     prop.getValue(  datasource, "username"   ) );
		connectionProperties.setUserPassword( prop.getValue(  datasource, "password"   ) );
		connectionProperties.setTimeout(      prop.getAttrVal(datasource, "timeout"    ) );
		connectionProperties.setAutoCommit(   prop.getAttrVal(datasource, "autocommit" ) );

		JdbcDatasourceProperties datasourceProperties = new JdbcDatasourceProperties( environmentId );

		datasourceProperties.setPoolMin(    prop.getAttrVal( datasource, "poolMin"     ) );
		datasourceProperties.setPoolMax(    prop.getAttrVal( datasource, "poolMax"     ) );
		datasourceProperties.setPoolStep(   prop.getAttrVal( datasource, "poolStep"    ) );
		datasourceProperties.setPingCycle(  prop.getAttrVal( datasource, "pingCycle"   ) );
		datasourceProperties.setPingEnable( prop.getAttrVal( datasource, "ping"        ) );

		setJdbcDatasource( connectionProperties, datasourceProperties );

	}

	public void setJdbcDatasource( Class driver, String url, String username, String password ) {
		setJdbcDatasource( driver.getName(), url, username, password );
	}

	public void setJdbcDatasource( String driverName, String url, String username, String password ) {

		JdbcConnectionProperties connectionProperties = new JdbcConnectionProperties();

		connectionProperties.setDriverName( driverName );
		connectionProperties.setUrl( url );
		connectionProperties.setUserName( username );
		connectionProperties.setUserPassword( password );

		JdbcDatasourceProperties datasourceProperties = new JdbcDatasourceProperties( environmentId );

		setJdbcDatasource( connectionProperties, datasourceProperties );

	}

	public void setJdbcDatasource( JdbcConnectionProperties connectionProperties, JdbcDatasourceProperties datasourceProperties ) {
		DatasourceFactory factory = new JdbcDataSourceFactory( datasourceProperties, connectionProperties );
		new DatasourceManager().set( environmentId, factory.getDataSource() );
	}

	private void setUnpooledJdbcDatasource( Node environment ) {

		Node datasource = environment.getChildElement( "datasourceJdbcUnpooled" );

		if( datasource.isNull() ) return;

		JdbcConnectionProperties connectionProperties = new JdbcConnectionProperties();

		connectionProperties.setDriverName(   prop.getValue(  datasource, "driver"     ) );
		connectionProperties.setUrl(          prop.getValue(  datasource, "url"        ) );
		connectionProperties.setUserName(     prop.getValue(  datasource, "username"   ) );
		connectionProperties.setUserPassword( prop.getValue(  datasource, "password"   ) );
		connectionProperties.setTimeout(      prop.getAttrVal(datasource, "timeout"    ) );
		connectionProperties.setAutoCommit(   prop.getAttrVal(datasource, "autocommit" ) );

		setUnpooledJdbcDatasource( connectionProperties );

	}

	private void setUnpooledJdbcDatasource( JdbcConnectionProperties connectionProperties ) {
		JdbcDatasourceProperties datasourceProperties = new JdbcDatasourceProperties( environmentId );
		datasourceProperties.setPooled( false );
		setJdbcDatasource( connectionProperties, datasourceProperties );
	}

	private void setJndiDatasource( Node environment ) {

		Node datasource = environment.getChildElement( "datasourceJndi" );

		if( datasource.isNull() ) return;

		JndiConnectionProperties jndiConnectionProperties = new JndiConnectionProperties(
				prop.getValue( datasource, "initialContext" ),
				prop.getValue( datasource, "providerUrl"    ),
				prop.getValue( datasource, "name"           )
		);

		setJndiDatasource( jndiConnectionProperties );

	}

	private void setJndiDatasource( JndiConnectionProperties jndiConnectionProperties ) {
		DatasourceFactory factory = new JndiDatasourceFactory( jndiConnectionProperties );
		new DatasourceManager().set( environmentId, factory.getDataSource() );
	}

	public void setJndiDatasource( String jndiName ) {
		setJndiDatasource( new JndiConnectionProperties( jndiName ) );
	}

}