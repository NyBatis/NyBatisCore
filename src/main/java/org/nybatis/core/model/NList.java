package org.nybatis.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.NListSerializer;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

/**
 * Multiple Data aggregated by NMap
 *
 * @author nayasis
 */
@JsonSerialize( using = NListSerializer.class )
public class NList implements Serializable, Cloneable, Iterable<NMap> {

	private static final long      serialVersionUID   = -3169472792493027837L;
    private static final String    NO_KEY             = NList.class.getName() + ".NO_KEY";

    protected Map<Object, Integer> header      = new LinkedHashMap<>();
    protected Map<Object, String>  alias       = new LinkedHashMap<>();
    protected List<NMap>           dataBody    = new ArrayList<>();


    public NList() {}

    public NList( NList initialData ) {
        if( initialData == null || initialData.size() == 0 ) return;
    	dataBody.addAll( initialData.dataBody );
        header.putAll( initialData.header );
        alias.putAll( initialData.alias );
    }

    public NList( List<?> initialData ) {
    	addRow( initialData );
    }

    public NList( List<NMap> data, Set<?> header ) {

        if( data == null || header == null ) return;

        for( NMap nmap : data )
            dataBody.add( nmap );

        for( Object key : header )
            this.header.put( key, data.size() );

    }

    /**
     * 별칭을 등록한다.
     *
     * @param alias 별칭
     */
    public NList addAliases( Object... alias ) {

        int startIndex = this.alias.size();

        Iterator<Object> iterator = header.keySet().iterator();

        for( int i = 0; i < startIndex; i++ ) {
            if( ! iterator.hasNext() ) return this;
            iterator.next();
        }

        for( Object text : alias ) {
            if( ! iterator.hasNext() ) break;
            this.alias.put( iterator.next(), StringUtil.nvl( text ) );
        }

        return this;

    }

    /**
     * key 에 해당하는 별칭을 세팅한다.
     *
     * @param key   key
     * @param alias 별칭
     * @param overwrite 덮어쓰기 여부 (false 일 경우, 별칭이 이미 세팅되어 있으면 변경하지 않는다.)
     */
    public NList setAlias( Object key, Object alias, boolean overwrite ) {

    	if( containsKey(key) ) {
            if( overwrite || ! this.alias.containsKey( key ) ) {
                this.alias.put( key, StringUtil.nvl( alias ) );
            }
        }

        return this;

    }

    /**
     * key 에 해당하는 별칭을 세팅한다.
     *
     * @param key   key
     * @param alias 별칭
     */
    public NList setAlias( Object key, String alias ) {
    	setAlias( key, alias, true );
        return this;
    }

    /**
     * key에 해당하는 별칭을 구한다.
     *
     * @param key key
     * @return 별칭
     */
    public String getAlias( Object key ) {
    	return containsKey( key ) ? alias.get( key ) : null;
    }

    /**
     * 별칭 목록을 구한다.
     *
     * @return 별칭 목록
     */
    public List<String> getAliases() {

    	refreshKey();

    	List<String> aliases = new ArrayList<>();

    	for( Object key : header.keySet() ) {
    		aliases.add( StringUtil.nvl( getAlias(key), StringUtil.nvl(key)) );
    	}

    	return aliases;

    }

    /**
     * key 정보를 추가한다.
     *
     * @param key 추가할 key
     * @return self-instance
     */
    public NList addKey( Object... key ) {

        for( Object val : key ) {
        	if( ! containsKey( val ) ) {
        		header.put( val, 0 );
        	}
        }

        return this;

    }


    /**
     * Refresh Header and Key information
     */
    public NList refreshKey() {

        Map<Object, Integer> newHeader = new HashMap<>();

        for( int i = dataBody.size() - 1; i >=0; i-- ) {
            for( Object key : dataBody.get( i ).keySet() ) {
                if( newHeader.containsKey(key) ) continue;
                newHeader.put( key, i + 1 );
            }
        }

        Map<Object, Integer> buffer = new LinkedHashMap<>();

        Set<Object> bufferKeyset = new LinkedHashSet<>();
        bufferKeyset.addAll( header.keySet() );

        for( Object key : header.keySet() ) {
            if( newHeader.containsKey(key) ) {
                buffer.put( key, newHeader.get( key ) );
                bufferKeyset.remove( key );
            }
        }

        for( Object key : bufferKeyset ) {
            buffer.put( key, newHeader.get( key ) );
        }

        header.clear();
        header.putAll( buffer );

        return this;

    }

    /**
     * 값을 추가한다.
     *
     * @param key   키
     * @param value 값
     */
    public NList addRow( Object key, Object value ) {

    	int dataSize  = size( key );
    	int totalSize = size();

        if( totalSize == dataSize ) {
    		NMap row = new NMap();
    		row.put( key, value );
    		dataBody.add( row );

        } else {
    		dataBody.get( dataSize ).put( key, value );
        }

        header.put( key, ++dataSize );

        return this;

    }

    /**
     * add row with json text
     *
     * <pre>
     * {@link NList} data = new {@link NList};
     *
     * data.add( "{key:'1', val:'AAA'}" );
     *
     * </pre>
     *
     * @param jsonString json 문자열
     */
    public NList addRow( String jsonString ) {
        addRow( new NMap( jsonString ) );
        return this;
    }

    public NList addRow( Map<?, ?> data ) {
        addRow( new NMap( data ) ) ;
        return this;
    }

    public NList addRow( NMap data ) {

    	if( data == null ) data = new NMap();
        dataBody.add( data );
        int size = dataBody.size();
        for( Object key : data.keySet() ) {
            header.put( key, size );
        }

        return this;
    }

    public NList addRow( NList data ) {
    	if( data != null ) {
            dataBody.addAll( data.dataBody );
            refreshKey();
        }
        return this;
    }

	/**
	 * 데이터를 추가한다.
	 *
	 * @param data 만약 generic이 String이면 데이터를 Json 으로 간주하고 추가하며, generic이 아니라면 NMap 형식으로 데이터를 추가한다.
	 */
	@SuppressWarnings( "rawtypes" )
    public NList addRow( List<?> data ) {

		if( data != null ) {
            for( Object e : data ) {

                if( e instanceof NMap ) {
                    addRow( (NMap) e );

                } else if( e instanceof Map ) {
                    addRow( new NMap( (Map) e ) );

                } else if( e instanceof String ) { // json string이라고 간주
                    addRow( new NMap( (String) e ) );

                } else {
                    addRow( new NMap( e ) );
                }

            }

        }

        return this;

	}

    public int size( Object key ) {
    	return Validator.nvl( header.get( key ), 0 );
    }

    public int size() {
    	return dataBody.size();
    }

    /**
     * 특정 key에 해당하는 컬럼 데이터들을 List 형태로 가져온다.
     *
     * @param key 추출할 컬럼 key
     * @return key에 해당하는 List
     */
    @SuppressWarnings( { "unchecked", "rawtypes" } )
    public List toList( String key ) {

    	List result = new ArrayList<>();

    	if( ! containsKey( key ) ) return result;

    	for( NMap row : dataBody ) {
    		result.add(row.get( key ) );
    	}

    	return result;

    }

    /**
     * 테이블을 특정 Bean List로 변환한다.
     * @param <T>
     *
     * @param klass 변환할 Bean의 Class
     * @return Bean으로 변환된 List
     */
    public <T> List<T> toList( Class<T> klass ) {

    	List<T> result = new ArrayList<T>();

    	for( NMap row : dataBody ) {
    		result.add( row.toBean( klass ) );
    	}

    	return result;

    }

    /**
     * Get List consisted with NMap
     *
     * @return Data List
     */
    public List<NMap> toList() {
        return dataBody;
    }

    /**
     * 행을 삭제한다.
     *
     * @param index 행 번호
     */
    public NList removeRow( int index ) {

        if( index < 0 ) return this;

        for( Object key :  getRow(index).keySet() ) {
            subtractKeySize( key );
        }

        dataBody.remove( index );

        return this;

    }

    private void subtractKeySize( Object key ) {
        if( header.containsKey( key ) ) {
            header.put( key, Math.max(header.get( key ) - 1, 0) );
        }
    }

    /**
     * 열을 삭제한다.
     *
     * @param key name of column
     */
    public NList removeKey( Object key ) {

        header.remove( key );

        for( NMap row : dataBody ) {
        	row.remove( key );
        }

        return this;

    }

    /**
     * 열을 삭제한다.
     *
     * @param keyIndex 열 번호
     */
    public NList removeKeyBy( int keyIndex ) {

    	Object key = getKey( keyIndex );

    	if( key != null ) removeKey( key );

        return this;

    }

    /**
     * 데이터를 세팅한다.
     *
     * @param key   키
     * @param rowIndex 인덱스
     * @param value 값
     */
    public NList set( Object key, int rowIndex, Object value ) {

        NMap data = dataBody.get( rowIndex );

        data.put( key, value );

        if( ! containsKey( key ) ) {
        	header.put( key, ++rowIndex );
        }

        return this;

    }

    /**
     * 데이터를 세팅한다.
     *
     * @param keyIndex	키에 해당하는 인덱스
     * @param rowIndex		인덱스
     * @param value		값
     */
    public NList setBy( int keyIndex, int rowIndex, Object value ) {

    	Object key = getKey( keyIndex );

    	if( key != null )
            set( key, rowIndex, value );

        return this;

    }

    /**
     * Set row data
     *
     * @param rowIndex index
     * @param map  map data
     */
    public NList setRow( int rowIndex, NMap map ) {

        dataBody.set( rowIndex, map );

        int size = rowIndex + 1;

        for( Object key : map.keySet() ) {
        	if( containsKey( key ) ) continue;
            header.put( key, size );
        }

        return this;

    }

    /**
     * Get Row Data
     * @param rowIndex index
     * @return row map data
     */
    public NMap getRow( int rowIndex ) {
        return dataBody.get( rowIndex );
    }

    public NList setBy( int index, Map<Object, Object> data ) {
    	setRow( index, new NMap( data ) );
        return this;
    }

    public Object get( Object key, int index ) {
        NMap data = dataBody.get( index );
        return data == null ? null : data.get( key );
    }

    public Object getBy( int keyIndex, int index ) {
        return get( getKey(keyIndex), index );
    }


    public String getString( Object key, int index ) {
    	return dataBody.get( index ).getString(key);
    }

    public String getStringBy( int keyIndex, int index ) {
    	return dataBody.get( index ).getString( getKey( keyIndex) );
    }

    public int getInt( Object key, int index ) {
    	return dataBody.get( index).getInt( key );
    }

    public int getIntBy( int keyIndex, int index ) {
    	return dataBody.get( index ).getInt( getKey( keyIndex) );
    }

    public long getLong( Object key, int index ) {
    	return dataBody.get( index ).getLong( key );
    }

    public long getLongBy( int keyIndex, int index ) {
    	return dataBody.get( index ).getLong( getKey(keyIndex) );
    }

    public float getFloat( Object key, int index ) {
    	return dataBody.get( index ).getFloat( key );
    }

    public float getFloatBy( int keyIndex, int index ) {
    	return dataBody.get( index ).getFloat( getKey( keyIndex) );
    }

    public double getDouble( Object key, int index ) {
    	return dataBody.get( index ).getDouble( key );
    }

    public double getDoubleBy( int keyIndex, int index ) {
    	return dataBody.get( index ).getDouble( getKey( keyIndex) );
    }

    public boolean containsKey( Object key ) {
    	return header.containsKey( key );
    }

    public boolean contains( NMap row ) {
        return dataBody.contains( row );
    }

    public int keySize() {
        return header.size();
    }

    public Set<Object> keySet() {
        return header.keySet();
    }

    public Set<Object> keySetCloned() {
        Set<Object> result = new LinkedHashSet<>();
        result.addAll( keySet() );
        return result;
    }

    private Object getKey( int keyIndex ) {

        Assertion.isTrue( keyIndex < 0 || keyIndex >= keySize(), new IndexOutOfBoundsException( String.format( "Index[%d] is out of bounds from 0 to %d", keyIndex, keySize() ) ) );

        Iterator<Object> iterator = header.keySet().iterator();

        for( int i = 0; i < keyIndex; i++ )
            iterator.next();

        return iterator.next();

    }

    /**
     * 중복을 제거한다.
     *
     * @return 중복이 제거된 NList
     */
    public NList deduplicate() {

    	NList result = new NList();

    	for( NMap row : dataBody ) {
    		if( result.contains( row ) ) continue;
    		result.addRow( row );
    	}

    	return result;

    }

    /**
     * Remove data include header information
     */
    public NList clear() {
    	header.clear();
    	alias.clear();
        dataBody.clear();
        return this;
    }

    /**
     * Remove data only
     */
    public NList clearData() {
        dataBody.clear();
        return this;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object object ) {

    	if( object == null ) return false;

    	if( object == this ) return true;

    	if( ! (object instanceof NList) ) return false;

    	NList table = (NList) object;

    	refreshKey();
    	table.refreshKey();

    	if( ! header.equals( table.header ) ) return false;

    	if( ! alias.equals( table.alias ) ) return false;

    	if( size() != table.size() ) return false;

    	for( int i = 0, iCnt = size(); i < iCnt; i++ ) {

    		NMap rowThis  = getRow( i );
    		NMap rowOther = table.getRow( i );

    		if( rowThis == null ) {
    			if( rowOther != null ) return false;

    		} else {
    			if( ! rowThis.equals( rowOther ) ) return false;
    		}

    	}

    	return true;

    }

    /**
     * print data only first 1000 rows
     *
     * @return grid data
     */
    public String toString() {
    	return new NListPrinter(this).toString(true, false);
    }

    /**
     * print all row data
     *
     * @return grid data
     */
    public String toDebugString() {
        return toDebugString( true, true );
    }

    /**
     * Print data
     *
     * @param printHeader if true, print header.
     * @param printAllRow if true, print all row.
     * @return
     */
    public String toDebugString( boolean printHeader, boolean printAllRow ) {
        return new NListPrinter(this).toString(printHeader, printAllRow);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public NList clone() {
        return new Reflector().clone(NList.this);
    }

    /**
     * Sort data
     *
     * @param comparator comparator to determine the order of the list.
     *                   A {@code null} value indicates that the elements' <i>natural ordering</i> should be used.
     */
    public NList sort( Comparator<NMap> comparator ) {
        Collections.sort( dataBody, comparator );
        return this;
    }

	@Override
	public Iterator<NMap> iterator() {

		final int size = size();

		return new Iterator<NMap>() {

			int index = 0;

			public boolean hasNext() {
				return index < size;
			}

			public NMap next() {

				NMap row = getRow( index );

				index++;

				return row;

			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
            public void forEachRemaining( Consumer<? super NMap> action ) {
                throw new UnsupportedOperationException();
            }

		};

	}


	@Override
    public void forEach( Consumer<? super NMap> action ) {
		Objects.requireNonNull( action );
		for( NMap row : this ) {
			action.accept( row );
		}
    }

	@Override
    public Spliterator<NMap> spliterator() {
        throw new UnsupportedOperationException();
    }

}
