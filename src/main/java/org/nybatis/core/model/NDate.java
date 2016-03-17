package org.nybatis.core.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.reflection.mapper.NDateDeserializer;
import org.nybatis.core.reflection.mapper.NDateSerializer;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * represents a specific instant in time with millisecond precision
 *
 * @author nayasis@gmail.com
 */
@JsonSerialize( using = NDateSerializer.class )
@JsonDeserialize( using = NDateDeserializer.class )
public class NDate {

	public static final NDate MIN_DATE = new NDate( "0000-01-01" );
	public static final NDate MAX_DATE = new NDate( "9999-12-31 23:59:59.999" );

    private Calendar currentTime = Calendar.getInstance();

    private static final String DEFAULT_OUTPUT_FORMAT = "YYYY-MM-DD HH:MI:SS";
    private static final String DEFAULT_INPUT_FORMAT  = "yyyyMMddHHmmssSSS";

    public static final String ISO_8601_24H_FULL_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    /**
     * 현재 시간을 기준으로 기본 날짜 객체를 생성한다.
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
     * date.setDate( "2011-12-24" ); -> 포맷 중간의 구분자가 달라도 처리 가능
     * date.setDate( "2011.12.24 12:20" );
     * date.setDate( "2011.12.24 13:20:45" );
     * </pre>
     *
     * @param date 날짜
     * @throws ParseException YYYY-MM-DD-HH-MI-SS 순서로 날짜를 해석하지 못했을 경우
     */
    public void setDate( String date ) throws ParseException {
        setDate( date, null );
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
     * @throws ParseException 정의한 format 으로 날짜를 해석하지 못했을 경우
     */
    public void setDate( String date, String format ) {

        if( date == null || date.length() == 0 ) {
            setDate( new Date() );
            return;
        }

        boolean isNullFormat = Validator.isEmpty( format );

        String pattern = getDefaultFormat( format, isNullFormat );

        String numString = isNullFormat ? StringUtil.extractNumber( date ) : date;

        if( isNullFormat ) {
            int maxLength = Math.min( pattern.length(), numString.length() );
            pattern   = pattern.substring( 0, maxLength );
            numString = numString.substring( 0, maxLength );
        }

        SimpleDateFormat sdf = new SimpleDateFormat( pattern );

        try {
	        currentTime.setTime( sdf.parse(numString) );
        } catch( java.text.ParseException e ) {
        	throw new ParseException( e, e.getMessage() );
        }

    }

    /**
     * Date 객체로 날짜를 세팅한다.
     *
     * @param date 날짜
     */
    public void setDate( Date date ) {
        this.currentTime.setTime( date );
    }

    /**
     * Date 객체로 날짜를 세팅한다.
     *
     * @param date 숫자형 날짜
     */
    public void setDate( long date ) {
    	this.currentTime.setTime( new Date(date) );
    }

    /**
     * Calendar 객체로 날짜를 세팅한다.
     *
     * @param date 날짜객체
     */
    public void setDate( Calendar date ) {
        this.currentTime = (Calendar) date.clone();
    }

    /**
     * NDate 객체로 날짜를 세팅한다.
     *
     * @param date 날짜객체
     */
    public void setDate( NDate date ) {
        this.currentTime = (Calendar) date.toCalendar().clone();
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return toString( DEFAULT_OUTPUT_FORMAT );
    }

    /**
     * 객체를 특정 포맷에 맞는 형식으로 출력한다.
     *
     * @param format 날짜포맷 [YYYY:년, MM:월, DD:일, HH:시, MI:분, SS:초, FFF: 밀리초]
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
     * @param format 사용자가 입력한 날짜 지정형식
     * @param stripYn yyyyMMddHHmmssSSS 이외의 문자 제외여부
     * @return yyyyMMddHHmmssSSS 이외의 문자는 제외된 날짜형식
     */
    private String getDefaultFormat( String format, boolean stripYn ) {

        if( format == null || format.length() == 0 ) return DEFAULT_INPUT_FORMAT;

        // UI 프레임워크와 형식을 일치시키기 위해 포맷 변형
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
     * 년도를 구한다.
     *
     * @return 4자리 년도
     */
    public int getYear() {
        return currentTime.get( Calendar.YEAR );
    }

    /**
     * 월을 구한다.
     *
     * @return 월 ( 1 - 12 )
     */
    public int getMonth() {
        return currentTime.get( Calendar.MONTH ) + 1;
    }

    /**
     * 일을 구한다.
     *
     * @return 일
     */
    public int getDay() {
        return currentTime.get( Calendar.DATE );
    }

    /**
     * 요일을 구한다.
     *
     * @return 요일 ( 1:일요일, 2:월요일, 3:화요일, 4:수요일, 5:목요일, 6:금요일, 7:토요일 )
     */
    public int getWeekDay() {
        return currentTime.get( Calendar.DAY_OF_WEEK );
    }

    /**
     * 시간을 구한다.
     *
     * @return 시간 ( 0 - 24 )
     */
    public int getHour() {
        return currentTime.get( Calendar.HOUR_OF_DAY );
    }

    /**
     * 분을 구한다.
     *
     * @return 분 (0-59)
     */
    public int getMinute() {
        return currentTime.get( Calendar.MINUTE );
    }

    /**
     * 초를 구한다.
     *
     * @return 초 (0-59)
     */
    public int getSecond() {
        return currentTime.get( Calendar.SECOND );
    }

    /**
     * 밀리초를 구한다.
     *
     * @return 밀리초
     */
    public int getMillisecond() {
        return currentTime.get( Calendar.MILLISECOND );
    }

    /**
     * 년도를 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addYear( int amount ) {
        currentTime.add( Calendar.YEAR, amount );
    }

    /**
     * 월을 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addMonth( int amount ) {
        currentTime.add( Calendar.MONTH, amount );
    }

    /**
     * 일을 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addDay( int amount ) {
        currentTime.add( Calendar.DATE, amount );
    }

    /**
     * 시간을 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addHour( int amount ) {
        currentTime.add( Calendar.HOUR_OF_DAY, amount );
    }

    /**
     * 분을 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addMinute( int amount ) {
        currentTime.add( Calendar.MINUTE, amount );
    }

    /**
     * 초를 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addSecond( int amount ) {
        currentTime.add( Calendar.SECOND, amount );
    }

    /**
     * 밀리초를 더하거나 뺀다.
     *
     * @param amount 더하거나 뺄 수량
     */
    public void addMillisecond( int amount ) {
        currentTime.add( Calendar.MILLISECOND, amount );
    }


    /**
     * 현재 일을 기준으로 날짜가 월초로 세팅된 객체를 구한다.
     *
     * <pre>
     * NDate date = new NDate( "2012.02.29 13:21:41" );
     *
     * System.out.println( date.getFirstMonthDate() ); --> '2012.02.01 13:21:41' 이 출력됨
     * </pre>
     *
     * @return 월초로 변경된 날짜객체
     */
    public NDate getFirstMonthDate() {

        Calendar newDate = Calendar.getInstance();

        newDate.set( getYear(), getMonth() - 1, 1, getHour(), getMinute(), getSecond() );
        newDate.set( Calendar.MILLISECOND, getMillisecond() );

        return new NDate( newDate );

    }

    /**
     * 현재 일을 기준으로 날짜가 월말로 세팅된 객체를 구한다.
     *
     * <pre>
     * NDate date = new NDate( "2012.02.29 13:21:41" );
     *
     * System.out.println( date.getLastMonthDate() ); --> '2012.02.29 13:21:41' 이 출력됨
     * </pre>
     *
     * @return 월말로 변경된 날짜객체
     */
    public NDate getLastMonthDate() {

        Calendar newDate = Calendar.getInstance();

        newDate.set( getYear(), getMonth(), 1, getHour(), getMinute(), getSecond() );
        newDate.set( Calendar.MILLISECOND, getMillisecond() );

        newDate.add( Calendar.DATE, -1 );

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
            e.printStackTrace();
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
            e.printStackTrace();
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

        int compareVal = compareTo( date );

        return compareVal == 1;

    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 크거나 같을 경우 true
     */
    public boolean greaterThanOrEqual( NDate date ) {

        int compareVal = compareTo( date );

        return compareVal == 1 || compareVal == 0;

    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 작을 경우 true
     */
    public boolean lessThan( NDate date ) {

        int compareVal = compareTo( date );

        return compareVal == -1;

    }

    /**
     * 날짜간 크기를 비교한다.
     *
     * @param date 비교할 날짜
     * @return 현재 날짜가 비교할 날짜보다 작거나 같을 경우 true
     */
    public boolean lessThanOrEqual( NDate date ) {

        int compareVal = compareTo( date );

        return compareVal == -1 || compareVal == 0;

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