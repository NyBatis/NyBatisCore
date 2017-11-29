package org.nybatis.core.db.session.type.orm;

import java.util.List;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.etc.SqlLogHider;
import org.nybatis.core.db.session.type.sql.SqlSessionImpl;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.sqlmaker.OrmTableSqlMaker;
import org.nybatis.core.db.sql.orm.vo.TableColumn;
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
        SqlLogHider.$.hideDebugLog( () -> {
            if( exists() ) {
                sqlSession.sql( tableSqlMaker.sqlDropTable( getLayout() ) ).execute();
                commit();
                refreshLayout();
            }
        });
        return this;
    }

    @Override
    public OrmTableHandler<T> set() {
        if( ! isDDLExecutable() ) return this;
        SqlLogHider.$.hideDebugLog( () -> {
            if( notExists() ) {
                createTable();
            } else {
                if( isChanged() ) {
                    NLogger.trace( ">> previous table layout\n{}", getLayout() );
                    NLogger.trace( ">> current  table layout\n{}", entityLayout );
                    try {
                        modifiyTable();
                    } catch( SqlException e ) {
                        if( canRecreatTable() ) {
                            NLogger.error( e );
                            NLogger.info( "table({}) will be re-create because DDL was not acceptable in environment(id:{},database:{})",
                                getTableName(),
                                getEnvironmentId(),
                                sqlSession.getDatabase().name
                            );
                            drop();
                            createTable();
                        } else {
                            throw new SqlConfigurationException( e,
                                "table({}) can't be re-create because ORM option[environment(id:{}) > ddl > recreation] is [false]",
                                getTableName(),
                                getEnvironmentId()
                            );
                        }
                    }
                }
            }
            refreshLayout();
            commit();
        });
        return this;
    }

    private boolean isDDLExecutable() {
        if( ! TableLayoutRepository.isEnableDDL( getEnvironmentId() ) ) {
            NLogger.warn( "table({}) can't be created because ORM option[environment(id:{}) > ddl > enable] is [false].",
                getTableName(),
                getEnvironmentId()
            );
            return false;
        }
        return true;
    }

    private String getTableName() {
        return EntityLayoutReader.getTableName( domainClass );
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
        executeSql( tableSqlMaker.sqlCreateTable(entityLayout) );
        if( isNotDatabase(SQLITE) ) {
            executeSql( tableSqlMaker.sqlAddPrimaryKey(entityLayout) );
        }
        for( TableIndex index : entityLayout.getIndices() ) {
            executeSql( tableSqlMaker.sqlCreateIndex(index, entityLayout) );
        }
    }

    private void modifiyTable() {

        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;

        addColumns( currLayout.getColumnsToAdd(prevLayout) );

        if( ! currLayout.isPkEqual(prevLayout) ) {
            modifyPk();
        }

        dropColumns( currLayout.getColumnsToDrop(prevLayout) );

        if( isDatabase(ORACLE) ) {
            modifyColumns( currLayout.getColumnsToModify(prevLayout), prevLayout );
        } else {
            modifyColumns( currLayout.getColumnsToModify(prevLayout) );
        }

        addIndices( currLayout.getIndicesToAdd(prevLayout) );
        dropIndices( currLayout.getIndicesToDrop(prevLayout) );
        modifyIndices( currLayout.getIndicesToModify(prevLayout) );



    }

    private void addColumns( List<TableColumn> columns ) {
        for( TableColumn column : columns ) {
            String sqlDefine = tableSqlMaker.sqlAddColumn( column, entityLayout );
            executeSql( sqlDefine );
        }
    }

    private void modifyColumns( List<TableColumn> columns ) {
        for( TableColumn column : columns ) {
            executeSql( tableSqlMaker.sqlModifyColumn(column, entityLayout) );
        }
    }

    private void modifyColumns( List<TableColumn> columns, TableLayout previousTable ) {

        for( TableColumn column : columns ) {

            TableColumn prevColumn = previousTable.getColumn( column.getKey() );

            String sqlTypeCurr    = tableSqlMaker.sqlModifyColumn( column,     true, false, false, entityLayout );
            String sqlTypePrev    = tableSqlMaker.sqlModifyColumn( prevColumn, true, false, false, entityLayout );
            String sqlDefaultCurr = tableSqlMaker.sqlModifyColumn( column,     false, true, false, entityLayout );
            String sqlDefaultPrev = tableSqlMaker.sqlModifyColumn( prevColumn, false, true, false, entityLayout );
            String sqlNotNullCurr = tableSqlMaker.sqlModifyColumn( column,     false, false, true, entityLayout );
            String sqlNotNullPrev = tableSqlMaker.sqlModifyColumn( prevColumn, false, false, true, entityLayout );

            if( sqlTypeCurr    != null && ! sqlTypeCurr.equals(sqlTypePrev)       ) executeSql( sqlTypeCurr    );
            if( sqlDefaultCurr != null && ! sqlDefaultCurr.equals(sqlDefaultPrev) ) executeSql( sqlDefaultCurr );
            if( sqlNotNullCurr != null && ! sqlNotNullCurr.equals(sqlNotNullPrev) ) executeSql( sqlNotNullCurr );

        }

    }


    private void dropColumns( List<TableColumn> columns ) {
        for( TableColumn column : columns ) {
            executeSql( tableSqlMaker.sqlDropColumn(column, entityLayout) );
        }
    }

    private void addIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            executeSql( tableSqlMaker.sqlCreateIndex(index, entityLayout) );
        }
    }

    private void dropIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            executeSql( tableSqlMaker.sqlDropIndex(index, entityLayout) );
        }
    }

    private void modifyIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            executeSql( tableSqlMaker.sqlDropIndex(index, entityLayout) );
            executeSql( tableSqlMaker.sqlCreateIndex(index, entityLayout) );
        }
    }

    private void modifyPk() {
        TableLayout prevLayout = getLayout();
        if( prevLayout != null && prevLayout.hasPk() ) {
            entityLayout.setPkName( prevLayout.getPkName() );
            executeSql( tableSqlMaker.sqlDropPrimaryKey(prevLayout) );
        }
        executeSql( tableSqlMaker.sqlAddPrimaryKey(entityLayout) );
    }

    private void executeSql( String sql ) {
        if( StringUtil.isEmpty(sql) ) return;
        sqlSession.sql( sql ).execute();
    }

    private boolean isDatabase( DatabaseName... dbName ) {
        return DatasourceManager.isDatabase( getEnvironmentId(), dbName );
    }

    private boolean isNotDatabase( DatabaseName... dbName ) {
        return ! isDatabase( dbName );
    }

}
