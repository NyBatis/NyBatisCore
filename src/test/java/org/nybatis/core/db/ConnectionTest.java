package org.nybatis.core.db;

import org.nybatis.core.db.datasource.factory.parameter.JdbcConnectionProperties;
import org.nybatis.core.db.datasource.DatasourceFactory;
import org.nybatis.core.db.datasource.factory.jdbc.JdbcDataSourceFactory;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionTest {

	@Test
	public void jdbcConnectionTest() throws SQLException {

		JdbcConnectionProperties connectionProperties = new JdbcConnectionProperties();
		
		connectionProperties.setDriverName( "org.sqlite.JDBC" );
		connectionProperties.setUrl( "jdbc:sqlite:./target/test-classes/localDb/SimpleLauncherHelloWorld.db" );
		
		DatasourceFactory datasourceFactory = new JdbcDataSourceFactory( connectionProperties );
		
		DataSource dataSource = datasourceFactory.getDataSource();
		
		Connection connection = dataSource.getConnection();
		
		connection.close();
		
	}
	
	
}
