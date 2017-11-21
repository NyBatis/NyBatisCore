package org.nybatis.core.db.session.handler;

import java.sql.*;
import java.util.List;
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

	protected SqlBean getSqlBean( String sqlOrSqlId ) {
		return getSqlBean( sqlOrSqlId, null );
	}

	protected SqlBean getSqlBean( String sqlOrSqlId, Object parameter ) {

		SqlNode sqlNode;

		if( SqlRepository.isExist( sqlOrSqlId ) ) {
			sqlNode = SqlRepository.get( sqlOrSqlId );
		} else {
			SqlReader sqlReader = new SqlReader();
			sqlNode = sqlReader.read( sqlOrSqlId );
		}

		return new SqlBean( sqlNode, parameter ).init( properties.clone() );
	}

	protected SqlBean getPagedSqlBean( String sqlOrSqlId, int start, int end ) {
		return getPagedSqlBean( sqlOrSqlId, null, start, end );
	}

	protected SqlBean getPagedSqlBean( String sqlOrSqlId, Object parameter, int start, int end ) {
		SqlBean sqlBean = getSqlBean( sqlOrSqlId, parameter );
		properties.setPageSql( start, end );
		return sqlBean.init( properties.clone() );
	}

	protected PreparedStatement getPreparedStatement( SqlBean sqlBean, Integer fetchSize, Integer lobPrefetchSize ) throws SQLException {

		sqlBean.build();
		PreparedStatement preparedStatement = connection.prepareStatement( sqlBean.getSql() );

		StatementController stmtHandler = new StatementController( sqlBean );
		stmtHandler.setParameter( preparedStatement );
		stmtHandler.setFetchSize( preparedStatement, fetchSize );
		stmtHandler.setLobPrefetchSize( preparedStatement, lobPrefetchSize );

		NLogger.debug( ">> {}\n{}", sqlBean, sqlBean.getDebugSql() );
		return preparedStatement;

	}

	protected PreparedStatement getPreparedStatement( String sqlId, Object parameter, Integer fetchSize, Integer lobPrefetchSize ) throws SQLException {
		return getPreparedStatement( getSqlBean( sqlId, parameter ), fetchSize, lobPrefetchSize );
	}

	protected PreparedStatement getPreparedStatement( String sqlId, Object parameter, Integer fetchSize ) throws SQLException {
		return getPreparedStatement( getSqlBean( sqlId, parameter ), fetchSize, null );
	}

	protected PreparedStatement getPreparedStatement( String sqlId, Object parameter ) throws SQLException {
		return getPreparedStatement(sqlId, parameter, null);
	}

	protected PreparedStatement getPreparedStatement( String sqlId ) throws SQLException {
		return getPreparedStatement(sqlId, null, null);
	}

	protected PreparedStatement getPagedPreparedStatement( String sqlId, Object parameter, int start, int end, Integer fetchSize, Integer lobPrefetchSize ) throws SQLException {
		return getPreparedStatement( getPagedSqlBean( sqlId, parameter, start, end ), fetchSize, lobPrefetchSize );
	}

	protected PreparedStatement getPagedPreparedStatement( String sqlId, Object parameter, int start, int end, Integer fetchSize ) throws SQLException {
		return getPreparedStatement( getPagedSqlBean( sqlId, parameter, start, end ), fetchSize, null );
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
		return toList( resultSet, true );
	}

	protected NList toList( ResultSet resultSet, boolean raiseErrorOnKeyDuplication ) throws SQLException {
		return new NList( toList( resultSet, NMap.class, raiseErrorOnKeyDuplication ) );
	}

	protected <T> List<T> toList( ResultSet resultSet, Class<T> returnType ) throws SQLException {
		return toList( resultSet, returnType, true );
	}

	protected <T> List<T> toList( ResultSet resultSet, Class<T> returnType, boolean raiseErrorOnKeyDuplication ) throws SQLException {
		return new ResultsetController( getEnvironmentId() ).toList( resultSet, returnType, raiseErrorOnKeyDuplication );
	}

	protected void toList( ResultSet resultSet, RowHandler rowHandler ) throws SQLException {
		toList( resultSet, rowHandler, true );
	}

	protected void toList( ResultSet resultSet, RowHandler rowHandler, boolean raiseErrorOnKeyDuplication ) throws SQLException {
		new ResultsetController( getEnvironmentId() ).toList( resultSet, rowHandler, raiseErrorOnKeyDuplication );
	}

	protected NMap toBean( ResultSet resultSet ) throws SQLException {
		return toBean( resultSet, NMap.class, true );
	}

	protected NMap toBean( ResultSet resultSet, boolean raiseErrorOnKeyDuplication ) throws SQLException {
		return toBean( resultSet, NMap.class, raiseErrorOnKeyDuplication );
	}

	protected <T> T toBean( ResultSet resultSet, Class<T> returnType ) throws SQLException {
		return toBean( resultSet, returnType, true );
	}

	protected <T> T toBean( ResultSet resultSet, Class<T> returnType, boolean raiseErrorOnKeyDuplication ) throws SQLException {
		final Object[] map = new Object[1];
		toList( resultSet, new RowHandler() {
			public void handle( NMap row ) {
				map[0] = row.toBean( returnType );
			}
		}, raiseErrorOnKeyDuplication );
		return (T) map[0];
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
		return Reflector.unwrapProxy( instance );
	}

}
