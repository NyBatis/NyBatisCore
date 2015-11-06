package org.nybatis.core.db.sql.sqlMaker;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.model.NDate;

/**
 * Parameter Value to bind in SQL
 *
 * @author nayasis
 */
public class BindParam {

	private String  key;
    private SqlType type;
    private Object  value;
    private boolean out    = false;

    /**
     * Default Constructor
     *
     * @param value value to set
     */
	public BindParam( Object value ) {
		setValue( null, value, null );
	}

    public BindParam( String key, Object value ) {
        setValue( key, value, null );
    }

    public BindParam( String key, Object value, SqlType sqlType ) {
    	setValue( key, value, sqlType );
    }

    public BindParam( String key, Object value, SqlType sqlType, boolean isOutParameter ) {

    	this.key   = key;
    	this.type  = sqlType;
    	this.value = value;
    	this.out   = isOutParameter;

    	setValue( key, value, sqlType );

    }

    public Object getValue() {
        return this.value;
    }

    public SqlType getType() {
        return type;
    }

    public String getKey() {
    	return key;
    }

    public boolean isOut() {
    	return out;
    }

    public String toString() {

		StringBuilder sb = new StringBuilder();

		sb.append( "{" );

		if( key  != null  ) sb.append( "key:" ).append( key ).append( ", " );
		if( type != null  ) sb.append( "type:" ).append( type ).append( ", " );
		if( value != null ) sb.append( "value:" ).append( value ).append( ", " );
		if( out == true   ) sb.append( "out:y" );

		sb.append( "}" );

		return sb.toString();

	}

    private void setValue( String key, Object value, SqlType sqlType ) {

    	this.key   = key;
    	this.type  = sqlType;
    	this.value = value;

        if( value == null ) {
			setNull();
        	return;
        }

        Class<?> klass = value.getClass();

        if( type == null ) {

        	type = SqlType.find( klass );

        	if( klass.isArray() ) {
        		this.value = Arrays.asList( value );
        	}


        } else {

            if( klass == byte[].class ) {
            	type = SqlType.BLOB;
            } else if( klass == Byte[].class ) {
            	type = SqlType.BLOB_BOXED;
            } else if( klass == String.class ) {

            	try {

            		if( type == SqlType.INT || type == SqlType.INTEGER ) {
            			this.value = Integer.parseInt( (String) value );
            		} else if( type == SqlType.BOOLEAN ) {
            			this.value = "true".equalsIgnoreCase( (String) value );
            		} else if( type == SqlType.BIT ) {
            			this.value = "true".equalsIgnoreCase( (String) value );
            		} else if( type == SqlType.TINYINT ) {
            			this.value = Byte.parseByte( (String) value );
            		} else if( type == SqlType.SMALLINT ) {
            			this.value = Short.parseShort( (String) value );
            		} else if( type == SqlType.FLOAT ) {
            			this.value = Float.parseFloat( (String) value );
            		} else if( type == SqlType.DOUBLE ) {
            			this.value = Double.parseDouble( (String) value );
            		} else if( type == SqlType.DATE || type == SqlType.TIME || type == SqlType.TIMESTAMP  ) {
            			this.value = new NDate( (String) value, "YYYY-MM-DD HH:MI:SS" ).toDate();
            		} else if( type == SqlType.REAL || type == SqlType.DECIMAL || type == SqlType.NUMERIC  ) {
            			this.value = new BigDecimal( (String) value );
            		} else if( type == SqlType.LIST ) {
            			this.value = new ArrayList<String>().add( (String) value );
            		}

            	} catch( NumberFormatException e ) {
            		throw new SqlConfigurationException( "parameter casting exception. (key:{}, value:{}, {}:{})", this.key, value, e.getClass().getSimpleName(), e.getMessage() );
            	}

            } else if( klass == Date.class ) {
            	if( type == SqlType.TIME || type == SqlType.TIMESTAMP ) return;
            	type = SqlType.DATE;

            } else if( klass == Calendar.class ) {
            	this.value = ((Calendar) this.value).getTime();
            	if( type == SqlType.TIME || type == SqlType.TIMESTAMP ) return;
            	type = SqlType.DATE;

            } else if( klass == NDate.class ) {
            	this.value = ((NDate) this.value).toDate();
            	if( type == SqlType.TIME || type == SqlType.TIMESTAMP ) return;
            	type = SqlType.DATE;

			// Databse Native Array Object (Must be defined it's type in database)
			} else if( type == SqlType.ARRAY ) {
				this.value = value;

            } else if( type == SqlType.VARCHAR || type == SqlType.CHAR ) {
            	this.value = value.toString();
            }

        }

    }

    private void setNull() {
    	value = null;
    	type  = SqlType.NULL;
    }

}