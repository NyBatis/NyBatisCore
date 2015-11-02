package org.nybatis.core.exception.unchecked;


/**
 * 업무예외 발생시 사용하는 Exception
 *
 * @author 정화수
 *
 */
public class BizException extends BaseRuntimeException {

    private static final long serialVersionUID = 1328446680943902961L;

    /**
     * Exception 생성자
     */
    public BizException() {
        super();
    }

    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public BizException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public BizException( Throwable rootCause, String message, Object... messageParam ) {
        super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public BizException( Throwable rootCause ) {
        super( rootCause );
    }

}
