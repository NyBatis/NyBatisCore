package org.nybatis.core.reflection.core;

import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.Person;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.PersonProperty;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.core.testClass.Chain;
import org.nybatis.core.reflection.core.testClass.KeyNameDifferentEntity;
import org.nybatis.core.reflection.mapper.NObjectMapper;
import org.nybatis.core.reflection.mapper.NObjectSqlMapper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class JsonConverterTest {

    @Test
    public void toJson() throws Exception {

        JsonConverter $ = new JsonConverter( new NObjectMapper() );

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
    public void recursiveErrorFree() {

        JsonConverter $ = new JsonConverter( new NObjectSqlMapper() );

        String json = "{\n" +
            "\"projectId\":\"SAC001\",\n" +
            "\"completeDate\":\"2017-11-28T16:37:07.458+0900\",\n" +
            "\"chainId\":\"SAC001:00000001\",\n" +
            "\"chainName\":\"UserDownloadInfo.getRawUserDownloadInfo\",\n" +
            "\"chainType\":\"SQL\",\n" +
            "\"skipYn\":\"N\",\n" +
            "\"existYn\":\"N\",\n" +
            "\"possibleChains\":\"{}\",\n" +
            "\"chainProp\":\"{}\",\n" +
            "\"etcProp\":\"{}\"\n" +
            "}";

//        Chain chain = Reflector.toBeanFrom( json, Chain.class );

        Chain chain = $.toBeanFrom( json, Chain.class );

        NLogger.debug( Reflector.toJson( chain ) );
        NLogger.debug( $.toJson( chain ) );

    }

    @Test
    public void bindJsonOnFieldHavingDifferentMethodName() {

        JsonConverter $ = new JsonConverter( new NObjectSqlMapper() );

        String json = "{\"age\":21,\"name\":\"Jhon Dow\"}";

        KeyNameDifferentEntity entity = $.toBeanFrom( json, KeyNameDifferentEntity.class );
        NLogger.debug( Reflector.toJson( entity ) );

        Assert.assertEquals( entity.getNameAnother(), "Jhon Dow" );

    }


}