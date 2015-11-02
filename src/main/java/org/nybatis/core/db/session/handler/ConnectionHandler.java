package org.nybatis.core.db.session.handler;

import org.nybatis.core.db.session.executor.SqlBean;
import org.nybatis.core.db.session.executor.util.ResultsetController;
import org.nybatis.core.db.session.executor.util.StatementController;
import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public abstract class ConnectionHandler {

	public abstract void execute( Connection connection ) throws Throwable;

	private SqlProperties properties;
	private Connection    connection;

	public void setProperties( SqlProperties properties ) {
		this.properties = properties;
	}

	public void setConnection( Connection connection ) {
		this.connection = connection;
	}

	protected SqlBean getSqlBean( String sqlId ) {
		return getSqlBean( sqlId, null );
	}

	protected SqlBean getSqlBean( String sqlId, Object parameter ) {

		SqlNode sqlNode = null;

		if( SqlRepository.isExist( sqlId ) ) {
			sqlNode = SqlRepository.get( sqlId );
		} else {
			SqlReader sqlReader = new SqlReader();
			sqlNode = sqlReader.read( sqlId );
		}

		return new SqlBean( sqlNode, parameter ).init( properties.clone() );
	}

	protected SqlBean getPagedSqlBean( String sqlId, int start, int end ) {
		return getPagedSqlBean( sqlId, null, start, end );
	}

	protected SqlBean getPagedSqlBean( String sqlId, Object parameter, int start, int end ) {

		SqlBean sqlBean = getSqlBean( sqlId, parameter );

		properties.setPageSql( start, end );

		return sqlBean.init( properties.clone() );

	}

	protected PreparedStatement getPreparedStatement( SqlBean sqlBean, Integer rowFetchSize ) throws SQLException {

		sqlBean.build();

		PreparedStatement preparedStatement = connection.prepareStatement( sqlBean.getSql() );

		StatementController stmtHandler = new StatementController( sqlBean );

		stmtHandler.setParameter( preparedStatement );
		stmtHandler.setRowFetchSize( preparedStatement, rowFetchSize );

		NLogger.debug( ">> {}\n{}", sqlBean, sqlBean.getDebugSql() );

		return preparedStatement;

	}

	protected PreparedStatement getPreparedStatement( String sqlId, Object parameter, Integer rowFetchSize ) throws SQLException {
		return getPreparedStatement( getSqlBean( sqlId, parameter ), rowFetchSize );
	}

	protected PreparedStatement getPreparedStatement( String sqlId, Object parameter ) throws SQLException {
		return getPreparedStatement(sqlId, parameter, null);
	}

	protected PreparedStatement getPreparedStatement( String sqlId ) throws SQLException {
		return getPreparedStatement(sqlId, null, null);
	}

	protected PreparedStatement getPagedPreparedStatement( String sqlId, Object parameter, int start, int end, Integer rowFetchSize ) throws SQLException {
		return getPreparedStatement( getPagedSqlBean( sqlId, parameter, start, end ), rowFetchSize);
	}

	protected PreparedStatement getPagedPreparedStatement( String sqlId, Object parameter, int start, int end ) throws SQLException {
		return getPagedPreparedStatement( sqlId, parameter, start, end, null );
	}

	protected PreparedStatement getPagedPreparedStatement( String sqlId, int start, int end ) throws SQLException {
		return getPagedPreparedStatement( sqlId, null, start, end, null );
	}

	private String getEnvironmentId() {
		return properties.getEnvironmentId();
	}

	protected NList toList( ResultSet resultSet ) throws SQLException {
		return new NList( toList( resultSet, NMap.class ) );
	}

	protected <T> List<T> toList( ResultSet resultSet, Class<T> returnType ) throws SQLException {
		return new ResultsetController( getEnvironmentId() ).toList( resultSet, returnType );
	}

	protected void toList( ResultSet resultSet, RowHandler rowHandler ) throws SQLException {
		new ResultsetController( getEnvironmentId() ).toList( resultSet, rowHandler );
	}

	protected Statement unwrap( Statement statement ) {
		return unwrapProxy( statement );
	}

	protected PreparedStatement unwrap( PreparedStatement preparedStatement ) {
		return unwrapProxy( preparedStatement );
	}

	public CallableStatement unwrap( CallableStatement callableStatement ) {
		return unwrapProxy( callableStatement );
	}

	protected Connection unwrap( Connection connection ) {
		return unwrapProxy( connection );
	}

	protected ResultSet unwrap( ResultSet resultSet ) {
		return unwrapProxy( resultSet );
	}

	private <T> T unwrapProxy( T instance ) {
		return new Reflector().unwrapProxyBean(instance);
	}

}
