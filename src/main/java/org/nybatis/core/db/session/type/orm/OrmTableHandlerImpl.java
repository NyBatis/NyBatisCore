package org.nybatis.core.db.session.type.orm;

import java.util.List;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.orm.sqlmaker.OrmTableSqlMaker;
import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;

import static org.nybatis.core.db.datasource.driver.DatabaseName.ORACLE;
import static org.nybatis.core.db.datasource.driver.DatabaseName.SQLITE;

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

    @Override
    public OrmTableHandler<T> drop() {
        if( exists() ) {
            sqlSession.sql( tableSqlMaker.sqlDropTable( getLayout() ) ).execute();
            commit();
            refreshLayout();
        }
        return this;
    }

    @Override
    public OrmTableHandler<T> set() {
        if( ! isDDLExecutable() ) return this;
        if( notExists() ) {
            createTable();
        } else {
            if( isChanged() ) {
                NLogger.debug( ">> previous table layout\n{}", getLayout() );
                NLogger.debug( ">> current  table layout\n{}", entityLayout );
                try {
                    modifiyTable();
                } catch( SqlException e ) {
                    if( canRecreatTable() ) {
                        drop();
                        createTable();
                    } else {
                        throw e;
                    }
                }
            }
        }
        refreshLayout();
        commit();
        return this;
    }

    private boolean isDDLExecutable() {
        if( ! TableLayoutRepository.isEnableDDL( getEnvironmentId() ) ) {
            NLogger.warn( "ORM table execute option(environment(id:{}) > ddl) is [false].", getEnvironmentId() );
            return false;
        }
        return true;
    }

    private boolean canRecreatTable() {
        return TableLayoutRepository.isRecreationDDL( getEnvironmentId() );
    }

    private void commit() {
        if( isDatabase(SQLITE) ) {
            sqlSession.commit();
        }
    }

    private void refreshLayout() {
        tableSqlMaker.refreshTableLayout( domainClass );
    }

    private boolean isChanged() {
        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;
        return ! prevLayout.isEqual( currLayout );
    }

    private void createTable() {
        sqlSession.sql( tableSqlMaker.sqlCreateTable(entityLayout) ).execute();
        if( isNotDatabase(SQLITE) ) {
            sqlSession.sql( tableSqlMaker.sqlAddPrimaryKey(entityLayout) ).execute();
        }
        for( TableIndex index : entityLayout.getIndices() ) {
            sqlSession.sql( tableSqlMaker.sqlCreateIndex(index, entityLayout) ).execute();
        }
    }

    private void modifiyTable() {

        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;

        addColumns( currLayout.getColumnsToAdd(prevLayout) );
        dropColumns( currLayout.getColumnsToDrop(prevLayout) );

        if( isDatabase(ORACLE) ) {
            modifyColumns( currLayout.getColumnsToModify(prevLayout), prevLayout );
        } else {
            modifyColumns( currLayout.getColumnsToModify(prevLayout) );
        }

        addIndices( currLayout.getIndicesToAdd(prevLayout) );
        dropIndices( currLayout.getIndicesToDrop(prevLayout) );
        modifyIndices( currLayout.getIndicesToModify(prevLayout) );

        if( ! currLayout.isPkEqual(prevLayout) ) {
            modifyPk();
        }

    }

    private void addColumns( List<Column> columns ) {
        for( Column column : columns ) {
            String sqlDefine = tableSqlMaker.sqlAddColumn( column, entityLayout );
            if( StringUtil.isNotEmpty(sqlDefine) ) sqlSession.sql( sqlDefine ).execute();
        }
    }

    private void modifyColumns( List<Column> columns ) {
        for( Column column : columns ) {
            sqlSession.sql( tableSqlMaker.sqlModifyColumn(column, entityLayout) ).execute();
        }
    }

    private void modifyColumns( List<Column> columns, TableLayout previousTable ) {

        for( Column column : columns ) {

            Column prevColumn = previousTable.getColumn( column.getKey() );

            String sqlTypeCurr    = tableSqlMaker.sqlModifyColumn( column,     true, false, false, entityLayout );
            String sqlTypePrev    = tableSqlMaker.sqlModifyColumn( prevColumn, true, false, false, entityLayout );
            String sqlDefaultCurr = tableSqlMaker.sqlModifyColumn( column,     false, true, false, entityLayout );
            String sqlDefaultPrev = tableSqlMaker.sqlModifyColumn( prevColumn, false, true, false, entityLayout );
            String sqlNotNullCurr = tableSqlMaker.sqlModifyColumn( column,     false, false, true, entityLayout );
            String sqlNotNullPrev = tableSqlMaker.sqlModifyColumn( prevColumn, false, false, true, entityLayout );

            if( sqlTypeCurr    != null && ! sqlTypeCurr.equals(sqlTypePrev)       ) sqlSession.sql( sqlTypeCurr    ).execute();
            if( sqlDefaultCurr != null && ! sqlDefaultCurr.equals(sqlDefaultPrev) ) sqlSession.sql( sqlDefaultCurr ).execute();
            if( sqlNotNullCurr != null && ! sqlNotNullCurr.equals(sqlNotNullPrev) ) sqlSession.sql( sqlNotNullCurr ).execute();

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
        TableLayout prevLayout = getLayout();
        if( prevLayout != null && prevLayout.hasPk() ) {
            entityLayout.setPkName( prevLayout.getPkName() );
            sqlSession.sql( tableSqlMaker.sqlDropPrimaryKey(prevLayout) ).execute();
        }
        sqlSession.sql( tableSqlMaker.sqlAddPrimaryKey(entityLayout) ).execute();
    }

    private boolean isDatabase( DatabaseName... dbName ) {
        return DatasourceManager.isDatabase( getEnvironmentId(), dbName );
    }

    private boolean isNotDatabase( DatabaseName... dbName ) {
        return ! isDatabase( dbName );
    }

}
