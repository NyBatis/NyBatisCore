package org.nybatis.core.exception.unchecked;


/**
 * An InvalidAccessException is runtime exception wrapper of {@code IllegalArgumentException}
 *
 * @author nayasis@gmail.com
 */
public class InvalidAccessException extends BaseRuntimeException {

    private static final long serialVersionUID = 6848966904417509380L;

    /**
     * Constructs without detail message
     */
    public InvalidAccessException() {
        super();
    }

    /**
     * Constructs with root cause
     *
     * @param rootCause root cause
     */
    public InvalidAccessException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructs with detail message
     *
     * @param message       error message ( or message code )
     * @param messageParam  parameters binding with '{}' phrase
     */
    public InvalidAccessException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Constructs with detail message and cause
     *
     * @param rootCause     root cause
     * @param message       error message ( or message code )
     * @param messageParam  parameters binding with '{}' phrase
     */
    public InvalidAccessException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
