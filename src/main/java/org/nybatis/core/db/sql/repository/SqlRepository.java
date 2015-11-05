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

	public static final SqlProperties EMPTY_PROPERTIES   = new SqlProperties();

	public static Map<String, SqlNode>     sqlRepository      = new Hashtable<>();

	// loaded file information. it must be cleared when configration build completed.
	public static Map<String, LoadedFile>  sqlRepositoryFiles = new Hashtable<>();

	public static boolean isExist( String sqlId ) {
		return sqlId != null && sqlRepository.containsKey( sqlId );
	}

	public static SqlNode get( String sqlId ) {
		if( sqlId == null ) return null;
		return sqlRepository.get( sqlId );
	}

	public static void put( String sqlId, SqlNode sqlNode ) {
		if( sqlId == null || sqlNode == null ) return;
		sqlRepository.put( sqlId, sqlNode );
	}

	public static SqlProperties getProperties( String sqlId ) {

		if( sqlId == null ) return EMPTY_PROPERTIES;

		SqlNode sqlNode = sqlRepository.get( sqlId );

		return sqlNode == null ? EMPTY_PROPERTIES : sqlNode.getProperties();

	}

	public static void setCacheProperties( String sqlId, String cacheId, Integer cacheFlushCycle ) {

		if( ! isExist(sqlId) ) return;

		SqlProperties properties = getProperties( sqlId );

		properties.setCacheId( cacheId );
		properties.setCacheFlushCycle( cacheFlushCycle );
		properties.isCacheEnable( true );

	}

	public static void setRowFetchCountProperties( String sqlId, Integer rowFetchCount ) {

		if( ! isExist(sqlId) ) return;

		SqlProperties properties = getProperties( sqlId );
		properties.setFetchSize( rowFetchCount );

	}

	public void readFrom( Path path, String datasourceId ) {

		if( path == null ) return;

		File file = path.toFile();

		if( ! file.exists() ) return;

		if( file.isDirectory() ) {
			readFromDirectory( file, datasourceId );

		} else if( file.isFile() ) {
			readFromFile( file, datasourceId );
		}

	}

	private void readFromDirectory( File directory, String datasourceId ) {

		List<Path> pathList = FileUtil.getList( directory.getAbsolutePath(), true, false, -1, "**.xml" );

		for( Path path : pathList ) {
			readFromFile( path.toFile(), datasourceId );
		}

	}

	private void readFromFile( File file, String datasourceId ) {
		NLogger.debug( "configurate sql (datasourceId: {}, sqlFile: {})", datasourceId, file );
		new SqlFileReader( file, datasourceId ).read();
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

	public void clearFileLoadingLog() {
		sqlRepositoryFiles.clear();
	}

}
