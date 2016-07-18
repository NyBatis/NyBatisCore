package org.nybatis.core.db.session.executor;

import org.nybatis.core.db.session.executor.util.DbUtils;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.db.sql.sqlNode.SqlProperties;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;

import java.util.Map;

/**
 * @author Administrator
 * @since 2015-09-12
 */
public class SelectKeyExecutor {

    private String token;

    public SelectKeyExecutor( String token ) {
        this.token = token;
    }

    public NMap selectKeys( SqlBean sqlBean ) {

        NMap selectKeys = selectKeys( sqlBean.getKeySqls(), sqlBean.getParams(), sqlBean.getProperties() );

        mergeSelectKeysToInputParams( sqlBean, selectKeys );

        return selectKeys;

    }

    private void mergeSelectKeysToInputParams( SqlBean sqlBean, NMap selectKeys ) {
        Reflector.merge( selectKeys, sqlBean.getInputParams() );
    }

    private NMap selectKeys( Map<String, SqlNode> keySqls, NMap sqlParam, SqlProperties properties ) {

        NMap result = new NMap();

        for( SqlNode keySqlNode : keySqls.values() ) {

            SqlBean keySqlBean = new SqlBean( keySqlNode, sqlParam ).init( properties ).build();

            String key = keySqlBean.getSqlId();
            Object val = selectKey( keySqlBean );

            result.put( key, val );
            sqlParam.put( key, val );

        }

        return result;

    }

    private Object selectKey( SqlBean sqlBean ) {
        return new SqlExecutor( token, sqlBean ).select( Object.class );
    }

}
