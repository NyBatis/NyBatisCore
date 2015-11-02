package org.nybatis.core.exception.unchecked;

import org.nybatis.core.message.Message;
import org.nybatis.core.util.StringUtil;

/**
 * 표준 RuntimeException
 *
 * @author 정화수
 *
 */
public class BaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5185435566147737597L;

    /**
     * Exception Code 정보
     */
    private String errorCode = "";

    /**
     * BaseRuntimeException 생성자
     */
    public BaseRuntimeException() {
        super();
    }

    /**
     * BaseRuntimeException 생성자
     *
     * @param rootCause 원인이 되는 예외
     */
    public BaseRuntimeException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * BaseRuntimeException 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public BaseRuntimeException( String message, Object... messageParam ) {
        super( Message.get(message, messageParam) );
    }

    /**
     * BaseRuntimeException 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public BaseRuntimeException( Throwable rootCause, String message, Object... messageParam ) {
        super( Message.get(message, messageParam), rootCause );
    }

    /**
     * 에러코드를 구한다.
     *
     * @return String 에러코드
     */
    public String getErrorCode() {
    	return errorCode;
    }

    /**
     * 에러코드를 세팅한다.
     *
     * @param errorCode 에러코드
     */
    public void setErrorCode( Object errorCode ) {
        this.errorCode = StringUtil.nvl( errorCode );
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    public String toString() {

        StringBuffer sb = new StringBuffer();

        if( ! StringUtil.isEmpty(errorCode) ) {
            sb.append( '[' ).append( errorCode ).append( ']' );
        }

        sb.append( super.getMessage() );

        return sb.toString();

    }

}
