package org.nybatis.core.db.session.executor;

import org.nybatis.core.db.cache.CacheManager;
import org.nybatis.core.db.session.executor.util.CacheResultsetController;
import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.session.executor.util.ResultsetController;
import org.nybatis.core.db.session.executor.util.StatementController;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.db.session.handler.SqlHandler;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.ClassCastException;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.worker.Pipe;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SqlExecutor {

	private String  token;
	private SqlBean sqlBean;

	private String  sqlId;
	private boolean isCacheEnable = false;
	private boolean isCacheClear  = false;

	public SqlExecutor( String token, SqlBean sqlBean ) {

		this.token   = token;
		this.sqlBean = sqlBean;
		this.sqlId   = sqlBean.getSqlId();

		isCacheEnable = CacheManager.isCacheSql( sqlId );
		isCacheClear  = sqlBean.getProperties().isCacheClear();

	}

	private void execute( SqlBean sqlBean, SqlHandler sqlHandler ) {

		Connection conn = null;

		try {

			conn = TransactionManager.getConnection( token, sqlBean.getEnvironmentId() );

			if( NLogger.isTraceEnabled() ) {
				NLogger.trace( "transaction token : [{}], isBegun : {}", token, TransactionManager.isBegun( token ) );
			}

			boolean autoCommitConnection = conn.getAutoCommit();
			boolean autoCommitTemporary  = sqlBean.getProperties().isAutocommit();
			boolean autoCommitForce      = autoCommitConnection == false && autoCommitTemporary == true;

			if( autoCommitForce ) {
				conn.setAutoCommit( true );
			}

			sqlHandler.run( sqlBean, conn );

			if( ( autoCommitConnection || autoCommitTemporary ) && TransactionManager.isBegun(token) ) {
				TransactionManager.commit( token );
				NLogger.trace( "auto-committed" );
			}

			if( autoCommitForce ) {
				conn.setAutoCommit( false );
			}

		} catch( SQLException e ) {

			SqlException exception = new SqlException( e, ">> {} Error (code:{}) {}\n>> Error SQL :\n{}", sqlBean, e.getErrorCode(), e.getMessage(), sqlBean.getDebugSql() );

			exception.setErrorCode( e.getErrorCode() );

	        throw exception;

		} catch( ClassCastException e ) {
			throw new SqlException( "{} parameter binding error. ({})\n{}", sqlBean, e.getMessage(), sqlBean.getDebugSql() );

        } finally {
			TransactionManager.releaseConnection( token, conn );
        }

	}

	private void executeQuery( SqlBean sqlBean, RowHandler rowHandler, Integer rowFetchSize ) {

		sqlBean.getProperties().isAutocommit( false );

		execute( sqlBean, ( injectedSqlBean, connection ) -> {

            PreparedStatement preparedStatement = connection.prepareStatement( injectedSqlBean.getSql() );

            StatementController stmtHandler = new StatementController( injectedSqlBean );

            stmtHandler.setParameter( preparedStatement );
            stmtHandler.setFetchSize( preparedStatement, rowFetchSize );
			stmtHandler.setLobPrefetchSize( preparedStatement );

            ResultSet rs = stmtHandler.executeQuery( preparedStatement );

            new ResultsetController( injectedSqlBean.getEnvironmentId() ).toList( rs, rowHandler );

        } );

	}

	private void executeQueryForCache( String method, RowHandler rowHandler, Integer rowFetchSize ) {

		List<NMap> result = new ArrayList<>();

		RowHandler rowHandlerForCache = new RowHandler() {
			public void handle( NMap row ) {
				result.add( row );
			}
		};

		executeQuery( sqlBean, rowHandlerForCache, rowFetchSize );

		Set header = rowHandlerForCache.getHeader();

		setCache( method, new NList(result, header) );

		rowHandler.setHeader( header );

		new ResultsetController( sqlBean.getEnvironmentId() ).toList( result, rowHandler );

	}

    public NMap call( Class<?>... resultSetReturnType ) {

		if( isCacheEnable && ! isCacheClear) {
			NMap cacheValue = (NMap) getCache( "call" );
			if( cacheValue != null ) return cacheValue;
		}

    	Pipe<NMap> pipe = new Pipe<>();

		selectKeys();

		sqlBean.build();

		execute( sqlBean, ( injectedSqlBean, connection ) -> {

            CallableStatement callableStatement = connection.prepareCall( injectedSqlBean.getSql().trim() );

            StatementController stmtHandler = new StatementController( injectedSqlBean );

            stmtHandler.setParameter( callableStatement );

            boolean hasResultSet = stmtHandler.execute( callableStatement );

            NMap result = stmtHandler.getOutParameter( injectedSqlBean, callableStatement, hasResultSet, resultSetReturnType );

            pipe.set( result );

        } );

		if( isCacheEnable ) {
			setCache( "call", pipe.get() );
		}

		return pipe.get();

	}

	public <T> T call( Class<T> returnType, Class<?>... resultSetReturnType ) {

		NMap result = call( resultSetReturnType );

		switch( result.size() ) {

			case 0 :
				try {
					return returnType.newInstance();
				} catch( InstantiationException | IllegalAccessException e ) {
					throw new ClassCastException( e, "ClassCastException at converting result of {}, {}", sqlBean, e.getMessage() );
				}
			case 1 :
				return Reflector.toBeanFrom( result.getByIndex( 0 ), returnType );

		}

		if( DbUtils.isPrimitive( returnType ) ) {
			return Reflector.toBeanFrom( result.getByIndex( 0 ), returnType );
		} else {
			return result.toBean( returnType );
		}

	}

	public void selectList( RowHandler rowHandler ) {

		if( isCacheEnable && ! isCacheClear) {

			@SuppressWarnings( "unchecked" )
            NList cacheValue = (NList) getCache( "selectList" );

			if( cacheValue != null ) {
				new CacheResultsetController().toList( cacheValue, rowHandler );
				return;
			}

		}

		selectKeys();

		sqlBean.build();

		if( isCacheEnable ) {
			executeQueryForCache( "selectList", rowHandler, null );
		} else {
			executeQuery( sqlBean, rowHandler, null );
		}

	}


	public NMap select() {

		if( isCacheEnable && ! isCacheClear) {
			NMap cacheValue = (NMap) getCache( "select" );
			if( cacheValue != null ) return cacheValue;
		}

		selectKeys();

		sqlBean.build();

		Pipe<NMap> pipe = new Pipe<>( new NMap() );

		executeQuery( sqlBean, new RowHandler() {
			public void handle( NMap row ) {
				pipe.set( row );
				stop();
			}
		}, 1 );


		if( isCacheEnable ) {
			setCache( "select", pipe.get() );
		}

		return pipe.get();

	}

	public int update() {

		Pipe<Integer> pipe = new Pipe<>();

		selectKeys();

		sqlBean.build();

		// When update, Transaction is startup automatically
		TransactionManager.begin( token );

		execute( sqlBean, ( injectedSqlBean, connection ) -> {

            PreparedStatement preparedStatement = connection.prepareStatement( injectedSqlBean.getSql() );

            StatementController stmtHandler = new StatementController( injectedSqlBean );

            stmtHandler.setParameter( preparedStatement );

            int affectedCount = stmtHandler.executeUpdate( preparedStatement );

            pipe.set( affectedCount );

        } );

		return pipe.get();

	}


	@SuppressWarnings( "unchecked" )
    public <T> List<T> selectList( Class<T> returnType ) {

		List<NMap> resultSet = selectList();

		List<T> result = new ArrayList<>();

		boolean isPrimitiveReturn = DbUtils.isPrimitive( returnType );

		if( isPrimitiveReturn ) {

			for( NMap row : resultSet ) {
				result.add( (T) new PrimitiveConverter(row.getByIndex( 0 )).cast(returnType) );
			}

		} else {

			for( NMap row : resultSet ) {
				result.add( row.toBean( returnType ) );
			}

		}

		return result;

	}

	public List<NMap> selectList() {
		return selectNList().toList();
	}

	public NList selectNList() {

		List<NMap> result = new ArrayList<>();

		RowHandler rowHandler = new RowHandler() {
			public void handle( NMap row ) {
				result.add( row );
			}
		};

		selectList( rowHandler );

		return new NList( result, rowHandler.getHeader() );

	}

	@SuppressWarnings( "unchecked" )
    public <T> T select( Class<T> returnType ) {

		NMap result = select();

		if( DbUtils.isPrimitive( returnType ) ) {

			PrimitiveConverter converter;

			if( result == null || result.size() == 0 ) {
				converter = new PrimitiveConverter();
			} else {
				converter = new PrimitiveConverter( result.getByIndex( 0 ) );
			}

			return (T) converter.cast( returnType );

		} else {

			try {
	            return result == null ? returnType.newInstance() : result.toBean( returnType );
            } catch( InstantiationException | IllegalAccessException e ) {
	            throw new ClassCastException( e, "ClassCastException at converting result of {}, {}", sqlBean, e.getMessage() );
            }

		}

	}

    public NMap selectKeys() {
		return new SelectKeyExecutor( token ).selectKeys( sqlBean );
	}

	private Object getCache( String method ) {
		sqlBean.build();
		return CacheManager.getCache( sqlId ).get( sqlBean.getCacheKey(method) );
	}

	private void setCache( String method, Object value ) {
		CacheManager.getCache( sqlId ).put( sqlBean.getCacheKey(method), value );
	}

}