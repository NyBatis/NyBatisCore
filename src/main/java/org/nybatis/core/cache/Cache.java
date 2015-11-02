package org.nybatis.core.cache;



/**
 * Interface that defines common cache operations.<br/><br/>
 *
 * <b>Note:</b> Due to the generic use of caching, it is recommended that
 * implementations allow storage of <tt>null</tt> values (for example to
 * cache methods that return {@code null}).
 *
 * @author nayasis@gmail.com
 *
 */
public interface Cache {

	int size();

	void setCapacity( int capacity );

	void setFlushCycle( int seconds );

	void put( Object key, Object value );

	void putIfAbsent( Object key, Object value );

	Object get( Object key );

	void clear( Object key );

	void clear();

}
