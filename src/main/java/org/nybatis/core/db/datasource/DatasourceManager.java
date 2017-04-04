package org.nybatis.core.db.datasource;

import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.datasource.driver.DatabaseAttributeManager;
import org.nybatis.core.db.datasource.factory.jdbc.JdbcDataSource;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class DatasourceManager {

	private static Map<String, DataSource>        datasourceRepository = new Hashtable<>();
	private static Map<String, DatabaseAttribute> attributeRepository  = new Hashtable<>();

	private static String defaultEnvironmentId;

	public void set( String environmentId, DataSource datasource ) throws DatabaseConfigurationException {

		if( StringUtil.isEmpty( defaultEnvironmentId ) && datasourceRepository.size() == 0 ) {
			defaultEnvironmentId = environmentId;
		}

		DatabaseAttribute databaseAttribute;
		try {
			databaseAttribute = DatabaseAttributeManager.get( datasource );
		} catch( DatabaseConfigurationException e ) {
			throw new DatabaseConfigurationException( e, "Error on initializing datasoure environment(id:{})", environmentId );
		}

		setPingQuery( datasource, databaseAttribute.getPingQuery() );

		datasourceRepository.put( environmentId, datasource );
		attributeRepository.put( environmentId, databaseAttribute );

		DatabaseAttributeManager.get( datasource );

	}

	private void setPingQuery( DataSource datasource, String pingSql ) {
		if( datasource instanceof JdbcDataSource ) {
			((JdbcDataSource) datasource).getDatasourceProperties().setPingQuery( pingSql );
		}
	}

	@SuppressWarnings( "static-access" )
    public void setDefaultDatasourceId( String defaultEnvironmentId ) {
		this.defaultEnvironmentId = defaultEnvironmentId;
	}

	public static DataSource get( String environmentId ) {
		return datasourceRepository.get( environmentId );
	}

	public static DataSource get() {
		return datasourceRepository.get( defaultEnvironmentId );
	}

	public static String getDefaultEnvironmentId() {
		return defaultEnvironmentId;
	}

	public static DatabaseAttribute getAttributes( String environmentId ) {
		return Validator.nvl( attributeRepository.get( environmentId ), new DatabaseAttribute() );
	}

	public static boolean isExist( String environmentId ) {
		return environmentId != null && datasourceRepository.containsKey( environmentId );
	}

	public static DatabaseAttribute getAttributes() {
		return attributeRepository.get( defaultEnvironmentId );
	}

	public static Map<String, DataSource> getDatasourceRepository() {
		return datasourceRepository;
	}

	public static Map<String, DatabaseAttribute> getAttributeRepository() {
		return attributeRepository;
	}

	public static Set<String> getEnvironments() {
		return datasourceRepository.keySet();
	}

	public static void printStatus() {

		if( ! NLogger.isTraceEnabled() ) return;

		StringBuilder sb = new StringBuilder();
		sb.append( ">> Connection pool status\n" );

		NList result = new NList();

		Set<String> environments = new TreeSet<>( getEnvironments() );

		for( String environmentId : environments ) {
			DataSource dataSource = get( environmentId );
			if( dataSource instanceof JdbcDataSource ) {
				result.addRow( ((JdbcDataSource) dataSource).getPoolStatus() );
			}
		}

		sb.append( result );

		NLogger.trace( sb );

	}

}
