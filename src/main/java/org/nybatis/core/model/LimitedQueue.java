package org.nybatis.core.model;

import java.util.LinkedList;

/**
 * @author nayasis
 * @since 2016-04-26
 */
public class LimitedQueue<T> extends LinkedList<T> {

    private int size;

    public LimitedQueue( int size ) {
        this.size = size;
    }

    @Override
    public boolean add( T element ) {
        super.add( element );
        while( size() > size ) {
            super.remove();
        }
        return true;
    }

    public boolean isFull() {
        return super.size() == size;
    }

}
