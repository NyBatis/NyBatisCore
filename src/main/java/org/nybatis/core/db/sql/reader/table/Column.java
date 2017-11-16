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
    private boolean nullable;
    private boolean pk;
    private int     size;
    private int     precison; // used in Number type column

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

    public void setDataType( int type, String typeName ) {

        SqlType sqlType = SqlType.find( typeName );

        if( sqlType == null ) {
            sqlType = SqlType.find( type );
        }

        if( sqlType == null ) {
            this.dataType     = type;
            this.dataTypeName = typeName;
        } else {
            this.dataType     = sqlType.toCode();
            this.dataTypeName = sqlType.name();
        }

    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable( boolean nullable ) {
        this.nullable = nullable;
    }

    public boolean isPk() {
        return pk;
    }

    public void setPk( boolean pk ) {
        this.pk = pk;
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

    public int getSize() {
        return size;
    }

    public void setSize( int size ) {
        this.size = size;
    }

    public int getPrecison() {
        return precison;
    }

    public void setPrecison( int precison ) {
        this.precison = precison;
    }
}
