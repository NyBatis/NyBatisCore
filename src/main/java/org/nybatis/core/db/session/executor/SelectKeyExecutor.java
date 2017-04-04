package org.nybatis.core.db.session.executor;

import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * Sql select key node executor
 *
 * @author nayasis@gmail.com
 * @since 2015-09-12
 */
public class SelectKeyExecutor {

    private String token;

    public SelectKeyExecutor( String token ) {
        this.token = token;
    }

    public NMap selectKeys( SqlBean sqlBean ) {
        NMap selectKeys = selectKeys( sqlBean.getKeySqls(), sqlBean.getParams(), sqlBean.getProperties() );
        sqlBean.mergeSelectKeys( selectKeys );
        return selectKeys;
    }

    private NMap selectKeys( Map<String, SqlNode> keySqls, NMap sqlParam, SqlProperties properties ) {

        NMap result = new NMap();

        for( SqlNode keySqlNode : keySqls.values() ) {

            SqlBean keySqlBean = new SqlBean( keySqlNode, sqlParam ).init( properties ).build();

            String key = keySqlBean.getSqlId();
            Object val = selectKey( keySqlBean );

            setValue( key, val, result );
            setValue( key, val, sqlParam );

        }

        return result;

    }

    private Object selectKey( SqlBean sqlBean ) {
        return new SqlExecutor( token, sqlBean ).select( Object.class );
    }


    private void setValue( String key, Object value, NMap map ) {

        List<String> subKeys = StringUtil.split( key, "\\." );

        NMap current = map;

        for( int i = 0, iCnt = subKeys.size() - 1; i < iCnt; i++ ) {
            String subKey = subKeys.get( i );
            Object subVal = current.get( subKey );

            if( subVal instanceof NMap ) {
                current = (NMap) subVal;
            } else {
                NMap nmap = new NMap();
                current.put( subKey, nmap );
                current = nmap;
            }
        }

        String subKey = subKeys.get( subKeys.size() - 1 );
        current.put( subKey, value );

    }


}
