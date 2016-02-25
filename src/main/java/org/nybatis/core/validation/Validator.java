package org.nybatis.core.validation;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.model.NDate;
import org.nybatis.core.util.Types;

/**
 * 값의 정합성을 확인하는 클래스
 *
 * @author nayasis
 */
public class Validator {

    /**
     * 날짜가 정상인지를 확인한다
     *
     * @param value 날짜 (YYYY-MM-DD 형식)
     * @return 날짜 정상여부
     */
    public static boolean isDate( String value ) {
        return isDate( value, null );
    }

    /**
     * 날짜가 정상인지를 확인한다.
     *
     * @param value 날짜
     * @param format 날짜를 구성하는 포맷 ((예) YYYY-MM-DD HH:MI:SS)
     * @return 날짜 정상여부
     */
    public static boolean isDate( String value, String format ) {

        try {
            new NDate( value, format );
            return true;
        } catch ( ParseException e ) {
            return false;
        }

    }

    /**
     * 문자열이 NULL인지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isNull( Object value ) {
        return value == null;
    }

    public static boolean isNotNull( Object value ) {
        return ! isNull( value );
    }

    public static boolean isBlank( String value ) {
    	return value == null || value.length() == 0 || value.trim().length() == 0;
    }

    public static boolean isNotBlank( String value ) {
        return ! isBlank( value );
    }

    public static boolean isEmpty( Object value ) {

        if( value == null ) return true;

        if( value instanceof String ) {
            return ( (String) value ).length() == 0;
        } else if( value instanceof StringBuffer ) {
            return ( (StringBuffer) value ).length() == 0;
        } else if( value instanceof StringBuilder ) {
            return ( (StringBuilder) value ).length() == 0;
        } else if( value instanceof Map ) {
            return ( (Map) value ).isEmpty();
        } else if( value instanceof Collection ) {
            return ( (Collection) value ).isEmpty();
        } else if( Types.isArray( value ) ) {
            return ( (Object[]) value).length == 0;
        }

        return false;

    }

    public static boolean isNotEmpty( Object value ) {
        return ! isEmpty( value );
    }

    /**
     * 정규식을 이용해 문자열을 검사한다.
     *
     * @param value   검사할 문자열
     * @param pattern 정규식
     * <pre>
     * (?i) : CaseInsensitive
     * (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     * (?m) : multiline mode
     * (?s) : dotall mode
     *        멀티라인 검색은 (?ms) 을 사용해야 정상적으로 작동한다.
     * (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return 정규식 일치여부
     * @see http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
     */
    public static boolean isMatched( String value, String pattern ) {

        if( value == null || pattern == null ) return false;

        return Pattern.matches( pattern, value );

    }

    /**
     * 정규식에 해당하는 패턴이 문자열 내에 존재하는지 여부를 확인한다.
     *
     * @param value   검사할 문자열
     * @param pattern 정규식
     * @return 패턴 존재여부
     */
    public static boolean isFinded( String value, String pattern ) {

    	if( value == null || pattern == null ) return false;

    	Pattern p = Pattern.compile( pattern, Pattern.MULTILINE | Pattern.DOTALL );

    	Matcher matcher = p.matcher( value );

    	return matcher.find();

    }

    /**
     * 정규식에 해당하는 패턴이 문자열 내에 존재하는지 여부를 확인한다.
     *
     * @param  value   검사할 문자열
     * @param  pattern 정규식
     * @param  flags   Match flags, a bit mask that may include
     *         {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *         {@link Pattern#UNICODE_CASE},     {@link Pattern#CANON_EQ},  {@link Pattern#UNIX_LINES},
     *         {@link Pattern#LITERAL},          {@link Pattern#COMMENTS},  {@link Pattern#UNICODE_CHARACTER_CLASS}
     * @return 패턴 존재여부
     */
    public static boolean isFinded( String value, String pattern, int flags ) {

    	if( value == null || pattern == null ) return false;

    	Pattern p = Pattern.compile( pattern, flags );

    	Matcher matcher = p.matcher( value );

    	return matcher.find();

    }

    /**
     * 문자열이 정수로만 구성되어 있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isFixedNumber( String value ) {
        return isMatched( value, "^[-0-9]+$" );
    }

    /**
     * 문자열이 양의 정수로 구성되어있는지 여부를 확인한다.
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isPositiveFixedNumber( String value ) {
        if( ! isFixedNumber( value ) ) return false;
        return Long.parseLong( value ) >= 0;
    }

    /**
     * 수치자료인지 여부를 확인한다. (소수점을 포함하는 경우에도 체크 가능)
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isNumeric( String value ) {

        try {
            Double.parseDouble( value );
        } catch( Exception e ) {
            return false;
        }

        return true;

    }

    /**
     * value의 class 가 숫자인지 여부를 확인한다.
     *
     * @param value 확인할 value
     * @return 숫자여부
     */
    public static boolean isNumericClass( Object value ) {

    	if( value == null ) return false;
    	return isNumericClass( value.getClass() );

    }

    /**
     * object 의 class 가 숫자인지 여부를 확인한다.
     *
     * @param object 확인할 Instance
     * @return 숫자여부
     */
    public static boolean isNumericClass( Class<?> klass ) {

    	if( klass == null ) return false;

    	return (
			klass == int.class        ||
			klass == Integer.class    ||
			klass == long.class       ||
			klass == Long.class       ||
			klass == short.class      ||
			klass == Short.class      ||
			klass == float.class      ||
			klass == Float.class      ||
			klass == double.class     ||
			klass == Double.class     ||
			klass == byte.class       ||
			klass == Byte.class       ||
			klass == BigDecimal.class ||
			klass == BigInteger.class
    	);

    }


    /**
     * object 의 class 가 Boolean 타입인지 여부를 확인한다.
     *
     * @param object 확인할 Instance
     * @return Boolean 타입 여부
     */
    public static boolean isBooleanClass( Object object ) {

    	if( object == null ) return false;

    	@SuppressWarnings( "rawtypes" )
        Class klass = object.getClass();

    	return ( klass == boolean.class || klass == Boolean.class );

    }

    /**
     * 문자열이 숫자를 가지고 있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean hasNumber( String value ) {
        return ! isMatched( value, "^[^0-9]+$" );
    }

    /**
     * 문자열에 한글을 가지고 있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean hasKorean( String value ) {
        return ! isMatched( value, "^[^ㄱ-ㅎㅏ-ㅣ가-힣]+$" );
    }

    /**
     * 문자열이 한글로만 구성되어있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isKorean( String value ) {
        return isMatched( value, "^[ㄱ-ㅎㅏ-ㅣ가-힣]+$" );
    }

    /**
     * 문자열이 영문자로만 구성되어있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isEnglish( String value ) {
        return isMatched( value, "^[a-zA-Z]+$" );
    }

    /**
     * 문자열에 영문자를 가지고 있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean hasEnglish( String value ) {
        return ! isMatched( value, "^[^a-zA-Z]+$" );
    }

    /**
     * 문자열이 한글과 숫자로만 구성되어 있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isNumberOrKorean( String value ) {
        return isMatched( value, "^[0-9ㄱ-ㅎㅏ-ㅣ가-힣]+$" );
    }

    /**
     * 문자열이 한글과 영문자로만 구성되어 있는지 여부를 확인한다.
     *
     * @param value 검사할 문자열
     * @return 검사결과
     */
    public static boolean isNumberOrEnglish( String value ) {
        return isMatched( value, "^[0-9a-zA-Z]+$" );
    }


    /**
     * 이메일 여부를 확인한다.
     *
     * @param value
     * @return
     */
    public static boolean isEmail( String value ) {
        return isMatched( value, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$" );
    }

    /**
     * Let you replace null (or empty)  with another value.
     *
     * if value is null or empty, examine replaceValue.
     * if replaceValue is null, examine next anotherReplaceValue.
     * if anotherReplaceValue is not null, it is returned as result.
     *
     * @param value                value to examine not null or not empty.
     * @param replaceValue         other value to examine not null.
     * @param anotherReplaceValue  another values to examine not null.
     * @return not null value from begin with.
     */
    public static <T> T nvl( T value, T replaceValue, T... anotherReplaceValue ) {

        if( isNotEmpty(value) )    return value;
        if( isNotNull(replaceValue) ) return replaceValue;

        for( T val : anotherReplaceValue ) {
            if( isNotNull( val ) ) return val;
        }

        return null;

    }

}
