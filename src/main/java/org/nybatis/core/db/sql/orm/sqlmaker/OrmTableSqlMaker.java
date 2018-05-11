package org.nybatis.core.db.sql.orm.sqlmaker;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.vo.TableColumn;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.StringUtil;

import java.sql.Types;
import java.util.*;

import static org.nybatis.core.db.datasource.driver.DatabaseName.*;

/**
 * ORM DDL SQL maker
 *
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class OrmTableSqlMaker {

    private String environmentId;

    public OrmTableSqlMaker( String environmentId ) {
        setEnvironmentId( environmentId );
    }

    public TableLayout getTableLayout( Class klass ) {
        try {
            return TableLayoutRepository.getLayout( environmentId, klass );
        } catch( SqlConfigurationException e ) {
            return null;
        }
    }

    public void refreshTableLayout( Class klass ) {
        TableLayoutRepository.clearLayout( environmentId, klass );
        String tableName = EntityLayoutReader.getTableName( klass );
        new OrmSqlMaker().readTable( environmentId, tableName, true );
    }

    public boolean exists( Class klass ) {
        try {
            getTableLayout( klass );
            return true;
        } catch( SqlConfigurationException e ) {
            return false;
        }
    }

    public boolean notExists( Class klass ) {
        return ! exists( klass );
    }

    public TableLayout getEntityLayout( Class klass ) {
        EntityLayoutReader reader = new EntityLayoutReader( environmentId );
        return reader.getTableLayout( klass );
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId( String environmentId ) {
        this.environmentId = environmentId;
    }

    public String sqlCreateTable( TableLayout table ) {
        StringBuilder sql = new StringBuilder();
        sql.append( String.format("CREATE TABLE %s (\n", table.getName()) );
        sql.append( toColumnCreationString(table) ).append( "\n" );
        sql.append( ")" );
        return sql.toString();
    }

    private boolean isDatabase( DatabaseName... dbName ) {
        return DatasourceManager.isDatabase( environmentId, dbName );
    }

    private String toColumnCreationString( TableLayout table ) {
        List<String> columns = new ArrayList<>();
        for( TableColumn column : table.getColumns() ) {
            columns.add( String.format( "\t%s %s", getColumnName(column), toColumnPropertiesString(column) ) );
        }
        if( isDatabase(SQLITE) ) {
            columns.add( String.format("\tPRIMARY KEY( %s )", serializeColumnNames(table.getPkColumnNames())) );
        }
        return StringUtil.join( columns, ",\n" );
    }

    private String getColumnName( TableColumn column ) {
        if( isDatabase( MYSQL,MARIA,SQLITE) ) {
            return column.getQuotedName();
        } else {
            return column.getName();
        }
    }

    private String toColumnPropertiesString( TableColumn column ) {
        StringBuilder sb = new StringBuilder();
        sb.append( toTypeString( column ) );
        sb.append( toDefaultString( column ) );
        sb.append( toNotNullString( column ) );
        return sb.toString();
    }

    private String toTypeString( TableColumn column ) {

        StringBuilder sb = new StringBuilder( " " );
        int dataType = column.getDataType();
        switch( dataType ) {
            case Types.VARCHAR :
                if( isDatabase(ORACLE) ) {
                    sb.append( "VARCHAR2" );
                } else {
                    sb.append( toColumnType(dataType) );
                }
                sb.append( toColumnSize(column) );
                break;
            case Types.DATE :
                if( isDatabase(H2) ) {
                    if( ! column.isDefinedByAnnotation() ) {
                        sb.append( "TIMESTAMP" );
                    } else {
                        sb.append( toColumnType(dataType) );
                    }
                } else {
                    sb.append( toColumnType(dataType) );
                }
                break;
            case Types.NUMERIC :
            case Types.DECIMAL :
            case Types.DOUBLE :
                if( isDatabase( MYSQL, MARIA) ) {
                    sb.append( toColumnType(dataType) );
                } else {
                    sb.append( "NUMBER" );
                }
                sb.append( toColumnSize(column) );
                break;
            case Types.VARBINARY :
            case Types.LONGVARBINARY :
                if( isDatabase( MYSQL,MARIA) ) {
                    sb.append( "TEXT" );
                } else {
                    sb.append( toColumnType(dataType) );
                }
                break;
            default :
                sb.append( toColumnType( dataType ) );
                sb.append( toColumnSize(column) );
        }

        return sb.toString();

    }

    private String toColumnType( int dataType ) {
        return SqlType.find(dataType).name;
    }

    private String toColumnSize( TableColumn column ) {
        StringBuilder sb = new StringBuilder();
        if( column.getSize() != null ) {
            sb.append( "(" ).append( column.getSize() );
            if( column.getPrecison() != null ) {
                sb.append( "," ).append( column.getPrecison() );
            }
            sb.append( ")" );
        }
        return sb.toString();
    }

    private String toNotNullString( TableColumn column ) {
        if( column.isNotNull() ) {
            return " NOT NULL";
        }
        return "";
    }

    private String toDefaultString( TableColumn column ) {
        StringBuilder sb = new StringBuilder();
        if( StringUtil.isNotEmpty(column.getDefaultValue()) ) {
            sb.append( " DEFAULT " );
            if( isNumeric(column.getDataType()) ) {
                sb.append( column.getDefaultValue() );
            } else {
                sb.append( "'" ).append( column.getDefaultValue() ).append( "'" );
            }
        }
        return sb.toString();
    }

    public String sqlDropColumn( TableColumn column, TableLayout table ) {
        if( isDatabase(ORACLE) ) {
            return String.format( "ALTER TABLE %s DROP (%s)", table.getName(), getColumnName(column) );
        } else {
            return String.format( "ALTER TABLE %s DROP COLUMN %s", table.getName(), getColumnName(column) );
        }
    }

    public String sqlModifyColumn( TableColumn column, boolean type, boolean defaultValue, boolean notNull, TableLayout table ) {

        StringBuilder sb = new StringBuilder();
        if( type )         sb.append( toTypeString( column ) );
        if( defaultValue ) sb.append( toDefaultString( column ) );
        if( notNull )      sb.append( toNotNullString( column ) );

        if( sb.length() == 0 ) return null;

        if( isDatabase( ORACLE ) ) {
            return String.format("ALTER TABLE %s MODIFY( %s %s )", table.getName(), getColumnName(column), sb );
        } else if( isDatabase(H2) ) {
            return String.format("ALTER TABLE %s ALTER COLUMN %s %s", table.getName(), getColumnName(column), sb );
        } else if( isDatabase( MYSQL,MARIA) ) {
            return String.format("ALTER TABLE %s CHANGE %s %s %s", table.getName(), getColumnName(column), getColumnName(column), sb );
        } else {
            return String.format("ALTER TABLE %s MODIFY %s %s", table.getName(), getColumnName(column), sb );
        }
    }

    public String sqlModifyColumn( TableColumn column, TableLayout table ) {
        return sqlModifyColumn( column, true, true, true, table  );
    }

    public String sqlAddColumn( TableColumn column, TableLayout table ) {
        return String.format("ALTER TABLE %s ADD( %s %s )", table.getName(), getColumnName(column), toColumnPropertiesString( column ) );
    }

    public String sqlDropTable( TableLayout tableLayout ) {
        return String.format( "DROP TABLE %s", tableLayout.getName() );
    }

    public String sqlDropPrimaryKey( TableLayout tableLayout ) {
        if( tableLayout == null || ! tableLayout.hasPk() ) return null;
        if( isDatabase( MYSQL, MARIA) ) {
            return String.format( "ALTER TABLE %s DROP PRIMARY KEY", tableLayout.getName() );
        } else {
            return String.format( "ALTER TABLE %s DROP CONSTRAINT %s", tableLayout.getName(), tableLayout.getPkName() );
        }
    }

    public String sqlAddPrimaryKey( TableLayout tableLayout ) {
        if( tableLayout == null || tableLayout.getPkColumns().size() == 0 ) return null;
        String pkName = tableLayout.getPkName();
        if( StringUtil.isEmpty(pkName) ) {
            pkName = "PK_" + tableLayout.getName();
        }
        if( isDatabase(ORACLE,H2) ) {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)",
                tableLayout.getName(), pkName, serializeColumnNames( tableLayout.getPkColumnNames() ) );
        } else if( isDatabase(SQLITE) ) {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT %s KEY (%s)",
                tableLayout.getName(), pkName, serializeColumnNames( tableLayout.getPkColumnNames() ) );
        } else if( isDatabase( MYSQL, MARIA) ) {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT PRIMARY KEY (%s)",
                tableLayout.getName(), serializeColumnNames( tableLayout.getPkColumnNames() ) );
        } else {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)",
                tableLayout.getName(), pkName, serializeColumnNames( tableLayout.getPkColumnNames() ) );
        }
    }

    private String serializeColumnNames( Collection<String> names ) {
        boolean needsToPatch = isDatabase( MYSQL, MARIA, SQLITE );
        Set<String> columnNames = new LinkedHashSet<>();
        for( String name : names ) {
            String[] words = name.split( " " );
            String key  = words[ 0 ];
            String desc = words.length > 1 ? " "+ words[1] : "";
            if( needsToPatch ) {
                key = "`" + StringUtil.toUncamel( key ) + "`";
            } else {
                key = StringUtil.toUncamel( key );
            }
            columnNames.add( key + desc );
        }
        return StringUtil.join( columnNames, "," );
    }

    public String sqlDropIndex( TableIndex index, TableLayout table ) {
        if( isDatabase( MYSQL,MARIA) ) {
            return String.format( "DROP INDEX %s ON %s", index.getName(), table.getName() );
        } else {
            return String.format( "DROP INDEX %s", index.getName() );
        }
    }

    public String sqlCreateIndex( TableIndex index, TableLayout table ) {
        return String.format( "CREATE INDEX %s ON %s(%s)", index.getName(), table.getName(), serializeColumnNames(index.getColumnNames()) );
    }

    private boolean isNumeric( int dataType ) {
        switch( dataType ) {
            case java.sql.Types.BIT :
            case java.sql.Types.TINYINT :
            case java.sql.Types.SMALLINT :
            case java.sql.Types.INTEGER :
            case java.sql.Types.BIGINT :
            case java.sql.Types.FLOAT :
            case java.sql.Types.REAL :
            case java.sql.Types.DOUBLE :
            case java.sql.Types.NUMERIC :
            case java.sql.Types.DECIMAL :
                return true;
        }
        return false;
    }

}
