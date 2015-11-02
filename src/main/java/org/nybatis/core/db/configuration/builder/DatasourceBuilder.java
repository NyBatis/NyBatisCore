package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.db.configuration.connection.JdbcConnectionProperties;
import org.nybatis.core.db.configuration.connection.JndiConnectionProperties;
import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.DatasourceFactory;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.jdbc.JdbcDataSourceFactory;
import org.nybatis.core.db.datasource.jndi.JndiDatasourceFactory;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.node.Node;

public class DatasourceBuilder {

	private String            environmentId;
	private PropertiesBuilder prop;

	public DatasourceBuilder( Node environment, PropertiesBuilder propertiesBuilder ) {

		prop          = propertiesBuilder;
		environmentId = environment.getAttrIgnoreCase( "id" );

		if( StringUtil.isEmpty( environmentId ) ) {
			NLogger.warn( "Environment element of database configuration has missed id.\n\n{}", environment );
			return;
		}


		if( isDefault( environment ) ) {
			new DatasourceManager().setDefaultDatasourceId( environmentId );
		}

		NLogger.debug( "configurate datasource (id:{})", environmentId );

		setDatasource( environment.getChildElement("datasource") );

	}

	private boolean isDefault( Node environment ) {
		return StringUtil.isTrue( environment.getAttrIgnoreCase( "default" ) );
	}

	private void setDatasource( Node datasource ) {

		if( datasource == null ) return;

		DatasourceFactory factory;

		switch( StringUtil.toLowerCase(prop.getAttrVal(datasource, "type")) ) {

			case "jdbc" :

				JdbcConnectionProperties connectionProperties = new JdbcConnectionProperties();

				connectionProperties.setDriverName(   prop.getValue(datasource, "driver"     ) );
				connectionProperties.setUrl(          prop.getValue(datasource, "url"        ) );
				connectionProperties.setUserName(     prop.getValue(datasource, "username"   ) );
				connectionProperties.setUserPassword( prop.getValue(datasource, "password"   ) );
				connectionProperties.setTimeout(      prop.getValue(datasource, "timeout"    ) );
				connectionProperties.setAutoCommit(   prop.getValue(datasource, "autocommit" ) );

				JdbcDatasourceProperties datasourceProperties = new JdbcDatasourceProperties();

				datasourceProperties.setPoolMin(    prop.getValue(datasource, "poolMin"   ) );
				datasourceProperties.setPoolMax(    prop.getValue(datasource, "poolMax"   ) );
				datasourceProperties.setPoolStep(   prop.getValue(datasource, "poolStep"  ) );
				datasourceProperties.setPingCycle(  prop.getValue(datasource, "pingCycle" ) );
				datasourceProperties.setPingEnable( prop.getValue(datasource, "ping"      ) );
				datasourceProperties.setPingQuery(  prop.getValue(datasource, "pingQuery" ) );
				datasourceProperties.setPooled(     prop.getValue(datasource, "unpooled"  ) );

				factory = new JdbcDataSourceFactory( datasourceProperties, connectionProperties );

				break;

			case "jndi" :

				JndiConnectionProperties jndiConnectionProperties = new JndiConnectionProperties(
						prop.getValue( datasource, "initialContext" ),
						prop.getValue( datasource, "providerUrl"    ),
						prop.getValue( datasource, "name"           )
				);

				factory = new JndiDatasourceFactory( jndiConnectionProperties );

				break;

			default :
				return;

		}

		new DatasourceManager().set( environmentId, factory.getDataSource() );

	}

}
