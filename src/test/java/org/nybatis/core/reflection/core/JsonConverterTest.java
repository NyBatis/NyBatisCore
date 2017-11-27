package org.nybatis.core.reflection.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.Person;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.PersonProperty;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.NObjectMapper;

import java.util.*;

import static org.junit.Assert.*;

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

}