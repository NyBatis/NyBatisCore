package org.nybatis.core.model;

import org.nybatis.core.reflection.Reflector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

/**
 * Tree model
 *
 * @param <T>
 */
public class NTree<T> implements Serializable {

    private static final long serialVersionUID = 4416218957983553486L;
    private static final int  tabSize          = 2;

    private T              data     = null;
    private NTree<T>       parent   = null;
    private List<NTree<T>> children = new ArrayList<>();

    /**
     * default contructor
     */
    public NTree() {}

    /**
     * constructor
     *
     * @param value default value
     */
    public NTree( T value ) {
        this.data = value;
    }

    /**
     * constructor
     *
     * @param parent    parent tree
     * @param value     default value to set in current tree
     */
    private NTree( NTree<T> parent, T value ) {
        this( value );
        this.parent = parent;
    }

    /**
     * Append data
     *
     * @param data data
     * @return appended node
     */
    public NTree<T> append( T data ) {
        
        NTree<T> child = new NTree<T>( this, data );

        children.add( child );

        return child;
        
    }

    /**
     * Append tree to leaf node
     *
     * <pre>
     *
     * 1. Source tree
     * ---------------------
     * ROOT
     *   A
     *   B
     * ---------------------
     *
     * 2. Target tree
     * ---------------------
     * A
     *   1
     *   2
     * ---------------------
     *
     * 3. Result
     * ---------------------
     * ROOT
     *   A
     *     A
     *       1
     *       2
     *   B
     * ---------------------
     *
     * </pre>
     * 
     * @param tree tree to append
     * @return self instance
     */
    public NTree appendTree( NTree<T> tree ) {

        if( tree != null )  {
            for( NTree<T> branch : getLeafNodes() ) {
                tree.clone().parent = branch;
            }
        }

        return this;

    }

    /**
     * Connect tree to leaf node. <br>
     * If value of leaf node is equal to value of attched tree's root node, attched tree's root node is omitted.
     *
     * <pre>
     *
     * 1. Source tree
     * ---------------------
     * ROOT
     *   A
     *   B
     * ---------------------
     *
     * 2. Target tree
     * ---------------------
     * A
     *   1
     *   2
     * ---------------------
     *
     * 3. Result
     * ---------------------
     * ROOT
     *   A
     *     1
     *     2
     *   B
     * ---------------------
     *
     * </pre>
     * 
     * @param tree tree to connect
     * @return self instance
     */
    public NTree connect( NTree<T> tree ) {

        if( tree == null || ! tree.hasChildren() ) return this;

        for( NTree<T> leaf : getLeafNodes() ) {

            if( (leaf.getValue() == null && tree.getValue() == null) || leaf.getValue().equals( tree.getValue() ) ) {
                for( NTree<T> child : tree.clone().getChildren() ) {
                    leaf.children.add( child );
                }
            }

        }

        return this;
        
    }

    /**
     * get value
     *
     * @return value
     */
    public T getValue() {
        return data;
    }

    /**
     * get all values contained in tree
     *
     * @return values
     */
    public Set<T> getValues() {

        Set<T> values = new HashSet<>();

        for( Leaf<T> leaf : toList() ) {
            values.add( leaf.getValue() );
        }

        return values;

    }

    /**
     * set value
     *
     * @param value value to set
     */
    public void setValue( T value ) {
        data = value;
    }

    /**
     * get children
     *
     * @return children
     */
    public List<NTree<T>> getChildren() {
        return children;
    }

    /**
     * get values in child branches
     *
     * @return values
     */
    public List<T> getChildValues() {
        List<T> list = new ArrayList<>();
        for( NTree<T> tree : children ) {
            list.add( tree.getValue() );
        }
        return list;
    }

    /**
     * check if tree has children
     *
     * @return true if tree has chidren
     */
    public boolean hasChildren() {
        return children.size() > 0;
    }

    /**
     * Check whether data was used in previous Ntree branch
     *
     * @param data
     * @return true if data was never used in Ntree branch
     */
    public boolean isRecursive( T data ) {

        if( this.data == null && data == null ) return true;
        if( this.data.equals(data) ) return true;

        NTree<T> parent = getParent();

        while( parent != null ) {
            if( parent.getValue().equals(data) ) return true;
            parent = parent.getParent();
        }

        return false;

    }


    /**
     * Get root node
     *
     * @return root node
     */
    public NTree<T> getRoot() {

        NTree<T> result = this;

        while( result.parent != null )
            result = result.parent;

        return result;
        
    }
    
    /**
     * Get leaf branche node of Ntree
     * 
     * <pre>
     * ---------------------
     * ROOT
     *   A
     *   B
     *     1
     *     2
     *     3
     *       A1
     * ---------------------
     * 
     * getLeafNodes() --> [A,1,2,A1]
     * 
     * </pre>
     * 
     * @return leaf node
     */
    public List<NTree<T>> getLeafNodes() {
        
        List<NTree<T>> result = new ArrayList<>();
        
        for( NTree<T> child : children ) {
            
            if( ! child.hasChildren() ) {
                result.add( child );
            } else {
                result.addAll( child.getLeafNodes() );
            }
            
        }
        
        return result;
        
    }

    /**
     * Reverse tree nodes
     *
     * <pre>
     * Tree has many branches so reversed hierachies must be a multitude. not be one.
     * </pre>
     *
     * @return NTree lists their hierachy is reversed
     */
    public List<NTree<T>> reverse() {

        List<Stack<T>> repository = new ArrayList<>();

        reverse( this, new Stack<>(), repository );

        List<NTree<T>> result = new ArrayList<>();

        for( Stack<T> stack : repository ) {

            NTree<T> Ntree = new NTree<>( stack.pop() );

            while( ! stack.empty() ) {
                Ntree = Ntree.append( stack.pop() );
            }

            result.add( Ntree.getRoot() );

        }

        return  result;

    }

    private void reverse( NTree<T> source, Stack<T> target, List<Stack<T>> repository ) {

        target.push( source.getValue() );

        if( source.hasChildren() ) {

            for( NTree<T> child : source.getChildren() ) {
                reverse( child, (Stack<T>) target.clone(), repository );
            }

        } else {
            repository.add( target );
        }

    }

    /**
     * get parent branch
     *
     * @return parent branch
     */
    public NTree<T> getParent() {
        return parent;
    }

    /**
     * check if parent branch is existing.
     * @return
     */
    public boolean hasParent() {
        return parent != null;
    }

    /**
     * Get depth of current node in entire tree
     *
     * @return depth of current node
     */
    public int getDepth() {

        int depth = 0;

        NTree<T> result = this;

        while( result.parent != null ) {
            result = result.parent;
            depth++;

        }

        return depth;

    }

    @Override
    public String toString() {
        return toString( 1, true );
    }

    private String toString( int level, boolean showLevel ) {
        
        StringBuilder sb = new StringBuilder();
        
        sb.append( getIndentation(level) );
        
        if( showLevel ) {
            sb.append( String.format( "%2d. ", level ) );
        }
        
        sb.append( data );
        
        for( NTree<T> child : children ) {
            sb.append( '\n' ).append( child.toString( level + 1, showLevel ) );
        }
        
        return sb.toString();

    }
    
    private char[] getIndentation( int level ) {
        
        char[] result = new char[ ( level - 1 ) * tabSize ];
        
        for( int i = 0, iCnt = result.length; i < iCnt; i++ ) {
            result[ i ] = ' ';
        }

        return result;
        
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public NTree<T> clone() {
        return Reflector.clone( this );
    }

    public NList toNList() {

        List<Queue<T>> repository = new ArrayList<>();

        toRow( this, new LinkedList<>(), repository );

        NList result = new NList();

        for( Queue<T> queue : repository ) {

            NMap row = new NMap();

            for( int i = 0, iCnt = queue.size(); i < iCnt; i++ ) {
                row.put( i, queue.poll() );
            }

            result.addRow( row );

        }

        return  result;

    }

    private void toRow( NTree<T> source, Queue<T> target, List<Queue<T>> repository ) {

        target.add( source.getValue() );

        if( source.hasChildren() ) {

            for( NTree<T> child : source.getChildren() ) {
                toRow( child, new LinkedList(target), repository );
            }

        } else {
            repository.add( target );
        }

    }

    public List<Leaf<T>> toList() {
        List<Leaf<T>> result = new ArrayList<>();
        toList( this, result, -1 );
        return result;
    }

    private void toList( NTree<T> source, List<Leaf<T>>repository, int depth ) {

        depth++;

        Leaf<T> leaf = new Leaf<>( repository.size(), depth, source.getValue() );

        repository.add( leaf );

        if( source.hasChildren() ) {
            for( NTree<T> child : source.getChildren() ) {
                toList( child, repository, depth );
            }
        }

    }

    public static class Leaf<T> {
        private int index;
        private int depth;
        private T   value = null;

        protected Leaf( int index, int depth, T value ) {
            this.index = index;
            this.depth = depth;
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public int getDepth() {
            return depth;
        }

        public T getValue() {
            return value;
        }

        public String toString() {
            return String.format( "{index:%d, depth:%d, value:%s}", index, depth, value );
        }
    }

}

