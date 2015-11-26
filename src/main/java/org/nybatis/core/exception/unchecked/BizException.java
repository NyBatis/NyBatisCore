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
     * @param errorCode   에러코드(메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public BizException( String errorCode, Object... messageParam ) {
        super( errorCode, messageParam );
        super.setErrorCode( errorCode );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param errorCode 에러코드(메세지코드)
     * @param messageParam '{}' 문자를 치환할 파라미터
     */
    public BizException( Throwable rootCause, String errorCode, Object... messageParam ) {
        super( rootCause, errorCode, messageParam );
        super.setErrorCode( errorCode );
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
