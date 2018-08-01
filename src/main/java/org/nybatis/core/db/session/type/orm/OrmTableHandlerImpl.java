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

import static org.nybatis.core.db.datasource.driver.DatabaseName.*;

/**
 * ORM entity table handler implements
 *
 * @author nayasis@gmail.com
 * @since 2017-11-20
 */
public class OrmTableHandlerImpl<T> implements OrmTableHandler<T> {

    private SqlSessionImpl       sqlSession;
    private OrmSessionProperties properties  = new OrmSessionProperties();
    private Class<T>             domainClass;
    private OrmTableSqlMaker     tableSqlMaker;

    private TableLayout          entityLayout;

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
    public synchronized TableLayout getLayout() {
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
    public synchronized boolean drop() {
        if( ! exists() ) return false;
        SqlLogHider.$.hideDebugLog( () -> {
            sqlSession.sql( tableSqlMaker.sqlDropTable( getLayout() ) ).execute();
            commit();
            refreshLayout();
        });
        return true;
    }

    @Override
    public synchronized boolean set() {
        if( ! isDDLExecutable() ) return false;
        Boolean[] result = { Boolean.FALSE };
        SqlLogHider.$.hideDebugLog( () -> {
            boolean modified = false;
            if( notExists() ) {
                modified = createTable();
            } else {
                if( isChanged() ) {
                    NLogger.trace( ">> previous table layout\n{}", getLayout() );
                    NLogger.trace( ">> current  table layout\n{}", entityLayout );
                    try {
                        modified = modifiyTable();
                    } catch( SqlException e ) {
                        if( canRecreatTable() ) {
                            NLogger.error( e );
                            NLogger.info( "table({}) will be re-create because DDL was not acceptable in environment(id:{},database:{})",
                                getTableName(),
                                getEnvironmentId(),
                                sqlSession.getDatabase().name
                            );
                            drop();
                            modified = createTable();
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
            if( modified ) {
                commit();
                refreshLayout();
            }
            result[0] = modified;
        });
        return result[0];
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

    private synchronized boolean isChanged() {
        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;
        return ! prevLayout.isEqual( currLayout );
    }

    private boolean createTable() {
        boolean result = false;
        result |= executeSql( tableSqlMaker.sqlCreateTable(entityLayout) );

        if( isNotDatabase(SQLITE) ) {
            result |= executeSql( tableSqlMaker.sqlAddPrimaryKey(entityLayout) );
        }
        for( TableIndex index : entityLayout.getIndices() ) {
            result |= executeSql( tableSqlMaker.sqlCreateIndex(index, entityLayout) );
        }
        return result;
    }

    private boolean modifiyTable() {

        boolean result = false;

        TableLayout prevLayout = getLayout();
        TableLayout currLayout = entityLayout;

        result |= addColumns( currLayout.getColumnsToAdd(prevLayout) );

        if( ! currLayout.isPkEqual(prevLayout) ) {
            result |= modifyPk();
        }

        result |= dropColumns( currLayout.getColumnsToDrop(prevLayout) );

        if( isDatabase(ORACLE) ) {
            result |= modifyColumns( currLayout.getColumnsToModify(prevLayout), prevLayout );
        } else {
            result |= modifyColumns( currLayout.getColumnsToModify(prevLayout) );
        }

        result |= addIndices( currLayout.getIndicesToAdd(prevLayout) );
        result |= dropIndices( currLayout.getIndicesToDrop(prevLayout) );
        result |= modifyIndices( currLayout.getIndicesToModify(prevLayout) );

        return result;

    }

    private boolean addColumns( List<TableColumn> columns ) {
        for( TableColumn column : columns ) {
            String sqlDefine = tableSqlMaker.sqlAddColumn( column, entityLayout );
            executeSql( sqlDefine );
        }
        return columns.size() > 0;
    }

    private boolean modifyColumns( List<TableColumn> columns ) {
        for( TableColumn column : columns ) {
            executeSql( tableSqlMaker.sqlModifyColumn(column, entityLayout) );
        }
        return columns.size() > 0;
    }

    private boolean modifyColumns( List<TableColumn> columns, TableLayout previousTable ) {

        boolean result = false;

        for( TableColumn column : columns ) {

            TableColumn prevColumn = previousTable.getColumn( column.getKey() );

            String sqlTypeCurr    = tableSqlMaker.sqlModifyColumn( column,     true, false, false, entityLayout );
            String sqlTypePrev    = tableSqlMaker.sqlModifyColumn( prevColumn, true, false, false, entityLayout );
            String sqlDefaultCurr = tableSqlMaker.sqlModifyColumn( column,     false, true, false, entityLayout );
            String sqlDefaultPrev = tableSqlMaker.sqlModifyColumn( prevColumn, false, true, false, entityLayout );
            String sqlNotNullCurr = tableSqlMaker.sqlModifyColumn( column,     false, false, true, entityLayout );
            String sqlNotNullPrev = tableSqlMaker.sqlModifyColumn( prevColumn, false, false, true, entityLayout );

            if( sqlTypeCurr    != null && ! sqlTypeCurr.equals(sqlTypePrev)       ) result |= executeSql( sqlTypeCurr    );
            if( sqlDefaultCurr != null && ! sqlDefaultCurr.equals(sqlDefaultPrev) ) result |= executeSql( sqlDefaultCurr );
            if( sqlNotNullCurr != null && ! sqlNotNullCurr.equals(sqlNotNullPrev) ) result |= executeSql( sqlNotNullCurr );

        }

        return result;

    }


    private boolean dropColumns( List<TableColumn> columns ) {
        for( TableColumn column : columns ) {
            executeSql( tableSqlMaker.sqlDropColumn(column, entityLayout) );
        }
        return columns.size() > 0;
    }

    private boolean addIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            executeSql( tableSqlMaker.sqlCreateIndex(index, entityLayout) );
        }
        return indices.size() > 0;
    }

    private boolean dropIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            executeSql( tableSqlMaker.sqlDropIndex(index, entityLayout) );
        }
        return indices.size() > 0;
    }

    private boolean modifyIndices( List<TableIndex> indices ) {
        for( TableIndex index : indices ) {
            executeSql( tableSqlMaker.sqlDropIndex(index, entityLayout) );
            executeSql( tableSqlMaker.sqlCreateIndex(index, entityLayout) );
        }
        return indices.size() > 0;
    }

    private boolean modifyPk() {
        TableLayout prevLayout = getLayout();
        if( prevLayout != null && prevLayout.hasPk() ) {
            entityLayout.setPkName( prevLayout.getPkName() );
            executeSql( tableSqlMaker.sqlDropPrimaryKey(prevLayout) );
        }
        return executeSql( tableSqlMaker.sqlAddPrimaryKey(entityLayout) );
    }

    private boolean executeSql( String sql ) {
        if( StringUtil.isEmpty(sql) ) return false;
        try {
            sqlSession.sql( sql ).execute();
            return true;
        } catch( SqlException e ) {
            // InnoDB 엔진 key-size 제약이 발생했을 경우
            if( isDatabase( MARIA, MYSQL ) && "1071".equals(e.getErrorCode()) ) {
                executeSql( tableSqlMaker.sqlMariaTableRowFormatDynamic(entityLayout) );
                sqlSession.sql( sql ).execute();
                return true;
            }
            return false;
        }
    }

    private boolean isDatabase( DatabaseName... dbName ) {
        return DatasourceManager.isDatabase( getEnvironmentId(), dbName );
    }

    private boolean isNotDatabase( DatabaseName... dbName ) {
        return ! isDatabase( dbName );
    }

}
