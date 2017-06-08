package org.nybatis.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.deserializer.NDateDeserializer;
import org.nybatis.core.reflection.serializer.simple.SimpleNDateSerializer;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * represents a specific instant in time with millisecond precision
 *
 * @author nayasis@gmail.com
 */
@JsonSerialize( using = SimpleNDateSerializer.class )
@JsonDeserialize( using = NDateDeserializer.class )
public class NDate implements Serializable {

	public static final NDate MIN_DATE = new NDate( "0000-01-01" );
	public static final NDate MAX_DATE = new NDate( "9999-12-31 23:59:59.999" );

    private Calendar currentTime = Calendar.getInstance();

    public static final String DEFAULT_OUTPUT_FORMAT = "YYYY-MM-DD HH:MI:SS";
    public static final String DEFAULT_INPUT_FORMAT  = "yyyyMMddHHmmssSSS";

    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static final Set<String> ISO_8601_COMPATIBLE_FORMATS = new LinkedHashSet<>(
            Arrays.asList( "yyyyMMdd'T'HHmmssZ", "yyyyMMdd'T'HHmmssSSSZ", "yyyyMMdd'T'HHmmssSSZ", "yyyyMMdd'T'HHmmssSZ" )
    );

    /**
     * constructor with current date
     */
    public NDate() {}

    /**
     * 특정 Date 객체로 기본 객체를 생성한다.
     *
     * @param date 생성자의 기준이 될 date 객체
     */
    public NDate( Date date ) {
        setDate( (Date) date.clone() );
    }

    /**
     * 특정 Calendar 객체로 기본 객체를 생성한다.
     *
     * @param date 생성자의 기준이 될 Calendar 객체
     */
    public NDate( Calendar date ) {
        setDate( date );
    }

    /**
     * 특정 NDate 객체로 기본 객체를 생성한다.
     *
     * @param date 생성자의 기준이 될 NDate 객체
     */
    public NDate( NDate date ) {
        setDate( date );
    }

    /**
     * 특정 NDate 객체로 기본 객체를 생성한다.
     *
     * @param date 생성자의 기준이 될 date 객체
     */
    public NDate( long date ) {
        setDate( date );
    }

    /**
     * [년-월-일-시-분-초] 순서로 날짜 객체를 생성한다.
     *
     * <pre>
     * NDate date01 = new NDate( "2012.01" );
     * NDate date02 = new NDate( "2012.01.02" );
     * NDate date03 = new NDate( "2012-01-02" );
     * NDate date04 = new NDate( "2012-01-02 13:20" );
     * NDate date05 = new NDate( "2012-01-02 13:20:42" );
     * </pre>
     *
     * @param date 날짜
     * @throws ParseException YYYY-MM-DD-HH-MI-SS 순서로 날짜를 해석하지 못했을 경우
     */
    public NDate( String date ) throws ParseException {
        setDate( date );
    }

    /**
     * 특정 형식에 대해 날짜 객체를 생성한다.
     *
     * <pre>
     * NDate date01 = new NDate( "01/22/1977", "MM/DD/YYYY" );
     * NDate date02 = new NDate( "23:42", "HH:MI" );
     * NDate date03 = new NDate( "23:42 01/22", "HH:MI MM/DD" );
     * </pre>
     *
     * @param date 날짜
     * @param format 날짜포맷 날짜포맷 [YYYY:년, MM:월, DD:일, HH:시, MI:분, SS:초 ]
     * @throws ParseException 정의한 format 으로 날짜를 해석하지 못했을 경우
     */
    public NDate( String date, String format ) throws ParseException {
        setDate( date, format );
    }

    /**
     * 날짜를 [년-월-일-시-분-초] 순의 형식으로 세팅한다.
     *
     * <pre>
     * NDate date = new NDate();
     *
     * date.setDate( "2011.12.24" );
     * date.setDate( "2011-12-24" ); → 포맷 중간의 구분자가 달라도 처리 가능
     * date.setDate( "2011.12.24 12:20" );
     * date.setDate( "2011.12.24 13:20:45" );
     * </pre>
     *
     * @param date date string
     * @return self instance
     * @throws ParseException YYYY-MM-DD-HH-MI-SS 순서로 날짜를 해석하지 못했을 경우
     */
    public NDate setDate( String date ) throws ParseException {
        return setDate( date, null );
    }

    /**
     * 날짜를 세팅한다.
     *
     * <pre>
     * NDate date = new NDate();
     *
     * date.setDate( "2011-12-24 23:10:45", "YYYY-MM-DD HH:MI:SS" );
     * </pre>
     *
     * @param date 날짜
     * @param format 날짜포맷 날짜포맷 [YYYY:년, MM:월, DD:일, HH:시, MI:분, SS:초 ]
     * @return self instance
     * @throws ParseException 정의한 format 으로 날짜를 해석하지 못했을 경우
     */
    public NDate setDate( String date, String format ) {

        if( StringUtil.isEmpty(date) ) {
            setDate( new Date() );
            return this;
        }

        boolean isNullFormat = Validator.isEmpty( format );

        String pattern = getDefaultFormat( format, isNullFormat );
        String value   = isNullFormat ? StringUtil.extractNumber( date ) : date;

        if( isNullFormat ) {
            int maxLength = Math.min( pattern.length(), value.length() );
            pattern   = pattern.substring( 0, maxLength );
            value = value.substring( 0, maxLength );
        }

        try {

            parse( value, pattern );
            return this;

        } catch( ParseException parseException ) {

            if( ISO_8601_24H_FULL_FORMAT.equals(pattern) ) {

                String isoValue = extractIsoValue( date );

                for( String isoFormat : ISO_8601_COMPATIBLE_FORMATS ) {
                    try {
                        parse( isoValue, isoFormat );
                        return this;
                    } catch( ParseException isoError ) {}
                }

                throw parseException;

            } else {
                throw parseException;
            }

        }

    }

    private String extractIsoValue( String value ) {
        return value.replaceAll( "[^0-9T\\+]", "" );
    }

    private void parse( String val, String pattern ) {

        SimpleDateFormat sdf = new SimpleDateFormat( pattern );

        try {
            currentTime.setTime( sdf.parse(val) );
        } catch( java.text.ParseException e ) {
            throw new ParseException( e, e.getMessage() );
        }

    }

    /**
     * Date 객체로 날짜를 세팅한다.
     *
     * @param date 날짜
     * @return self instance
     */
    public NDate setDate( Date date ) {
        this.currentTime.setTime( date ); return this;
    }

    /**
     * Date 객체로 날짜를 세팅한다.
     *
     * @param date 숫자형 날짜
     * @return self instance
     */
    public NDate setDate( long date ) {
    	this.currentTime.setTime( new Date(date) );
        return this;
    }

    /**
     * Calendar 객체로 날짜를 세팅한다.
     *
     * @param date 날짜객체
     * @return self instance
     */
    public NDate setDate( Calendar date ) {
        this.currentTime = (Calendar) date.clone();
        return this;
    }

    /**
     * NDate 객체로 날짜를 세팅한다.
     *
     * @param date 날짜객체
     * @return self instance
     */
    public NDate setDate( NDate date ) {
        this.currentTime = (Calendar) date.toCalendar().clone();
        return this;
    }

    /**
     * NDate 객체를 Date 객체로 변환한다.
     *
     * @return Date 객체
     */
    public Date toDate() {
        return this.currentTime.getTime();
    }

    /**
     * NDate 객체를 Calendar 객체로 변환한다.
     *
     * @return Calendar 객체
     */
    public Calendar toCalendar() {
        return this.currentTime;
    }

    /**
     * get time in milli-seconds
     *
     * @return milli-seconds time value
     */
    public long toTime() {
        return this.currentTime.getTimeInMillis();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toString( DEFAULT_OUTPUT_FORMAT );
    }

    /**
     * 객체를 특정 포맷에 맞는 형식으로 출력한다.
     *
     * @param format 날짜포맷 (YYYY:년, MM:월, DD:일, HH:시, MI:분, SS:초, FFF: 밀리초)
     * @return 포맷에 맞는 날짜 문자열
     */
    public String toString( String format ) {

        String pattern = getDefaultFormat( format, false );

        SimpleDateFormat sdf = new SimpleDateFormat( pattern );

        return sdf.format( toDate() );

    }

    /**
     * 특정 형식문자열을 제외한 나머지 문자열을 제거한 format 을 구한다.
     *
     * @param format 사용자가 입력한 날짜 지정형식 (YYYY:년, MM:월, DD:일, HH:시, MI:분, SS:초, FFF: 밀리초)
     * @param stripYn yyyyMMddHHmmssSSS 이외의 문자 제외여부
     * @return yyyyMMddHHmmssSSS 이외의 문자는 제외된 날짜형식
     */
    private String getDefaultFormat( String format, boolean stripYn ) {

        if( format == null || format.length() == 0 ) return DEFAULT_INPUT_FORMAT;

        format = format
        	.replaceAll( "YYYY", "yyyy" )
            .replaceAll( "([^D])DD([^D]|$)", "$1dd$2" )
            .replaceAll( "MI",     "mm"   )
            .replaceAll( "([^S])SS([^S]|$)", "$1ss$2" )
            .replaceAll( "F",    "S"    );

        if( stripYn ) format = format.replaceAll( "[^y|M|d|H|m|s|S]", "" );

        return format;

    }

    /**
     * truncate date's hour/minute/second/millisecond and remain it's date related properties only
     *
     * @return truncated date ( 00:00:00 )
     */
    public NDate truncate() {
        return setHour( 0 ).setMinute( 0 ).setSecond( 0 ).setMillisecond( 0 );
    }

    /**
     * get year
     *
     * @return year
     */
    public int getYear() {
        return currentTime.get( Calendar.YEAR );
    }

    /**
     * get month
     *
     * @return month ( 1 - 12 )
     */
    public int getMonth() {
        return currentTime.get( Calendar.MONTH ) + 1;
    }

    /**
     * get day
     *
     * @return day
     */
    public int getDay() {
        return currentTime.get( Calendar.DATE );
    }

    /**
     * get week day
     *
     * @return week day ( 1:sunday, 2:monday, 3:thuesday, 4:wednesday, 5:thursday, 6:friday, 7:saturdays )
     */
    public int getWeekDay() {
        return currentTime.get( Calendar.DAY_OF_WEEK );
    }

    /**
     * get hours
     *
     * @return hours ( 0 - 24 )
     */
    public int getHour() {
        return currentTime.get( Calendar.HOUR_OF_DAY );
    }

    /**
     * get miniutes
     *
     * @return minutes (0-59)
     */
    public int getMinute() {
        return currentTime.get( Calendar.MINUTE );
    }

    /**
     * get seconds
     *
     * @return seconds (0-59)
     */
    public int getSecond() {
        return currentTime.get( Calendar.SECOND );
    }

    /**
     * get milli-seconds
     *
     * @return milli-seconds
     */
    public int getMillisecond() {
        return currentTime.get( Calendar.MILLISECOND );
    }

    /**
     * add or subtract year
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addYear( int value ) {
        currentTime.add( Calendar.YEAR, value );
        return this;
    }

    /**
     * set year
     *
     * @param value value to set
     * @return  self instance
     */
    public NDate setYear( int value ) {
        currentTime.set( Calendar.YEAR, value );
        return this;
    }

    /**
     * adds or subtracts month
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addMonth( int value ) {
        currentTime.add( Calendar.MONTH, value );
        return this;
    }

    /**
     * set month
     *
     * @param value value to set
     * @return  self instance
     */
    public NDate setMonth( int value ) {
        currentTime.set( Calendar.MONTH, value );
        return this;
    }

    /**
     * adds or subtracts day
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addDay( int value ) {
        currentTime.add( Calendar.DATE, value );
        return this;
    }

    /**
     * set day
     *
     * @param value value to set
     * @return self instance
     */
    public NDate setDay( int value ) {
        currentTime.set( Calendar.DATE, value );
        return this;
    }

    /**
     * adds or subtracts hour
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addHour( int value ) {
        currentTime.add( Calendar.HOUR_OF_DAY, value );
        return this;
    }

    /**
     * set hour (24-hour clock)
     *
     * @param value value to set
     * @return self instance
     */
    public NDate setHour( int value ) {
        currentTime.set( Calendar.HOUR_OF_DAY, value );
        return this;
    }

    /**
     * adds or subtracts minute
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addMinute( int value ) {
        currentTime.add( Calendar.MINUTE, value );
        return this;
    }

    /**
     * set minute
     *
     * @param value value to set
     * @return self instance
     */
    public NDate setMinute( int value ) {
        currentTime.set( Calendar.MINUTE, value );
        return this;
    }

    /**
     * adds or subtracts second
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addSecond( int value ) {
        currentTime.add( Calendar.SECOND, value );
        return this;
    }

    /**
     * set second
     *
     * @param value value to set
     * @return  self instance
     */
    public NDate setSecond( int value ) {
        currentTime.set( Calendar.SECOND, value );
        return this;
    }

    /**
     * adds or subtracts mili-second
     *
     * @param value value to add or subtract
     * @return self instance
     */
    public NDate addMillisecond( int value ) {
        currentTime.add( Calendar.MILLISECOND, value );
        return this;
    }

    /**
     * set mili-second
     *
     * @param value value to add or subtract
     * @return  self instance
     */
    public NDate setMillisecond( int value ) {
        currentTime.set( Calendar.MILLISECOND, value );
        return this;
    }


    /**
     * get beginning of month date from current date.
     *
     * <pre>
     * NDate date = new NDate( "2012.02.29 13:21:41" );
     *
     * System.out.println( date.getBeginningOfMonth() ); → '2012.02.01 00:00:00'
     * </pre>
     *
     * @return new NDate to be setted with beginning of month date
     */
    public NDate getBeginningOfMonth() {

        Calendar newDate = Calendar.getInstance();
        newDate.set( getYear(), getMonth() - 1, 1, 0, 0, 0 );
        newDate.set( Calendar.MILLISECOND, 0 );

        return new NDate( newDate );

    }

    /**
     * get end of month date from current date.
     *
     * <pre>
     * NDate date = new NDate( "2012.02.29 13:21:41" );
     *
     * System.out.println( date.getEndOfMonth() ); → '2012.02.29 23:59:59.999'
     * </pre>
     *
     * @return new NDate to be setted with end of month date
     *
     */
    public NDate getEndOfMonth() {

        Calendar newDate = Calendar.getInstance();

        newDate.set( getYear(), getMonth(), 1, 0, 0, 0 );
        newDate.set( Calendar.MILLISECOND, 0 );
        newDate.add( Calendar.MILLISECOND, -1 );

        return new NDate( newDate );

    }

    /**
     * 두 날짜간 일자 차이를 구한다.
     *
     * @param date 비교할 날짜
     * @return 일자 차이
     */
    public int getBetweenDays( NDate date ) {

        Calendar c1 = null;
        Calendar c2 = null;

        try {
            c1 = new NDate( toString("YYYYMMDD" ) ).toCalendar();
            c2 = new NDate( date.toString("YYYYMMDD" ) ).toCalendar();
        } catch ( ParseException e ) {
            NLogger.error( e );
        }

        long diff = getDifference( c1, c2 );

        return (int) ( diff / 86400000 ); // 24 * 60 * 60 * 1000

    }

    /**
     * 두 날짜간 시간 차이를 구한다.
     *
     * @param       date    비교할 날짜
     * @return      시간 차이
     */
    public int getBetweenHours( NDate date ) {

        Calendar c1 = null;
        Calendar c2 = null;

        try {
            c1 = new NDate( toString("YYYYMMDDHHMISS" ) ).toCalendar();
            c2 = new NDate( date.toString("YYYYMMDDHHMISS" ) ).toCalendar();
        } catch ( ParseException e ) {
            NLogger.error( e );
        }

        long diff = getDifference( c1, c2 );

        return (int) ( diff / 3600000 ); // 60 * 60 * 1000

    }

    /**
     * 두 날짜간의 크기를 비교한다.
     *
     * @param   date   비교할 날짜객체
     * @return  -1 : 현재 날짜가 비교할 날짜 이전일 경우, 0 : 두 날짜가 동일할 경우, 1 : 현재 날짜가 비교할 날짜 이후일 경우
     */
    public int compareTo( NDate date ) {

        long srcTime = toCalendar().getTimeInMillis();
        long trgTime = date.toCalendar().getTimeInMillis();
        return ( srcTime < trgTime ? -1 : (srcTime == trgTime ? 0 : 1) );

    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 클 경우 true
     */
    public boolean greaterThan( NDate date ) {
        return compareTo( date ) > 0 ;
    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 크거나 같을 경우 true
     */
    public boolean greaterThanOrEqual( NDate date ) {
        return compareTo( date ) >= 0;
    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 작을 경우 true
     */
    public boolean lessThan( NDate date ) {
        return compareTo( date ) < 0;
    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 작거나 같을 경우 true
     */
    public boolean lessThanOrEqual( NDate date ) {
        return compareTo( date ) <= 0;
    }

    /**
     * 캘린더 객체간의 시간차를 구한다.
     *
     * @param c1 비교할 첫번째 캘린더 객체
     * @param c2 비교할 두번째 캘린더 객체
     * @return 시간차 (밀리세컨드 단위)
     */
    private long getDifference( Calendar c1, Calendar c2 ) {

        long milis1 = c1.getTimeInMillis();
        long milis2 = c2.getTimeInMillis();

        return Math.abs( milis1 - milis2 );

    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public NDate clone() {
        return new NDate( toDate() );
    }

    @Override
    public boolean equals( Object object ) {

        if( object == null ) return false;
        if( this == object ) return true;

        if( object instanceof NDate ) {
            NDate nDate = (NDate) object;
            return currentTime.equals( nDate.currentTime );
        } else if( object instanceof Date ) {
            return toDate().equals( object );
        } else if( object instanceof Calendar ) {
            return currentTime.equals( object );
        }

        return false;

    }

}