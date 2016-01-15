package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.log.NLoggerPrinter;
import org.nybatis.core.reflection.Reflector;
import org.testng.annotations.Test;

import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author 1002159
 * @since 2016-01-12
 */
public class QueryParameterTest {

    @Test
    public void testGetValue() throws Exception {

        NLoggerPrinter logger = NLogger.getLogger( "com.jayway.jsonpath.internal.path.CompiledPath" );

        System.out.printf( "isDebugEnable : %s\n", logger.isDebugEnabled() );

        String json = "{\n" +
                "\t\"id\": {\n" +
                "\t\t\"name\": \"merong\",\n" +
                "\t\t\"age\": 21,\n" +
                "\t\t\"job\": [\"student\", \"parent\"]\n" +
                "\t}\n" +
                "}";


        Map<String, Object> map = new Reflector().toMapFromJson( json );

        QueryParameter parameter = new QueryParameter( map );



        System.out.printf( "val : %s\n", parameter.get( "id" ) );
//        System.out.printf( "val : %s\n", parameter.getValue( null ) );
        System.out.printf( "val : %s\n", parameter.get( "id.name" ) );
        System.out.printf( "val : %s\n", parameter.get( "id.job[1]" ) );
        System.out.printf( "val : %s\n", parameter.get( "id.job[3]" ) );

    }
}