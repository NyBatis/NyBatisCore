package org.nybatis.core.exception.unchecked;


/**
 * Runtime {@link #DecodingException}
 *
 * @author nayasis@gmail.com
 *
 */
public class DecodingException extends BaseRuntimeException {

    private static final long serialVersionUID = 6848966904417509380L;

    /**
     * Constructs an exception with no detail message.
     */
    public DecodingException() {
        super();
    }

    /**
     * Constructs an exception with detail cause.
     *
     * @param rootCause root cause
     */
    public DecodingException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Exception 생성자
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public DecodingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public DecodingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
