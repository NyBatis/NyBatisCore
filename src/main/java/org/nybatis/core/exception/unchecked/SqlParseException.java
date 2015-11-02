package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When parsing sql test expression
 *
 * @author nayasis@gmail.com
 *
 */
public class SqlParseException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public SqlParseException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlParseException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlParseException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public SqlParseException( Throwable rootCause ) {
        super( rootCause );
    }

}
