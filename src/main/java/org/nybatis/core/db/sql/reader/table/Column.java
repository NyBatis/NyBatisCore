package org.nybatis.core.db.sql.reader.table;

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
    private int     dataType;
    private String  dataTypeName;
    private boolean notNull;
    private boolean pk;
    private Integer size;
    private Integer precison; // used in Number type column
    private String  comment;

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
        this.notNull = true;
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
        this.size = size;
    }

    public Integer getPrecison() {
        return precison;
    }

    public void setPrecison( Integer precison ) {
        this.precison = precison;
    }

    public String getComment() {
        return comment;
    }

    public void setComment( String comment ) {
        this.comment = StringUtil.trim( comment );
    }
}
