package org.nybatis.core.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.nybatis.core.reflection.Reflector;

import org.testng.annotations.Test;

public class JacksonMappingTest {

	@SuppressWarnings( "unused" )
	private static class Person {

        public String name;
		public int    age;
		public double salary;

		public Person( String name, int age, double salary ) {
			this.name = name;
			this.age  = age;
			this.salary  = salary;
		}

		public String toString() {
			return Reflector.toString( this );
		}

	}

	@SuppressWarnings( "unchecked" )
    @Test
	public void convertBeanToMapTest() {

		Person p = new Person( "nayasis", 37, 1024 );

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = mapper.convertValue( p, Map.class );

		System.out.println( map );

	}

	@Test
	public void convertMapToBeanTest() {

		Map<String, Object> map = new HashMap<>();

		map.put( "name",    "merong" );
		map.put( "age",     37       );
		map.put( "salary",  1024.0   );

		ObjectMapper mapper = new ObjectMapper();

		Person person = mapper.convertValue( map, Person.class );

		System.out.println( person );

	}

	@SuppressWarnings( "unchecked" )
    @Test
	public void convertJsonToMap() throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> map = mapper.readValue( "{ \"name\" : \"merong\", \"age\" : 37, \"salary\" : 1024.2 }", Map.class );

		System.out.println( map );

	}

	@SuppressWarnings( "unchecked" )
    @Test
	public void convertJsonToMapWithSingleQuotedString() throws IOException {

		ObjectMapper mapper = new ObjectMapper();

		mapper.configure( JsonParser.Feature.ALLOW_SINGLE_QUOTES, true );

		Map<String, Object> map = mapper.readValue( "{ 'name' : 'merong', 'age' : 37, 'salary' : 1024.2 }", Map.class );

		System.out.println( map );

		map = mapper.readValue( "{ \"name\" : \"merong\", \"age\" : 37, \"salary\" : 1024.2 }", Map.class );

		System.out.println( map );

	}

	@Test
	public void convertMapToJson() throws IOException {

		Map<String, Object> map = new HashMap<>();

		map.put( "name",    "merong" );
		map.put( "age",     37       );
		map.put( "salary",  1024.0   );

		ObjectMapper mapper = new ObjectMapper();

		String result = mapper.writeValueAsString( map );

		System.out.println( result );

	}

	@Test
	public void convertBeanToJson() throws IOException {

		Person p = new Person( "nayasis", 37, 1024 );

		ObjectMapper mapper = new ObjectMapper();

		String result = mapper.writeValueAsString( p );

		System.out.println( result );

	}

}
