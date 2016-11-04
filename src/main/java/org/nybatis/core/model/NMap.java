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
import java.util.Map;

/**
 * Single Data
 *
 * @author nayasis@gmail.com
 *
 */
public class NMap extends LinkedHashMap {

	private static final long serialVersionUID = 3398423628018291863L;

	private boolean ignoreCastingError = true;

	/**
	 * default constructor
	 */
	public NMap() {
	    super();
	}

	/**
	 * constructor
	 *
	 * @param value	initial value
	 */
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
		bind( value );
	}

	/**
	 * ignore type cast error on calling get(..) method
	 * @param ignore whether or not ignoring type cast error
	 * @return self instance
	 */
	public NMap ignoreCastingError( boolean ignore ) {
		ignoreCastingError = ignore;
		return this;
	}

	/**
	 * get ignoring option of type casting error.
	 * @return if true, ignore type casting error.
	 */
	public boolean isIgnoreCastingError() {
		return ignoreCastingError;
	}

	/**
	 * bind value from Bean or Map or JSON text
	 * @param beanOrMapOrJson	bind value (Bean or Map or JSON text)
	 * @return self instance
	 */
    public NMap bind( Object beanOrMapOrJson ) {
		super.putAll( Reflector.toMapFrom( beanOrMapOrJson ) );
	    return this;
	}

	/**
	 * Convert data to Json format
	 * @param prettyPrint pretty print Y/N
	 * @return Json string
	 */
	public String toJson( boolean prettyPrint ) {
		return Reflector.toJson( this, prettyPrint );
	}

	/**
	 * Convert data to Json format
	 *
	 * @return Json string
	 */
	public String toJson() {
		return toJson( false );
	}

	/**
	 * convert data to specific Bean
	 *
	 * @param klass	Class type to convert
	 * @return converted bean
	 */
	public <T> T toBean( Class<T> klass ) {
		return Reflector.toBeanFrom( this, klass );
	}

	private PrimitiveConverter getConverter( Object key ) {
		return new PrimitiveConverter( get(key) );
	}

	/**
	 * get value as string
	 * @param key key
	 * @return String value
	 */
	public String getString( Object key ) {
		return getConverter(key).toString();
	}

	/**
	 * get value as int
	 * @param key key
	 * @return int value
	 */
	public int getInt( Object key ) {
		return getConverter(key).toInt();
	}

	/**
	 * get value as short
	 * @param key key
	 * @return short value
	 */
	public short getShort( Object key ) {
		return getConverter(key).toShort();
	}

	/**
	 * get value as char
	 * @param key key
	 * @return char value
	 */
	public char getChar( Object key ) {
		return getConverter(key).toChar();
	}

	/**
	 * get value as long
	 * @param key key
	 * @return long value
	 */
	public long getLong( Object key ) {
		return getConverter(key).toLong();
	}

	/**
	 * get value as float
	 * @param key key
	 * @return float value
	 */
	public float getFloat( Object key ) {
		return getConverter(key).toFloat();
	}

	/**
	 * get value as boolean
	 * @param key key
	 * @return boolean value
	 */
	public boolean getBoolean( Object key ) {
		return getConverter(key).toBoolean();
	}

	/**
	 * get value as byte
	 * @param key key
	 * @return byte value
	 */
	public Byte getByte( Object key ) {
		return getConverter(key).toByte();
	}

	/**
	 * get value as NDate
	 * @param key key
	 * @return NDate value
	 */
	public NDate getNDate( Object key ) {
		return getConverter(key).toNDate();
	}

	/**
	 * get value as Date
	 * @param key key
	 * @return Date value
	 */
	public Date getDate( Object key ) {
		return getConverter(key).toDate();
	}

	/**
	 * get value as double
	 * @param key key
	 * @return double value
	 */
	public double getDouble( Object key ) {
		return getConverter( key ).toDouble();
	}

	/**
	 * get value as BigDecimal
	 * @param key key
	 * @return BigDecimal value
	 */
	public BigDecimal getBigDecimal( Object key ) {
		return getConverter( key ).toBigDecimal();
	}

	/**
	 * get value as BigInt
	 * @param key key
	 * @return BigInt value
	 */
	public BigInteger getBigInt( Object key ) {
		return getConverter( key ).toBigInt();
	}

	/**
	 * get value (auto type casting)
	 * @param key key
	 * @return value
	 */
	public <T> T getAs( Object key ) {
		Object val = get( key );
		return val == null ? null : (T) val;
	}

	/**
	 * Get value by json path
	 *
	 * @param jsonPath json path
	 * @see <a href="https://github.com/jayway/JsonPath">json path example</a>
	 * @return value(s) extracted by json path
	 * @throws JsonPathNotFoundException occurs when json path is not found.
	 */
	public Object getByJsonPath( String jsonPath ) throws JsonPathNotFoundException {

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

		return val;

	}

	/**
	 * get value by key's index
	 *
	 * @param keyIndex key index
	 * @return value of key by index
	 */
	public Object getByIndex( int keyIndex ) {
        return super.get( getKey(keyIndex) );
	}

	/**
	 * get key by index
	 *
	 * @param index	sequence index
	 * @return key
	 */
	public Object getKey( int index ) {

        int maxIndex = super.size() - 1;
        if( 0 > index || index > maxIndex ) throw new ArrayIndexOutOfBoundsException( String.format("maxIndex : %d, inputedIndex : %d", maxIndex, index) );

        Iterator<Object> iterator = super.keySet().iterator();
        for( int i = 0; i < index; i++ ) {
            iterator.next();
        }
        return iterator.next();

	}

	@Override
	public NMap clone() {
		return Reflector.clone( this );
	}

	/**
	 * get debug string
	 *
	 * @return	debug string contains key and value
	 */
	public String toDebugString() {
		return toDebugString( true, false );
	}

	/**
	 * get debug string contains key's class type and value
	 *
	 * @param showHeader	if true, show header
	 * @param showType		if true, show key's class type
	 * @return debug string
	 */
	public String toDebugString( boolean showHeader, boolean showType ) {

		NList result = new NList();

		for( Object key : keySet() ) {
			result.add( "key", key );
			Object val = get( key );
			if( showType ) {
				result.add( "type", val == null ? null : val.getClass().getTypeName() );
			}
			result.add( "val", val );
		}

		return result.toDebugString( showHeader, true );

	}

	/**
	 * get hashcode for value.
	 *
	 * @return hashcode for value
	 */
    public int getValueHash() {
		return Reflector.toJson(this, false, true, false).hashCode();
	}


}
