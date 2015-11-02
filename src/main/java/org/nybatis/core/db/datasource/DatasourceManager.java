package org.nybatis.core.db.datasource;

import org.nybatis.core.db.datasource.driver.DriverAttributes;
import org.nybatis.core.db.datasource.driver.DriverManager;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.Map;

public class DatasourceManager {

	private static Map<String, DataSource>       datasourceRepository = new Hashtable<>();
	private static Map<String, DriverAttributes> attributeRepository  = new Hashtable<>();

	private static String defaultEnvironmentId;

	public void set( String environmentId, DataSource datasource ) {

		if( StringUtil.isEmpty( defaultEnvironmentId ) && datasourceRepository.size() == 0 ) {
			defaultEnvironmentId = environmentId;
		}

		DriverAttributes driverAttributes = DriverManager.get( datasource );

		datasourceRepository.put( environmentId, datasource );
		attributeRepository.put( environmentId, driverAttributes );

		DriverManager.get( datasource );

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

	public static DriverAttributes getAttributes( String environmentId ) {
		return Validator.nvl( attributeRepository.get( environmentId ), new DriverAttributes() );
	}

	public static boolean isExist( String environmentId ) {
		return environmentId != null && datasourceRepository.containsKey( environmentId );
	}

	public static DriverAttributes getAttributes() {
		return attributeRepository.get( defaultEnvironmentId );
	}

}
