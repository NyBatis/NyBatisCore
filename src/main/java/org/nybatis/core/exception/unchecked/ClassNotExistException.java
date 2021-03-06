package org.nybatis.core.exception.unchecked;


/**
 * Runtime Exception for ClassNotFound
 *
 * @author nayasis@gmail.com
 *
 */
public class ClassNotExistException extends BaseRuntimeException {

    private static final long serialVersionUID = 6848966904417509380L;

    /**
     * Constructs with no detail message.
     */
    public ClassNotExistException() {
        super();
    }

    /**
     * Constructs with detail cause.
     *
     * @param rootCause 원인이 되는 예외
     */
    public ClassNotExistException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public ClassNotExistException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public ClassNotExistException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
