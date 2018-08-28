package org.nybatis.core.db.sql.orm.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.orm.indicator.klass.ClassSqltypeIndicator;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

/**
 * Table Column Layout
 *
 * @author nayasis@gmail.com
 * @since 2015-09-08
 */
public class TableColumn {

    private String  key;
    private String  name;
    private Integer dataType;
    private String  dataTypeName;
    private boolean notNull;
    private boolean pk;
    private Integer size;
    private Integer precison; // used in Number type column
    private String  defaultValue;
    private boolean definedByAnnotation = false;

    @JsonIgnore
    private TableLayout table;

    public TableColumn( TableLayout tableLayout ) {
        this.table = tableLayout;
    }

    public String getKey() {
        return key;
    }

    public void setKey( String key ) {
        if( Validator.isEmpty(key) ) return;
        if( key.contains("_") || Validator.isMatched(key, "^[A-Z].*$") ) {
            this.key = StringUtil.toCamel( key );
            this.name = key.toLowerCase();
        } else {
            this.key = key;
            this.name = key;
        }
    }

    public String getName() {
        return name;
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

    public void setDataType( Class klass ) {
        setDataType( findColumnType(klass).code, null );
        setSize( findColumnLength(klass) );
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

        Integer length    = null;
        Integer precision = null;

        if( Validator.isMatched( typeName, "\\w+?\\(.+?\\)" ) ) {
            String[] infos = typeName.replaceFirst( "^.*?\\((.+?)\\)", "$1" ).split( "," );
            if( infos.length >= 1 ) length    = new PrimitiveConverter( infos[0] ).toInt();
            if( infos.length >= 2 ) precision = new PrimitiveConverter( infos[1] ).toInt();
        }

        if( canAssignLength()    ) {
            size = length;
        } else {
            size = null;
        }

        if( canAssignPrecision() )  {
            precison = precision;
        } else {
            precison = null;
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

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue( String defaultValue ) {
        defaultValue = StringUtil.trim( defaultValue );
        if( StringUtil.isEmpty(defaultValue) ) {
            this.defaultValue = null;
        } else if( Validator.isMatched( defaultValue, "'.+?'" ) ) {
            this.defaultValue = defaultValue.replaceFirst( "'(.+?)'", "$1" );
        } else {
            this.defaultValue = defaultValue;
        }
    }

    public boolean isDefinedByAnnotation() {
        return definedByAnnotation;
    }

    public void setDefinedByAnnotation( boolean definedByAnnotation ) {
        this.definedByAnnotation = definedByAnnotation;
    }

    @JsonIgnore
    public TableLayout getTable() {
        return table;
    }

    @JsonIgnore
    public void setTable( TableLayout table ) {
        this.table = table;
    }

    public boolean isEqual( TableColumn column ) {
        return isEqual( column, true, false );
    }

    public boolean isEqual( TableColumn column, boolean checkPkDifference, boolean checkDefaultValueDifference ) {
        if( column == null ) return false;
        if( StringUtil.isNotEqual(key, column.key) ) return false;
        if( ! isClob(this) && ! isClob(column) ) {
            if( ! isBlob(this) && !isBlob(column) ) {
                if( dataType != null && column.dataType != null && ! dataType.equals(column.dataType) ) {
                    if( dataTypeName != null && column.dataTypeName != null && ! dataTypeName.equals( column.dataTypeName ) )
                        return false;
                }
            }
        }
        if( notNull  != column.notNull  ) return false;
        if( checkDefaultValueDifference && StringUtil.isNotEqual(defaultValue, column.defaultValue) ) return false;
        if( checkPkDifference && pk != column.pk ) return false;
        if( size != null && column.size != null && ! size.equals(column.size) ) return false;
        return !( precison != null && column.precison != null && !precison.equals( column.precison ) );
    }

    private boolean isClob( TableColumn column ) {
        if( column == null || column.dataType == null ) return false;
        if( column.dataType == SqlType.CLOB.code     ) return true;
        if( column.dataType == SqlType.LONGTEXT.code ) return true;
        return false;
    }

    private boolean isBlob( TableColumn column ) {
        if( column == null || column.dataType == null ) return false;
        if( column.dataType == SqlType.BLOB.code          ) return true;
        if( column.dataType == SqlType.LONGVARBINARY.code ) return true;
        return false;
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

    /**
     * get SqlType for column creation
     *
     * @param klass	matched class
     * @return SqlType
     */
    private SqlType findColumnType( Class<?> klass ) {
        return ClassSqltypeIndicator.$.getSqlType( klass, table.getEnvironmentId() );
    }

    private Integer findColumnLength( Class klass ) {
        return ClassSqltypeIndicator.$.getLength( klass, table.getEnvironmentId() );
    }

}
