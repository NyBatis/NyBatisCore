package org.nybatis.core.util;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.xml.bind.DatatypeConverter;
import org.nybatis.core.exception.unchecked.ClassNotExistException;
import org.nybatis.core.exception.unchecked.EncodingException;
import org.nybatis.core.exception.unchecked.UncheckedIOException;


/**
 * String Hadling Utility
 *
 * @author nayasis@gmail.com
 */
public class StringUtil {

	/**
	 * Full-width 문자의 Console 출력공간 크기를 세팅한다.
	 *
	 * @param width 출력할 크기
	 */
	public static void setCjkCharacterWidth( int width ) {
		CharacterUtil.setCjkCharacterWidth( width );
	}

	/**
	 * 문자열의 길이를 구한다. <br>
	 *
	 * CJK 문자일 경우 1글자의 크기를 {@link StringUtil#setCjkCharacterWidth(int)} 로 세팅한 width 로 변환해 구한다.
	 *
	 * @param value 검사할 문자열
	 * @return 문자열의 길이
	 */
	public static int getCjkLength( Object value ) {

		if( value == null ) return 0;

		String val = value.toString();

		if( CharacterUtil.getFullwidthCharacterWidth() == 1 ) return val.length();

		int result = 0;

		for( int i = 0, iCnt = val.length(); i < iCnt; i++ ) {
			result += CharacterUtil.getLength( val.charAt( i ) );
		}

		return result;

	}

	/**
	 * CJK 문자크기 특성을 반영하여 lpad 처리된 문자열을 구한다.
	 *
     * @param value    	조작할 문자열
     * @param length	결과 문자열 길이
     * @param padChar	PADDING 문자
     * @return String Padding 된 문자열
	 */
	public static String lpadCJK( Object value, int length, char padChar ) {

		int adjustLength = ( CharacterUtil.getFullwidthCharacterWidth() == 1 || value == null )	? length
				: value.toString().length() + ( length - getCjkLength( value ) );

		return lpad( value, adjustLength, padChar );

	}

	/**
	 * CJK 문자크기 특성을 반영하여 rpad 처리된 문자열을 구한다.
	 *
     * @param value    	조작할 문자열
     * @param length	결과 문자열 길이
     * @param padChar	PADDING 문자
     * @return String Padding 된 문자열
	 */
	public static String rpadCJK( Object value, int length, char padChar ) {
		int adjustLength = ( CharacterUtil.getFullwidthCharacterWidth() == 1 || value == null )	? length
				: value.toString().length() + ( length - getCjkLength( value ) );

		return rpad( value, adjustLength, padChar );
	}

	/**
	 * 문자열이 비어있는지 여부를 확인한다.
	 *
	 * <pre>
	 * 1. 문자열이 null 이면 true
	 * 2. 문자열이 "" 이면 true
	 * </pre>
	 *
	 * @param value 검사할 문자열
	 * @return 비어있는지 여부
	 */
	public static boolean isEmpty( Object value ) {
		return value == null || value.toString().length() == 0;
	}

	public static boolean isBlank( Object value ) {
		if( value == null ) return true;
		String val = value.toString();
		return val.length() == 0 || val.trim().length() == 0;
	}

	public static String trim( Object string ) {
		return nvl( string ).trim();
	}

	/**
	 * check if there are equal without null difference
	 *
	 * @param value		value to check differenc
	 * @param another	another value to check difference
	 * @return true if equals
	 */
	public static boolean isEqual( Object value, Object another ) {
		return StringUtil.nvl(value).equals( StringUtil.nvl(another) );
	}

	/**
	 * check if there are not equal without null difference
	 *
	 * @param value		value to check differenc
	 * @param another	another value to check difference
	 * @return true if not equals
	 */
	public static boolean isNotEqual( Object value, Object another ) {
		return ! isEqual( value, another );
	}

	/**
	 * 문자열이 비어있지 않은지 여부를 확인한다.
	 *
	 * <pre>
	 * 1. 문자열이 null 이면 false
	 * 2. 문자열이 "" 이면 false
	 * </pre>
	 *
	 * @param value 검사할 문자열
	 * @return 비어있지 않은지 여부
	 */
	public static boolean isNotEmpty( Object value ) {
		return ! isEmpty( value );
	}

	public static boolean isNotBlank( Object value ) {
		return ! isBlank( value );
	}

	/**
	 * Bind parameter at mark '#{...}' in text<br>
	 *
	 * <pre>
	 * NMap parameter = new NMap( "{'name':'abc', 'age':'2'}" );
	 *
	 * StringUtil.bindParam( "1", parameter )               → 1
	 * StringUtil.bindParam( "#{name}", parameter )         → abc
	 * StringUtil.bindParam( "PRE #{age} POST", parameter ) → PRE 2 POST
	 * </pre>
	 *
	 * @param value value text. if value has '#{..}', it is replaced by value of parameter.
	 *                 key of value is inner text of '#{..}' pattern.
	 * @param parameter parameter contains key and value
	 * @return parameter binded string
	 */
	public static String bindParam( Object value, Map parameter ) {

		Pattern pattern = Pattern.compile( "#\\{(.+?)\\}" );

		Matcher matcher = pattern.matcher( nvl(value) );

		StringBuffer sb = new StringBuffer();

		while( matcher.find() ) {

			String key = matcher.group().replaceAll( "#\\{(.+?)\\}", "$1" );
			String val = StringUtil.nvl( parameter.get( key ) );

			matcher.appendReplacement( sb, val );
		}

		matcher.appendTail( sb );

		return sb.toString();

	}

	/**
	 * 문자열을 주어진 포맷에 맞춰 출력한다.
	 *
	 * 포맷은 '{}' 문자를 치환가능문자로 사용한다.
	 *
	 * <pre>
	 * {@link StringUtil#format}( "{}는 사람입니다.", "정화종" ); → "정화종은 사람입니다."
	 * {@link StringUtil#format}( "{}는 사람입니다.", "ABC"    ); → "ABC는 사람입니다."
	 * {@link StringUtil#format}( "{}는 사람입니다." );           → "는 사람입니다."
	 * {@link StringUtil#format}( "사람입니다." );               → "사람입니다."
	 * </pre>
	 *
	 * @param format 문자열 포맷
	 * @param param  '{}' 문자를 치환할 파라미터
	 * @return 포맷에 맞는 문자열
	 */
	public static String format( Object format, Object... param ) {

		if( isEmpty( format ) ) return "";

		if( ! (format instanceof CharSequence) ) {
			return format.toString();
		}

		String string = nvl( format );

		int paramSize   = param.length;
		int paramCursor = 0;

		StringBuilder result = new StringBuilder();

		int lastIndex  = string.length() - 1;

		boolean checkLastIndex = false;

		for( int i = 0; i < lastIndex; i++ ) {

			char currCh = string.charAt( i );
			char nextCh = string.charAt( i + 1 );

			if( currCh == '{' && nextCh == '}' ) {

				i++;

				if( paramSize == 0 || paramCursor == paramSize ) continue;

				String paramToAppend = ( param[ paramCursor ] == null ) ? "null" : param[ paramCursor ].toString();

				paramCursor++;

				result.append( paramToAppend );

				if( i < lastIndex ) {

					nextCh = string.charAt( i + 1 );

					if( hasHangulJosa( paramToAppend, nextCh ) ) {

						i++;

						switch( nextCh ) {

							case '은' : case '는' :
								result.append( hasHangulJongsung( paramToAppend ) ? '은' : '는' );
								break;
							case '이' : case '가' :
								result.append( hasHangulJongsung( paramToAppend ) ? '이' : '가' );
								break;
							case '을' : case '를' :
								result.append( hasHangulJongsung( paramToAppend ) ? '을' : '를' );
								break;

						}

					}

				} else if( i == lastIndex ) {
					checkLastIndex = true;
				}

			} else {
				result.append( currCh );
			}

		}

		if( ! checkLastIndex ) {
			result.append( string.charAt(lastIndex) );
		}

		return result.toString();

	}

    /**
     * 문자열의 마지막 글자가 한글 받침을 가지고 있는지 여부를 확인한다.
     *
     * <pre>
     * {@link StringUtil}{@link #hasHangulJongsung(String)}
     *
     * </pre>
     *
     * @param string
     * @return
     */
    private static boolean hasHangulJongsung( String string ) {
    	return isNotEmpty( string ) && CharacterUtil.hasHangulJongsung( string.charAt( string.length() - 1 ) );
    }

    private static boolean hasHangulJosa( String string, char c ) {

		if( isEmpty(string) ) return false;

    	if( ! CharacterUtil.isKorean( string.charAt( string.length() - 1 ) ) ) return false;

    	switch( c ) {
    		case '은' : case '는' : case '이' : case '가' : case '을' : case '를' :
    			return true;
    	}

    	return false;

    }

    /**
     *
     * 입력한 문자열 앞뒤에  특정문자를 Left Padding한 문자열을 반환한다.
     *
     * <pre>
     *
     * [사용 예제]
     *
     * {@link StringUtil#lpad}("AAAAAA", 'Z', 10) ) → ZZZZAAAAAA
     *
     * </pre>
     *
     * @param value    	조작할 문자열
     * @param length	결과 문자열 길이
     * @param padChar	PADDING 문자
     * @return String Padding 된 문자열
     */
    public static String lpad( Object value, int length, char padChar ) {

        String text        = nvl( value );
        int    textCharCnt = text.length();
        int    index       = Math.max( length - textCharCnt, 0 );

        char[] result = new char[ length ];

        for( int i = 0; i < index; i++ ) {
            result[ i ] = padChar;
        }

        for( int i = 0, iCnt = Math.min(length, textCharCnt); i < iCnt; i++ ) {

            result[ index + i ] = text.charAt( i );

        }

        return new String( result );

    }

    /**
     *
     * 입력한 문자열 앞뒤에  특정문자를 Right Pading한 문자열을 반환한다.
     *
     * <pre>
     *
     * [사용 예제]
     *
     * {@link StringUtil#rpad}("AAAAAA", 'Z', 10) ) → AAAAAAZZZZ
     *
     * </pre>
     *
     * @param value    	조작할 문자열
     * @param length	결과 문자열 길이
     * @param padChar	PADDING 문자
     * @return String Padding 된 문자열
     */
    public static String rpad( Object value, int length, char padChar ) {

        String text  = nvl( value );
        int    index = Math.min( length, text.length() );

        char[] result = new char[ length ];

        for( int i = 0; i < index; i++ ) {
            result[ i ] = text.charAt( i );
        }

        for( int i = index; i < length; i++ ) {
            result[ i ] = padChar;
        }

        return new String( result );

    }


    /**
     * 입력값이 null일 경우 공백문자로 치환받는다.
     *
     * @param val 입력값
     * @return NVL 문자열
     */
    public static String nvl( Object val ) {
    	return ( val == null ) ? "" : val.toString();
    }

    /**
     * 입력값이 null일 경우, 지정한 문자로 치환받는다.
     *
     * @param val 입력값
     * @param nvlValue 지정한 문자
     * @return NVL 문자열
     */
    public static String nvl( Object val, Object nvlValue ) {
    	return ( val == null ) ? nvl( nvlValue ) : val.toString();
    }

    /**
	 *
	 * convert text to camel case
	 *
     * <pre>
     * String text = DataConverter.getCamel( "unicode_text" );
     * System.out.println( text ); → "unicodeText""
     * </pre>
     * @param text   text to convert
     * @return camel cased text
     */
    public static String toCamel( String text ) {

    	if( isEmpty(text) ) return "";

    	text = text.toLowerCase();
        Pattern pattern = Pattern.compile( "(_[a-zA-Z0-9])" );
        Matcher matcher = pattern.matcher( text );
        StringBuffer sb = new StringBuffer();

        while( matcher.find() ) {
            String r = matcher.group().substring( 1 );
            if( matcher.start() != 0 ) r = r.toUpperCase();
            matcher.appendReplacement( sb, r );
        }

        matcher.appendTail( sb );

        return sb.toString();

    }

    /**
     * convert camel cased text to underscored text
	 *
     * <pre>
     * String text = StringUtil.toUncamel( "unicodeText" );
     * System.out.println( text ); → "unicode_text"
     * </pre>
     * @param text     text to convert
     * @return underscored text
     */
    public static String toUncamel( String text ) {

        Pattern pattern = Pattern.compile( "([A-Z])" );
        Matcher matcher = pattern.matcher( text );
        StringBuffer sb = new StringBuffer();

        while( matcher.find() ) {
            if( matcher.start() == 0 ) continue;
            String r = matcher.group();
            matcher.appendReplacement( sb, "_" + r.toLowerCase() );
        }

        matcher.appendTail( sb );
        return sb.toString();

    }

    /**
     * <pre>
     * 문자열을 escape 한다.
     *
     * json 구조 등이 깨지지 않게 하는데 사용된다.
     * </pre>
     *
     * @param value text
     * @return 특수문자가 제거된 텍스트
     */
    public static String escape( Object value ) {

    	if( isEmpty(value) ) return "";

    	StringBuilder sb = new StringBuilder();

        for( char ch : value.toString().toCharArray() ) {

            switch( ch ) {

                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                case '/':  sb.append("\\/");  break;

                default:
                    if( ch >= '\u0000' && ch <= '\u001F' ) {
                    	sb.append("\\u").append( lpad(Integer.toHexString(ch), 4, '0').toUpperCase() );
                    } else {
                    	sb.append(ch);
                    }
            }

        }

        return sb.toString();

    }

    /**
     * <pre>
     * 1. \\u**** 형식의 문자열을 unicode 문자열로 변경한다.
     *
     * StringUtils.unescape( "\uacb0\uc7ac\uae08\uc561\uc624\ub958" );
     *
     * → "결제금액오류"
     *
     * 2. \\n 형식으로 표현된 Sequence 문자열을 원래 Sequence 문자로 변경한다.
     *
     * StringUtil.unescape( "\\n\\n" ) → "\n\n"
     * </pre>
     *
     * @param param 문자열
     * @return 유니코드 문자열
     */
    public static String unescape( Object param ) {

		if( isEmpty(param) ) return "";

        String  srcTxt  = param.toString();
        Pattern pattern = Pattern.compile( "\\\\(b|t|n|f|r|\\\"|\\\'|\\\\)|([u|U][0-9a-fA-F]{4})" );
        Matcher matcher = pattern.matcher( srcTxt );

        StringBuffer sb = new StringBuffer( srcTxt.length() );

        while( matcher.find() ) {

        	String replacedChar = null;

        	if( matcher.start(1) >= 0 ) {
        		replacedChar = getUnescapedSequence( matcher.group(1) );

        	} else if( matcher.start(2) >= 0 ) {
        		replacedChar = getUnescapedUnicodeChar( matcher.group(2) );
        	}

            matcher.appendReplacement( sb, Matcher.quoteReplacement(replacedChar) );

        }

        matcher.appendTail( sb );

        return sb.toString();

    }

    private static String getUnescapedUnicodeChar( String escapedString ) {
    	try {
    		String hex = escapedString.substring( 2 );
    		int hexNumber = Integer.parseInt( hex, 16 );
    		return Character.toString( (char) hexNumber );
    	} catch( StringIndexOutOfBoundsException e ) {
    		throw new StringIndexOutOfBoundsException( String.format( "Char to unescape unicode : [%s]", escapedString ) );
    	}
    }

    private static String getUnescapedSequence( String escapedString ) {
    	switch( escapedString.charAt(0) ) {
    		case 'b' : return "\b";
    		case 't' : return "\t";
    		case 'n' : return "\n";
    		case 'f' : return "\f";
    		case 'r' : return "\r";
    	}
    	return escapedString;
    }

    /**
     * 리스트 객체를 문자열로 만든다.
     *
     * <pre>
     * List&lt;String&gt; list = Arrays.asList( "a", "b", null, "c" );
     *
     * StringUtil.join( list, "," );
     *
     * → a,b,c
     * </pre>
     *
     * @param list 리스트 객체
     * @param concator 엘리먼트 사이를 연결시킬 구분 문자열
     * @return joined text
     */
    public static String join( Collection<?> list, String concator ) {

    	if( list == null || list.size() == 0 ) return "";

    	StringBuilder sb    = new StringBuilder();
    	int           index = list.size();

    	for( Object e : list ) {
    		index--; if( e == null ) continue;
    		sb.append( e.toString() );
    		if( index > 0 ) sb.append( concator );
    	}
    	return sb.toString();

    }

	/**
	 * Split string around matches of the given <a href="../util/regex/Pattern.html#sum">regular expression</a>.
	 *
	 * @param value				string value
	 * @param regexDelimeter	the delimiting regular expression
	 * @return	string array comupted by splitting around matches of the given regular expression
	 */
	public static List<String> split( Object value, String regexDelimeter ) {
		return split( value, regexDelimeter, false );
	}

	/**
	 * Split string around matches of the given <a href="../util/regex/Pattern.html#sum">regular expression</a>.
	 *
	 * @param value				string value
	 * @param regexDelimeter	the delimiting regular expression
	 * @param returnDelimeter	include delimeter in result
	 * @return	string array comupted by splitting around matches of the given regular expression
	 */
	public static List<String> split( Object value, String regexDelimeter, boolean returnDelimeter ) {

		List<String> result = new ArrayList<>();

		if( isEmpty(value) ) return result;

		String val = trim( value );

		if( isEmpty(regexDelimeter) ) {
			result.add( val );
			return result;
		}

		Pattern pattern = Pattern.compile( regexDelimeter );
		Matcher matcher = pattern.matcher( val );

		int caret = 0;

		while( matcher.find() ) {

			if( caret != matcher.start() ) {
				result.add( val.substring( caret, matcher.start() ).trim() );
			}

			if( returnDelimeter ) {
				result.add( matcher.group() );
			}

			caret = matcher.end();

		}

		if( caret != val.length() ) {
			result.add( val.substring( caret ).trim() );
		}

		return result;

	}


	/**
     * tokenize text by seperator
     *
     * @param value     value to tokenize
     * @param separator sepertator to tokenize
     * @return tokenized word list
     */
    public static List<String> tokenize( Object value, String separator ) {

    	List<String> result = new ArrayList<>();

    	if( isEmpty(value) ) return result;

    	String workVal = value.toString();

    	if( isEmpty(separator) ) {
			result.add( workVal );
			return result;
		}

    	int fromIndex = 0, toIndex = 0, separatorLength = separator.length();

    	List<int[]> indexes = new ArrayList<>();

    	while( true ) {
    		toIndex = workVal.indexOf( separator, fromIndex );
    		if( toIndex < 0 ) {
    			indexes.add( new int[] {fromIndex, workVal.length()} );
    			break;
    		}
    		indexes.add( new int[] {fromIndex, toIndex} );
    		fromIndex = toIndex + separatorLength;
    	}

    	for( int[] index : indexes ) {
    		result.add( workVal.substring( index[0], index[1] ) );
    	}

    	return result;

    }

    /**
     * 전화번호에 지역번호, 국번, 번호를 구분해 -를 붙여서 return 해주는 메소드
     *
     * @param phoneNumber phone number
	 * @return phone number delimeted with '-'
     */
	public static String getPhoneNumber( Object phoneNumber ) {
    	if( isEmpty(phoneNumber) ) return "";
    	return nvl(phoneNumber).replaceAll( "\\D*(02|\\d{3})\\D*(\\d{3,4})\\D*(\\d{4})", "$1-$2-$3" );
    }

    /**
     * change word's first character to upper case
     *
     * @param text text to change
     * @return uncapitalized text
     */
    public static String uncapitalize( Object text ) {
    	if( isEmpty(text) ) return "";
    	char[] array = nvl( text ).toCharArray();
    	array[ 0 ] = Character.toLowerCase( array[0] );
    	return new String( array );
    }

    /**
     * change word's first character to lower case
     *
     * @param text text to change
     * @return capitalized text
     */
    public static String capitalize( Object text ) {
    	if( isEmpty(text) ) return "";
    	char[] array = nvl( text ).toCharArray();
    	array[ 0 ] = Character.toUpperCase( array[0] );
    	return new String( array );
    }

    /**
     * Compress multiple space to single space
     *
     * <pre>
     * {@link StringUtil#compressSpace}( "A     B" ); → "A B"
     * {@link StringUtil#compressSpace}( "A    B" );  → "A B"
     * {@link StringUtil#compressSpace}( "A   B" );   → "A B"
     * {@link StringUtil#compressSpace}( "A  B" );    → "A B"
     * {@link StringUtil#compressSpace}( "A B" );     → "A B"
     * </pre>
     *
     * @param value text value
     * @return text with space compressed
     */
    public static String compressSpace( Object value ) {
    	if( isEmpty(value) ) return "";
    	return value.toString().replaceAll( "  +", " " ).trim();
    }

	/**
	 * Compress multiple space or enter to single space
	 *
	 * <pre>
	 * {@link StringUtil#compressSpaceOrEnter}( "A     B" );   → "A B"
	 * {@link StringUtil#compressSpaceOrEnter}( "A B" );       → "A B"
	 * {@link StringUtil#compressSpaceOrEnter}( "A \n\n B" );  → "A B"
	 * </pre>
	 *
	 * @param value text value
	 * @return text with space or enter compressed
	 */
	public static String compressSpaceOrEnter( Object value ) {
		if( isEmpty(value) ) return "";
		return value.toString().replaceAll( "[ \n\r]+", " " ).trim();
	}

	/**
	 * Compress multiple enter to single enter
	 *
	 * <pre>
	 * {@link StringUtil#compressEnter}( "A\n\n\nB" );   → "A\nB"
	 * </pre>
	 *
	 * @param value text value
	 * @return text with enter compressed
	 */
	public static String compressEnter( Object value ) {
		if( isEmpty(value) ) return "";
		return value.toString().replaceAll( " *[\n\r]", "\n" ).replaceAll( "[\n\r]+", "\n" );
	}

    /**
     * encode object to text
     *
     * @param value object to encode
     * @return encoded text
     * @throws UncheckedIOException if I/O exception occurs.
     */
    public static String encode( Object value ) {

    	String result;

    	try (
    		ByteArrayOutputStream bos = new ByteArrayOutputStream();
    		ObjectOutputStream    oos = new ObjectOutputStream( bos )
		) {
    		oos.writeObject( value );
    		oos.close();
    		result = DatatypeConverter.printBase64Binary( bos.toByteArray() );

    	} catch ( IOException e ) {
    		throw new UncheckedIOException( e );
		}

        return result;

    }

    /**
     * decode text to object
     *
     * @param value text to decode as object
     * @return decoded object
     * @throws UncheckedIOException if I/O exception occurs.
     * @throws ClassNotExistException if class is not found in class loader.
     */
    public static Object decode( Object value ) {

    	byte o[] = DatatypeConverter.parseBase64Binary( nvl(value) );

        Object vo;

    	try (
    		ByteArrayInputStream bis = new ByteArrayInputStream( o );
    		ObjectInputStream    ois = new ObjectInputStream( bis )
		) {
    		vo = ois.readObject();

    	} catch (IOException e) {
    		throw new UncheckedIOException( e );
		} catch ( ClassNotFoundException e) {
			throw new ClassNotExistException( e );
		}

        return  vo;

    }

	/**
	 * encode URL
	 * @param url url to encode
	 * @return encoded URL
	 * @throws EncodingException	if an encoding error occurs.
	 */
    public static String encodeUrl( Object url ) throws EncodingException {
    	try {
			return URLEncoder.encode( nvl(url), "UTF-8" );
    	} catch( UnsupportedEncodingException e ) {
        	throw new EncodingException( e );
        }
    }

	/**
	 * decode URL
	 * @param url url to decode
	 * @return decoded URL
	 * @throws EncodingException	if an decoding error occurs.
	 */
    public static String decodeUrl( Object url ) throws EncodingException {
    	try {
    		return URLDecoder.decode( nvl( url ), "UTF-8" );
    	} catch( UnsupportedEncodingException e ) {
        	throw new EncodingException( e );
        }
    }

    /**
	 * extract number characters from word
	 *
	 * @param string word
	 * @return number characters
	 */
	public static String extractNumber( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^0-9]", "" );
	}

	/**
	 * extracter upper characters from word
	 *
	 * @param string word
	 * @return upper characters
	 */
	public static String extractUpperCharacters( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^A-Z]", "" );
	}

	/**
	 * 문자열에서 소문자만 추출한다.
	 *
	 * @param string 작업할 대상 문자열
	 * @return 소문자만 추출된 문자열
	 */
	public static String extractLowerCharacters( String string ) {
		if( isEmpty( string ) ) return "";
		return string.replaceAll( "[^a-z]", "" );
	}

	/**
	 * compress text
	 *
	 * @param value text value
	 * @return compressed text
	 */
	public static String compress( String value ) {
		if( isEmpty(value) ) return "";
        try(
        	ByteArrayOutputStream out  = new ByteArrayOutputStream();
        	GZIPOutputStream      gzip = new GZIPOutputStream( out )
		) {
        	gzip.write( value.getBytes() );
        	gzip.close();
        	return out.toString( StandardCharsets.ISO_8859_1.toString() );
        } catch( IOException e ) {
        	throw new UncheckedIOException( e );
        }
	}

	/**
	 *  decompress text
	 *
	 * @param value text value
	 * @return decompressed text
	 */
	public static String decompress( String value ) {
		if( isEmpty(value) ) return "";
        try(
        	ByteArrayInputStream input        = new ByteArrayInputStream( value.getBytes( StandardCharsets.ISO_8859_1 ));
        	GZIPInputStream      gzip         = new GZIPInputStream( input );
        	BufferedReader       bufferReader = new BufferedReader( new InputStreamReader( gzip ) )
		) {
        	StringBuilder sb = new StringBuilder();
        	String line;
        	while( (line = bufferReader.readLine()) != null ) {
        		sb.append( line );
        	}
        	return sb.toString();
        } catch( IOException e ) {
        	throw new UncheckedIOException( e );
        }
	}

	/**
	 * <pre>
	 *
	 * 문자열을 LIKE 로 비교한다.
	 *
	 * (DBMS의 Like 검색기능과 동일)
	 *
	 * {@link StringUtil#like}( "ABCDEFG", "%BCD%"   ) → true
	 * {@link StringUtil#like}( "ABCDEFG", "%BCD_F%" ) → true
	 * {@link StringUtil#like}( "AB_DEFG", "AB.DEFG" ) → false
	 *
	 *  </pre>
	 *
	 * @param value   비교할 문자열
	 * @param pattern 비교할 LIKE 패턴 ( "_" : 임의 문자 1개, "%" : 임의 문자열, "\\_" : "_" 문자, "\\%" : "%" 문자 )
	 * @return 비교결과
	 */
	public static boolean like( Object value, String pattern ) {

		pattern = removeRegexpKeyword( pattern );

		StringBuilder newPattern = new StringBuilder();

		for( int i = 0, iCnt = pattern.length() - 1; i <= iCnt; i++ ) {

			char chCurr = pattern.charAt( i );

			switch( chCurr ) {

				case '\\' :

					char ch2ndNext = ( i == iCnt ) ? ' ' : pattern.charAt( i + 1 );

					if( ch2ndNext == '\\' ) {

						char ch3rdNext = ( i == iCnt - 1 ) ? ' ' : pattern.charAt( i + 2 );

						if( ch3rdNext == '_' || ch3rdNext == '%' ) {
							newPattern.append( ch3rdNext );
							i =+ 3;

						} else {
							newPattern.append( chCurr ).append( ch2ndNext );
							i =+ 2;
						}

					} else {
						newPattern.append( chCurr );
					}

					break;

				case '_' : newPattern.append( '.'    ); break;
				case '%' : newPattern.append( ".*?"  ); break;
				default  : newPattern.append( chCurr );

			}

		}

		return Pattern.compile( newPattern.toString(), Pattern.DOTALL ).matcher( nvl(value) ).matches();

	}

	/**
	 * <pre>
	 *
	 * 문자열 패턴이 LIKE 형식이 아닌지 비교한다.
	 *
	 * (DBMS의 Like 검색기능과 동일)
	 *
	 * {@link StringUtil#notLike}( "ABCDEFG", "%BCD%"   ) → false
	 * {@link StringUtil#notLike}( "ABCDEFG", "%BCD_F%" ) → false
	 * {@link StringUtil#notLike}( "AB_DEFG", "AB.DEFG" ) → true
	 *
	 *  </pre>
	 *
	 * @param value   비교할 문자열
	 * @param pattern 비교할 LIKE 패턴 ( "_" : 임의 문자 1개, "%" : 임의 문자열, "\\_" : "_" 문자, "\\%" : "%" 문자 )
	 * @return 비교결과
	 */
	public static boolean notLike( Object value , String pattern ) {
		return ! like( value, pattern );
	}

	/**
	 * 정규식 예약문자 <font style="color:blue">([](){}.*+?$^|#\)</font> 앞에 <font style="color:red">\</font> 문자를 붙여준다.
	 *
	 * @param pattern 변환할 정규식 패턴문자열
	 * @return  변환된 문자열
	 */
	public static String removeRegexpKeyword( String pattern ) {

		pattern = nvl( pattern );

		StringBuilder newPattern = new StringBuilder();

		for( char c : pattern.toCharArray() ) {

			if( "[](){}.*+?$^|#\\".indexOf( c ) != -1 ) {
				newPattern.append( '\\' );
			}

			newPattern.append( c );

		}

		return newPattern.toString();

	}

	/**
	 * extract words matched by regular expression.
	 *
	 * <pre>
	 *
	 *  String pattern = "#\\{(.+?)}";
	 *  List&lt;String&gt; finded = StringUtil.capturePatterns( "/admkr#{AAAA}note#{BBBB}ananan#{AAAA}sss", pattern );
	 *  System.out.println( finded ); -> ['AAAA','BBBB', 'AAAA']
	 *
	 *  ----------------------------------------------------------------
	 *
	 *  StringUtil.capturePatterns( "1.2.3.4", "\\." )   -> []
	 *  StringUtil.capturePatterns( "1.2.3.4", "(\\.)" ) -> ['.', '.', '.']
	 *
	 * </pre>
	 *
	 * @param value   target value to inspect
	 * @param pattern regular expression (only captured pattern (wrapped by (...)) can be extracted)
	 * @return captured words
	 */
	public static List<String> capturePatterns( Object value, String pattern ) {
		Pattern p = ( pattern == null ) ? null : Pattern.compile( pattern );
		return capturePatterns( value, p );
	}

	/**
	 * extract words matched by regular expression.
	 *
	 * <pre>
	 *
	 *  String pattern = "#\\{(.+?)}";
	 *  List&lt;String&gt; finded = StringUtil.capturePatterns( "/admkr#{AAAA}note#{BBBB}ananan#{AAAA}sss", pattern );
	 *  System.out.println( finded ); -> ['AAAA','BBBB', 'AAAA']
	 *
	 *  ----------------------------------------------------------------
	 *
	 *  StringUtil.capturePatterns( "1.2.3.4", "\\." )   -> []
	 *  StringUtil.capturePatterns( "1.2.3.4", "(\\.)" ) -> ['.', '.', '.']
	 *
	 * </pre>
	 *
	 * @param value   target value to inspect
	 * @param pattern regular expression (only captured pattern (wrapped by (...)) can be extracted)
	 * @return captured words
	 */
	public static List<String> capturePatterns( Object value, Pattern pattern ) {
		List<String> result = new ArrayList<>();
		if( isEmpty(value) || isEmpty(pattern) ) return result;
		Matcher matcher = pattern.matcher( value.toString() );
		while( matcher.find() ) {
			for( int i = 1, iCnt = matcher.groupCount(); i <= iCnt; i++ ) {
				result.add( matcher.group(i) );
			}
		}
		return result;
	}

	/**
	 * Converts all of the characters in value to lower case
	 *
	 * @param value value to convert
	 * @return the String, converted to lowercase.
	 */
	public static String toLowerCase( Object value ) {
		return nvl( value ).toLowerCase();
	}

	/**
	 * Converts all of the characters in value to upper case
	 *
	 * @param value value to convert
	 * @return the String, converted to uppercase.
	 */
	public static String toUpperCase( Object value ) {
		return nvl( value ).toUpperCase();
	}

	/**
	 * Return value to Y or N
	 *
	 * @param value value to convert
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>Y</td><td>N</td></tr>
	 *     <tr>
	 *       <td>
     *         <ul>
	 *           <li>y</li>
	 *           <li>yes</li>
	 *           <li>t</li>
	 *           <li>true</li>
     *         </ul>
	 *       </td>
	 *       <td>
	 *         <ul>
	 *           <li>Null or empty</li>
	 *           <li>Not in 'Y' condition</li>
	 *         </ul>
	 *       </td>
	 *     </tr>
	 *   </table>
	 *
	 * @return 'Y' or 'N'
	 */
	public static String toYn( Object value ) {

		if( isEmpty(value) ) return "N";

		if( value instanceof Boolean ) {
			return ((Boolean) value).compareTo( true ) == 0 ? "Y" : "N";

		} else {

			String text = trim( value );

			if( "y".equalsIgnoreCase(text) )    return "Y";
			if( "yes".equalsIgnoreCase(text) )  return "Y";
			if( "t".equalsIgnoreCase(text) )    return "Y";
			if( "true".equalsIgnoreCase(text) ) return "Y";

			return "N";

		}

	}

	/**
	 * Return value to true or false.
	 *
	 * @param value value to convert
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>true</td><td>false</td></tr>
	 *     <tr><td><ul>
	 *         <li>y</li>
	 *         <li>yes</li>
	 *         <li>t</li>
	 *         <li>true</li>
	 *     </ul></td><td><ul>
	 *         <li>Null or empty</li>
	 *         <li>Not in 'Y' condition</li>
	 *     </ul></td></tr>
	 *   </table>
	 * @return true if value is positive
	 */
	public static boolean toBoolean( Object value ) {
		return isTrue( value );
	}

	/**
	 *
	 * Check value is true or false
	 *
	 * @param value value to determine
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>true</td><td>false</td></tr>
	 *     <tr><td><ul>
	 *         <li>y</li>
	 *         <li>yes</li>
	 *         <li>t</li>
	 *         <li>true</li>
	 *     </ul></td><td><ul>
	 *         <li>Null or empty</li>
	 *         <li>Not in 'Y' condition</li>
	 *     </ul></td></tr>
	 *   </table>
	 * @return true if value is positive
	 */
	public static boolean isTrue( Object value ) {
		return "Y".equals( toYn( value ) );
	}

	/**
	 * Check value is not true
	 *
	 * @param value value to determine
	 *   <table summary="rule" border="1" style="border-collapse:collapse; border:1px gray solid;">
	 *     <tr><td>true</td><td>false</td></tr>
	 *     <tr><td><ul>
	 *         <li>y</li>
	 *         <li>yes</li>
	 *         <li>t</li>
	 *         <li>true</li>
	 *     </ul></td><td><ul>
	 *         <li>Null or empty</li>
	 *         <li>Not in 'Y' condition</li>
	 *     </ul></td></tr>
	 *   </table>
	 * @return true if value is negative
	 */
	public static boolean isNotTrue( Object value ) {
		return ! isTrue( value );
	}

	/**
	 * Check a string is equals to other string.
	 *
	 * it is free from NullPointException.
	 *
	 * @param one    string to compare
	 * @param other  other string to compare
	 * @return true if each are equal.
	 */
	public static boolean equals( String one, String other ) {

		if( one == null && other == null ) return true;
		if( one == null && other != null ) return false;
		if( one != null && other == null ) return false;

		return one.equals( other );

	}

	/**
	 * check String value has CJK (Chinese, Japanese, Korean) character
	 * @param value valut to check
	 * @return true if value has CJK character.
	 */
	public static boolean hasCJKCharacter( Object value ) {

		if( isEmpty(value) ) return false;

		for( char ch : value.toString().toCharArray() ) {
			if( CharacterUtil.isCJK(ch) ) return true;
		}

		return false;

	}

	/**
	 * clear XSS in text
	 *
	 * @param value target value
	 * @return escaped string
	 */
	public static String clearXss( Object value ) {

		if( isEmpty(value) ) return "";

		StringBuilder sb = new StringBuilder();

		for( char ch : value.toString().toCharArray() ) {

			switch( ch ) {
				case '<' :  sb.append("&lt;");   break;
				case '>' :  sb.append("&gt;");   break;
				case '"' :  sb.append("&#34;");  break;
				case '\'':  sb.append("&#39;");  break;
				case '(' :  sb.append("&#40;");  break;
				case ')' :  sb.append("&#41;");  break;
				case '{' :  sb.append("&#123;"); break;
				case '}' :  sb.append("&#125;"); break;
				default:
					sb.append( ch );
			}

		}

		return sb.toString();

	}

	/**
	 * unclear XSS in text
	 *
	 * @param value target value
	 * @return unescaped string
	 */
	public static String unclearXss( Object value ) {

		if( isEmpty(value) ) return "";

		StringBuilder sb = new StringBuilder();

		char[] chars = value.toString().toCharArray();

		for( int i = 0, limit = chars.length - 1; i <= limit; i++ ) {

			if( chars[i] != '&' ) {
				sb.append( chars[i] );
				continue;
			}

			String code = String.format( "&%c%c%c%c%c"
				,chars[ Math.min(i + 1,limit) ]
				,chars[ Math.min(i + 2,limit) ]
				,chars[ Math.min(i + 3,limit) ]
				,chars[ Math.min(i + 4,limit) ]
				,chars[ Math.min(i + 5,limit) ]
			);

			if( code.startsWith( "&lt;" ) ) {
				sb.append( '<' ); i+=3;
			} else if ( code.startsWith( "&gt;" ) ) {
				sb.append( '>' ); i+=3;
			} else if ( code.startsWith( "&#34;" ) ) {
				sb.append( '"' ); i+=4;
			} else if ( code.startsWith( "&#39;" ) ) {
				sb.append( '\'' ); i+=4;
			} else if ( code.startsWith( "&#40;" ) ) {
				sb.append( '(' ); i+=4;
			} else if ( code.startsWith( "&#41;" ) ) {
				sb.append( ')' ); i+=4;
			} else if ( code.startsWith( "&#123;" ) ) {
				sb.append( '{' ); i+=5;
			} else if ( code.startsWith( "&#125;" ) ) {
				sb.append( '}' ); i+=5;
			} else {
				sb.append( chars[i] );
			}

		}

		return sb.toString();

	}

	/**
	 * apply mask pattern to word
	 *
	 * <pre>
	 * String word = "01031155023";
	 *
	 * StringUtil.mask( "",                word ) ); -&gt; ""
	 * StringUtil.mask( "***_****_****",   word ) ); -&gt; "010_3115_5023"
	 * StringUtil.mask( "***_****_***",    word ) ); -&gt; "010_3115_502"
	 * StringUtil.mask( "\\****_****_***", word ) ); -&gt; "*010_3115_502"
	 * StringUtil.mask( "***_****_***\\*", word ) ); -&gt; "010_3115_502*"
	 * StringUtil.mask( "***_****_***\\",  word ) ); -&gt; "010_3115_502"
	 * </pre>
	 *
	 * @param maskPattern	mask pattern to apply. only '*' character is substitute with word.
	 *                     if you want to print '*' character itself, set pattern as '\\*'
	 * @param word  word to mask
	 * @return masked text
	 */
	public static String mask( String maskPattern, String word ) {

		if( isEmpty(maskPattern) || isEmpty(word) ) return "";

		StringBuilder sb = new StringBuilder();

		int k = 0;

		int lastIdxMask = maskPattern.length() - 1;
		int lastIdxWord = word.length() - 1;

		for( int i = 0; i <= lastIdxMask; i++ ) {

			char curr = maskPattern.charAt( i );
			char next = ( i == lastIdxMask ) ? '\n' : maskPattern.charAt( i + 1 );

			if( curr == '\\' ) {
				if( i != lastIdxMask ) sb.append( next );
				i++;
				continue;
			}

			if( curr == '*' ) {
				sb.append( word.charAt( k ) );
				k++;
				if( k > lastIdxWord ) break;
			} else {
				sb.append( curr );
			}

		}

		return sb.toString();
	}


	/**
	 * get similarity between 0 and 1.
	 *
	 * 0 is non-matched and 1 is perfect-matched.
	 *
	 * @param source
	 * @param target
     * @return
     */
	public static double similarity( String source, String target ) {

		String longer = nvl(source), shorter = nvl(target);
		if( longer.length() < shorter.length() ) {
			String temp = longer;
			longer = shorter; shorter = temp;
		}
		int longerLength = longer.length();
		if (longerLength == 0) return 1.0;

		return (longerLength - getLavenshteinDistance(longer, shorter)) / (double) longerLength;

	}

	/**
	 * get Levenshtein distance
	 *
	 * @param source
	 * @param target
     * @return
	 * @see http://rosettacode.org/wiki/Levenshtein_distance#Java
	 * @see https://en.wikipedia.org/wiki/Levenshtein_distance
     */
	private static int getLavenshteinDistance( String source, String target ) {

		source = toLowerCase( source );
		target = toLowerCase( target );

		int[] costs = new int[ target.length() + 1 ];

		for (int i = 0; i <= source.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= target.length(); j++) {
				if (i == 0) {
					costs[j] = j;
				} else {
					if (j > 0) {
						int newValue = costs[j-1];
						if (source.charAt(i - 1) != target.charAt(j - 1))
							newValue = Math.min( Math.min(newValue, lastValue), costs[j]) + 1;
						costs[j-1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if ( i > 0 )
				costs[target.length()] = lastValue;
		}
		return costs[target.length()];
	}


}
