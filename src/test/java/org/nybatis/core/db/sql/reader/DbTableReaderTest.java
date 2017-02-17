package org.nybatis.core.db.sql.reader;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.type.sql.SqlSession;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.log.NLogger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-09
 */
public class DbTableReaderTest {

    @BeforeClass
    public void init() {

        DatabaseConfigurator.build();
        SqlSession sqlSession = SessionManager.openSession( "sqlite" );

        sqlSession.sql( "CREATE TABLE IF NOT EXISTS TEST_ORM_PROD ( list_id TEXT, prod_id TEXT, price NUMBER, prod_name TEXT, image BLOB, PRIMARY KEY(list_id,prod_id) )" ).execute();
        sqlSession.commit();

    }

    @Test
    public void makeOrmSql() throws Throwable {

        String environmentId = "sqlite";
        String tableName     = "TEST_ORM_PROD";

        DbTableReader dbTableReader = new DbTableReader();
        dbTableReader.read( environmentId, tableName );
        printLoadedOrmSql( environmentId, tableName );

    }

    public void printLoadedOrmSql( String environmentId, String tableName ) {

        String sqlIdPrefix = Const.db.getOrmSqlIdPrefix( environmentId, tableName );

        NLogger.debug( sqlIdPrefix );
        NLogger.debug( Const.db.getOrmSqlIdPrefix( environmentId, tableName ) );

        NLogger.debug( SqlRepository.get( sqlIdPrefix + Const.db.ORM_SQL_INSERT_PK ) );
        NLogger.debug( SqlRepository.get( sqlIdPrefix + Const.db.ORM_SQL_UPDATE_PK ) );
        NLogger.debug( SqlRepository.get( sqlIdPrefix + Const.db.ORM_SQL_DELETE ) );
        NLogger.debug( SqlRepository.get( sqlIdPrefix + Const.db.ORM_SQL_SELECT_PK ) );

    }

}