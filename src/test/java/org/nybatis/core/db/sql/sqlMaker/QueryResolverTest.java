package org.nybatis.core.db.sql.sqlMaker;

import java.util.Arrays;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

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
            + "\n WHERE   name  = ${name:clob}"
            + "\n AND     value LIKE '%#{age:out:int}%'"
            + "\n AND     rownum < #{rownum}"
            + "\n AND     list IN (#{list})";
        
        
        
        NMap param = new NMap();
        
        param.put( "name",   "#{age}" );
        param.put( "age", "18" );
        param.put( "rownum", 5 );
        param.put( "list", Arrays.asList( "A", "B", "C", "D" ) );

        QueryResolver queryResolver = new QueryResolver( sql, param );

        assertEquals(
                "\n" +
                "SELECT  name,\n" +
                "        age AS \"AGE'18'\", \n" +
                "        menu, -- comment ${name}\n" +
                "        /* menu, -- comment ${name}\n" +
                "        menu, -- comment ${name} \n" +
                "        menu, -- comment ${name} */\n" +
                "FROM    TABLE \n" +
                "WHERE   name  = '18'\n" +
                "AND     value LIKE '%#{age:INT}%'\n" +
                "AND     rownum < 5\n" +
                "AND     list IN ('A','B','C','D')"
                , queryResolver.getDebugSql() );

        assertEquals(
                "\n" +
                "SELECT  name,\n" +
                "        age AS \"AGE?\", \n" +
                "        menu, -- comment ${name}\n" +
                "        /* menu, -- comment ${name}\n" +
                "        menu, -- comment ${name} \n" +
                "        menu, -- comment ${name} */\n" +
                "FROM    TABLE \n" +
                "WHERE   name  = ?\n" +
                "AND     value LIKE '%?%'\n" +
                "AND     rownum < ?\n" +
                "AND     list IN (?,?,?,?)"
                , queryResolver.getSql() );


        assertEquals( queryResolver.getBindParams().toString(),
                "[{key:age, type:VARCHAR, value:18}, {key:age, type:VARCHAR, value:18}, {key:age, type:INT, value:18, out:y}, {key:rownum, type:INTEGER, value:5}, {key:list, type:VARCHAR, value:A}, {key:list, type:VARCHAR, value:B}, {key:list, type:VARCHAR, value:C}, {key:list, type:VARCHAR, value:D}]"
        );
        
    }

    @Test
    public void loopSqlTemplateTest() {

        String sql = "names LIKE '%' || #{names} || '%' OR names LIKE '%' || #{index} || '%'  -- #{index}";

        QueryResolver queryResolver = new QueryResolver();

        NLogger.debug( sql );

        String sql01 = QueryResolver.makeLoopSql( sql, "names", "names_0_NybatisLoopNode" );
        NLogger.debug( sql01 );

        assertEquals( sql01, "names LIKE '%' || #{names_0_NybatisLoopNode} || '%' OR names LIKE '%' || #{index} || '%'  -- #{index}" );

        String sql02 = QueryResolver.makeLoopSql( sql, "index", "index_0_NybatisLoopNode" );
        NLogger.debug( sql02 );

        assertEquals( sql02, "names LIKE '%' || #{names} || '%' OR names LIKE '%' || #{index_0_NybatisLoopNode} || '%'  -- #{index}" );

    }

    public void forEach() {



    }

}
