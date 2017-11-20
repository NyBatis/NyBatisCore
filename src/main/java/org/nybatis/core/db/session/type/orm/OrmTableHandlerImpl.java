package org.nybatis.core.db.session.type.orm;

import java.util.List;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.orm.sqlmaker.OrmTableSqlMaker;
import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;

/**
 * ORM entity table handler implements
 *
 * @author nayasis@gmail.com
 * @since 2017-11-20
 */
public class OrmTableHandlerImpl<T> implements OrmTableHandler<T> {

    private SqlSessionImpl       sqlSession;
    private OrmSessionProperties properties  = new OrmSessionProperties();
    private Class<T>             domainClass = null;
    private OrmTableSqlMaker     tableSqlMaker;

    private TableLayout          entityLayout = null;

    public OrmTableHandlerImpl( SqlSessionImpl sqlSession, OrmSessionProperties properties, Class<T> domainClass ) {
        this.sqlSession    = sqlSession;
        this.properties    = properties;
        this.domainClass   = domainClass;
        this.tableSqlMaker = new OrmTableSqlMaker( getEnvironmentId() );
        this.entityLayout  = tableSqlMaker.getEntityLayout( domainClass );
    }

    private String getEnvironmentId() {
        return sqlSession.getProperties().getRepresentativeEnvironmentId();
    }

    @Override
    public TableLayout getLayout() {
        try {
            return tableSqlMaker.getTableLayout( domainClass );
        } catch( SqlConfigurationException e ) {
            return null;
        }
    }

    @Override
    public boolean exists() {
        return ! notExists();
    }

    @Override
    public boolean notExists() {
        return getLayout() == null;
    }

    private TableLayout getEntityLayout() {
        return tableSqlMaker.getEntityLayout( domainClass );
    }

    @Override
    public void drop() {
        String sql = tableSqlMaker.sqlDropTable( entityLayout );
        sqlSession.sql( sql ).execute();
    }

    @Override
    public void set() {
        if( ! TableLayoutRepository.isEnableToCreateTable( getEnvironmentId() ) ) {
            NLogger.warn( "ORM table modification option is off on environment(id:{})", getEnvironmentId() );
            return;
        }
        if( notExists() ) {
            createTable();
            tableSqlMaker.refreshTableLayout( domainClass );
        } else {
            if( isChanged() ) {
                modifiyTable();
                tableSqlMaker.refreshTableLayout( domainClass );
            }
        }

    }

    private boolean isChanged() {
        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;
        return ! prevLayout.isEqual( currLayout );
    }

    private void createTable() {
        sqlSession.sql( tableSqlMaker.sqlCreateTable(entityLayout) ).execute();
        sqlSession.sql( tableSqlMaker.sqlAddPkIndex(entityLayout) ).execute();
        for( TableIndex index : entityLayout.getIndices() ) {
            sqlSession.sql( tableSqlMaker.sqlCreateIndex(index, entityLayout) ).execute();
        }
    }

    private void modifiyTable() {

        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;

        addColumns( currLayout.getColumnsToAdd(prevLayout) );
        dropColumns( currLayout.getColumnsToDrop(prevLayout) );
        modifyColumns( currLayout.getColumnsToModify(prevLayout) );

        if( ! currLayout.isPkEqual(prevLayout) ) {
            modifyPk();
        }

        addIndices( currLayout.getIndicesToAdd(prevLayout) );
        dropIndices( currLayout.getIndicesToDrop(prevLayout) );
        modifyIndices( currLayout.getIndicesToModify(prevLayout) );

    }

    private void addColumns( List<Column> columns ) {
        for( Column column : columns ) {
            sqlSession.sql( tableSqlMaker.sqlAddColumn(column, entityLayout) ).execute();
        }
    }

    private void modifyColumns( List<Column> columns ) {
        for( Column column : columns ) {
            sqlSession.sql( tableSqlMaker.sqlModifyColumn(column, entityLayout) ).execute();
        }
    }

    private void dropColumns( List<Column> columns ) {
        for( Column column : columns ) {
            sqlSession.sql( tableSqlMaker.sqlDropColumn(column, entityLayout) ).execute();
        }
    }

    private void addIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            sqlSession.sql( tableSqlMaker.sqlCreateIndex(index, entityLayout) ).execute();
        }
    }

    private void dropIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            sqlSession.sql( tableSqlMaker.sqlDropIndex(index, entityLayout) ).execute();
        }
    }

    private void modifyIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            sqlSession.sql( tableSqlMaker.sqlDropIndex(index, entityLayout) ).execute();
            sqlSession.sql( tableSqlMaker.sqlCreateIndex(index, entityLayout) ).execute();
        }
    }

    private void modifyPk() {
        sqlSession.sql( tableSqlMaker.sqlDropPkIndex(entityLayout) ).execute();
        sqlSession.sql( tableSqlMaker.sqlAddPkIndex(entityLayout) ).execute();
    }

}
