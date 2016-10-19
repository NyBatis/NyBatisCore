package org.nybatis.core.message;

import org.nybatis.core.conf.Const;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.ClassUtil;
import org.nybatis.core.util.NProperties;
import org.nybatis.core.util.StringUtil;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 코드 기반의 메세지를 관리하는 유틸 클래스
 *
 * @author 정화수
 *
 */
public class Message {

    /**
     * 메세지를 관리하는 Pool
     */

    private static Map<String, Map<Locale, String>> messagePool = new Hashtable<>();

    private static Locale NULL_LOCALE = new Locale( "", "" );

    static {
    	loadPool();
    }

    /**
     * 코드값에 해당하는 메세지를 가져온다.
     *
     * <ol>
     *   <li>
     *     <pre>
     * 메세지의 '{}' 문자는 치환가능문자로 사용하며
     *
     * 메세지코드 com.0001 이 '{}는 사람입니다.' 로 설정되어 있을 경우
     *
     * Message.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Message.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * 미정의된 메세지코드를 가져올 경우, 메세지코드가 그대로 출력된다.
     *
     * 만약 merong 이라는 메세지코드가 정의되어 있지 않다면
     *
     * Message.get( "merong" ); → "merong"
     *     </pre>
     *   </li>
     * </ol>
     *
     *
     * @param code 메세지 코드
     * @param param '{}' 문자를 치환할 파라미터
     * @return 코드값에 해당하는 메세지
     */
    public static String get( Locale locale, Object code, Object... param ) {
        return StringUtil.format( getMessage( code, locale ), param );
    }

    public static String get( Object code, Object... param ) {
    	return get( Locale.getDefault(), code, param );
    }

    /**
     * Repository에서 코드값에 해당하는 메세지를 가져온다.
     *
     * @param code 메세지 코드
     * @return 코드값에 해당하는 메세지
     */
    private static String getMessage( Object code, Locale locale ) {

        if( code == null ) return "null";

    	if( ! messagePool.containsKey(code) ) return StringUtil.nvl( code );

    	Map<Locale, String> messages = messagePool.get( code );

        Locale pickedLocal = messages.containsKey( locale ) ? locale : NULL_LOCALE;

    	return messages.get( pickedLocal );

    }

    /**
     * 메세지 Pool에 담겨있는 내용을 JavaScript 컨텐츠로 만든다.
     *
     * @return 파일에 기록할 컨텐츠
     */
    public static String toJavaScript( String javascriptMessageObjectName ) {

        StringBuffer contents = new StringBuffer();

        contents.append( javascriptMessageObjectName ).append( '=' );

        Map<String, String> map = new HashMap<>();

        for( String key : messagePool.keySet() ) {
            map.put( key, get(key) );
        }

        contents.append( Reflector.toJson( map ) );
        contents.append( "\n" );

        return contents.toString();

    }

    /**
     * 메세지파일(property형식)을 메모리에 적재한다.
     *
     * <pre>
     * /config/message 디렉토리에 들어있는 *.prop 형식의 파일들을 읽어들인다. (파일명 오름차순 순으로 정렬한다.)
     * </pre>
     */
    public static void loadPool() {

        String configPath = Const.path.toResourceName( Const.path.getConfigMessage() );

        List<String> resourceNames = ClassUtil.findResources( configPath + "/**.prop" );

        Collections.sort( resourceNames );

        for( String name : resourceNames ) {
            loadPool( name );
        }

    }

    /**
     * 메세지파일(property형식)을 메모리에 적재한다.
     *
     * @param filePath 메세지파일의 경로
     * @throws UncheckedIOException
     */
    public static void loadPool( String filePath ) throws UncheckedIOException {

        Locale locale = getLocaleFrom( filePath );

        NProperties properties = new NProperties( filePath );

        for( String key : properties.keySet() ) {
            if( ! messagePool.containsKey(key) ) {
                messagePool.put( key, new Hashtable<>() );
            }

            Map<Locale, String> messages = messagePool.get( key );
            messages.put( locale, properties.get( key ) );

        }

    }

    public static void clearPool() {
    	messagePool.clear();
    }

    public static void refreshPool() {
    	clearPool();
    	loadPool();
    }

    private static Locale getLocaleFrom( String filePath ) {

    	String baseName = FileUtil.removeExtention( new File( filePath ).getName() );

    	List<String> sentences = StringUtil.tokenize( baseName, "." );

    	int size = sentences.size();

    	if( size <= 1 ) return NULL_LOCALE;

    	String localeString = sentences.get( size - 1 );

    	String country  = StringUtil.extractUpperCharacters( localeString );
    	String language = StringUtil.extractLowerCharacters( localeString );

    	if( StringUtil.isEmpty( country  ) ) country  = Locale.getDefault().getCountry();
    	if( StringUtil.isEmpty( language ) ) language = Locale.getDefault().getLanguage();

    	return new Locale( language, country );

    }

}
