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
 * @author nayasis
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

	public <T> T toBean( Class<T> klass ) {
		return Reflector.toBeanFrom( this, klass );
	}

	private PrimitiveConverter getConverter( Object key ) {
		return new PrimitiveConverter( get(key) );
	}

	public String getString( Object key ) {
		return getConverter(key).toString();
	}

	public int getInt( Object key ) {
		return getConverter(key).toInt();
	}

	public short getShort( Object key ) {
		return getConverter(key).toShort();
	}

	public char getChar( Object key ) {
		return getConverter(key).toChar();
	}

	public long getLong( Object key ) {
		return getConverter(key).toLong();
	}

	public float getFloat( Object key ) {
		return getConverter(key).toFloat();
	}

	public boolean getBoolean( Object key ) {
		return getConverter(key).toBoolean();
	}

	public Byte getByte( Object key ) {
		return getConverter(key).toByte();
	}

	public NDate getNDate( Object key ) {
		return getConverter(key).toNDate();
	}

	public Date getDate( Object key ) {
		return getConverter(key).toDate();
	}

	public Calendar getCalender( Object key ) {
		return getConverter( key ).toCalendar();
	}

	public double getDouble( Object key ) {
		return getConverter( key ).toDouble();
	}

	public BigDecimal getBigDecimal( Object key ) {
		return getConverter( key ).toBigDecimal();
	}

	public BigInteger getBigInt( Object key ) {
		return getConverter( key ).toBigInt();
	}

	public void put( Object key ) {
		put( key, null );
	}

	public <T> T getAs( Object key ) {
		return castType( get( key ) );
	}

	private <T> T castType( Object val ) {
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

	public Object getByIndex( int keyIndex ) {
        return super.get( getKey(keyIndex) );
	}

	public Object getKey( int index ) {

        int maxIndex = super.size() - 1;

        if( 0 > index || index > maxIndex ) throw new ArrayIndexOutOfBoundsException( String.format("maxIndex : %d, inputedIndex : %d", maxIndex, index) );

        Iterator<Object> iterator = super.keySet().iterator();

        for( int i = 0, iCnt = index; i < iCnt; i++ ) {
            iterator.next();
        }

        return iterator.next();

	}

	@Override
	public NMap clone() {
		return Reflector.clone( this );
	}

	public String toDebugString() {
		return toDebugString( true, false );
	}

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
	 * get hashcode for value. <br><br>
	 *
	 * @return hashcode for value
	 */
    public int getValueHash() {
		return Reflector.toJson(this, false, true, false).hashCode();
	}


}
