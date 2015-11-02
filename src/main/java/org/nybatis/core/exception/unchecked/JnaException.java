package org.nybatis.core.exception.unchecked;


/**
 * XML SQL에서 발생하는 RuntimeException
 *
 * @author 정화수
 *
 */
public class JnaException extends BaseRuntimeException {

    private static final long serialVersionUID = 1638221578469974985L;

    /**
     * Exception 생성자
     */
    public JnaException() {
        super();
    }

    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public JnaException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public JnaException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public JnaException( Throwable rootCause ) {
        super( rootCause );
    }

}
