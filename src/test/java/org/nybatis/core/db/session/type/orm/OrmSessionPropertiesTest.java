package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-18
 */
public class OrmSessionPropertiesTest {

    private OrmSessionProperties properties = new OrmSessionProperties();

    @Test
    public void setWhere() {

        properties.addWhere( "  WHERE A = #{A} AND B = #{B}", "BBB" );

        Assert.assertEquals( "AND ( A = #{NybatisOrm-User-0-NybatisSingleParameter} AND B = #{NybatisOrm-User-0-NybatisSingleParameter} )", properties.getParameter().getString( Const.db.ORM_PARAMETER_WHERE ) );

        NMap param = new NMap();

        param.put( "name", "NAME" );
        param.put( "age", 19 );

        properties.removeWhere();
        properties.addWhere( "  WHERE name = #{name} AND age = #{age} ", param );

        Assert.assertEquals( "AND ( name = #{NybatisOrm-User-0-name} AND age = #{NybatisOrm-User-0-age} )", properties.getParameter().get( Const.db.ORM_PARAMETER_WHERE ).toString() );

        properties.setOrderBy( "Order  by name, age" );

        Assert.assertEquals( "ORDER BY name, age", properties.getParameter().get( Const.db.ORM_PARAMETER_ORDER_BY).toString() );

    }

    @Test
    public void setParameter() {

        TestDomain testDomain = new TestDomain();
        testDomain.setProdId( "001" );

        NMap param = new NMap();
        param.put( "name", "NAME" );
        param.put( "age", 19 );

        properties = new OrmSessionProperties();
        properties.removeWhere();
        properties.addWhere( "where name = #{name} and age =  #{age}", param );
        NLogger.debug( properties.getParameter() );
        Assert.assertEquals( "{NybatisOrm-User-0-name=NAME, NybatisOrm-User-0-age=19, NybatisOrm-DynamicSqlWhere=AND ( name = #{NybatisOrm-User-0-name} and age =  #{NybatisOrm-User-0-age} )}", properties.getParameter().toString() );

        properties = new OrmSessionProperties();
        properties.setEntityParameter( testDomain );
        NLogger.debug( properties.getParameter() );
        Assert.assertEquals( "{NybatisOrm-Entity-listId=null, NybatisOrm-Entity-prodId=001, NybatisOrm-Entity-price=null, NybatisOrm-Entity-prodName=null}", properties.getParameter().toString() );

        properties.setEntityParameter( testDomain );
        properties.removeWhere();
        properties.addWhere( "where name = #{name} and age =  #{age}", param );
        NLogger.debug( properties.getParameter() );
        Assert.assertEquals( "{NybatisOrm-Entity-listId=null, NybatisOrm-Entity-prodId=001, NybatisOrm-Entity-price=null, NybatisOrm-Entity-prodName=null, NybatisOrm-User-0-name=NAME, NybatisOrm-User-0-age=19, NybatisOrm-DynamicSqlWhere=AND ( name = #{NybatisOrm-User-0-name} and age =  #{NybatisOrm-User-0-age} )}", properties.getParameter().toString() );

        properties.setOrderBy( "name, age" );
        NLogger.debug( properties.getParameter() );
        Assert.assertEquals( "{NybatisOrm-Entity-listId=null, NybatisOrm-Entity-prodId=001, NybatisOrm-Entity-price=null, NybatisOrm-Entity-prodName=null, NybatisOrm-User-0-name=NAME, NybatisOrm-User-0-age=19, NybatisOrm-DynamicSqlWhere=AND ( name = #{NybatisOrm-User-0-name} and age =  #{NybatisOrm-User-0-age} ), NybatisOrm-DynamicSqlOrderBy=ORDER BY name, age}", properties.getParameter().toString() );

    }

}