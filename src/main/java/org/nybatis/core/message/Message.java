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
 * Message utility class based on message code
 *
 * @author nayasis@gmail.com
 *
 */
public class Message {

    // message pool ( code / local / message text )
    private static Map<String, Map<Locale, String>> messagePool = new Hashtable<>();

    private static Locale NULL_LOCALE = new Locale( "", "" );

    static {
    	loadPool();
    }

    /**
     * get message correspoding to code
     *
     * <ol>
     *   <li>
     *     <pre>
     *  '{}' in message are replaced with binding parameters.
     *
     * if message code "com.0001" is "{}는 사람입니다.", then
     *
     * Message.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Message.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * if message code is not defined, return code itself.
     *
     * if "merong" is just code and not defined, then
     *
     * Message.get( "merong" ); → "merong"
     *     </pre>
     *   </li>
     * </ol>
     *
     *
     * @param locale    locale
     * @param code      message code
     * @param param     binding parameter replaced with '{}'
     * @return message correspoding to code
     */
    public static String get( Locale locale, Object code, Object... param ) {
        return StringUtil.format( getMessage( code, locale ), param );
    }

    /**
     * get default locale's message correspoding to code.
     *
     * <ol>
     *   <li>
     *     <pre>
     *  '{}' in message are replaced with binding parameters.
     *
     * if message code "com.0001" is "{}는 사람입니다.", then
     *
     * Message.get( "com.0001", "정화종" ); → "정화종은 사람입니다."
     * Message.get( "com.0001", "ABC"    ); → "ABC는 사람입니다."
     *     </pre>
     *   </li>
     *   <li>
     *     <pre>
     * if message code is not defined, return code itself.
     *
     * if "merong" is just code and not defined, then
     *
     * Message.get( "merong" ); → "merong"
     *     </pre>
     *   </li>
     * </ol>
     *
     *
     * @param code      message code
     * @param param     binding parameter replaced with '{}'
     * @return message correspoding to code
     */
    public static String get( Object code, Object... param ) {
    	return get( Locale.getDefault(), code, param );
    }

    /**
     * get message from repository
     *
     * @param code      message code
     * @param locale    locale
     * @return message corresponding to code
     */
    private static String getMessage( Object code, Locale locale ) {

        if( code == null ) return "null";

    	if( ! messagePool.containsKey(code) ) return StringUtil.nvl( code );

    	Map<Locale, String> messages = messagePool.get( code );

        Locale pickedLocal = messages.containsKey( locale ) ? locale : NULL_LOCALE;

    	return messages.get( pickedLocal );

    }

    /**
     * convert message pool to javascript object
     *
     * @param javascriptMessageObjectName   object name of javascript
     * @return javascript contents
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
     * load message file (*.prop) to memory
     *
     * <pre>
     * base path is '/config/message/*.prop'
     * </pre>
     *
     * @throws UncheckedIOException if I/O exception occurs.
     */
    public static void loadPool() throws UncheckedIOException {

        String configPath = Const.path.toResourceName( Const.path.getConfigMessage() );

        List<String> resourceNames = ClassUtil.findResources( configPath + "/**.prop" );

        Collections.sort( resourceNames );

        for( String name : resourceNames ) {
            loadPool( name );
        }

    }

    /**
     *
     * load message file to memory
     *
     * @param filePath message file path
     * @throws UncheckedIOException  if I/O exception occurs.
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

    /**
     * clear message pool
     */
    public static void clearPool() {
    	messagePool.clear();
    }

    /**
     * refresh message pool
     */
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
