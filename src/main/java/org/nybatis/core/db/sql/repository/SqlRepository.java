package org.nybatis.core.db.sql.repository;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.nybatis.core.db.sql.reader.SqlFileReader;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.validation.Validator;

/**
 * Sql Repository to store structured sql parsed from xml
 */
public class SqlRepository {

	public static final SqlProperties EMPTY_PROPERTIES = new SqlProperties();
	public static Map<String,SqlNode> sqlRepository    = new Hashtable<>();

	public static boolean isExist( String sqlId ) {
		return sqlId != null && sqlRepository.containsKey( sqlId );
	}

	public static SqlNode get( String sqlId ) {
		if( sqlId == null ) return null;
		return sqlRepository.get( sqlId );
	}

	public static void remove( String sqlId ) {
		sqlRepository.remove( sqlId );
	}

	public static void put( String sqlId, SqlNode sqlNode ) {
		put( sqlId, sqlNode, null );
	}

	public static void put( String sqlId, SqlNode sqlNode, String xmlFile ) {

		if( sqlId == null || sqlNode == null ) return;

		if( isExist(sqlId) ) {

			String environmentId = sqlNode.getEnvironmentId();

			// if sql id is duplicated, skip later one.
			if( get(sqlId).containsEnvironmentId( environmentId ) ) {
				if( get(sqlId).getSqlHash() != sqlNode.getSqlHash() ) {
					if( xmlFile == null ) {
						NLogger.warn( "Sql(id:{}) is duplicated. later sql will be ignored.\n\t\t>> {}", sqlId, sqlNode.getSqlSkeleton() );
					} else {
						NLogger.warn( "Sql(id:{}, file:{}) is duplicated.\n{}", sqlId, xmlFile, sqlNode.getSqlSkeleton() );
					}
				}
			} else {
				get( sqlId ).addEnvironmentId( environmentId );
				if( xmlFile == null ) {
					NLogger.trace( "Sql({}) has multiple environment({})", sqlId, environmentId );
				} else {
					NLogger.trace( "Sql({}) has multiple environment({}) because of sql mapper file({}).", sqlId, environmentId, xmlFile );
				}
			}


		} else {
			sqlRepository.put( sqlId, sqlNode );
		}

	}

	public static SqlProperties getProperties( String sqlId ) {

		if( sqlId == null ) return EMPTY_PROPERTIES;

		SqlNode sqlNode = sqlRepository.get( sqlId );

		return sqlNode == null ? EMPTY_PROPERTIES : sqlNode.getProperties();

	}

	public static void setFetchSize( String sqlId, Integer fetchSize ) {

		if( ! isExist(sqlId) ) return;

		SqlProperties properties = getProperties( sqlId );
		properties.setFetchSize( fetchSize );

	}

	public void readFromDirectory( String directory, String environmentId ) {

		if( FileUtil.exists( directory ) ) {
			NLogger.trace( "read sql in directory (environment:{}, path:{})", environmentId, directory );
			List<Path> pathList = FileUtil.search( directory, true, false, -1, "**.xml" );
			for( Path path : pathList ) {
				readFromFile( path.toString(), environmentId );
			}
		}

		List<String> resourceNames = ClassUtil.findResources( directory + "/**.xml" );

		if( Validator.isNotEmpty(resourceNames) )
			NLogger.trace( "read sql in classpath (environment:{}, path:{})", environmentId, directory );

		for( String resourceName : resourceNames ) {
			readFromFile( resourceName, environmentId );
		}

	}

	public void readFromFile( String file, String environmentId ) {
		NLogger.trace( "configurate sql (environmentId: {}, sqlFile: {})", environmentId, file );
		new SqlFileReader( file, environmentId ).read();
	}

	public String toString() {

		String LINE = "------------------------------------------------------------------------\n";

		StringBuilder sb = new StringBuilder();

		sb.append( "\n>> Sql Repository : \n\n" );

		for( SqlNode sql : sqlRepository.values() ) {
			sb.append( LINE ).append( sql );
		}

		sb.append( LINE );

		return sb.toString();

	}

	public Collection<SqlNode> getSqls() {
		return sqlRepository.values();
	}

}
