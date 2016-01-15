package org.nybatis.core.model;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.reflection.Reflector;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Single Data
 *
 * @author nayasis
 *
 */
public class NMap extends LinkedHashMap {

	private static final long serialVersionUID = 3398423628018291863L;

	private boolean ignoreCastingError = true;

	public NMap() {
	    super();
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
    public NMap( Map value ) {
		super( value );
	}

	/**
	 * Constructor
	 *
	 * @param value if value is String (or StringBuffer or StringBuilder), init map with json parser.
	 *              if value is Entity, init map with Bean Parser.
	 */
	public NMap( Object value ) {

		if( value instanceof String || value instanceof StringBuffer || value instanceof StringBuilder ) {
			fromJson( value.toString() );
		} else {
			fromBean( value );
		}

	}

	public NMap ignoreCastingError( boolean ignore ) {
		ignoreCastingError = ignore;
		return this;
	}

	public boolean isIgnoreCastingError() {
		return ignoreCastingError;
	}

    public NMap fromJson( String json ) {
		super.putAll( new Reflector().toMapFromJson(json) );
	    return this;
	}

    public NMap fromBean( Object bean ) {
		super.putAll( new Reflector().toMapFromBean( bean ) );
	    return this;
	}

	/**
	 * Convert data to Json format
	 * @param prettyPrint pretty print Y/N
	 * @return Json string
	 */
	public String toJson( boolean prettyPrint ) {
		return new Reflector().toJson( this, prettyPrint );
	}

	/**
	 * Convert data to Json format
	 *
	 * @return Json string
	 */
	public String toJson() {
		return toJson(false);
	}

	public <T> T toBean( Class<T> klass ) {
		return new Reflector().toBeanFromMap( this, klass );
	}

	private PrimitiveConverter getConverter( Object key ) {
		return new PrimitiveConverter( get(key) );
	}

	public String getString( Object key ) {
		return getConverter( key ).toString();
	}

	public String getStringBy( int keyIndex ) {
		return getString( getKey( keyIndex ) );
	}

	public int getInt( Object key ) {
		return getConverter(key).toInt();
	}

	public int getIntByIndex( int keyIndex ) {
		return getInt( getKey( keyIndex ) );
	}

	public short getShort( Object key ) {
		return getConverter(key).toShort();
	}

	public short getShortByIndex( int keyIndex ) {
		return getShort( getKey( keyIndex ) );
	}

	public char getChar( Object key ) {
		return getConverter( key).toChar();
	}

	public char getCharByIndex( int keyIndex ) {
		return getChar( getKey( keyIndex ) );
	}

	public long getLong( Object key ) {
		return getConverter( key).toLong();
	}

	public long getLongByIndex( int keyIndex ) {
		return getLong( getKey( keyIndex ) );
	}

	public float getFloat( Object key ) {
		return getConverter( key).toFloat();
	}

	public float getFloatByIndex( int keyIndex ) {
		return getFloat( getKey( keyIndex ) );
	}

	public boolean getBoolean( Object key ) {
		return getConverter( key ).toBoolean();
	}

	public Boolean getBooleanByIndex( int keyIndex ) {
		return getBoolean( getKey( keyIndex ) );
	}

	public Byte getByte( Object key ) {
		return getConverter( key).toByte();
	}

	public Byte getByteByIndex( int keyIndex ) {
		return getByte( getKey( keyIndex ) );
	}

	public NDate getNDate( Object key ) {
		return getConverter(key).toNDate();
	}

	public NDate getNDateByIndex( int keyIndex ) {
		return  getNDate( getKey(keyIndex) );
	}

	public Date getDate( Object key ) {
		return getConverter( key).toDate();
	}

	public Calendar getCalender( Object key ) {
		return getConverter( key ).toCalendar();
	}

	public Calendar getCalenderByIndex( int keyIndex ) {
		return getCalender( getKey( keyIndex ) );
	}

	public double getDouble( Object key ) {
		return getConverter( key ).toDouble();
	}

	public double getDoubleByIndex( int keyIndex ) {
		return getDouble( getKey( keyIndex ) );
	}

	public BigDecimal getBigDecimal( Object key ) {
		return getConverter( key ).toBigDecimal();
	}

	public BigDecimal getBigDecimalByIndex( int keyIndex ) {
		return getBigDecimal( getKey( keyIndex ) );
	}

	public BigInteger getBigInt( Object key ) {
		return getConverter( key ).toBigInt();
	}

	public BigInteger getBigIntByIndex( int keyIndex ) {
		return getBigInt( getKey( keyIndex ) );
	}

	public void put( Object key ) {
		put( key, null );
	}

	public <T> T getAs( Object key ) {
		return castType( get( key ) );
	}

	/**
	 * Get value by json path
	 *
	 * @param jsonPath json path
	 * @see https://github.com/jayway/JsonPath
	 * @return value(s) extracted by json path
	 * @throws JsonPathNotFoundException
	 */
	public <T> T getByJsonPath( String jsonPath ) throws JsonPathNotFoundException {

		Object val;

		if( containsKey( jsonPath ) ) {
			val = get( jsonPath );
		} else {

			try {
				val =  JsonPath.read( this, jsonPath );
			} catch( PathNotFoundException e ) {
				throw new JsonPathNotFoundException( e.getMessage() );
			} catch( IllegalArgumentException e ) {
				throw new JsonPathNotFoundException( e.getMessage() );
			}

		}

		return castType( val );

	}

	private <T> T castType( Object val ) {
		return val == null ? null : (T) val;
	}

	public <T> T getByIndex( int keyIndex ) {
        return castType( super.get( getKey( keyIndex ) ) );
	}

	public <T> T getKey( int index ) {

        int maxIndex = super.size() - 1;

        if( 0 > index || index > maxIndex ) throw new ArrayIndexOutOfBoundsException( String.format("maxIndex : %d, inputedIndex : %d", maxIndex, index) );

        Iterator<Object> iterator = super.keySet().iterator();

        for( int i = 0, iCnt = index; i < iCnt; i++ ) {
            iterator.next();
        }

        return castType( iterator.next() );

	}

	public Set<Object> keySetCloned() {
		Set<Object> result = new LinkedHashSet<>();
		result.addAll( super.keySet() );
		return result;
	}

	public NMap clone() {
		return new Reflector().clone( this );
	}

	public String toDebugString() {
		return toDebugString( true, false );
	}

	public String toDebugString( boolean showHeader, boolean showType ) {

		NList result = new NList();

		for( Object key : keySet() ) {

			result.addRow( "key", key );

			Object val = get( key );

			if( showType ) {
				result.addRow( "type", val == null ? null : val.getClass().getTypeName() );
			}

			result.addRow( "val", val );

		}

		return result.toDebugString( showHeader, true );


	}

	/**
	 * get hashcode for value. <br/><br/>
	 *
	 * @return hashcode for value
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
    public int getValueHash() {
		return new Reflector().toJson(this, false, true).hashCode();
	}


}
