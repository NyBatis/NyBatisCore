package org.nybatis.core.exception.checked;

import org.nybatis.core.message.Message;
import org.nybatis.core.util.StringUtil;

/**
 * 표준 Exception
 *
 * @author 정화수
 *
 */
public class BaseException extends Exception {

    private static final long serialVersionUID = -5534786628056441239L;

    /**
     * Exception Code 정보
     */
    private Object errorCode = "";

    /**
     * BaseException 생성자
     */
    public BaseException() {
        super();
    }

    /**
     * BaseException 생성자
     *
     * @param rootCause 원인이 되는 예외
     */
    public BaseException( Throwable rootCause ) {
        super( rootCause );
    }
    
    /**
     * BaseException 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public BaseException( String message, Object... messageParam ) {
        super( Message.get(message, messageParam) );
    }

    /**
     * BaseException 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public BaseException( Throwable rootCause, String message, Object... messageParam ) {
        super( Message.get(message, messageParam), rootCause );
    }

    /**
     * 에러코드를 구한다.
     *
     * @return String 에러코드
     */
    public String getErrorCode() {
        return StringUtil.nvl( this.errorCode );
    }

    /**
     * 에러코드를 세팅한다.
     *
     * @param errorCode 에러코드
     */
    public void setErrorCode( Object errorCode ) {
        this.errorCode = errorCode;
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
