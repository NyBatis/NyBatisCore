package org.nybatis.core.db.sql.repository;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.nybatis.core.db.sql.reader.SqlFileReader;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;

/**
 * Sql Repository to store structured sql parsed from xml
 */
public class SqlRepository {

	public static final SqlProperties      EMPTY_PROPERTIES   = new SqlProperties();
	public static Map<String, SqlNode>     sqlRepository      = new Hashtable<>();

	public static boolean isExist( String sqlId ) {
		return sqlId != null && sqlRepository.containsKey( sqlId );
	}

	public static SqlNode get( String sqlId ) {
		if( sqlId == null ) return null;
		return sqlRepository.get( sqlId );
	}

	public static void put( String sqlId, SqlNode sqlNode ) {
		put( sqlId, sqlNode, null );
	}

	public static void put( String sqlId, SqlNode sqlNode, File xmlFile ) {

		if( sqlId == null || sqlNode == null ) return;

		if( isExist( sqlId ) ) {

			String environmentId = sqlNode.getEnvironmentId();

			// if sql id is duplicated, skip later one.
			if( get(sqlId).containsEnvironmentId( environmentId ) ) {

				if( xmlFile == null ) {
					NLogger.warn( "Sql(id:{}) is duplicated. later sql will be ignored.\n\t\t>> {}", sqlId, sqlNode.getSqlSkeleton() );
				} else {
					NLogger.warn( "Sql(id:{}, file:{}) is duplicated.\n{}", sqlId, xmlFile.getPath(), sqlNode.getSqlSkeleton() );
				}

			} else {

				get( sqlId ).addEnvironmentId( environmentId );

				if( xmlFile == null ) {
					NLogger.trace( "Sql({}) has multiple environment({})", sqlId, environmentId );
				} else {
					NLogger.trace( "Sql({}) has multiple environment({}) because of sql mapper file({}).", sqlId, environmentId, xmlFile.getPath() );
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

//	public static void setCacheProperties( String sqlId, String cacheId, Integer flushCycle ) {
//
//		if( ! isExist(sqlId) ) return;
//
//		SqlProperties properties = getProperties( sqlId );
//
//		properties.setCacheId( cacheId );
//		properties.setCacheFlushCycle( flushCycle );
//		properties.isCacheEnable( true );
//
//	}

	public static void setFetchSize( String sqlId, Integer fetchSize ) {

		if( ! isExist(sqlId) ) return;

		SqlProperties properties = getProperties( sqlId );
		properties.setFetchSize( fetchSize );

	}

	public void readFrom( Path path, String environmentId ) {

		if( path == null ) return;

		File file = path.toFile();

		if( ! file.exists() ) return;

		if( file.isDirectory() ) {
			readFromDirectory( file, environmentId );

		} else if( file.isFile() ) {
			readFromFile( file, environmentId );
		}

	}

	private void readFromDirectory( File directory, String environmentId ) {

		List<Path> pathList = FileUtil.getList( directory.getAbsolutePath(), true, false, -1, "**.xml" );

		for( Path path : pathList ) {
			readFromFile( path.toFile(), environmentId );
		}

	}

	private void readFromFile( File file, String environmentId ) {
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
