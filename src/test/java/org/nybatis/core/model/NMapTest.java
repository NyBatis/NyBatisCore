package org.nybatis.core.model;


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import bsh.Primitive;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.testng.Assert;
import org.testng.annotations.Test;

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

		private String name;
		private int    age;
		public  double salary;

	}

	@Test
	public void convertFromVo() {

		Person p = new Person();

		p.name = "정화수";
		p.age  = 37;
		p.salary = 102.3;

		NMap NMap = new NMap( p );

		System.out.println( NMap );


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

		Assert.assertEquals( false, r1.equals( r2 ) );

		r2.put( 3, 3 );
		Assert.assertEquals( false, r1.equals( r2 ) );

		r2.put( 3, "3" );
		Assert.assertEquals( true, r1.equals( r2 ) );

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
		System.out.println( new Reflector().toJson( new TreeMap(nrow01)) );
		System.out.println( nrow01.getValueHash() );
		System.out.println( "-----------------------------------------");
//		System.out.println( nrow02.hashCode() );
		System.out.println( nrow02 );
		System.out.println( nrow02.toJson().hashCode() );
		System.out.println( new Reflector().toJson( new TreeMap(nrow02)) );
		System.out.println( nrow02.getValueHash() );
		System.out.println( "-----------------------------------------");
//		System.out.println( nrow02.hashCode() );
		System.out.println( nrow03 );
		System.out.println( nrow03.toJson().hashCode() );
		System.out.println( new Reflector().toJson( new TreeMap(nrow03)) );
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

		Assert.assertEquals( map.get( "set"  ) instanceof Set,    true  );
		Assert.assertEquals( map.get( "set"  ) instanceof List,   false );
		Assert.assertEquals( map.get( "byte01" ) instanceof byte[], true  );
		Assert.assertEquals( map.get( "byte02" ) instanceof Byte[], true  );

		map.fromBean( testMap );

		Assert.assertEquals( map.get( "set"  ) instanceof Set,    false );
		Assert.assertEquals( map.get( "set"  ) instanceof List,   true  );
		Assert.assertEquals( map.get( "byte01" ) instanceof byte[], true  );
		Assert.assertEquals( map.get( "byte02" ) instanceof byte[], true  );

	}


}
