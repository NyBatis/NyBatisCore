package org.nybatis.core.reflection;

import java.io.File;
import java.text.ParseException;
import java.util.Map;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.testModel.Link;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@SuppressWarnings( "rawtypes" )
public class ReflectorTest {

	Reflector reflector = new Reflector();

	@Test
	public void simpleTest() {

		NLogger.debug( reflector.toJson( makeTestPerson(), true ) );

		String json = reflector.toJson( makeTestPerson(), false );

		assertEquals( "{\"firstName\":\"Hwasu\",\"lastName\":\"Jung\",\"phone\":{\"code\":2,\"number\":\"322-3493\"},\"fax\":{\"code\":9999,\"number\":\"00100\"},\"phoneList\":[]}", json );

        Map map1 = reflector.toMapFromJson( json );

		NLogger.debug( map1 );

		Person p1 = reflector.toBeanFromJson( json, Person.class );

		NLogger.debug( reflector.getFieldReport( p1 ) );

		Person p2 = reflector.toBeanFromMap( map1, Person.class );
		NLogger.debug( reflector.getFieldReport( p2 ) );

		Map map2 = reflector.toMapFromBean( p2 );

		NLogger.debug( map2 );

	}

	private Person makeTestPerson() {

		Person p = new Person();

		p.firstName = "Hwasu";
		p.lastName  = "Jung";
		p.phone.code = 2;
		p.phone.number = "322-3493";
		p.fax.code = 9999;
		p.fax.number = "00100";

		return p;

	}

	@Test
	public void cloneTest() {

		Link link = new Link( new File("//NAS/emul/SuperFamicom/emulator/snes9x1.45 NK Custom/snes9x.exe") );

		link.setId( 123456 );
		link.setTitle( "Merong" );
		link.setGroupName( "Samurai Showdown" );

		NLogger.debug( link );

		link = link.clone();

		link.setId( null );
		link.setTitle( "Modified Merong !!" );

		NLogger.debug( link );

	}

	@Test
	public void singleQuoteTesst() {

		String jsonText = String.format( "{'path':'%s/target/classes/ibatis_config/sql'}", "MERONG");

		NLogger.debug( jsonText );

		Map<String, Object> map = new Reflector().toMapFromJson(jsonText);

		NLogger.debug( map      );

	}

	@Test
	public void objectMapperTest() throws ParseException {

		FromVo fromVo = new FromVo( "Hwasu", 39, "1977-01-22" );

		NLogger.debug( "fromVo : {}", reflector.toJson( fromVo ) );

		Map map = reflector.toMapFromBean( fromVo );

		NLogger.debug( new NMap( map ).toDebugString() );

		NMap expectedMap = new NMap();

		expectedMap.put( "name", "Hwasu" );
		expectedMap.put( "age", 39 );
		expectedMap.put( "birth", "1977-01-22 00:00:00" );

		assertEquals( expectedMap, map, "convert bean to map" );

		ToVo bean = reflector.toBeanFromMap( map, ToVo.class );

		NLogger.debug( bean );

		ToVo expectedBean = new ToVo( "Hwasu", "1977-01-22" );
		expectedBean.setAge( 39 );

		assertEquals( expectedBean.toString(), bean.toString(), "convert map to bean" );

	}

	@Test
	public void mergeTest() {

		NMap fromNMap = new NMap();

		fromNMap.put( "name", "hwasu" );
		fromNMap.put( "age",  "20" );

		ToVo toVo = new ToVo();

		NLogger.debug( "before\n{}", fromNMap );

		reflector.merge( fromNMap, toVo );

		NLogger.debug( "after\n{}", toVo );

	}

}
