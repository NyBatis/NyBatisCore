package org.nybatis.core.db.datasource.jdbc;

import javax.sql.DataSource;

import org.nybatis.core.db.configuration.connection.JdbcConnectionProperties;
import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.DatasourceFactory;

public class JdbcDataSourceFactory implements DatasourceFactory {

	private JdbcDatasourceProperties datasourceProperties;
	private JdbcConnectionProperties connectionProperties;

	/**
	 * Create Pooled Datasource Factory
	 *
	 * @param datasourceProperties
	 * @param connectionProperties
	 */
	public JdbcDataSourceFactory( JdbcDatasourceProperties datasourceProperties, JdbcConnectionProperties connectionProperties ) {
		this.datasourceProperties = datasourceProperties;
		this.connectionProperties = connectionProperties;
	}

	/**
	 * Create Unpooled Datasource Factory
	 *
	 * @param connectionProperties
	 */
	public JdbcDataSourceFactory( JdbcConnectionProperties connectionProperties ) {
		this.datasourceProperties = new JdbcDatasourceProperties();
		this.connectionProperties = connectionProperties;
		datasourceProperties.setPooled( false );
	}

	public DataSource getDataSource() {
		return datasourceProperties.isPooled()
			? new JdbcDataSource( datasourceProperties, connectionProperties )
			: new JdbcUnpooledDataSource( connectionProperties );
	}

}
