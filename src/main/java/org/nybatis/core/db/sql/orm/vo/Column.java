package org.nybatis.core.db.sql.orm.vo;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.util.StringUtil;

/**
 * Table Column Layout
 *
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public class Column {

    private String  key;
    private Integer dataType;
    private String  dataTypeName;
    private boolean notNull;
    private boolean pk;
    private Integer size;
    private Integer precison; // used in Number type column
    private boolean definedByAnnotation = false;

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getName() {
        return StringUtil.toUncamel( key );
    }

    /**
     * get data type of column
     *
     * <pre>
     *  it comapres with {@link java.sql.Types}
     * </pre>
     *
     * @return column type
     */
    public int getDataType() {
        return dataType;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataType( int type ) {
        setDataType( type, null );
    }

    public void setDataType( int type, String typeName ) {

        SqlType sqlType = SqlType.find( typeName );

        if( sqlType == null ) {
            sqlType = SqlType.find( type );
        }

        if( sqlType == null ) {
            this.dataType     = type;
            this.dataTypeName = typeName;
        } else {
            this.dataType     = sqlType.code;
            this.dataTypeName = sqlType.name;
        }

        if( ! canAssignLength()    ) size     = null;
        if( ! canAssignPrecision() ) precison = null;

    }

    public boolean isNotNull() {
        return notNull;
    }

    public void setNotNull( boolean notNull ) {
        this.notNull = notNull;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk( boolean pk ) {
        this.pk = pk;
        if( pk ) {
            this.notNull = true;
        }
    }

    public String getDataTypeForSqlMaking() {
        switch( dataTypeName ) {
            case "BLOB" :
            case "CLOB" :
            case "DATE" :
                return ":" + dataTypeName;
            default :
                return "";
        }
    }

    public Integer getSize() {
        return size;
    }

    public void setSize( Integer size ) {
        if( ! canAssignLength() ) return;
        this.size = size;
    }

    public Integer getPrecison() {
        return precison;
    }

    public void setPrecison( Integer precison ) {
        if( ! canAssignPrecision() ) return;
        this.precison = precison;
    }

    public boolean isDefinedByAnnotation() {
        return definedByAnnotation;
    }

    public void setDefinedByAnnotation( boolean definedByAnnotation ) {
        this.definedByAnnotation = definedByAnnotation;
    }

    public boolean isEqual( Column column ) {
        return isEqual( column, true );
    }

    public boolean isEqual( Column column, boolean checkPkDifference ) {
        if( column == null ) return false;
        if( StringUtil.isNotEqual(key, column.key) )                   return false;
        if( dataType != column.dataType ) return false;
        if( notNull  != column.notNull  ) return false;
        if( checkPkDifference && pk != column.pk ) return false;
        if( size     != column.size     ) return false;
        if( precison != column.precison ) return false;
        return true;
    }

    private boolean canAssignLength() {
        if( dataType == null ) return true;
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
            case java.sql.Types.CHAR :
            case java.sql.Types.VARCHAR :
            case java.sql.Types.LONGVARCHAR :
                return true;
        }
        return false;
    }

    private boolean canAssignPrecision() {
        if( dataType == null ) return true;
        switch( dataType ) {
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
