package org.nybatis.core.util;

import org.nybatis.core.model.NList;
import org.testng.annotations.Test;

public class CharacterUtilTest {


	@Test
	public void isJapanese() {

		char[] testCharArray = { 'か', 'フ', 'ﾌ', '！', '。', 'A', '정', 'ㄱ', '鄭'  };
		
		NList result = new NList();
		
		for( char c : testCharArray ) {
			
			result.add( "code", Integer.toHexString( c ) );
			result.add( "Half-width", CharacterUtil.isHalfWidth( c ) );
			result.add( "testCharacter", c );
			result.add( "isCJK", CharacterUtil.isCJK( c ) );
			result.add( "unicodeBlock", Character.UnicodeBlock.of( c ) );
			
		}

		System.out.println( result );
		
		System.out.println( "フフフフ" );
		System.out.println( "ﾌﾌﾌﾌﾌﾌﾌﾌ" );
		System.out.println( "！！！！" );
		System.out.println( "!!!!" );

	}
	
}
