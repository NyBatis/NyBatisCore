package org.nybatis.core.reflection.core;

import org.junit.Test;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.Person;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.PersonProperty;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.core.testClass.Chain;
import org.nybatis.core.reflection.mapper.NObjectMapper;
import org.nybatis.core.reflection.mapper.NObjectSqlMapper;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class JsonConverterTest {

    JsonConverter $ = new JsonConverter( new NObjectMapper() );

    @Test
    public void toJson() throws Exception {

        Person person = new Person();

        String json = $.toJson( getSampleData(), true, true, true );

        NLogger.debug( json );

        json = $.toJson( getSampleData(), false, false, true );

        NLogger.debug( json );

        NLogger.debug( Reflector.toJson( getSampleData(), true, true, true ) );
        NLogger.debug( Reflector.toJson( getSampleData(), true, false, true ) );

    }

    private Person getSampleData() {
        Person person = new Person( "01001", "Jhon Dow", 21  );
        person.getProperty().add( new PersonProperty( "01001", "America", "Delaware" ) );
        return person;
    }

    @Test
    public void recursiveTest() {

        JsonConverter $ = new JsonConverter( new NObjectSqlMapper() );

        String json = "{\"projectId\":\"SAC001\",\"completeDate\":\"2017-11-28T16:37:07.458+0900\",\"chainId\":\"SAC001:00000001\",\"chainName\":\"UserDownloadInfo.getRawUserDownloadInfo\",\"chainType\":\"SQL\",\"skipYn\":\"N\",\"existYn\":\"N\",\"possibleChains\":\"{}\",\"chainProp\":\"{}\"}";

        Chain chain = Reflector.toBeanFrom( json, Chain.class );

        chain = $.toBeanFrom( json, Chain.class );

        NLogger.debug( Reflector.toJson( chain ) );
        NLogger.debug( $.toJson( chain ) );

    }

}