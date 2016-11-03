package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.GlobalSqlParameter;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

import java.util.Stack;

import static org.nybatis.core.conf.Const.db.PARAMETER_INNER_FOR_EACH;

/**
 * NyBatis QueryParameter
 *
 * @author 1002159
 * @since 2016-01-07
 */
public class QueryParameter extends NMap {

    public QueryParameter() {}

    public QueryParameter( Object value ) {
        init( value );
    }

    /**
     * Add global parameters
     *
     * @return self instance
     */
    public QueryParameter addGlobalParameters() {
        putAll( GlobalSqlParameter.getThreadLocalParameters() );
        return this;
    }

    /**
     * Init Query Parameter from value
     *
     * @param value single value (primitive or array or list) or Map
     * @return self instance
     */
    public QueryParameter init( Object value ) {

        clear();

        if( value == null ) return this;

        if( DbUtils.isPrimitive( value ) ) {

            NMap singleParam = new NMap();
            singleParam.put( Const.db.PARAMETER_SINGLE, value );

            bind( singleParam );

        } else {
            bind( value );
        }

        return this;

    }

    /**
     * Get value ( used in toString of SqlNode element )
     * @param key   key
     * @return value
     */
    public Object get( String key ) {

        Object val = getValueFromLoopParam( key );

        if( val != null ) return val;

        return getValueFromNMap( key );

    }

    private Object getValueFromNMap( String key ) {

        try {
            return getByJsonPath( key );
        } catch( JsonPathNotFoundException e ) {
            return null;
        }

    }

    private Object getValueFromLoopParam( String key ) {

        if( ! hasForEachInnerParam() ) return null;

        Stack<String> words = new Stack<>();
        words.addAll( StringUtil.tokenize( key, "." ) );

        NMap innerParam = getForEachInnerParam();

        String lastWord = words.pop();

        while( ! words.empty() ) {

            String subKey = StringUtil.join( words, "::" );
            words.pop();

            if( ! innerParam.containsKey( subKey ) ) continue;

            try {
                return innerParam.getByJsonPath( subKey + "." + lastWord );
            } catch( JsonPathNotFoundException e ) {
                return null;
            }

        }

        return null;

    }

    private boolean hasForEachInnerParam() {
        return containsKey( PARAMETER_INNER_FOR_EACH );
    }

    private NMap getForEachInnerParam() {
        return getAs( PARAMETER_INNER_FOR_EACH );
    }

    /**
     * Set inner parameter to be used only in query assembling of SqlNode
     * @param key   key
     * @param value value
     * @return self instance
     */
    public QueryParameter setForEachInnerParam( String key, Object value ) {
        if( ! hasForEachInnerParam() ) {
            put( PARAMETER_INNER_FOR_EACH, new NMap() );
        }
        getForEachInnerParam().put( StringUtil.nvl( key ).replaceAll( "\\.", "::" ), value );
        return this;
    }

}
