package org.nybatis.core.util;

import org.nybatis.core.model.NList;
import org.testng.annotations.Test;

public class CharacterUtilTest {


	@Test
	public void isJapanese() {

		char[] testCharArray = { 'か', 'フ', 'ﾌ', '！', '。', 'A', '정', 'ㄱ', '鄭'  };
		
		NList result = new NList();
		
		for( char c : testCharArray ) {
			
			result.addRow( "code", Integer.toHexString( c ) );
			result.addRow( "Half-width", CharacterUtil.isHalfWidth( c ) );
			result.addRow( "testCharacter", c );
			result.addRow( "isCJK", CharacterUtil.isCJK( c ) );
			result.addRow( "isCJK2", CharacterUtil.isCJK2( c ) );
			result.addRow( "unicodeBlock", Character.UnicodeBlock.of( c ) );
			
		}

		System.out.println( result );
		
		System.out.println( "フフフフ" );
		System.out.println( "ﾌﾌﾌﾌﾌﾌﾌﾌ" );
		System.out.println( "！！！！" );
		System.out.println( "!!!!" );

	}
	
}
