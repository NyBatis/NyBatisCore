package org.nybatis.core.db.session.executor;

import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.session.executor.util.ResultsetController;
import org.nybatis.core.db.session.executor.util.StatementController;
import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.db.session.handler.SqlHandler;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.worker.Pipe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlExecutor {

	private String  token;
	private SqlBean sqlBean;
	private String  sqlId;

	public SqlExecutor( String token, SqlBean sqlBean ) {
		this.token   = token;
		this.sqlBean = sqlBean;
		this.sqlId   = sqlBean.getSqlId();
	}

	private void execute( SqlBean sqlBean, SqlHandler sqlHandler ) {

		Connection conn = null;

		try {

			DbUtils.logCaller();

			conn = TransactionManager.getConnection( token, sqlBean.getEnvironmentId() );

			if( NLogger.isTraceEnabled() ) {
				NLogger.trace( ">> transaction token : [{}], isBegun : {}", token, TransactionManager.isBegun( token ) );
			}

			sqlHandler.run( sqlBean, conn );

		} catch( SQLException e ) {

			SqlException exception = new SqlException( e, ">> {} Error (code:{}) {}\n>> Error SQL :\n{}", sqlBean, e.getErrorCode(), e.getMessage(), sqlBean.getDebugSql() );
			exception.setErrorCode( e.getErrorCode() );
			exception.setDatabaseName( sqlBean.getDatasourceAttribute().getDatabase() );

			NLogger.trace( exception );

	        throw exception;

		} catch( ClassCastingException e ) {

			SqlException exception = new SqlException( e, "{} parameter binding error. ({})\n{}", sqlBean, e.getMessage(), sqlBean.getDebugSql() );
			exception.setDatabaseName( sqlBean.getDatasourceAttribute().getDatabase() );

			NLogger.trace( exception );

			throw exception;

		} catch( Exception e ) {
			NLogger.error( ">> {}.\n>> SQL to try :\n{}>> parameter :\n{}\n>> Stack trace :\n{}",
					e.getMessage(), sqlBean.getDebugSql(), sqlBean.getParams().toDebugString( true, true ), e );
			throw e;
        } finally {
			TransactionManager.releaseConnection( token, conn );
        }

	}

	private void executeQuery( SqlBean sqlBean, RowHandler rowHandler, Integer rowFetchSize ) {

		execute( sqlBean, ( injectedSqlBean, connection ) -> {

            PreparedStatement preparedStatement = connection.prepareStatement( injectedSqlBean.getSql() );
            StatementController stmtHandler = new StatementController( injectedSqlBean );
            stmtHandler.setParameter( preparedStatement );
            stmtHandler.setFetchSize( preparedStatement, rowFetchSize );
			stmtHandler.setLobPrefetchSize( preparedStatement );
            ResultSet rs = stmtHandler.executeQuery( preparedStatement );

            new ResultsetController( injectedSqlBean.getEnvironmentId() ).toList( rs, rowHandler, true );

        } );

	}

    public NMap call( Class<?>... resultSetReturnType ) {

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

		return pipe.get();

	}

	public <T> T call( Class<T> returnType, Class<?>... resultSetReturnType ) {

		NMap result = call( resultSetReturnType );

		switch( result.size() ) {

			case 0 :
				try {
					return returnType.newInstance();
				} catch( InstantiationException | IllegalAccessException e ) {
					throw new ClassCastingException( e, "ClassCastingException at converting result of {}, {}", sqlBean, e.getMessage() );
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
		selectKeys();
		sqlBean.build();
		executeQuery( sqlBean, rowHandler, sqlBean.getProperties().getFetchSize() );
	}


	public NMap select() {
		selectKeys();
		sqlBean.build();
		Pipe<NMap> pipe = new Pipe<>( new NMap() );
		executeQuery( sqlBean, new RowHandler() {
			public void handle( NMap row ) {
				pipe.set( row );
				stop();
			}
		}, 1 );
		return pipe.get();
	}

	public int update() {

		selectKeys();
		sqlBean.build();
		Pipe<Integer> pipe = new Pipe<>();

		// When update, Transaction is startup automatically
		TransactionManager.begin( token );

		execute( sqlBean, ( injectedSqlBean, connection ) -> {

            PreparedStatement preparedStatement = connection.prepareStatement( injectedSqlBean.getSql() );
            StatementController stmtHandler = new StatementController( injectedSqlBean );
            stmtHandler.setParameter( preparedStatement );
            int affectedCount = stmtHandler.executeUpdate( preparedStatement );
            pipe.set( affectedCount );

        });

		return pipe.get();

	}

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
	            throw new ClassCastingException( e, "ClassCastingException at converting result of {}, {}", sqlBean, e.getMessage() );
            }

		}

	}

    public NMap selectKeys() {
		return new SelectKeyExecutor( token ).selectKeys( sqlBean );
	}

}