package org.nybatis.core.validation;

import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.model.NDate;
import org.nybatis.core.util.Types;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validator to check value's validation
 *
 * @author nayasis@gmail.com
 */
public class Validator {

    /**
     * check whether value is valid date format.
     *
     * @param value value (format is supposed to 'YYYY-MM-DD')
     * @return true value is valid date format.
     */
    public static boolean isDate( String value ) {
        return isDate( value, null );
    }

    /**
     * check whether value is valid date format.
     *
     * @param value  date text
     * @param format date format (ex: YYYY-MM-DD HH:MI:SS)
     * @return true value is valid date format.
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
     * check whether value is null.
     * @param value check value
     * @return true if value is null.
     */
    public static boolean isNull( Object value ) {
        return value == null;
    }

    /**
     * check whether value is not null.
     * @param value check value
     * @return true if value is not null.
     */
    public static boolean isNotNull( Object value ) {
        return ! isNull( value );
    }

    /**
     * check whether value is null or empty or consists with only spaces.
     * @param value check value
     * @return true if value is null or empty or consists with only spaces.
     */
    public static boolean isBlank( String value ) {
    	return value == null || value.length() == 0 || value.trim().length() == 0;
    }

    /**
     * check whether value is not null nor not empty or not consists with only spaces.
     * @param value check value
     * @return true if value is not null nor not empty or not consists with only spaces.
     */
    public static boolean isNotBlank( String value ) {
        return ! isBlank( value );
    }

    /**
     * check whether value is null or empty.<br>
     *
     * Condition to judge empty is different from type of instance.
     * <pre>
     *     1. String, StringBuffer, StringBuilder : empty
     *     2. Map, Collection : empty
     *     3. Array : size is zero.
     *     4. Any
     * </pre>
     * @param value check value
     * @return true if value is null or empty.
     */
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
        } else if( Types.isArrayOrList( value ) ) {
            return Array.getLength( value ) == 0;
        }

        return false;

    }

    /**
     * check whether value is not null nor not empty.<br>
     *
     * Condition to judge empty is different from type of instance.
     * <pre>
     *     1. String, StringBuffer, StringBuilder : empty
     *     2. Map, Collection : empty
     *     3. Array : size is zero.
     *     4. Any
     * </pre>
     * @param value check value
     * @return true if value is not null nor not empty.
     */
    public static boolean isNotEmpty( Object value ) {
        return ! isEmpty( value );
    }

    /**
     * check if value is String or StringBuffer or StringBuilder
     *
     * @param value check value
     * @return  true if value is included in [ String, StringBuffer, StringBuilder ]
     */
    public static boolean isString( Object value ) {
        return Types.isString( value );
    }

    /**
     * check if value is not String nor StringBuffer nor StringBuilder
     *
     * @param value check value
     * @return  true if value is not included in [ String, StringBuffer, StringBuilder ]
     */
    public static boolean isNotString( Object value ) {
        return ! Types.isString( value );
    }

    /**
     * check whether value is matched with regular expression pattern.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if value is matched with regular expression pattern.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isMatched( String value, String pattern ) {
        return value != null && pattern != null && Pattern.matches( pattern, value );
    }

    /**
     * check whether value is not matched with regular expression pattern.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if value is not matched with regular expression pattern.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotMatched( String value, String pattern ) {
        return ! isMatched( value, pattern );
    }

    /**
     * check whether regular expression pattern is found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if regular expression pattern is found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isFound( String value, String pattern ) {
    	if( value == null || pattern == null ) return false;
    	Pattern regexp  = Pattern.compile( pattern, Pattern.MULTILINE | Pattern.DOTALL );
    	Matcher matcher = regexp.matcher( value );
    	return matcher.find();
    }

    /**
     * check whether regular expression pattern is not found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * <pre>
     * <b>Prefix category</b>
     *   - (?i) : CaseInsensitive
     *   - (?x) : whitespace is ignored, and embedded comments starting with # are ignored
     *   - (?m) : multiline mode
     *   - (?s) : dotall mode
     *   -        multi-line text (contains '\n' character) search needs option '(?ms)'
     *   - (?d) : Unix lines mode (only the '\n' line terminator is recognized in the behavior of ., ^, and $.)
     * </pre>
     * @return true if regular expression pattern is not found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotFound( String value, String pattern ) {
        return ! isFound( value, pattern );
    }

    /**
     * check whether regular expression pattern is found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * @param flags   Match flags, a bit mask that may include
     *         {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *         {@link Pattern#UNICODE_CASE},     {@link Pattern#CANON_EQ},  {@link Pattern#UNIX_LINES},
     *         {@link Pattern#LITERAL},          {@link Pattern#COMMENTS},  {@link Pattern#UNICODE_CHARACTER_CLASS}
     * @return true if regular expression pattern is found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isFound( String value, String pattern, int flags ) {
    	if( value == null || pattern == null ) return false;
    	Pattern regexp  = Pattern.compile( pattern, flags );
    	Matcher matcher = regexp.matcher( value );
    	return matcher.find();
    }

    /**
     * check whether regular expression pattern is not found in value.
     *
     * @param value   check value
     * @param pattern regular expression pattern
     * @param flags   Match flags, a bit mask that may include
     *         {@link Pattern#CASE_INSENSITIVE}, {@link Pattern#MULTILINE}, {@link Pattern#DOTALL},
     *         {@link Pattern#UNICODE_CASE},     {@link Pattern#CANON_EQ},  {@link Pattern#UNIX_LINES},
     *         {@link Pattern#LITERAL},          {@link Pattern#COMMENTS},  {@link Pattern#UNICODE_CHARACTER_CLASS}
     * @return true if regular expression pattern is not found in value.
     * @see <a href="http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html">regex pattern</a>
     */
    public static boolean isNotFound( String value, String pattern, int flags ) {
        return ! isFound( value, pattern, flags );
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
            return true;
        } catch( Exception e ) {
            return false;
        }
    }

    /**
     * check value's class is Numeric class <br>
     *   - int, Integer, long, Long, short, Short, float, Float, double, Double, byte, Byte, BigDecimal, BigInteger
     *
     * @param value object to check.
     * @return true if value's class is Numeric class
     */
    public static boolean isNumericClass( Object value ) {
    	return value != null && isNumericClass( value.getClass() );
    }

    /**
     * check class is Numeric class <br>
     *   - int, Integer, long, Long, short, Short, float, Float, double, Double, byte, Byte, BigDecimal, BigInteger
     *
     * @param klass class to check.
     * @return true if it is Numeric class
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
     * check whether value's pattern is email or not
     *
     * @param value check value
     * @return true if value's pattern is email
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
     * @param value                 value to examine not null or not empty.
     * @param replaceValue          other value to examine not null.
     * @param anotherReplaceValue   another values to examine not null.
     * @param <T> 			        expected class of return
     * @return not null value from begin with.
     */
    public static <T> T nvl( T value, T replaceValue, T... anotherReplaceValue ) {
        if( isNotEmpty(value) )       return value;
        if( isNotNull(replaceValue) ) return replaceValue;
        for( T val : anotherReplaceValue ) {
            if( isNotNull( val ) ) return val;
        }
        return null;
    }

}
