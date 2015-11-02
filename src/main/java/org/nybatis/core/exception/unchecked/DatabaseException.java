package org.nybatis.core.exception.unchecked;


/**
 * DB 사용시 발생하는 RuntimeException
 *
 * @author 정화수
 *
 */
public class DatabaseException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception 생성자
     */
    public DatabaseException() {
        super();
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     */
    public DatabaseException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public DatabaseException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public DatabaseException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
