package org.nybatis.core.db.session.executor.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapper;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;
import org.nybatis.core.exception.unchecked.JdbcImplementException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
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
		return toList( resultSet, returnType, true );
	}

    public <T> List<T> toList( ResultSet resultSet, Class<T> returnType, boolean raiseErrorOnKeyDuplication ) throws SQLException {

		List<T> list = new ArrayList<>();

		boolean isPrimitiveReturn = DbUtils.isPrimitive( returnType );

		if( isPrimitiveReturn ) {

			toList( resultSet, new RowHandler() {
                @SuppressWarnings( "unchecked" )
                public void handle( NMap row ) {
                	list.add( (T) new PrimitiveConverter(row.getByIndex( 0 )).cast(returnType) );
				}
			}, raiseErrorOnKeyDuplication );

		} else {
			toList( resultSet, new RowHandler() {
				public void handle( NMap row ) {
					list.add( row.toBean(returnType) );
				}
			}, raiseErrorOnKeyDuplication );
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

    public void toList( ResultSet resultSet, RowHandler rowHandler, boolean raiseErrorOnKeyDuplication ) throws SQLException {
		toList( resultSet, rowHandler, null, raiseErrorOnKeyDuplication );
	}

	public void toList( ResultSet resultSet, RowHandler rowHandler, Integer rowFetchSize, boolean raiseErrorOnKeyDuplication ) throws SQLException {

		if( resultSet == null ) return;

		try {

			Header header= getHaeder( resultSet, raiseErrorOnKeyDuplication );
			rowHandler.setHeader( header.keySet() );

    		if( rowFetchSize != null ) {
    			resultSet.setFetchSize( rowFetchSize );
				NLogger.trace( "rowFetchSize : {}", rowFetchSize );
    		}

    		while( resultSet.next() ) {

    			NMap row = new NMap();

    			for( int i = 0, iCnt = header.size(); i < iCnt; i++ ) {

    				String  key   = header.getName( i );
    				SqlType type  = header.getType( i );
					if( key == null ) continue;

    				try {
    					row.put( key, getResult(type, resultSet, i + 1)  );
    				} catch( SQLException e ) {
						TypeMapperIF mapper = TypeMapper.get( environmentId, type );
    					String mapperName = mapper == null ? null : mapper.getClass().getName();
    					throw new SQLException( String.format( "%s (colunmName:%s, type:%s, mapper:%s)", e.getMessage(), key, type, mapperName ), e );
					} catch( ClassCastException e ) {
						// if sqlType in resultSet has error because of abnormal JDBC driver implements
						SqlType alternativeType = SqlType.find( String.class );
						try {
							row.put( key, getResult( alternativeType, resultSet, i + 1 ) );
						} catch( ClassCastException error ) {
							TypeMapperIF mapper = TypeMapper.get( environmentId, type );
							String mapperName = mapper == null ? null : mapper.getClass().getName();
							throw new SQLException( String.format( "%s (colunmName:%s, type:%s, mapper:%s)", e.getMessage(), key, type, mapperName ), e );
						}
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

	public Header getHaeder( ResultSet resultSet, boolean raiseErrorOnKeyDuplication ) throws SQLException {

		Header header = new Header();
		if( resultSet == null ) return header;

		ResultSetMetaData metaData = resultSet.getMetaData();
		int columnCount = metaData.getColumnCount();
		List<String>  duplicatedKeys = new ArrayList<>();

		for( int i = 1; i <= columnCount; i++ ) {
			String name = StringUtil.toCamel( metaData.getColumnLabel(i) );
			if( header.contains( name ) ) duplicatedKeys.add( name );
			header.add( name, metaData.getColumnType( i ) );
		}

		if( duplicatedKeys.size() > 0 ) {
			if( raiseErrorOnKeyDuplication ) {
				throw new SQLException( String.format( "ResultSet has duplicated key. %s", duplicatedKeys) );
			} else {
				NLogger.warn( "ResultSet has duplicated key. {}", duplicatedKeys );
			}
		}

		return header;

	}

	private List<NMap> toList( ResultSet resultSet, boolean raiseErrorOnKeyDuplication ) throws SQLException {

		List<NMap> list = new ArrayList<>();

        toList( resultSet, new RowHandler() {
        	public void handle( NMap row ) {
        		list.add( row );
        	}
        }, null, raiseErrorOnKeyDuplication );

		return list;

	}

	private Object getResult( SqlType sqlType, ResultSet resultSet, int columnIndex ) throws SQLException {
		try {
			Object result = TypeMapper.get( environmentId, sqlType ).getResult( resultSet, columnIndex );
			if( result instanceof ResultSet ) {
				result = toList( (ResultSet) result, true );
			}
			return result;
		} catch( JdbcImplementException | SQLException e ) {
			TypeMapper.setUnimplementedMapper( environmentId, sqlType, e );
			return getResult( sqlType, resultSet, columnIndex );
		} catch( NullPointerException e ) {
			throw new SqlConfigurationException( "type mapper is not valid. (SqlType:{}, environmentId:{}, column:{}, rsValue:{}",
				sqlType, environmentId, columnIndex, resultSet.getString(columnIndex)
			);
		}
	}

}
