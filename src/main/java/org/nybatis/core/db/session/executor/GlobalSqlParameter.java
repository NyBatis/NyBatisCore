package org.nybatis.core.db.session.executor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nybatis.core.context.NThreadLocal;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.log.NLogger;

public class GlobalSqlParameter {

	private static final String SQL_PARAM            = GlobalSqlParameter.class.getName() + ".SQL_PARAM";
	private static final String ENVIRONMENT          = GlobalSqlParameter.class.getName() + ".ENVIRONMENT";
	private static final String ENVIRONMENT_DEFAULT  = GlobalSqlParameter.class.getName() + ".ENVIRONMENT_DEFAULT";

    private static Map<Object, Object> getMap() {

		if( ! NThreadLocal.containsKey( SQL_PARAM ) ) {
			NThreadLocal.set( SQL_PARAM, new HashMap<>() );
		}

		return (Map<Object, Object>) NThreadLocal.get( SQL_PARAM );

	}

	public static void put( Object key, Object value ) {
		getMap().put( key, value );
	}

    public static void putAll( Map params ) {
		getMap().putAll( params );
	}

	public static void clear() {
		getMap().clear();
	}

	public static Object get( Object key ) {
		return getMap().get( key );
	}

	public static void remove( Object key ) {
		getMap().remove( key );
	}

	public static boolean containsKey( Object key ) {
		return getMap().containsKey( key );
	}

	public static Set<Object> keyset() {
		return getMap().keySet();
	}

	public static Map<Object, Object> getThreadLocalParameters() {
		return getMap();
	}

	public static void setEnvironmentId( String id ) {

		if( DatasourceManager.get( id ) == null ) {
			NLogger.warn( "Environment(id:{}) is not exits.", id );
			return;
		}

		NThreadLocal.set( ENVIRONMENT, id );

	}

	public static void removeEnvironmentId() {
		NThreadLocal.set( ENVIRONMENT, null );
	}

	public static String getEnvironmentId() {
		return (String) NThreadLocal.get( ENVIRONMENT );
	}

	public static void setDefaultEnvironmentId( String id ) {

		if( DatasourceManager.get( id ) == null ) {
			throw new DatabaseConfigurationException( "Environment({}) is not exits.", id );
		}

		NThreadLocal.set( ENVIRONMENT_DEFAULT, id );

	}

	public static void removeDefaultEnvironmentId() {
		NThreadLocal.set( ENVIRONMENT_DEFAULT, null );
	}

	public static String getDefaultEnvironmentId() {
		return (String) NThreadLocal.get( ENVIRONMENT_DEFAULT );
	}

}