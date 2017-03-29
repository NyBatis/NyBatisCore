package org.nybatis.core.exception.unchecked;


/**
 * Cloning failure RuntimeException
 *
 * @author nayasis@gmail.com
 *
 */
public class CloningException extends BaseRuntimeException {

    private static final long serialVersionUID = 6848966904417509380L;

    /**
     * Constructor
     */
    public CloningException() {
        super();
    }

    /**
     * Constructor
     *
     * @param rootCause root cause
     */
    public CloningException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Constructor
     *
     * @param message       error message ( or message code )
     * @param messageParam  parameter binding with '{}' phrase
     */
    public CloningException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Constructor
     *
     * @param rootCause     root cause
     * @param message       error message ( or message code )
     * @param messageParam  parameter binding with '{}' phrase
     */
    public CloningException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
