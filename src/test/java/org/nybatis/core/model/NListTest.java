package org.nybatis.core.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class NListTest {

    @Test
    public void PrintAlign() {

    	NList data = new NList();

        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "정A" );
        data.addRow( "KEY", "AA" );
        data.addRow( "KEY", "123" );
        data.addRow( "KEY", new HashMap<>() );

        System.out.println( data );

    }

    @Test
    public void SimpleTest() {

        NList data = new NList();

        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "정A" );
        data.addRow( "KEY", "AA" );
        data.addRow( "KEY", "123" );

        data.addRow( new NMap( "{'KEY':'1', 'VAL':'2'}" ) );

        data.addRow( "KEY", "456" );
        data.addRow( "KEY", "789" );

        data.addRow( new NMap( "{'KEY':'정화수', 'VAL':'정화종'}" ) );

        data.addRow( "VAL", "000" );

        data.addRow( "ETC", 987654321 );

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

        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "정A" );
        data.addRow( "KEY", "AA" );
        data.addRow( "KEY", "123" );

        data.addRow( new NMap( "{'KEY':'1', 'VAL':'2'}" ) );

        data.addRow( "KEY", "456" );
        data.addRow( "KEY", "789" );

        data.addRow( new NMap( "{'KEY':'정화수', 'VAL':'정화종'}" ) );

        data.addRow( "VAL", "000" );

        data.addRow( "ETC", 987654321 );

        data.addAliases( "키" );
        data.addAliases( "값", "기타네요기타네요", "매칭" );

        data.setAlias( "KEY", "변경된 키" );

        System.out.println( data );

//        ExcelUtil.writeTo( "d:/test.xls", data );

    }

	@Test
	public void convertValueObjectToNRow() {

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

		testData.addRow( dummyList );

		assertEquals( testData.size(), 100 );
		assertEquals( testData.keySet().toString(), "[a, b]" );
		assertEquals( testData.getString( "a", 3 ), "1" );
		assertEquals( testData.getString( "b", 3 ), "3" );

	}

	@SuppressWarnings("unused")
	private class Merong {

		private String a = "1";
		private int    b = 3;

	}

	public NList getDummyData() {

    	NList data = new NList();

        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "정A" );
        data.addRow( "KEY", "AA" );
        data.addRow( "KEY", "123" );

        data.addRow( "VAL", "1111" );
        data.addRow( "VAL", "2222" );
        data.addRow( "VAL", "3333" );
        data.addRow( "VAL", "4444" );

        data.addRow( 2, "1111" );
        data.addRow( 2, "2222" );
        data.addRow( 2, "3333" );
        data.addRow( 2, "4444" );

        return data;

	}

	@Test
	public void deduplicateTest() {

    	NList data = new NList();

        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "정" );
        data.addRow( "KEY", "123" );

        data.addRow( "VAL", "1" );
        data.addRow( "VAL", "1" );
        data.addRow( "VAL", "2" );
        data.addRow( "VAL", "3" );

        NList deduplicatedTable = data.deduplicate();

    	NList dataRef = new NList();

    	dataRef.addRow( "KEY", "정" );
    	dataRef.addRow( "KEY", "정" );
    	dataRef.addRow( "KEY", "123" );

    	dataRef.addRow( "VAL", "1" );
    	dataRef.addRow( "VAL", "2" );
    	dataRef.addRow( "VAL", "3" );

        assertEquals( dataRef, deduplicatedTable );

	}

	@Test
	public void removeKeyTest() {

		NList dummyData = getDummyData();

		NLogger.debug( dummyData );

		dummyData.removeKeyBy( 2 );
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

}
