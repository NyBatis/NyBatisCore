package org.nybatis.core.exception.unchecked;


/**
 * ClassCast 사용시 발생하는 RuntimeException
 *
 * @author 정화수
 *
 */
public class ClassCastingException extends BaseRuntimeException {

    private static final long serialVersionUID = 6848966904417509380L;

    /**
     * Exception 생성자
     */
    public ClassCastingException() {
        super();
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     */
    public ClassCastingException( Throwable rootCause ) {
        super( rootCause );
    }
    
    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public ClassCastingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public ClassCastingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
