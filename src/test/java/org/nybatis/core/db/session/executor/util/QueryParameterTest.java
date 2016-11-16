package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.log.NLoggerPrinter;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.vo.Card;
import org.nybatis.core.reflection.Reflector;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
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


        Map<String, Object> map = Reflector.toMapFromJson( json );

        QueryParameter parameter = new QueryParameter( map );



        System.out.printf( "val : %s\n", parameter.get( "id" ) );
//        System.out.printf( "val : %s\n", parameter.getValue( null ) );
        System.out.printf( "val : %s\n", parameter.get( "id.name" ) );
        System.out.printf( "val : %s\n", parameter.get( "id.job[1]" ) );
        System.out.printf( "val : %s\n", parameter.get( "id.job[3]" ) );

    }

    @Test
    public void preserveOriginalParameter() {

        List<Card> list = new ArrayList<>();

        Card card = new Card();
        card.cardId  = "A";
        card.expoOrd = 1;

        list.add( card );

        NMap map = new NMap();

        map.put( "list", list );

        QueryParameter queryParameter = new QueryParameter( map );

        // 원본 파라미터 (map) 상태는 계속 유지되어야 함

        NMap convertedCard = ((List<NMap>) queryParameter.get( "list" )).get( 0 );
        Card originalCard  = ((List<Card>)map.get( "list" )).get( 0 );

    }

}