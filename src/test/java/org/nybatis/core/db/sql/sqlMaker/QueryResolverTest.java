package org.nybatis.core.db.sql.sqlMaker;

import java.util.Arrays;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.testng.Assert;
import org.testng.annotations.Test;

public class QueryResolverTest {
    
    @Test
    public void basic() {
        
        String sql = 
           "\n SELECT  name,"
         + "\n         age AS \"AGE${name}\", "
         + "\n         menu, -- comment ${name}"
         + "\n         /* menu, -- comment ${name}"
         + "\n         menu, -- comment ${name} "
         + "\n         menu, -- comment ${name} */"
         + "\n FROM    TABLE "
         + "\n WHERE   name  = ${name:babo}"
         + "\n AND     value LIKE '%#{age:out:int}%'"
         + "\n AND     rownum < #{rownum}"
         + "\n AND     list IN (#{list})";
        
        
        
        NMap param = new NMap();
        
        param.put( "name",   "#{age}" );
        param.put( "age",    "18"     );
        param.put( "rownum", 5        );
        param.put( "list",   Arrays.asList("A","B","C","D") );
        
        
        QueryResolver queryResolver = new QueryResolver( sql, param );
        
        NLogger.debug( queryResolver.getSql()        );
        NLogger.debug( sql        );
        NLogger.debug( queryResolver.getDebugSql()   );
        NLogger.debug( queryResolver.getBindParams() );
        
    }
    
    @Test
    public void bindParamTest() {
        
        String sql = 
                "\n SELECT  name,"
                        + "\n         age AS \"AGE${name}\", "
                        + "\n         menu, -- comment ${name}"
                        + "\n         /* menu, -- comment ${name}"
                        + "\n         menu, -- comment ${name} "
                        + "\n         menu, -- comment ${name} */"
                        + "\n FROM    TABLE "
                        + "\n WHERE   name  = ${name:babo}"
                        + "\n AND     value LIKE '%#{age:out:int}%'"
                        + "\n AND     rownum < #{rownum}"
                        + "\n AND     list IN (#{list})";
        
        
        
        NMap param = new NMap();
        
        param.put( "name",   "#{age}" );
        param.put( "age", "18" );
        param.put( "rownum", 5 );
        param.put( "list", Arrays.asList( "A", "B", "C", "D" ) );
        
        
        QueryResolver queryResolver = new QueryResolver( sql, param );
        
        Assert.assertEquals( "[{key:age, type:VARCHAR, value:18}, {key:age, type:VARCHAR, value:18}, {key:age, type:INT, value:18, out:true}, {key:rownum, type:INTEGER, value:5}, {key:list, type:ARRAY, value:[A, B, C, D]}]",
                queryResolver.getBindParams().toString()
        );
        
    }

    @Test
    public void loopSqlTemplateTest() {

        String sql = "names LIKE '%' || #{names} || '%' -- #{index}";

        QueryResolver queryResolver = new QueryResolver();

        NLogger.debug( sql );

        sql = QueryResolver.makeLoopSql( sql, "names", "names_0_NybatisLoopNode" );
        NLogger.debug( sql );

        sql = QueryResolver.makeLoopSql( sql, "index", "index_0_NybatisLoopNode" );
        NLogger.debug( sql );

    }

}
