package org.nybatis.core.model;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.StringUtil;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.testng.Assert.assertEquals;

public class NListTest {

    @Test
    public void PrintAlign() {

    	NList data = new NList();

        data.add( "KEY", "정" );
        data.add( "KEY", "정A" );
        data.add( "KEY", "AA" );
        data.add( "KEY", "123" );
        data.add( "KEY", new HashMap<>() );

        System.out.println( data );

    }

    @Test
    public void SimpleTest() {

        NList data = new NList();

        data.add( "KEY", "정" );
        data.add( "KEY", "정A" );
        data.add( "KEY", "AA" );
        data.add( "KEY", "123" );

        data.addRow( new NMap( "{'KEY':'1', 'VAL':'2'}" ) );

        data.add( "KEY", "456" );
        data.add( "KEY", "789" );

        data.addRow( new NMap( "{'KEY':'정화수', 'VAL':'정화종'}" ) );

        data.add( "VAL", "000" );

        data.add( "ETC", 987654321 );

        System.out.println( data );

        assertEquals( 8, data.size( "KEY" ) );
        assertEquals( 9, data.size( "VAL" ) );
        assertEquals( 1, data.size( "ETC" ) );

        data.removeRow( 0 );

        System.out.println( data );

    }

    @Test
    public void AliasTest() throws IOException {

    	NList data = new NList();

        data.add( "KEY", "정" );
        data.add( "KEY", "정A" );
        data.add( "KEY", "AA" );
        data.add( "KEY", "123" );

        data.addRow( new NMap( "{'KEY':'1', 'VAL':'2'}" ) );

        data.add( "KEY", "456" );
        data.add( "KEY", "789" );

        data.addRow( new NMap( "{'KEY':'정화수', 'VAL':'정화종'}" ) );

        data.add( "VAL", "000" );

        data.add( "ETC", 987654321 );

        data.addAliases( "키" );
        data.addAliases( "값", "기타네요기타네요", "매칭" );

        data.setAlias( "KEY", "변경된 키" );

        System.out.println( data );

//        ExcelUtil.writeTo( "d:/test.xls", data );

    }

	@Test
	public void convertValueObjectToNRow() {

        System.out.println( Reflector.toJson(new Merong()));

		System.out.println( new NMap( new Merong() ) );

		assertEquals( "{a=1, b=3}", new NMap( new Merong() ).toString() );

	}

	@Test
	public void convertValueObjectToNMultiData() {

		List<Merong> dummyList = new ArrayList<>();

		for( int i = 0; i < 100; i++ ) {
			dummyList.add( new Merong() );
		}

		NList testData = new NList();

		testData.addRows( dummyList );

		assertEquals( testData.size(), 100 );
		assertEquals( testData.keySet().toString(), "[a, b]" );
		assertEquals( testData.getString( "a", 3 ), "1" );
		assertEquals( testData.getString( "b", 3 ), "3" );

	}

	@SuppressWarnings("unused")
	private class Merong {

		public String a = "1";
        public int    b = 3;

	}

	public NList getDummyData() {

    	NList data = new NList();

        data.add( "KEY", "정" );
        data.add( "KEY", "정A" );
        data.add( "KEY", "AA" );
        data.add( "KEY", "123" );

        data.add( "VAL", "1111" );
        data.add( "VAL", "2222" );
        data.add( "VAL", "3333" );
        data.add( "VAL", "4444" );

        data.add( 2, "1111" );
        data.add( 2, "2222" );
        data.add( 2, "3333" );
        data.add( 2, "4444" );

        return data;

	}

	@Test
	public void deduplicateTest() {

    	NList data = new NList();

        data.add( "KEY", "정" );
        data.add( "KEY", "정" );
        data.add( "KEY", "정" );
        data.add( "KEY", "123" );

        data.add( "VAL", "1" );
        data.add( "VAL", "1" );
        data.add( "VAL", "2" );
        data.add( "VAL", "3" );

        NList deduplicatedTable = data.deduplicate();

    	NList dataRef = new NList();

    	dataRef.add( "KEY", "정" );
    	dataRef.add( "KEY", "정" );
    	dataRef.add( "KEY", "123" );

    	dataRef.add( "VAL", "1" );
    	dataRef.add( "VAL", "2" );
    	dataRef.add( "VAL", "3" );

        assertEquals( dataRef, deduplicatedTable );

	}

	@Test
	public void removeKeyTest() {

		NList dummyData = getDummyData();

		NLogger.debug( dummyData );

		dummyData.removeKey( dummyData.getKey( 2 ) );
		NLogger.debug( dummyData );

	}

    @Test
    public void toJsonTest() {

		NList dummyData = getDummyData();
		NLogger.debug( dummyData );

        String jsonFromRaw  = Reflector.toJson( dummyData );
        String jsonFromList = Reflector.toJson( dummyData.toList() );

        assertEquals( jsonFromList, jsonFromRaw );

        System.out.println( jsonFromRaw  );
        System.out.println( jsonFromList );

    }

    @Test
    public void mergeNList() {

        NList a = new NList();
        a.add( "Key1", "1" );
        a.add( "Key1", "2" );
        a.add( "Key1", "3" );
        a.add( "Key1", "4" );
        a.add( "Key2", "A" );

        NList b = new NList();
        b.add( "Key2", "B" );
        b.add( "Key3", "1" );
        b.add( "Key3", "2" );
        b.add( "Key3", "3" );

        a.addRows( b );

        NLogger.debug( a );

        assertEquals( a.size(), 7 );
        assertEquals( a.size("Key1"), 4 );
        assertEquals( a.size("Key2"), 5 );
        assertEquals( a.size("Key3"), 7 );

        assertEquals( a.get( "Key1", 1 ), "2" );
        assertEquals( a.get( "Key2", 3 ), null );
        assertEquals( a.get( "Key2", 4 ), "B" );
        assertEquals( a.get( "Key3", 5 ), "2" );

    }

    @Test
    public void createNew() {

        NList a = getDummyData();
        NList b = new NList( a );

        assertEquals( a.equals(b), true );

        NList c = new NList( "{'a':1,'b':2}" );
        NList d = new NList( "[{'a':1,'b':2},{'a':1,'b':2},{'a':1,'b':2}]" );

        assertEquals( c.size(), 1 );
        assertEquals( d.size(), 3 );
        assertEquals( d.getInt( "b", 2 ), 2 );

    }

    @Test
    public void preserveKeySequence() {

        NList list = new NList();
        list.addRow( "{\"리스트ID\":\"TAR000001715\",\"리스트명\":\"테스트\",\"노출여부\":\"N\",\"그룹ID\":\"TAR00\",\"상품수\":0,\"카드링크\":\"N\",\"등록ID\":\"kinesis\",\"등록일자\":\"2016-11-02 17:55:47\",\"수정ID\":\"kinesis\",\"수정일자\":\"2016-11-02 17:55:47\",\"스마트오퍼링\":\"\"}" );
        String keys = StringUtil.join( list.keySet(), "," );

        assertEquals( keys, "리스트ID,리스트명,노출여부,그룹ID,상품수,카드링크,등록ID,등록일자,수정ID,수정일자,스마트오퍼링" );

    }

}
