package org.nybatis.core.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.nybatis.core.reflection.Reflector;

/**
 * Single Data
 *
 * @author nayasis
 *
 */
public class NMap extends LinkedHashMap<Object, Object> {

	private static final long serialVersionUID = 3398423628018291863L;

	private boolean isChanged = false;

	private boolean ignoreCastingError = true;

	public NMap() {
	    super();
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
    public NMap( Map vo ) {
		super( vo );
	}

	public NMap( Object vo ) {
		fromBean( vo );
	}

	public NMap( String json ) {
	    fromJson( json );
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

	public int getIntBy( int keyIndex ) {
		return getInt( getKey( keyIndex ) );
	}

	public short getShort( Object key ) {
		return getConverter( key).toShort();
	}

	public short getShortBy( int keyIndex ) {
		return getShort( getKey( keyIndex ) );
	}

	public char getChar( Object key ) {
		return getConverter( key).toChar();
	}

	public char getCharBy( int keyIndex ) {
		return getChar( getKey( keyIndex ) );
	}

	public long getLong( Object key ) {
		return getConverter( key).toLong();
	}

	public long getLongBy( int keyIndex ) {
		return getLong( getKey( keyIndex ) );
	}

	public float getFloat( Object key ) {
		return getConverter( key).toFloat();
	}

	public float getFloatBy( int keyIndex ) {
		return getFloat( getKey( keyIndex ) );
	}

	public boolean getBoolean( Object key ) {
		return getConverter( key ).toBoolean();
	}

	public Boolean getBooleanBy( int keyIndex ) {
		return getBoolean( getKey( keyIndex ) );
	}

	public Byte getByte( Object key ) {
		return getConverter( key).toByte();
	}

	public Byte getByteBy( int keyIndex ) {
		return getByte( getKey( keyIndex ) );
	}

	public NDate getNDate( Object key ) {
		return getConverter( key).toNDate();
	}

	public Date getDate( Object key ) {
		return getConverter( key).toDate();
	}

	public Calendar getCalender( Object key ) {
		return getConverter( key ).toCalendar();
	}

	public double getDouble( Object key ) {
		return getConverter( key ).toDouble();
	}

	public double getDoubleBy( int keyIndex ) {
		return getDouble( getKey( keyIndex ) );
	}

	public BigDecimal getBigDecimal( Object key ) {
		return getConverter( key ).toBigDecimal();
	}

	public BigDecimal getBigDecimalBy( int keyIndex ) {
		return getBigDecimal( getKey( keyIndex ) );
	}

	public BigInteger getBigInt( Object key ) {
		return getConverter( key ).toBigInt();
	}

	public BigInteger getBigIntBy( int keyIndex ) {
		return getBigInt( getKey( keyIndex ) );
	}

	public Object put( Object key ) {
		isChanged = true;
		return put( key, null );
	}

	public Object put( Object key, Object value ) {
		isChanged = true;
		return super.put( key, value );
	}

	public Object putIfAbsent( Object key, Object value ) {
		isChanged = true;
		return super.putIfAbsent( key, value );
	}

	public void putAll( Map<?, ?> map ) {
		isChanged = true;
		super.putAll( map );
	}

	public Object getBy( int keyIndex ) {
        return super.get( getKey( keyIndex ) );
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
	 * It only re-sort first level keys.
	 * so, if data has various reculsive unsorted map, result may be inaccuracy. <br/>
	 *
	 * @return hashcode for value
	 */
	@SuppressWarnings( { "rawtypes", "unchecked" } )
    public int getValueHash() {
		return new Reflector().toJson(new TreeMap(this)).hashCode();
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged( boolean changed ) {
		isChanged = changed;
	}

}
