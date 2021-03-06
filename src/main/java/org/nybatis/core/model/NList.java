package org.nybatis.core.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.serializer.simple.SimpleNListSerializer;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Assertion;
import org.nybatis.core.validation.Validator;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

/**
 * Multiple Data aggregated with NMap
 *
 * @author nayasis@gmail.com
 */
@JsonSerialize( using = SimpleNListSerializer.class )
public class NList implements Serializable, Cloneable, Iterable<NMap> {

	private static final long serialVersionUID = -3169472792493027837L;

    protected Map<Object, Integer> header      = new LinkedHashMap<>();
    protected Map<Object, String>  alias       = new LinkedHashMap<>();
    protected List<NMap>           dataBody    = new ArrayList<>();

    /**
     * default constructor
     */
    public NList() {}

    /**
     * constructor
     *
     * @param json json text
     */
    public NList( String json ) {
        _addRow( null, json, false );
        refreshKey();
    }

    /**
     * constructor
     *
     * @param nlist  NList data
     */
    public NList( NList nlist ) {
        if( nlist == null || nlist.size() == 0 ) return;
    	dataBody.addAll( nlist.dataBody );
        header.putAll( nlist.header );
        alias.putAll( nlist.alias );
    }

    /**
     * constructor
     *
     * @param list   list
     */
    public NList( Collection list ) {
    	this( list, null );
    }

    /**
     * constructor
     *
     * @param list      list
     * @param header    specific header
     */
    public NList( Collection list, Set<?> header ) {

        boolean headerExist = Validator.isNotEmpty( header );

        _addRows( null, list, !headerExist );

        if( headerExist ) {
            for( Object key : header )
                this.header.put( key, list.size() );
        }

    }

    /**
     * add alias corresponding to keyset
     *
     * @param alias alias list
     * @return self instance
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
     * set alias of key
     * @param key       key to named alias
     * @param alias     alias corresponding key
     * @param overwrite if false, do not assign alias to key already assigned.
     * @return self instance
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
     * set alias
     *
     * @param key   key
     * @param alias alias
     * @return self instance
     */
    public NList setAlias( Object key, String alias ) {
    	setAlias( key, alias, true );
        return this;
    }

    /**
     * get alias corresponding key
     * @param key   column key
     * @return alias
     */
    public String getAlias( Object key ) {
    	return containsKey(key) ? alias.get(key) : null;
    }

    /**
     * get alias list
     *
     * @return list's aliases
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
     * add key in header
     * @param key key to add
     * @return self instance
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
     *
     * @return self instance
     */
    public NList refreshKey() {

        Map<Object, Integer> currentHeader = new LinkedHashMap<>();

        // read last index from bottom
        for( int i = dataBody.size() - 1; i >=0; i-- ) {
            for( Object key : dataBody.get(i).keySet() ) {
                if( currentHeader.containsKey(key) ) continue;
                currentHeader.put( key, i + 1 );
            }
        }

        // fill in previous order (and remove it from current header)
        Map<Object, Integer> buffer = new LinkedHashMap<>();
        for( Object key : header.keySet() ) {
            if( currentHeader.containsKey(key) ) {
                buffer.put( key, currentHeader.get(key) );
                currentHeader.remove(key);
            }
        }

        // fill the rest in current header
        for( Object key : currentHeader.keySet() ) {
            buffer.put( key, currentHeader.get(key) );
        }

        // swap
        header.clear();
        header.putAll( buffer );

        return this;

    }

    /**
     * add row
     *
     * @param key   key
     * @param value value
     * @return self instance
     */
    public NList add( Object key, Object value ) {

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
     * add row
     *
     * <pre>
     * {@link NList} data = new {@link NList};
     *
     * data.add( "{key:'1', val:'AAA'}" );
     * </pre>
     *
     * @param value row data (Bean, Map, JSON text)
     * @return self instance
     */
    public NList addRow( Object value ) {
        _addRow( null, value, true );
        return this;
    }

    /**
     * add row
     *
     * <pre>
     * {@link NList} data = new {@link NList};
     *
     * data.add( 0, "{key:'1', val:'AAA'}" );
     * </pre>
     *
     * @param index index at which to insert the first element from collection
     * @param value row data (Bean, Map, JSON text)
     * @return self instance
     */
    public NList addRow( int index, Object value ) {
        _addRow( index, value, true );
        return this;
    }

    private void _addRow( Integer index, Object value, boolean syncronizeHeaderData ) {

        if( value == null ) return;

        if( value instanceof NMap ) {
            _addRowFromNMap( index, (NMap) value, syncronizeHeaderData );

        } else if( value instanceof Map ) {
            _addRowFromNMap( index, new NMap( value ), syncronizeHeaderData );

        } else if( value instanceof Collection ) {
            _addRows( index, (Collection) value, syncronizeHeaderData );

        } else if( value instanceof NList ) {
            addRows( index, (NList) value );

        } else if( Types.isArray(value) ) {
            _addRows( index, Types.toList( value ), syncronizeHeaderData );

        } else if( Types.isString( value ) ) {

            String json = value.toString();

            try {
                List list = Reflector.toListFromJson( json );
                _addRows( index, list, false );
                if( syncronizeHeaderData ) {
                    refreshKey();
                }
            } catch( JsonIOException e ) {
                Map<String, Object> map = Reflector.toMapFrom( json );
                _addRow( index, map, syncronizeHeaderData );
            }

        } else {
            _addRow( index, new NMap( value ), syncronizeHeaderData );
        }

    }

    private void _addRowFromNMap( Integer index, NMap data, boolean syncronizeHeaderData ) {

        if( index == null ) {
            dataBody.add( Validator.nvl(data, new NMap()) );
        } else {
            dataBody.add( index, Validator.nvl(data, new NMap()) );
        }

        if( syncronizeHeaderData ) {
            int size = dataBody.size();
            for( Object key : data.keySet() ) {
                header.put( key, size );
            }
        }

    }

    /**
     * add rows from another NList data
     *
     * @param nlist NList data
     * @return self instance
     */
    public NList addRows( NList nlist ) {
        _addRows( null, nlist, true );
        return this;
    }

    /**
     * add rows from another NList data
     *
     * @param index index at which to insert the first element from collection
     * @param nlist NList data
     * @return self instance
     */
    public NList addRows( int index, NList nlist ) {
        _addRows( index, nlist, true );
        return this;
    }

    private void _addRows( Integer index, NList nlist, boolean syncronizeHeaderData ) {

        if( nlist == null ) return;

        if( index == null ) {
            dataBody.addAll( nlist.dataBody );
        } else {
            dataBody.addAll( index, nlist.dataBody );
        }

        if( ! syncronizeHeaderData ) return;

        for( Object key : nlist.header.keySet() ) {
            if( header.containsKey(key) ) {
                header.put( key, header.get(key) + nlist.header.get(key) );
            } else {
                header.put( key, nlist.header.get(key) );
            }
        }

    }

    /**
     * add rows from another List contains Map or Bean or JSON text.
     *
     * @param list List data
     * @return self instance
     */
    public NList addRows( Collection list ) {
        _addRows( null, list, true );
        return this;
	}

    /**
     * add rows from another List contains Map or Bean or JSON text.
     *
     * @param index index at which to insert the first element from collection
     * @param list List data
     * @return self instance
     */
    public NList addRows( int index, Collection list ) {
        _addRows( index, list, true );
        return this;
    }

    private void _addRows( Integer index, Collection list, boolean syncronizeHeaderData ) {
        if( list != null ) {
            for( Object e : list ) {
                _addRow( index, e, syncronizeHeaderData );
            }
        }
    }

    /**
     * get data size of header key
     *
     * @param key header key
     * @return data size
     */
    public int size( Object key ) {
    	return Validator.nvl( header.get( key ), 0 );
    }

    /**
     * get total row size
     *
     * @return data size
     */
    public int size() {
    	return dataBody.size();
    }

    /**
     * get values corresponding key
     *
     * @param key   column key
     * @param <T> 	expected class of return
     * @return values corresponding key
     */
    public <T> List<T> toList( String key ) {

    	List result = new ArrayList<>();

    	if( ! containsKey( key ) ) return result;

    	for( NMap row : dataBody ) {
    		result.add(row.get( key ) );
    	}

    	return result;

    }

    /**
     * convert to list
     *
     * @param klass generic type class
     * @param <T> 	expected class of return
     * @return converted list
     */
    public <T> List<T> toList( Class<T> klass ) {
        return toList( klass, false );
    }

    /**
     * convert to list
     *
     * @param klass                     generic type class
     * @param ignoreCastingException    if true, ignore casting exception
     * @param <T> 	                    expected class of return
     * @return converted list
     */
    public <T> List<T> toList( Class<T> klass, boolean ignoreCastingException ) {

        List<T> result = new ArrayList<T>();

        for( NMap row : dataBody ) {
            try {
                result.add( row.toBean( klass ) );
            } catch( Exception e ) {
                if( ignoreCastingException ) {
                    NLogger.trace( e );
                } else {
                    throw e;
                }
            }
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
     * remove row
     *
     * @param index row index
     * @return self instance
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
     * remove row
     *
     * @param key column key
     * @return self instance
     */
    public NList removeKey( Object key ) {
        header.remove( key );
        for( NMap row : dataBody ) {
        	row.remove( key );
        }
        return this;
    }

    /**
     * set data in row
     *
     * @param key       key
     * @param rowIndex  row index
     * @param value     value
     * @return self instance
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
     * set row data
     * @param rowIndex          row index
     * @param beanOrMapOrJson   data (Bean, map, json)
     * @return self instance
     */
    public NList setRow( int rowIndex, Object beanOrMapOrJson ) {
        _setRow( rowIndex, beanOrMapOrJson, true );
        return this;

    }

    private NList _setRow( int rowIndex, Object value, boolean syncronizeHeaderData ) {

        if( value == null || Types.isArrayOrList(value) ) return this;

        if( value instanceof NMap ) {
            setRowFromNMap( rowIndex, (NMap) value, syncronizeHeaderData );
        } else {
            setRowFromNMap( rowIndex, new NMap(value), syncronizeHeaderData );
        }

        return this;

    }

    private void setRowFromNMap( int rowIndex, NMap map, boolean syncronizeHeaderData ) {

        dataBody.set( rowIndex, map );

        if( syncronizeHeaderData ) {
            int size = rowIndex + 1;
            for( Object key : map.keySet() ) {
                if( containsKey( key ) ) continue;
                header.put( key, size );
            }
        }

    }


    /**
     * Get row data
     * @param rowIndex row index
     * @return map data
     */
    public NMap getRow( int rowIndex ) {
        return dataBody.get( rowIndex );
    }

    /**
     * get value in row
     *
     * @param key   key
     * @param index row index
     * @return value
     */
    public Object get( Object key, int index ) {
        NMap data = dataBody.get( index );
        return data == null ? null : data.get( key );
    }

    public String getString( Object key, int index ) {
    	return getRow( index ).getString(key);
    }

    public int getInt( Object key, int index ) {
    	return getRow( index ).getInt( key );
    }

    public long getLong( Object key, int index ) {
    	return getRow( index ).getLong( key );
    }

    public float getFloat( Object key, int index ) {
    	return getRow( index ).getFloat( key );
    }

    public double getDouble( Object key, int index ) {
    	return getRow( index ).getDouble( key );
    }

    public boolean containsKey( Object key ) {
    	return header.containsKey( key );
    }

    public boolean contains( NMap row ) {
        return dataBody.contains( row );
    }

    /**
     * get key header size
     *
     * @return size of key header
     */
    public int keySize() {
        return header.size();
    }

    /**
     * get key header
     * @return key header
     */
    public Set<Object> keySet() {
        return header.keySet();
    }

    /**
     * get key by sequence
     *
     * @param keyIndex key index (sequence)
     * @return key
     */
    public Object getKey( int keyIndex ) {

        Assertion.isTrue( 0 <= keyIndex &&  keyIndex <= keySize(), new IndexOutOfBoundsException( String.format( "Index[%d] is out of bounds from 0 to %d", keyIndex, keySize() ) ) );

        Iterator<Object> iterator = header.keySet().iterator();

        for( int i = 0; i < keyIndex; i++ )
            iterator.next();

        return iterator.next();

    }

    /**
     * remove duplicated data
     *
     * @return deduplicated NList
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
     *
     * @return self instance
     */
    public NList clear() {
    	header.clear();
    	alias.clear();
        dataBody.clear();
        return this;
    }

    /**
     * Remove data only
     *
     * @return self instance
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
     * @return debug string
     */
    public String toDebugString( boolean printHeader, boolean printAllRow ) {
        return new NListPrinter(this).toString(printHeader, printAllRow);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public NList clone() {
        return Reflector.clone(NList.this);
    }

    /**
     * Sort data
     *
     * @param comparator comparator to determine the order of the list.
     *                   A {@code null} value indicates that the elements' <i>natural ordering</i> should be used.
     *
     * @return self instance
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
