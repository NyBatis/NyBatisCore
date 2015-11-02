package org.nybatis.core.exception.unchecked;


/**
 * Runtime {@link #EncodingException}
 *
 * @author nayasis@gmail.com
 *
 */
public class EncodingException extends BaseRuntimeException {

    private static final long serialVersionUID = 6848966904417509380L;

    /**
     * Constructs an exception with no detail message.
     */
    public EncodingException() {
        super();
    }

    /**
     * Constructs an exception with detail cause.
     *
     * @param rootCause 원인이 되는 예외
     */
    public EncodingException( Throwable rootCause ) {
        super( rootCause );
    }
    
    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public EncodingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public EncodingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
