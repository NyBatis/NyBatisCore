package org.nybatis.core.db;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.configuration.builder.EnvironmentBuilder;
import org.nybatis.core.db.configuration.builder.SqlTextBuilder;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.sqlite.JDBC;
import org.testng.annotations.Test;

/**
 * @author nayasis
 * @since 2016-03-18
 */
public class DatabaseCodeConfigurationTest {

    private String environmentId = "ConfInJava";

    @Test
    public void setEnvironment() {

//        # database connection properties
//        driver=org.sqlite.JDBC
//        url=jdbc:sqlite:#{default.base}/localDb/TestDb01

        String url = String.format( "jdbc:sqlite:%s/localDb/TestDb01", Const.path.getBase() );

        new EnvironmentBuilder( environmentId ).setJdbcDatasource( JDBC.class, url, "username", "password" );

    }

    @Test
    public void sendSql() {

        setEnvironment();

        NList list = getSqlSession().sql( "SELECT * FROM PROD" ).list().selectNList();

        NLogger.debug( list );

    }

    @Test
    public void setSql() {

        setEnvironment();

        new SqlTextBuilder( environmentId )
                .set( "test.sql.01", "SELECT * FROM PROD" )
                .set( "test.sql.01", "IT IS NOT SQL" ) // later one is ignored
                .set( "test.sql.02", "SELECT 1 FROM PROD" );

        NLogger.debug( new SqlRepository().toString() );

    }

    @Test
    public void sendSqlId() {
        setSql();
        NLogger.debug( select( "test.sql.01" ) );
        NLogger.debug( select( "test.sql.02" ) );
    }

    private NList select( String id ) {
        return getSqlSession().sqlId( id ).list().selectNList();
    }

    private SqlSession getSqlSession() {
        return SessionManager.openSession();
    }

}
