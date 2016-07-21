package org.nybatis.core.db.datasource.factory.jdbc;

import javax.sql.DataSource;

import org.nybatis.core.db.datasource.factory.parameter.JdbcConnectionProperties;
import org.nybatis.core.db.configuration.connectionPool.JdbcDatasourceProperties;
import org.nybatis.core.db.datasource.DatasourceFactory;

/**
 * JDBC datasource factory
 */
public class JdbcDataSourceFactory implements DatasourceFactory {

	private JdbcDatasourceProperties datasourceProperties;
	private JdbcConnectionProperties connectionProperties;

	/**
	 * Create Pooled Datasource Factory
	 *
	 * @param datasourceProperties	datasource configuration properties
	 * @param connectionProperties	JDBC connection properties
	 */
	public JdbcDataSourceFactory( JdbcDatasourceProperties datasourceProperties, JdbcConnectionProperties connectionProperties ) {
		this.datasourceProperties = datasourceProperties;
		this.connectionProperties = connectionProperties;
	}

	/**
	 * Create Unpooled Datasource Factory
	 *
	 * @param connectionProperties JDBC connection properties
	 */
	public JdbcDataSourceFactory( JdbcConnectionProperties connectionProperties ) {
		this.datasourceProperties = new JdbcDatasourceProperties();
		this.connectionProperties = connectionProperties;
		datasourceProperties.setPooled( false );
	}

	/**
	 * get datasource
	 *
	 * @return datasource
	 */
	public DataSource getDataSource() {
		return datasourceProperties.isPooled()
			? new JdbcDataSource( datasourceProperties, connectionProperties )
			: new JdbcUnpooledDataSource( connectionProperties );
	}

}
