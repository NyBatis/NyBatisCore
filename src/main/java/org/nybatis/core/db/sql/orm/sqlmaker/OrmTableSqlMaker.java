package org.nybatis.core.db.sql.orm.sqlmaker;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.datasource.driver.DatabaseName;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.vo.Column;
import org.nybatis.core.db.sql.orm.vo.TableIndex;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.StringUtil;

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
        TableLayoutRepository.getLayout( environmentId, klass );
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
        EntityLayoutReader reader = new EntityLayoutReader();
        return reader.getTableLayout( klass );
    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId( String environmentId ) {
        this.environmentId = environmentId;
    }

    public String sqlCreateTable( TableLayout tableLayout ) {
        StringBuilder sql = new StringBuilder();
        sql.append( "CREATE TABLE " );
        sql.append( tableLayout.getName() ).append( " (\n" );
        sql.append( toColumnCreationString(tableLayout) ).append( "\n" );
        sql.append( ")" );
        return sql.toString();
    }

    private boolean isDatabase( DatabaseName... dbName ) {
        return DatasourceManager.isDatabase( environmentId, dbName );
    }

    private boolean isNotDatabase( DatabaseName... dbName ) {
        return ! isDatabase( dbName );
    }

    private String toColumnCreationString( TableLayout table ) {
        List<String> columns = new ArrayList<>();
        for( Column column : table.getColumns() ) {
            columns.add( String.format( "\t%s %s", column.getName(), toColumnPropertiesString(column) ) );
        }
        if( isDatabase(SQLITE) ) {
            columns.add( String.format("\tPRIMARY KEY( %s )", StringUtil.join(table.getPkColumnNamesUncameled(),", ")) );
        }
        return StringUtil.join( columns, ",\n" );
    }

    private String toColumnPropertiesString( Column column ) {
        StringBuilder sb = new StringBuilder();
        sb.append( toTypeString( column ) );
        sb.append( toDefaultString( column ) );
        sb.append( toNotNullString( column ) );
        return sb.toString();
    }

    private String toTypeString( Column column ) {

        StringBuilder sb = new StringBuilder( ' ' );
        int dataType = column.getDataType();
        switch( dataType ) {
            case Types.VARCHAR :
                if( isDatabase(ORACLE) ) {
                    sb.append( "VARCHAR2" );
                } else {
                    sb.append( SqlType.find(dataType).name );
                }
                sb.append( "(" ).append( column.getSize() ).append( ")" );
                break;
            case Types.DATE :
                if( isDatabase(H2) ) {
                    if( ! column.isDefinedByAnnotation() ) {
                        sb.append( "TIMESTAMP" );
                    }
                } else {
                    sb.append( SqlType.find(dataType).name );
                }
                break;
            case Types.NUMERIC :
            case Types.DECIMAL :
            case Types.DOUBLE :
                sb.append( "NUMBER" );
                if( column.getSize() != null ) {
                    sb.append( "(" ).append( column.getSize() );
                    if( column.getPrecison() != null ) {
                        sb.append( "," ).append( column.getPrecison() );
                    }
                    sb.append( ")" );
                }
                break;
            default :
                sb.append( SqlType.find(dataType).name );
        }
        return sb.toString();

    }

    private String toNotNullString( Column column ) {
        if( column.isNotNull() ) {
            return " NOT NULL";
        }
        return "";
    }

    private String toDefaultString( Column column ) {
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

    public String sqlDropColumn( Column column, TableLayout table ) {
        return String.format( "ALTER TABLE %s DROP (%s)", table.getName(), column.getName() );
    }

    public String sqlModifyColumn( Column column, boolean type, boolean defaultValue, boolean notNull, TableLayout table ) {

        StringBuilder sb = new StringBuilder();
        if( type )         sb.append( toTypeString( column ) );
        if( defaultValue ) sb.append( toDefaultString( column ) );
        if( notNull )      sb.append( toNotNullString( column ) );

        if( sb.length() == 0 ) return null;

        if( isDatabase( ORACLE ) ) {
            return String.format("ALTER TABLE %s MODIFY( %s %s )", table.getName(), column.getName(), sb );
        } else if( isDatabase( H2 ) ) {
            return String.format("ALTER TABLE %s ALTER COLUMN %s %s", table.getName(), column.getName(), sb );
        } else {
            return String.format("ALTER TABLE %s MODIFY( %s %s )", table.getName(), column.getName(), sb );
        }
    }

    public String sqlModifyColumn( Column column, TableLayout table ) {
        return sqlModifyColumn( column, true, true, true, table  );
    }

    public String sqlAddColumn( Column column, TableLayout table ) {
        return String.format("ALTER TABLE %s ADD( %s %s )", table.getName(), column.getName(), toColumnPropertiesString( column ) );
    }

    public String sqlDropTable( TableLayout tableLayout ) {
        return String.format( "DROP TABLE %s", tableLayout.getName() );
    }

    public String sqlDropPrimaryKey( TableLayout tableLayout ) {
        if( tableLayout == null || ! tableLayout.hasPk() ) return null;
        return String.format( "ALTER TABLE %s DROP CONSTRAINT %s", tableLayout.getName(), tableLayout.getPkName() );
    }

    public String sqlAddPrimaryKey( TableLayout tableLayout ) {
        String pkName = tableLayout.getPkName();
        if( StringUtil.isEmpty(pkName) ) {
            pkName = "PK_" + tableLayout.getName();
        }
        if( isDatabase(ORACLE,H2) ) {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)",
                tableLayout.getName(), pkName, StringUtil.join( tableLayout.getPkColumnNamesUncameled(), "," ) );
        } else if( isDatabase(SQLITE) ) {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT %s KEY (%s)",
                tableLayout.getName(), pkName, StringUtil.join( tableLayout.getPkColumnNamesUncameled(), "," ) );
        } else {
            return String.format( "ALTER TABLE %s ADD CONSTRAINT %s PRIMARY KEY (%s)",
                tableLayout.getName(), pkName, StringUtil.join(tableLayout.getPkColumnNamesUncameled(),",") );
        }
    }

    public String sqlDropIndex( TableIndex index, TableLayout table ) {
        return String.format( "DROP INDEX %s", index.getName() );
    }

    public String sqlCreateIndex( TableIndex index, TableLayout table ) {
        return String.format( "CREATE INDEX %s ON %s(%s)", index.getName(), table.getName(), StringUtil.join(index.getUncameledColumnNames(),",") );
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

    private boolean isString( int dataType ) {
        switch( dataType ) {
            case java.sql.Types.VARCHAR :
            case java.sql.Types.CHAR :
                return true;
        }
        return false;
    }

}
