package org.nybatis.core.db.sql.sqlMaker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.model.NDate;
import org.nybatis.core.util.StringUtil;

public class BindStruct {

	private String  key    = null;
    private SqlType type   = null;
    private boolean out    = false;
    private boolean inQuot = false; // binding 변수가 ' ' 구문 사이에 있는지 여부

    public BindStruct( String key, SqlType type, boolean outParameter, boolean inQuot ) {
    	this.key   = key;
    	this.type  = type;
    	this.out   = outParameter;
    }

    public BindStruct( String keyInfo, boolean inQuot ) {

		this.inQuot = inQuot;

        List<String> keys = StringUtil.split( keyInfo, ":" );

        this.out = isOutParameter( keys );

        switch( keys.size() ) {
        	case 0 :
        		this.key  = "";
        		break;
        	case 1 :
        		this.key  = keys.get( 0 );
        		break;
        	default :
        		this.key  = keys.get( 0 );
        		this.type = SqlType.find( keys.get(1) );

        }

    }

    public String getKey() {
    	return key;
    }

    public SqlType getType() {
    	return type == null ? SqlType.VARCHAR : type;
    }

    public boolean isOut() {
    	return out;
    }

	private boolean isOutParameter( List<String> keys ) {

		Boolean isOutParameter = null;

		for( int i = keys.size() - 1; i > 0; i-- ) {

	    	String prop = keys.get( i ).toLowerCase();

	    	if( "in".equals(prop) ) {

	    		if( isOutParameter == null ) isOutParameter = false;
	    		keys.remove( i );

	    	} else if( "out".equals(prop) ) {

	    		if( isOutParameter == null ) isOutParameter = true;
	    		keys.remove( i );

	    	}

	    }

		return isOutParameter != null && isOutParameter == true;

	}

	public BindParam toBindParam( Object value ) {
		return new BindParam( key, value, type, out );
	}

    public String toString() {

    	if( out ) {
    		return String.format( "{key:%s, type:%s, out:%b}", getKey(), getType(), isOut() );
    	} else {
    		return String.format( "{key:%s, type:%s}", getKey(), getType() );
    	}

    }

    protected String toDebugParam( BindParam bindValue ) {

    	if( out ) return String.format( "#{%s:%s}", getKey(), getType() );

    	Object value = bindValue.getValue();

        if( bindValue.getType() == SqlType.LIST ) {

        	List<String> temp = new ArrayList<>();

        	for( Object e : (List) value ) {
        		temp.add( toDebugParam(e) );
        	}

        	return StringUtil.join( temp, "," );

        }

        return toDebugParam( value );

    }

    private String toDebugParam( Object value ) {

    	if( value == null ) return "NULL";

        if( value instanceof String || value instanceof Boolean ) {

            if( inQuot ) {
                return value.toString();
            } else {
                return String.format( "'%s'", value );
            }

        } else if( value instanceof Date ) {
			String dateString = new NDate( (Date) value ).toString( "YYYY-MM-DD HH:MI:SS" );
			return String.format( "TO_DATE( '%s','%s')", dateString, "YYYY-MM-DD HH24:MI:SS" );
		}

        return value.toString();

    }

}
