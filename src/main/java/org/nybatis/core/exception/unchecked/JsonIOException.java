package org.nybatis.core.exception.unchecked;


/**
 * This exception is raised when Format of Json was invalid so unable to read an input stream or write to one.
 *
 * @author nayasis@gmail.com
 *
 */
public class JsonIOException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * constructor
     */
    public JsonIOException() {
        super();
    }

    /**
     * constructor
     */
    public JsonIOException( Throwable rootCause ) {
        super( rootCause );
    }
    
    /**
     * constructor
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public JsonIOException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * constructor
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public JsonIOException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
