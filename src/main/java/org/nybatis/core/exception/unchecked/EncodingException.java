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
     * @param rootCause root cause for this exception
     */
    public EncodingException( Throwable rootCause ) {
        super( rootCause );
    }
    
    /**
     * Exception 생성자
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public EncodingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public EncodingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
