package org.nybatis.core.db.session.type.sql;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseAttribute;
import org.nybatis.core.db.datasource.driver.DatabaseAttributeManager;
import org.nybatis.core.db.session.SessionCreator;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.handler.ConnectionHandler;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.db.transaction.TransactionManager;
import org.nybatis.core.exception.unchecked.BaseRuntimeException;
import org.nybatis.core.exception.unchecked.DatabaseException;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.MethodInvocator;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 * SqlSession Implements
 *
 * @author nayasis@gmail.com
 * @since 2015-09-11
 */
public class SqlSessionImpl implements SqlSession {

    private String         token;
    private SqlProperties  properties;
    private SqlProperties  originalProperties;

    public SqlSessionImpl( String token, SqlProperties properties ) {
        init( token, properties );
    }

    private void init( String token, SqlProperties properties ) {

        this.token              = token;
        this.originalProperties = Validator.nvl( properties, new SqlProperties() );
        this.properties         = originalProperties.clone();

    }

    public SqlSessionImpl initProperties() {
        properties = originalProperties.clone();
        return this;
    }

    public SqlProperties getProperties() {
        return properties;
    }

    public String getToken() {
        return token;
    }

    @Override
    public SessionExecutor sqlId( String id ) {
        return new SessionExecutorImpl( this ).sqlId( id );
    }

    @Override
    public SessionExecutor sqlId( String id, Object parameter ) {
        return new SessionExecutorImpl( this ).sqlId( id, parameter );
    }

    @Override
    public SessionExecutor sql( String sql ) {
        return new SessionExecutorImpl( this ).sql( sql );
    }

    @Override
    public SessionExecutor sql( String sql, Object parameter ) {
        return new SessionExecutorImpl( this ).sql( sql, parameter );
    }

    @Override
    public BatchExecutor batchSqlId( String id, List<?> parameters ) {
        return new BatchExecutorImpl( this ).batchSqlId( id, parameters );
    }

    @Override
    public BatchExecutor batchSql( List<String> sqlList ) {
        return new BatchExecutorImpl( this ).batchSql( sqlList );
    }

    @Override
    public BatchExecutor batchSql( String sql, List<?> parameters ) {
        return new BatchExecutorImpl( this ).batchSql( sql, parameters );
    }

    @Override
    public SqlSession commit() {
        TransactionManager.commit( token );
        return this;
    }

    @Override
    public SqlSession rollback() {
        TransactionManager.rollback( token );
        return this;
    }

    @Override
    public SqlSession beginTransaction() {
        TransactionManager.begin( token );
        return this;
    }

    @Override
    public SqlSession endTransaction() {
        TransactionManager.end( token );
        return this;
    }

    @Override
    public boolean isTransactionBegun() {
        return TransactionManager.isBegun( token );
    }

    @Override
    public SqlSession useConnection( ConnectionHandler worker ) throws BaseRuntimeException {

        if( worker == null ) return this;

        Connection connection = null;

        try {

            connection = TransactionManager.getConnection( token, properties.getRepresentativeEnvironmentId() );

            Connection protectedConnection = getProtectedConnection( connection );

            worker.setProperties( properties );
            worker.setConnection( protectedConnection );

            worker.execute( protectedConnection );

            return this;

        } catch( Throwable e ) {
            throw new BaseRuntimeException( e );

        } finally {
            TransactionManager.releaseConnection( token, connection );
            initProperties();

        }

    }

    private Connection getProtectedConnection( Connection connection ) {

        return Reflector.wrapProxy( connection, new Class<?>[] {Connection.class}, new MethodInvocator() {
            public Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable {

                if( "close".equals(method.getName()) ) {
                    throw new DatabaseException( "connection's close method is not supprted in useConnection feature." );
                }

                return method.invoke( connection, arguments );

            }

        } );

    }

    @Override
    public SqlSession setEnvironmentId( String id ) {
        originalProperties.setEnvironmentId( id );
        properties.setEnvironmentId( id );
        return this;
    }

    @Override
    public <T> OrmSession<T> openOrmSession( String tableName, Class<T> domainClass ) {
        return new SessionCreator().createOrmSession( token, properties.getEnvironmentId(), tableName, domainClass );
    }

    @Override
    public <T> OrmSession<T> openOrmSession( Class<T> domainClass ) {
        return openOrmSession( null, domainClass );
    }

    @Override
    public SqlSession openSeperateSession() {
        return SessionManager.openSeperateSession( properties.getEnvironmentId() );
    }

    @Override
    public String getDatabaseName() {
        DatabaseAttribute attributes = DatasourceManager.getAttributes( properties.getEnvironmentId() );
        return ( attributes == null ) ? "" : attributes.getDatabase();
    }

}