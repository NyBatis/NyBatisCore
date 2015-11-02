package org.nybatis.core.db.session.executor.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DriverAttributes;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapper;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;
import org.nybatis.core.db.sql.mapper.implement.ByteArrayMapper;
import org.nybatis.core.exception.unchecked.JdbcImplementException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.util.StringUtil;

public class ResultsetController {

	private String environmentId;

	public ResultsetController( String environmentId ) {
		this.environmentId = environmentId;
	}

    public <T> List<T> toList( ResultSet resultSet, Class<T> returnType ) throws SQLException {

		List<T> list = new ArrayList<>();

		boolean isPrimitiveReturn = DbExecUtils.isPrimitive( returnType );

		if( isPrimitiveReturn ) {

			toList( resultSet, new RowHandler() {
                @SuppressWarnings( "unchecked" )
                public void handle( NMap row ) {
                	list.add( (T) new PrimitiveConverter(row.getBy(0)).cast(returnType) );
				}
			} );

		} else {

			toList( resultSet, new RowHandler() {
				public void handle( NMap row ) {
					list.add( row.toBean(returnType) );
				}
			} );

		}

		return list;

	}

    public void toList( List<NMap> cacheResult, RowHandler rowHandler ) {

    	if( cacheResult == null ) return;

    	for( NMap row : cacheResult ) {
    		rowHandler.handle( row );
    		if( rowHandler.isBreak() ) break;
    	}

    }

    public void toList( ResultSet resultSet, RowHandler rowHandler ) throws SQLException {
		toList( resultSet, rowHandler, null );
	}

	public void toList( ResultSet resultSet, RowHandler rowHandler, Integer rowFetchSize ) throws SQLException {

		try {

			if( resultSet == null ) return;

			Header header= getHaeder( resultSet );

			rowHandler.setHeader( header.keySet() );

    		if( rowFetchSize != null ) {
    			resultSet.setFetchSize( rowFetchSize );
    			NLogger.debug( "rowFetchSize : {}", rowFetchSize );
    		}

    		while( resultSet.next() ) {

    			NMap row = new NMap();

    			for( int i = 0, iCnt = header.size(); i < iCnt; i++ ) {

    				String  key   = header.getName( i );
    				SqlType type  = header.getType( i );

    				try {

    					row.put( key, getResult(type, resultSet, i + 1)  );

    				} catch( SQLException e ) {

    					@SuppressWarnings( "rawtypes" )
						TypeMapperIF mapper = TypeMapper.get( type );

    					String mapperName = mapper == null ? null : mapper.getClass().getName();

    					throw new SQLException( String.format( "%s (colunmName:%s, type:%s, mapper:%s)", e.getMessage(), key, type, mapperName ), e );

    				}

    			}

    			rowHandler.handle( row );

    			if( rowHandler.isBreak() ) break;

    		}

		} catch( SQLException e ) {
			throw e;
		} finally {
			try { if( resultSet != null ) resultSet.close(); } catch( SQLException e ) {}
		}

	}

	public Header getHaeder( ResultSet resultSet ) throws SQLException {

		Header header = new Header();

		if( resultSet == null ) return header;

		ResultSetMetaData metaData = resultSet.getMetaData();

		int columnCount = metaData.getColumnCount();

		List<String>  duplicatedKeys = new ArrayList<>();

		for( int i = 1; i <= columnCount; i++ ) {

			String name = StringUtil.toCamel( metaData.getColumnName( i ) );

			if( header.contains( name ) ) duplicatedKeys.add( name );

			header.add( name, metaData.getColumnType( i ) );

		}

		if( duplicatedKeys.size() > 0 ) {
			throw new SQLException( String.format( "ResultSet has duplicated key. %s", duplicatedKeys) );
		}

		return header;

	}

	private List<NMap> toList( ResultSet resultSet ) throws SQLException {

		List<NMap> list = new ArrayList<>();

        toList( resultSet, new RowHandler() {
        	public void handle( NMap row ) {
        		list.add( row );
        	}
        }, null );

		return list;

	}

	private Object getResult( SqlType sqlType, ResultSet resultSet, int columnIndex ) throws SQLException {

		try {

			Object result = TypeMapper.get( environmentId, sqlType ).getResult( resultSet, columnIndex );

			if( result instanceof ResultSet ) {
				result = toList( (ResultSet) result );
			}

			return result;

		} catch( JdbcImplementException e ) {

			if( sqlType == SqlType.BLOB ) {

				DriverAttributes attributes = DatasourceManager.getAttributes( environmentId );

				if( attributes.enableBlobGet() ) {

					attributes.enableBlobGet( false );
					TypeMapper.put( environmentId, SqlType.BLOB, new ByteArrayMapper() );

					return getResult( sqlType, resultSet, columnIndex );

				}

			}

			throw (SQLException) e.getCause();

		}

	}

}
