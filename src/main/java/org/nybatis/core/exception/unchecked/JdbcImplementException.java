package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When Jdbc Driver's function is not implemented properly.
 *
 * @author nayasis@gmail.com
 *
 */
public class JdbcImplementException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public JdbcImplementException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public JdbcImplementException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public JdbcImplementException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public JdbcImplementException( Throwable rootCause ) {
        super( rootCause );
    }

}
