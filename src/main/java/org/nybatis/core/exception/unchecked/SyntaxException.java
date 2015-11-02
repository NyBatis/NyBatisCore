package org.nybatis.core.exception.unchecked;


/**
 * Syntex error RuntimeException
 *
 * @author nayasis@gmail.com
 *
 */
public class SyntaxException extends BaseRuntimeException {

    private static final long serialVersionUID = 1638221578469974985L;

    /**
     * Exception Contsturctor
     */
    public SyntaxException() {
        super();
    }

    /**
     * Exception Contsturctor
     *
     * @param message   error message or error code
     * @param messageParam parameters to bind with '<code>{}<code>'
     */
    public SyntaxException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Contsturctor
     *
     * @param rootCause cause to raise exception
     * @param message   error message or error code
     * @param messageParam parameters to bind with '<code>{}<code>'
     */
    public SyntaxException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public SyntaxException( Throwable rootCause ) {
        super( rootCause );
    }

}
