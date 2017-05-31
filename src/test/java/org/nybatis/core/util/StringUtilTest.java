package org.nybatis.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.nybatis.core.conf.Const;
import org.nybatis.core.log.NLogger;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class StringUtilTest {

	@Test
	public void changeHangulJosa() {

		assertEquals( "카드를 등록합니다."       , StringUtil.format( "{}를 등록합니다." , "카드"      ) );
		assertEquals( "카드템플릿을 등록합니다." , StringUtil.format( "{}를 등록합니다." , "카드템플릿") );
		assertEquals( "카드는 등록됩니다."       , StringUtil.format( "{}는 등록됩니다." , "카드"      ) );
		assertEquals( "카드템플릿은 등록됩니다." , StringUtil.format( "{}는 등록됩니다." , "카드템플릿") );
		assertEquals( "카드가 등록됩니다."       , StringUtil.format( "{}가 등록됩니다." , "카드"      ) );
		assertEquals( "카드템플릿이 등록됩니다." , StringUtil.format( "{}가 등록됩니다." , "카드템플릿") );

	}

	@Test
	public void unescape() {

		assertEquals( "결재금액오\n\n류", StringUtil.unescape( "\uacb0\uc7ac\uae08\uc561\uc624\\n\\n\ub958" ) );

		assertEquals( "\\", StringUtil.unescape( "\\\\" ) );
		assertEquals( "\"", StringUtil.unescape( "\\\"" ) );
		assertEquals( "\'", StringUtil.unescape( "\\\'" ) );

	}

	@Test
	public void compress() {

		assertEquals( "ISO-8859-1", StandardCharsets.ISO_8859_1.toString() );

		String testString01 = "정화수는 정주호랑 김선지랑 행복하게 오래오래 살았어요 !";
		String testString02 = "I am a boy !";

		System.out.println( StringUtil.compress( testString01 ) );
		System.out.println( StringUtil.compress( testString02 ) );
		System.out.println( StringUtil.decompress( StringUtil.compress( testString01 ) ) );
		System.out.println( StringUtil.decompress( StringUtil.compress( testString02 ) ) );

		assertEquals( testString01, StringUtil.decompress( StringUtil.compress( testString01 ) ) );
		assertEquals( testString02, StringUtil.decompress( StringUtil.compress( testString02 ) ) );

	}

	@Test
	public void urlEncodeAndDecode() {

		System.out.println( StringUtil.encodeUrl( "http://unikys.tistory.com/195?menuId=DP13+DP14" ) );

	}

	@Test
	public void likeTest() {

		Assert.assertTrue( StringUtil.like( "ABCD", "_B%" ) );
		Assert.assertFalse( StringUtil.like( "ABCD", "_F%" ) );
		Assert.assertTrue( StringUtil.like( "[A]B_D", "[_]%" ) );
		Assert.assertFalse( StringUtil.like( "A\\ACD", "A\\%" ) );
		Assert.assertTrue( StringUtil.like( "A%ACD", "A\\%%" ) );
		Assert.assertTrue( StringUtil.like( "AB_D", "%\\_%" ) );
		Assert.assertTrue( StringUtil.like( "AB_D", "%_%_%" ) );

	}

	@Test
	public void orOperation() {

		NLogger.debug( Integer.toHexString( 0x00 | 0x01 ) );

		NLogger.debug( Integer.toHexString( 0x10 | 0x01 ) );
		NLogger.debug( Integer.toHexString( 0x10 | 0x02 ) );
		NLogger.debug( Integer.toHexString( ( 0x10 | 0x01 ) & 1 ) );

	}

	@Test
	public void capturePattern() {

		String value = "jdbc:sqlite:./target/test-classes/localDb/#{Merong}#{Nayasis}SimpleLauncherHelloWorld.db";

		List<String> capturedList = StringUtil.capturePatterns( value, "#\\{(.+?)\\}" );

		assertEquals( 2, capturedList.size() );
		assertEquals( Arrays.asList( "Merong", "Nayasis" ), capturedList );

	}

	@Test
	public void join() {

		List<String> testArray = Arrays.asList( "1", "2", null, "3" );

		String result = StringUtil.join( testArray, ";" );

		assertEquals("1;2;3", result);

	}

	@Test
	public void tokenize() {

		assertEquals( "[I , m ,  boy || you , re ,  girl]", StringUtil.tokenize( "I am a boy || you are a girl", "a" ).toString() );
		assertEquals( "[I am a boy ,  you are a girl]", StringUtil.tokenize( "I am a boy || you are a girl", "||" ).toString() );
		assertEquals( "[I am a boy || you are a girl]", StringUtil.tokenize( "I am a boy || you are a girl", "" ).toString() );
		assertEquals( "[I am a boy || you are a girl]", StringUtil.tokenize( "I am a boy || you are a girl", "ZZ" ).toString() );

		NLogger.debug( StringUtil.tokenize( "I am a boy || you are a girl", " " ).toString() );

	}

	@Test
	public void regextRemoveEnter() {

		String test =
				"\n" +
				"\n" +
				"\n" +
				"  <col1 id=\"c1\">값1</col1>\n" +
				"  <col2 id=\"c2\" val=\"val2\">값2</col2>\n" +
				"\n" +
				"\n";

		test = test
				.replaceFirst( "^\n*", "" )
				.replaceFirst( "\n*$", "" )
				;

		NLogger.debug( test );

	}

	@Test
	public void rpad() {

		String pattern = "|{}|";

		int length = 50;

		NLogger.debug( pattern, StringUtil.rpad("1671287205674218853", length, ' ') ) ;
		NLogger.debug( pattern, StringUtil.rpad("[199.0, 392.0, 120.0, 70.0]", length, ' ') ) ;
		NLogger.debug( pattern, StringUtil.rpad("", length, ' ') ) ;

	}

	@Test
	public void sr() {

		NLogger.debug( StringUtil.trim( "aaaa" ).replaceFirst( "^#\\{", "" ).replaceFirst( "\\}$", "" ) );

	}

	@Test
	public void isTrue() {

		assertEquals( StringUtil.isTrue( "true" ), true );
		assertEquals( StringUtil.isTrue( "trUe" ), true );
		assertEquals( StringUtil.isTrue( "t" ), true );
		assertEquals( StringUtil.isTrue( "T" ), true );
		assertEquals( StringUtil.isTrue( "y" ), true );
		assertEquals( StringUtil.isTrue( "Y" ), true );
		assertEquals( StringUtil.isTrue( "yes" ), true );
		assertEquals( StringUtil.isTrue( "yEs" ), true );

		assertEquals( StringUtil.isTrue( "false" ), false );
		assertEquals( StringUtil.isTrue( "n" ), false );
		assertEquals( StringUtil.isTrue( "No" ), false );
		assertEquals( StringUtil.isTrue( "another" ), false );

	}

	@Test
	public void regTest() {

		System.out.println( "#{name.value[1]}".replaceAll( "#\\{.+?(\\..+?)?\\}", String.format("#{%s$1}", Const.db.PARAMETER_SINGLE) ) );

	}

	@Test
	public void split() {

		String val = "DP01+DP02+^DP03|DP40";

		assertEquals( StringUtil.split( val, "(\\+(\\^)?|\\|)", true ).toString(), "[DP01, +, DP02, +^, DP03, |, DP40]"  );

		NLogger.debug( StringUtil.split( " ", "," ) );

	}

	@Test
	public void camel() {

		assertEquals( "lndPlus19Yn", StringUtil.toCamel( "lnd_plus19_yn" ) );
		assertEquals( "lnd_plus19_yn", StringUtil.toUncamel( "lndPlus19Yn" ) );

	}

	@Test
	public void xss() {

		String word01 = "<script>";
		String word02 = "&lt;script&gt;";
		String word03 = "<script>aaa(\"aaa\",'b'){}</script>";

		assertEquals( word02, StringUtil.clearXss( word01 ) );
		assertEquals( word01, StringUtil.unclearXss( word02 ) );
		assertEquals( word03, StringUtil.unclearXss( word03 ) );

	}

	@Test
	public void mask() {

		String word = "01031155023";

		assertEquals( "", StringUtil.mask( "", word ) );
		assertEquals( "010_3115_5023", StringUtil.mask( "***_****_****", word ) );
		assertEquals( "010_3115_502", StringUtil.mask( "***_****_***", word ) );
		assertEquals( "*010_3115_502", StringUtil.mask( "\\****_****_***", word ) );
		assertEquals( "010_3115_502*", StringUtil.mask( "***_****_***\\*", word ) );
		assertEquals( "010_3115_502", StringUtil.mask( "***_****_***\\", word ) );

	}

}
