package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When treat sql on Database
 *
 * @author nayasis@gmail.com
 *
 */
public class SqlException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public SqlException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public SqlException( Throwable rootCause ) {
        super( rootCause );
    }

}
