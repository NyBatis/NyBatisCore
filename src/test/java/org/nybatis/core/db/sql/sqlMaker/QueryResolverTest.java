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

        assertEquals( "\n" +
                    " SELECT  name,\n" +
                    "         age AS \"AGE'18'\", \n" +
                    "         menu, -- comment ${name}\n" +
                    "         /* menu, -- comment ${name}\n" +
                    "         menu, -- comment ${name} \n" +
                    "         menu, -- comment ${name} */\n" +
                    " FROM    TABLE \n" +
                    " WHERE   name  = '18'\n" +
                    " AND     value LIKE '%#{age:INT}%'\n" +
                    " AND     rownum < 5\n" +
                    " AND     list IN ('A','B','C','D')"
                , queryResolver.getDebugSql() );

        assertEquals( "\n" +
                " SELECT  name,\n" +
                "         age AS \"AGE?\", \n" +
                "         menu, -- comment ${name}\n" +
                "         /* menu, -- comment ${name}\n" +
                "         menu, -- comment ${name} \n" +
                "         menu, -- comment ${name} */\n" +
                " FROM    TABLE \n" +
                " WHERE   name  = ?\n" +
                " AND     value LIKE '%?%'\n" +
                " AND     rownum < ?\n" +
                " AND     list IN (?,?,?,?)"
                , queryResolver.getSql() );


        assertEquals( queryResolver.getBindParams().toString(),
                "[{key:age, type:VARCHAR, value:18, valueClass:class java.lang.String}, {key:age, type:VARCHAR, value:18, valueClass:class java.lang.String}, {key:age, type:INT, value:18, valueClass:class java.lang.Integer, out:y}, {key:rownum, type:INTEGER, value:5, valueClass:class java.lang.Integer}, {key:list, type:VARCHAR, value:A, valueClass:class java.lang.String}, {key:list, type:VARCHAR, value:B, valueClass:class java.lang.String}, {key:list, type:VARCHAR, value:C, valueClass:class java.lang.String}, {key:list, type:VARCHAR, value:D, valueClass:class java.lang.String}]"
        );
        
    }

    @Test
    public void ifelseTest() {

        String sql =
            "<if test=\"#{condition} == 'oracle'\">\n" +
            "  SELECT  /*+ DbmsObject.getView [oracle] */\n" +
            "          view_name AS name,\n" +
            "          owner     AS owner,\n" +
            "          text      AS text\n" +
            "  FROM    ALL_VIEWS\n" +
            "  WHERE   owner IN ( #{users} )\n" +
            "</if>\n" +
            "<elseif test=\"#{condition} == 'mysql' OR #{nybatis.database} == 'maria' \">\n" +
            "  SELECT  /*+ DbmsObject.getView [mysql,maria] */\n" +
            "          table_name      AS name,\n" +
            "          table_schema    AS owner,\n" +
            "          view_definition AS text\n" +
            "  FROM    INFORMATION_SCHEMA.VIEWS\n" +
            "  WHERE   table_schema IN ( #{users} )\n" +
            "</elseif>\n" +
            "<elseif test=\"#{condition} == 'h2' \">\n" +
            "  SELECT  table_name      AS name,\n" +
            "          table_catalog   AS owner,\n" +
            "          view_definition AS text\n" +
            "  FROM    INFORMATION_SCHEMA.VIEWS\n" +
            "  WHERE   table_schema = 'PUBLIC'\n" +
            "  AND     table_catalog IN ( #{users} )\n" +
            "</elseif>\n" +
            "<else>\n" +
            "  SELECT  /*+ DbmsObject.getView [else] */\n" +
            "          A.*\n" +
            "  FROM (  SELECT  NULL AS name, NULL AS owner, NULL AS text ) A\n" +
            "  WHERE   1=2\n" +
            "</else>";

        NMap param = new NMap();
        param.put( "condition", "oracle" );
        param.put( "users", new String[] {"A","B"} );

        QueryResolver queryResolver = new QueryResolver( sql, param );

        System.out.println( queryResolver.getDebugSql() );


    }

}
