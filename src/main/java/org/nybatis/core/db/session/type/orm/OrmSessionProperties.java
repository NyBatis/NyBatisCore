package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.util.DbExecUtils;
import org.nybatis.core.db.sql.repository.Column;
import org.nybatis.core.db.sql.repository.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Assertion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-16
 */
public class OrmSessionProperties {

    private String  environmentId;
    private String  tableName;

    private List<String>  wheres  = new ArrayList<>();
    private String        orderBy = null;

    private NMap    entityParameter = new NMap();
    private NMap    userParameter   = new NMap();

    public OrmSessionProperties() {}

    public OrmSessionProperties newInstance() {

        OrmSessionProperties newProperties = new OrmSessionProperties();
        newProperties.environmentId = environmentId;
        newProperties.tableName     = tableName;

        return newProperties;

    }

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId( String environmentId ) {
        this.environmentId = environmentId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName( String tableName ) {
        this.tableName = tableName;
    }

    private String sqlIdPrefix() {
        return Const.db.getOrmSqlIdPrefix( environmentId, tableName );
    }

    public String sqlIdSelectSingle() {
        return sqlIdPrefix() + Const.db.ORM_SQL_SELECT_SINGLE;
    }

    public String sqlIdSelectList() {
        return sqlIdPrefix() + Const.db.ORM_SQL_SELECT_LIST;
    }

    public String sqlIdInsert() {
        return sqlIdPrefix() + Const.db.ORM_SQL_INSERT;
    }

    public String sqlIdUpdate() {
        return sqlIdPrefix() + Const.db.ORM_SQL_UPDATE;
    }

    public String sqlIdDeleteSingle() {
        return sqlIdPrefix() + Const.db.ORM_SQL_DELETE_SINGLE;
    }

    public String sqlIdDeleteList() {
        return sqlIdPrefix() + Const.db.ORM_SQL_DELETE_LIST;
    }

    public void addWhere( String where, Object parameter ) {

        if( StringUtil.isBlank(where) ) return;

        int currentWhereIndex = wheres.size();

        String prefix = String.format( "%s%d-", Const.db.ORM_PARAMETER_USER, currentWhereIndex );

        NMap inputParam = setParameter( prefix, parameter );


        where = where.trim().replaceFirst( "(?ims)^WHERE ", "" ).replaceFirst( "(?ims)^AND ", "" );

        String singleParamKey = String.format( "%s%s", prefix, Const.db.PARAMETER_SINGLE );

        if( parameter == null ) {
            inputParam.put( singleParamKey, null );
        }

        if( inputParam.containsKey(singleParamKey) ) {
            where = where.replaceAll( "#\\{(.*?)\\}", String.format("#{%s}", singleParamKey) );

        } else {
            where = where.replaceAll( "#\\{(.*?)\\}", String.format( "#{%s$1}", prefix ) );
        }

        userParameter.putAll( inputParam );

        this.wheres.add( String.format( "AND ( %s )", where ) );

    }

    public void removeWhere() {
        wheres.clear();
        userParameter.clear();
    }

    public void setOrderBy( String orderBy ) {
        if( StringUtil.isBlank( orderBy ) ) return;
        this.orderBy = "ORDER BY " + orderBy.trim().replaceFirst( "(?ims)^ORDER +?BY ", "" );
    }

    public OrmSessionProperties clone() {
        return new Reflector().clone( this );
    }

    public void setEntityParameter( Object parameter ) {
        this.entityParameter = setParameter( Const.db.ORM_PARAMETER_ENTITY, parameter );
    }

    public NMap getEntityParameter() {
        return this.entityParameter;
    }

    private NMap setParameter( String prefix, Object parameter ) {

        NMap params = DbExecUtils.toNRowParameter( parameter );

        NMap newParams = new NMap();

        for( Object key : params.keySet() ) {
            newParams.put( prefix + key, params.get( key ) );
        }

        return newParams;

    }

    public NMap getParameter() {

        NMap result = new NMap();
        result.putAll( entityParameter );
        result.putAll( userParameter );

        if( wheres.size() > 0 ) result.put( Const.db.ORM_PARAMETER_WHERE,    StringUtil.join( wheres, "\n" ) );
        if( orderBy != null   ) result.put( Const.db.ORM_PARAMETER_ORDER_BY, orderBy );

        return result;

    }

    public List<NMap> getParameters( List<?> parameters ) {

        Assertion.isNotNull( parameters, "parameters is null" );

        List<NMap> list = new ArrayList<>();

        int index = 0;

        for( Object parameter : parameters ) {
            setEntityParameter( parameter );
            if( ! isPkNotNull() ) {
                throw new SqlException( "Parameters' PK has null.(index:{}, value:{})", index, getPkValues() );
            }
            list.add( getParameter() );
            index++;
        }

        entityParameter = new NMap();

        return list;

    }

    public boolean isPkNotNull() {

        TableLayout layout = TableLayoutRepository.getLayout( environmentId, tableName );

        for( Column column : layout.getPkColumns() ) {
            Object val = entityParameter.get( Const.db.ORM_PARAMETER_ENTITY + column.getKey() );
            if( val == null ) return false;
        }

        return true;

    }

    public String getPkValues() {

        TableLayout layout = TableLayoutRepository.getLayout( environmentId, tableName );

        NMap params = new NMap();

        for( Column column : layout.getPkColumns() ) {
            params.put( column.getKey(), entityParameter.get( Const.db.ORM_PARAMETER_ENTITY + column.getKey() ) );
        }

        return params.toJson();

    }

}
