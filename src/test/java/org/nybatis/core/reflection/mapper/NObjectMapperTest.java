package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.vo.Person;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.*;

/**
 * @author nayasis@onestorecorp.com
 * @since 2017-02-17
 */
public class NObjectMapperTest {

    @Test
    public void figureout() throws IOException {

        NObjectMapper objectMapper = new NObjectMapper();

        JavaType javaType = objectMapper.constructType( Person.class );

        BeanDescription description = objectMapper.getSerializationConfig().introspect( javaType );

        System.out.println( "merong !");

    }

}