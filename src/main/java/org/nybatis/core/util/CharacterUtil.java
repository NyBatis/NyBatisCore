package org.nybatis.core.util;

import java.lang.Character.UnicodeBlock;



/**
 * 문자 처리 유틸
 * 
 */
public class CharacterUtil {

	/** 초성 */
	private static char[] HANGUL_1ST = new char[] { 'ㄱ','ㄲ','ㄴ','ㄷ','ㄸ','ㄹ','ㅁ','ㅂ','ㅃ','ㅅ','ㅆ','ㅇ','ㅈ','ㅉ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ' };
	
	/** 중성 */
	private static char[] HANGUL_2ND = new char[] { 'ㅏ','ㅐ','ㅑ','ㅒ','ㅓ','ㅔ','ㅕ','ㅖ','ㅗ','ㅘ','ㅙ','ㅚ','ㅛ','ㅜ','ㅝ','ㅞ','ㅟ','ㅠ','ㅡ','ㅢ','ㅣ' };
	
	/** 종성 */
	private static char[] HANGUL_3RD = new char[] { '\0','ㄱ','ㄲ','ㄳ','ㄴ','ㄵ','ㄶ','ㄷ','ㄹ','ㄺ','ㄻ','ㄼ','ㄽ','ㄾ','ㄿ','ㅀ','ㅁ','ㅂ','ㅄ','ㅅ','ㅆ','ㅇ','ㅈ','ㅊ','ㅋ','ㅌ','ㅍ','ㅎ' };	

	/**
	 * Full-width 문자의 Console 출력크기
	 */
	private static int fullwidthCharacterWidth = 2;
	
	/**
	 * Full-width 문자의 Console 출력공간 크기를 세팅한다.
	 * 
	 * @param width 출력할 크기
	 */
	public static void setCjkCharacterWidth( int width ) {
		if( width >= 1 )
			fullwidthCharacterWidth = width;
	}

	/**
	 * Full-width 문자의 Console 출력공간 크기를 구한다.
	 * 
	 * @return Full-width 문자의 출력공간 크기
	 */
	public static int getFullwidthCharacterWidth() {
		return fullwidthCharacterWidth;
	}
	
	/**
	 * 한글문자의 초성/중성/종성을 분리한다.
	 * 
     * <pre>
     *
     * CharacterUtil.disassembleKorean( '롱' ); → [ 'ㄹ','ㅗ','ㅇ'] 을 반환
     * CharacterUtil.disassembleKorean( '수' ); → ['ㅅ','ㅜ','\0' ] 을 반환
     * CharacterUtil.disassembleKorean( 'H'  ); → null 을 반환
     *
     * </pre>
	 * 
	 * @param ch 검사할 문자
	 * @return 초성/중성/종성으로 분리된 배열 (분리할 수 없을 경우 null 반환)
	 */
	public static char[] disassembleKorean( char ch ) {
		
		// 한글의 자음/모음을 분리할 수 없을 경우 null 반환
		if( ch < 0xAC00 || ch > 0xD79F ) return null;
		
		ch -= 0xAC00;

    	int idx3rd = ch % 28;
    	int idx2nd = ( (ch - idx3rd) / 28 ) % 21;
    	int idx1st = ((ch - idx3rd) / 28) / 21;

    	char[] result = new char[3];

    	result[0] = HANGUL_1ST[ idx1st ];
    	result[1] = HANGUL_2ND[ idx2nd ];
    	result[2] = HANGUL_3RD[ idx3rd ];

    	return result;		
		
	}
	
	/**
	 * 한글문자의 종성을 가지고 있는지 여부를 확인한다.
	 * 
	 * <pre>
	 * 
	 * CharacterUtil.hasHangulJongsung( 'H'  ) → false
	 * CharacterUtil.hasHangulJongsung( '수' ) → false
	 * CharacterUtil.hasHangulJongsung( '롱' ) → true
	 * 
	 * </pre>
	 * 
	 * @param ch 검사할 문자
	 * @return 한글문자 종성 소유여부
	 */
	public static boolean hasHangulJongsung( char ch ) {
		
		char[] result = disassembleKorean( ch );
		
		if( result == null ) return false;
		
		return result[2] == '\0';
		
	}

	/**
	 * Font 크기가 Half-width 로 분류되는지 여부를 확인한다.
	 * 
	 * @param ch 검사할 문자
	 * @return Half-width 분류여부
	 * 
	 * @see <a href="http://unicode.org/reports/tr11">http://unicode.org/reports/tr11</a>
	 * @see <a href="http://unicode.org/charts/PDF/UFF00.pdf">http://unicode.org/charts/PDF/UFF00.pdf</a>
	 *
	 */
	public static boolean isHalfWidth( char ch ) {

		if( ch < 0x0020 ) return true;  // 특수문자

		if( 0x0020 <= ch && ch <= 0x007F ) return true;  // ASCII (Latin characters, symbols, punctuation,numbers)
		
		// FF61 ~ FF64 : Halfwidth CJK punctuation
		// FF65 ~ FF9F : Halfwidth Katakanana variants
		// FFA0 ~ FFDC : Halfwidth Hangul variants
		if( 0xFF61 <= ch && ch <= 0xFFDC ) return true;
		
		// FFE8 ~ FFEE : Halfwidth symbol variants
		if( 0xFFE8 <= ch && ch <= 0xFFEE ) return true;
		
//		if( 0x0020 <= ch && ch <= 0x007F ) return true;  // ASCII (Latin characters, symbols, punctuation,numbers)
//		if( 0x1100 <= ch && ch <= 0x11FF ) return false; // Hangul Jamo (Korean)
//		if( 0x3000 <= ch && ch <= 0x303F ) return false; // CJK punctuation
//		if( 0x3040 <= ch && ch <= 0x309F ) return false; // Hiragana (Japanese)
//		if( 0x30A0 <= ch && ch <= 0x30FF ) return false; // Katakana (Japanese)
//		if( 0x3130 <= ch && ch <= 0x318F ) return false; // Hangul Compatibility (Korean for KS X1001 compatibility)
//		if( 0xAC00 <= ch && ch <= 0xD7AC ) return false; // Hangul Syllables
//		if( 0xF900 <= ch && ch <= 0xFaFF ) return false; // CJK Compatibility Ideographs Block
//		if( 0xFF00 <= ch && ch <= 0xFFEF ) return false; // Latin characters and half-width Katakana and Hangul (Half-width and Full-width)
		// FF01 ~ FF5E : Fullwidth ASCII variants
		// FF5F ~ FF60 : Fullwidth brackets
		// FF61 ~ FF64 : Halfwidth CJK punctuation
		// FF65 ~ FF9F : Halfwidth Katakanana variants
		// FFA0 ~ FFDC : Halfwidth Hangul variants
		// FFE8 ~ FFEE : Halfwidth symbol variants
//		if( 0x4E00 <= ch && ch <= 0x9FFF ) return false; // CJK unifed ideographs – Common and uncommon
//		if( 0x3400 <= ch && ch <= 0x4DBF ) return false; // CJK unified ideographs Extension A – Rare
//		if( 0x2000 <= ch && ch <= 0x2FFF ) return false; // CJK unified ideographs Extension B – Very rare

		return false;
		
	}

	/**
	 * 유형(half-width or full-width)을 반영하여 문자의 길이를 구한다.
	 * 
	 * @param ch 검사할 문자
	 * @return 문자 길이
	 */
	public static int getLength( char ch ) {
		
		return isHalfWidth( ch ) ? 1 : fullwidthCharacterWidth;
		
	}
	
	/**
	 * 문자가 중국어/일본어/한국어인지 여부를 확인한다.
	 * 
	 * 
	 * @param ch 검사할 문자열
	 * @return CJK 여부
	 */
	public static boolean isCJK( char ch ) {

		if( 0x0020 <= ch && ch <= 0x007F ) return false; // ASCII (Latin characters, symbols, punctuation,numbers)

		if( 0x1100 <= ch && ch <= 0x11FF ) return true;  // Hangul Jamo (Korean)
		if( 0x3000 <= ch && ch <= 0x303F ) return true;  // CJK punctuation
		if( 0x3040 <= ch && ch <= 0x309F ) return true;  // Hiragana (Japanese)
		if( 0x30A0 <= ch && ch <= 0x30FF ) return true;  // Katakana (Japanese)
		if( 0x3130 <= ch && ch <= 0x318F ) return true;  // Hangul Compatibility (Korean for KS X1001 compatibility)
		if( 0xAC00 <= ch && ch <= 0xD7AC ) return true;  // Hangul Syllables
		if( 0xF900 <= ch && ch <= 0xFaFF ) return true;  // CJK Compatibility Ideographs Block
		if( 0x4E00 <= ch && ch <= 0x9FFF ) return true;  // CJK unifed ideographs – Common and uncommon
		if( 0x3400 <= ch && ch <= 0x4DBF ) return true;  // CJK unified ideographs Extension A – Rare
		if( 0x2000 <= ch && ch <= 0x2FFF ) return true;  // CJK unified ideographs Extension B – Very rare

		// FF61 ~ FF64 : Halfwidth CJK punctuation
		// FF65 ~ FF9F : Halfwidth Katakanana variants
		// FFA0 ~ FFDC : Halfwidth Hangul variants
		return 0xFF61 <= ch && ch <= 0xFFDC;

	}

	/**
	 * 한글문자 여부를 구한다.
	 * 
	 * @param ch 검사할 문자
	 * @return 한글 여부
	 */
	public static boolean isKorean( char ch ) {

		if( 0x1100 <= ch && ch <= 0x11FF ) return true;  // Hangul Jamo (Korean)
		if( 0x3130 <= ch && ch <= 0x318F ) return true;  // Hangul Compatibility (Korean for KS X1001 compatibility)
		if( 0xAC00 <= ch && ch <= 0xD7AC ) return true;  // Hangul Syllables
		return 0xFFA0 <= ch && ch <= 0xFFDC;

	}

	/**
	 * 일본문자 여부를 구한다.
	 * 
	 * @param ch 검사할 문자
	 * @return 일본문자 여부
	 */
	public static boolean isJapanese( char ch ) {
		
		if( 0x3040 <= ch && ch <= 0x309F ) return true;  // Hiragana (Japanese)
		if( 0x30A0 <= ch && ch <= 0x30FF ) return true;  // Katakana (Japanese)
		return 0xFF65 <= ch && ch <= 0xFF9F;

	}

	/**
	 * 중국문자 여부를 구한다.
	 * 
	 * @param ch 검사할 문자
	 * @return 중국문자 여부
	 */
	public static boolean isChinese( char ch ) {

		if( 0x3000 <= ch && ch <= 0x303F ) return true;  // CJK punctuation
		if( 0xF900 <= ch && ch <= 0xFaFF ) return true;  // CJK Compatibility Ideographs Block
		if( 0x4E00 <= ch && ch <= 0x9FFF ) return true;  // CJK unifed ideographs – Common and uncommon
		if( 0x3400 <= ch && ch <= 0x4DBF ) return true;  // CJK unified ideographs Extension A – Rare
		if( 0x2000 <= ch && ch <= 0x2FFF ) return true;  // CJK unified ideographs Extension B – Very rare

		// FF61 ~ FF64 : Halfwidth CJK punctuation
		// FF65 ~ FF9F : Halfwidth Katakanana variants
		// FFA0 ~ FFDC : Halfwidth Hangul variants
		if( 0xFF61 <= ch && ch <= 0xFFDC ) return true;
		
		if( 0x3040 <= ch && ch <= 0x309F ) return true;  // Hiragana (Japanese)
		if( 0x30A0 <= ch && ch <= 0x30FF ) return true;  // Katakana (Japanese)
		return 0xFF65 <= ch && ch <= 0xFF9F;

	}

	public static boolean isCJK2( char ch ) {

		UnicodeBlock unicodeBlock = Character.UnicodeBlock.of( ch );
		
		return	unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
             || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
	         || unicodeBlock == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
	         || unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS
	         || unicodeBlock == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
	         || unicodeBlock == Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT
	         || unicodeBlock == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
	         || unicodeBlock == Character.UnicodeBlock.HIRAGANA
	         || unicodeBlock == Character.UnicodeBlock.KATAKANA
	         || unicodeBlock == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO
	         || unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO
	         || unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_A
	         || unicodeBlock == Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_B
	         || unicodeBlock == Character.UnicodeBlock.HANGUL_SYLLABLES
	         || unicodeBlock == Character.UnicodeBlock.ENCLOSED_CJK_LETTERS_AND_MONTHS
	    ;
		
	}
	
}
