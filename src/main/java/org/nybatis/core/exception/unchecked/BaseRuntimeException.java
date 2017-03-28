package org.nybatis.core.exception.unchecked;

import org.nybatis.core.message.Message;
import org.nybatis.core.util.StringUtil;

/**
 * Base RuntimeException
 *
 * @author nayasis@gmail.com
 *
 */
public class BaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 5185435566147737597L;

    /**
     * Exception Code
     */
    private String errorCode = "";

    public BaseRuntimeException() {
        super();
    }

    /**
     * Constructor
     *
     * @param rootCause root cause
     */
    public BaseRuntimeException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructor
     *
     * @param message       error message ( or message code )
     * @param messageParam  parameters binding with '{}' phrase
     */
    public BaseRuntimeException( String message, Object... messageParam ) {
        super( Message.get(message, messageParam) );
    }

    /**
     * Constructor
     *
     * @param rootCause     root cause
     * @param message       error message ( or message code )
     * @param messageParam  parameters binding with '{}' phrase
     */
    public BaseRuntimeException( Throwable rootCause, String message, Object... messageParam ) {
        super( Message.get(message, messageParam), rootCause );
    }

    /**
     * get error code
     *
     * @return String error code
     */
    public String getErrorCode() {
    	return errorCode;
    }

    /**
     * set error code
     *
     * @param errorCode error code
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
