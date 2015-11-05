package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapper;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;
import org.nybatis.core.db.sql.mapper.implement.ByteArrayMapper;
import org.nybatis.core.db.sql.sqlMaker.BindParam;
import org.nybatis.core.db.sql.sqlMaker.BindStruct;
import org.nybatis.core.exception.unchecked.ClassCastException;
import org.nybatis.core.exception.unchecked.JdbcImplementException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StopWatcher;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatementController {

	private SqlBean sqlBean;

	public StatementController( SqlBean sqlBean ) {
		this.sqlBean = sqlBean;
	}

	public ResultSet executeQuery( PreparedStatement statement ) throws SQLException {

		StopWatcher watcher = new StopWatcher();

		ResultSet resultSet = statement.executeQuery();

		NLogger.debug( ">> {} elapsed : [{}]ms\n{}", sqlBean, watcher.elapsedMiliSeconds(), sqlBean.getDebugSql() );

		return resultSet;

	}

	public int executeUpdate( PreparedStatement statement ) throws SQLException {

		StopWatcher watcher = new StopWatcher();

		int affectedCount = statement.executeUpdate();

		NLogger.debug( ">> {} elapsed : [{}]ms\n{}", sqlBean, watcher.elapsedMiliSeconds(), sqlBean.getDebugSql() );

		return affectedCount;

	}

	/**
	 * execute CallableStatement
	 *
	 * @param statement CallableStatement
	 * @return true if the return has resultSet, false if the first result is an update count or there is no result

	 * @throws SQLException
	 */
	public boolean execute( CallableStatement statement ) throws SQLException {

		StopWatcher watcher = new StopWatcher();

		boolean result = statement.execute();

		NLogger.debug( ">> {} elapsed : [{}]ms\n{}", sqlBean, watcher.elapsedMiliSeconds(), sqlBean.getDebugSql() );

		return result;

	}

	public void setParameter( CallableStatement statement ) throws SQLException, ClassCastException {

		for( int i = 0, iCnt = sqlBean.getBindParams().size(); i < iCnt; i++ ) {

			BindStruct struct = sqlBean.getBindStructs().get( i );
			BindParam  param  = sqlBean.getBindParams().get( i );

			try {
    			if( struct.isOut() ) {
    				TypeMapper.get( sqlBean.getEnvironmentId(), struct.getType() ).setOutParameter( statement, i + 1 );
    			} else {
    				i = setParameter( sqlBean.getEnvironmentId(), statement, i, param );
    			}
			} catch( ClassCastException e ) {
				throw new org.nybatis.core.exception.unchecked.ClassCastException( e, "{}, index:{}, value:{}", e.getMessage(), i, param.getValue() );
			}

		}

	}

	public void setParameter( PreparedStatement statement ) throws SQLException {

		int index = 1;

		for( BindParam value : sqlBean.getBindParams() ) {

			try {
				index = setParameter( sqlBean.getEnvironmentId(), statement, index, value );
    		} catch( ClassCastException e ) {
    			throw new ClassCastException( e, "{}, index:{}, value:{}", e.getMessage(), index, value );
    		}

		}

	}

	public void setFetchSize( Statement statement, Integer size ) throws SQLException {

		if( size == null && sqlBean.getProperties().hasSpecificFetchSize() ) {
			size = sqlBean.getProperties().getFetchSize();
		}

		if( size != null ) {
			statement.setFetchSize( size );
			NLogger.trace( "setFetchSize : {}", size );
		}

	}

	public void setLobPrefetchSize( Statement statement ) throws SQLException {
		setLobPrefetchSize( statement, null );
	}

	public void setLobPrefetchSize( Statement statement, Integer size ) throws SQLException {

		if( ! sqlBean.getDatasourceAttribute().enableToDoLobPrefetch() ) return;

		if( size == null && sqlBean.getProperties().hasSpecificLobPreFetchSize() ) {
			size = sqlBean.getProperties().getLobPrefetchSize();
		}

		if( size != null ) {

			try {

				OracleStatementController oracleStatementController = new OracleStatementController();
				oracleStatementController.setLobPrefetchCount( statement, size );
				NLogger.trace( "setLobPreFetchSize : {}", size );

			} catch( NoClassDefFoundError e ) {
				NLogger.info( "Environment(id:{}) is not support Lob Prefetch.", sqlBean.getEnvironmentId() );
				sqlBean.getDatasourceAttribute().enableToDoLobPrefetch( false );
			}

		}

	}

	public NMap getOutParameter( SqlBean sqlBean, CallableStatement statement, boolean hasResultSet, Class<?>... resultSetReturnType ) throws SQLException {

		NMap result = new NMap();

		int indexOfResultSetReturnTypes = 0;

		if( hasResultSet ) {
			ResultSet rs = statement.getResultSet();
			if( rs != null ) {
				result.put( "#" + indexOfResultSetReturnTypes, new ResultsetController( sqlBean.getEnvironmentId()).toList(rs, getResultSetReturnType(indexOfResultSetReturnTypes++, resultSetReturnType)) );
			}
		}

		while( statement.getMoreResults() ) {

			ResultSet rs = statement.getResultSet();
			if( rs == null ) break;

			result.put( "#" + indexOfResultSetReturnTypes, new ResultsetController( sqlBean.getEnvironmentId()).toList(rs, getResultSetReturnType(indexOfResultSetReturnTypes++, resultSetReturnType)) );
		}

		int index = 0;

		for( BindStruct struct : sqlBean.getBindStructs() ) {

			index++; if( ! struct.isOut() ) continue;

			SqlType sqlType = getOutParameterType( statement, index, struct );

			if( struct.getType().toCode() == SqlType.RS.toCode() ) {

				ResultSet rs = (ResultSet) getResult( sqlType, statement, index );

				result.put( struct.getKey(), new ResultsetController( sqlBean.getEnvironmentId()).toList(rs, getResultSetReturnType(indexOfResultSetReturnTypes++, resultSetReturnType)) );

			} else {
				result.put( struct.getKey(), getResult( sqlType, statement, index ) );
			}

		}

		return result;

	}

	private SqlType getOutParameterType( CallableStatement statement, int parameterIndex, BindStruct struct ) {

		SqlType outParamType;

		if( sqlBean.getDatasourceAttribute().enableToGetParameterType() ) {

			try {

				int outParamTypeCode = statement.getParameterMetaData().getParameterType( parameterIndex );
				outParamType = SqlType.find( outParamTypeCode );

				NLogger.debug( ">> OutParameter Type : {}, {}, {}", struct.getKey(), outParamType, outParamTypeCode );

			} catch( SQLException e ) {
				sqlBean.getDatasourceAttribute().enableToGetParameterType( false );
				outParamType = struct.getType();
			}

		} else {
			outParamType = struct.getType();
		}

        return outParamType;

	}

	private Class<?> getResultSetReturnType( int index, Class<?>... resultSetReturnType ) {

		if( resultSetReturnType.length == 0 ) return NMap.class;

		index = Math.min( index, resultSetReturnType.length - 1 );

		return resultSetReturnType[ index ];

	}

	private Object getResult( SqlType sqlType, CallableStatement statement, int paramIndex ) throws SQLException {

		try {
			return TypeMapper.get( sqlBean.getEnvironmentId(), sqlType ).getResult( statement, paramIndex );
		} catch( JdbcImplementException e ) {

			if( sqlType == SqlType.BLOB ) {

				if( sqlBean.getDatasourceAttribute().enableToGetBlob() ) {

					sqlBean.getDatasourceAttribute().enableToGetBlob( false );
					TypeMapper.put( sqlBean.getEnvironmentId(), SqlType.BLOB, new ByteArrayMapper() );

					return getResult( sqlType, statement, paramIndex );

				}

			}

			throw (SQLException) e.getCause();

		}

	}

	/**
	 * Bind parameter to Statement
	 *
	 * @param environmentId Nybatis Database Configuration Environmen ID
	 * @param statement PreparesdStatement or CallableStatement
	 * @param paramIndex index for binding parameter at ?
	 * @param value value to binding parameter
	 * @return next parameter binding index
	 * @throws SQLException
	 */
	private int setParameter( String environmentId, Statement statement, int paramIndex, BindParam value ) throws SQLException {

		try {

			TypeMapperIF<Object> typeMapper = TypeMapper.get( environmentId, value.getType() );
			setParameter( typeMapper, statement, paramIndex++, value );

			return paramIndex;

		} catch( JdbcImplementException e ) {

			if( value.getType() == SqlType.BLOB ) {

				DatabaseAttribute attributes = DatasourceManager.getAttributes( environmentId );

				if( attributes.enableToGetBlob() ) {

					attributes.enableToGetBlob( false );
					TypeMapper.put( environmentId, SqlType.BLOB, new ByteArrayMapper() );

					return setParameter( environmentId, statement, paramIndex, value );

				}

			}

			throw (SQLException) e.getCause();

		}

	}

	private void setParameter( TypeMapperIF<Object> typeMapper, Statement statement, int paramIndex, BindParam value ) throws SQLException {

		if( statement instanceof PreparedStatement ) {
			typeMapper.setParameter( (PreparedStatement) statement, paramIndex, value.getValue() );
		} else if( statement instanceof CallableStatement ) {
			typeMapper.setParameter( (CallableStatement) statement, paramIndex, value.getValue() );
		}

	}


}
