package org.nybatis.core.model;


import org.nybatis.core.exception.unchecked.BizException;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.vo.Card;
import org.nybatis.core.reflection.Reflector;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class NMapTest {

	@SuppressWarnings( "unused" )
	private static class Person {

        public String getName() {
			return name;
		}
		public void setName( String name ) {
			this.name = name;
		}
		public int getAge() {
			return age;
		}
		public void setAge( int age ) {
			this.age = age;
		}
		public NDate getBirth() {
			return birth;
		}
		public void setBirth( NDate birth ) {
			this.birth = birth;
		}

		private String name;
		private int    age;
		private NDate  birth = new NDate();
		public  double salary;

	}

	@Test
	public void convertFromVo() {

		Person p = new Person();

		p.name = "정화수";
		p.age  = 37;
		p.salary = 102.3;

		NMap map = new NMap( p );

		System.out.println( map );

		NLogger.debug( map );

		// {name=정화수, age=37, birth=2016-11-03T03:09:52.016+0900, salary=102.3}

	}

	@Test
	public void equalTest() {

		NMap r1 = new NMap();
		NMap r2 = new NMap();

		r1.put( 1, 1 );
		r1.put( 2, 2 );
		r1.put( 3, "3" );

		r2.put( 1, 1 );
		r2.put( 2, new Integer(2) );

		assertEquals( false, r1.equals( r2 ) );

		r2.put( 3, 3 );
		assertEquals( false, r1.equals( r2 ) );

		r2.put( 3, "3" );
		assertEquals( true, r1.equals( r2 ) );

	}

    @Test
    @SuppressWarnings( { "rawtypes", "unchecked" } )
	public void cacheKey() {

		Person person01 = new Person();

		person01.name   = "정화수";
		person01.age    = 37;
		person01.salary = 102.3;

		Person person02 = new Person();

		person02.name   = "정화수";
		person02.age    = 37;
		person02.salary = 102.3;

		NMap nrow01 = new NMap( person01 );
		NMap nrow02 = new NMap( person02 );
		NMap nrow03 = new NMap();

		nrow03.put( "salary", 102.3    );
		nrow03.put( "name",   "정화수" );
		nrow03.put( "age",    37       );
		nrow03.put( "array",  new int[] {1,2,3,4} );

		nrow01.put( "array", new int[] {1,2,3,4} );
		nrow02.put( "array", new int[] {1,2,3,4} );

//		System.out.println( nrow01.hashCode() );
		System.out.println( nrow01 );
		System.out.println( nrow01.toJson().hashCode() );
		System.out.println( Reflector.toJson( new TreeMap(nrow01)) );
		System.out.println( nrow01.getValueHash() );
		System.out.println( "-----------------------------------------");
//		System.out.println( nrow02.hashCode() );
		System.out.println( nrow02 );
		System.out.println( nrow02.toJson().hashCode() );
		System.out.println( Reflector.toJson( new TreeMap(nrow02)) );
		System.out.println( nrow02.getValueHash() );
		System.out.println( "-----------------------------------------");
//		System.out.println( nrow02.hashCode() );
		System.out.println( nrow03 );
		System.out.println( nrow03.toJson().hashCode() );
		System.out.println( Reflector.toJson( new TreeMap(nrow03)) );
		System.out.println( nrow03.getValueHash() );

		Object t = "1";

		System.out.println( (int) t );

	}

    @Test
    public void convertFromJson() {

    	String json = "{ \"10\" : null }";

    	NMap a = new NMap( json );

    	NLogger.debug( a );

    }

	@Test
	public void nullTest() {

		String json = null;

		NMap a = new NMap( json );

		NLogger.debug( a.toDebugString(false, false) );

	}

	@Test
	public void convertSetToList() {

		Map<String, Object> testMap = new HashMap<>();

		Set<String> testSet = new HashSet<>();

		testSet.add( "A" );
		testSet.add( "B" );
		testSet.add( "C" );

		byte[] testArray01 = new byte[] { 0, 1, 2, 3, 4, 5 };
		Byte[] testArray02 = new Byte[] { 0, 1, 2, 3, 4, 5 };

		testMap.put( "set",    testSet     );
		testMap.put( "byte01", testArray01 );
		testMap.put( "byte02", testArray02 );

		NMap map = new NMap( testMap );

		assertEquals( map.get( "set" ) instanceof Set, true );
		assertEquals( map.get( "set" ) instanceof List, false );
		assertEquals( map.get( "byte01" ) instanceof byte[], true );
		assertEquals( map.get( "byte02" ) instanceof Byte[], true );

		map.bind( testMap );

		assertEquals( map.get( "set" ) instanceof Set, false );
		assertEquals( map.get( "set" ) instanceof List, true );
		assertEquals( map.get( "byte01" ) instanceof byte[], true );
		assertEquals( map.get( "byte02" ) instanceof byte[], true );

	}

	@Test
	public void extractByJsonPath() throws JsonPathNotFoundException {

		String json = "{\n" +
				"\t\"id\": {\n" +
				"\t\t\"name\": \"merong\",\n" +
				"\t\t\"age\": 21,\n" +
				"\t\t\"job\": [\"student\", \"parent\"]\n" +
				"\t}\n" +
				"}";

		NMap map = new NMap( json );

		assertEquals( map.getByJsonPath("id").toString(), "{name=merong, age=21, job=[student, parent]}" );

		try {
			NLogger.debug( map.getByJsonPath( "null" ) );
			throw new BizException( "Expected : {}", "No results for path: $['null']" );
		} catch( JsonPathNotFoundException e ) {}

		try {
			NLogger.debug( map.getByJsonPath( null ) );
			throw new BizException( "Expected : {}", "path can not be null or empty" );
		} catch( JsonPathNotFoundException e ) {}

		assertEquals( map.getByJsonPath("id.name").toString(),   "merong" );
		assertEquals( map.getByJsonPath("id.job[1]").toString(), "parent" );

		try {
			NLogger.debug( map.getByJsonPath( "id.job[3]" ) );
			throw new BizException( "Expected : {}", "No results for path: $['null']" );
		} catch( JsonPathNotFoundException e ) {}

		NLogger.debug( map.getByJsonPath( "id[0]" ) );

	}

	@Test
	public void dateConvertion() {

		NMap param = new NMap();

		NDate ndate = new NDate();
		Date  date  = new Date();

		param.put( "ndate", ndate );
		param.put( "date",  date  );

		NMap convertedMap = new NMap().bind( param );
		NLogger.debug( convertedMap );

		DateBean dateBean = convertedMap.toBean( DateBean.class );

		NLogger.debug( dateBean );

		assertTrue( ndate.equals( dateBean.ndate ) );
		assertTrue( date.equals( dateBean.date ) );

		System.out.println( ">>> " + Integer.MAX_VALUE );

	}

	@Test
	public void jsonPath() throws JsonPathNotFoundException {

		NList nList = new NList( "[ {\n" +
				"     \"cardId\" : \"CRD0002323\",\n" +
				"     \"expoOrd\" : 1\n" +
				"   }, {\n" +
				"     \"cardId\" : \"CRD0003010\",\n" +
				"     \"expoOrd\" : 2\n" +
				"   }, {\n" +
				"     \"cardId\" : \"CRD0003011\",\n" +
				"     \"expoOrd\" : 3\n" +
				"   }, {\n" +
				"     \"cardId\" : \"CRD0003330\",\n" +
				"     \"expoOrd\" : 4\n" +
				"   }, {\n" +
				"     \"cardId\" : \"CRD0003343\",\n" +
				"     \"expoOrd\" : 5\n" +
				"   }, {\n" +
				"     \"cardId\" : \"CRD0002420\",\n" +
				"     \"expoOrd\" : 6\n" +
				"   } ]" );

		NLogger.debug( nList );

		assertEquals( nList.size(), 6 );
		assertEquals( nList.get( "cardId", 3 ), "CRD0003330" );

		NMap map = new NMap();
		map.put( "card", nList.toList() );

		assertEquals( map.getByJsonPath( "card[0].cardId" ), "CRD0002323" );

		// POJO value is not Map so JsonPath is not working !
		map.put( "card", nList.toList(Card.class) );

		// convert key structure to JsonPath
		Map jsonPathMap = new JsonPathMapper().toJsonPath( map );
		map.clear();
		map.putAll( jsonPathMap );

		assertEquals( map.getByJsonPath( "card[0].cardId" ), "CRD0002323" );

	}

	@Test
	public void convertSubEntty() {

		Card card = new Card();

//		card.getCopyTargetTenant().setS02( "Y" );

		Map<String, Object> objectMap = Reflector.toMapFrom( card );

//		String s = Reflector.toJson( "" );

		NLogger.debug( objectMap );

	}

}
