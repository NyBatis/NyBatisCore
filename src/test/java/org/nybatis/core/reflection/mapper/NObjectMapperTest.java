package org.nybatis.core.reflection.mapper;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import org.nybatis.core.reflection.vo.Person;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * @author nayasis@gmail.com
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