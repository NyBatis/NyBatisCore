package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When parsing sql test expression
 *
 * @author nayasis@gmail.com
 *
 */
public class SqlTypeMappingException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public SqlTypeMappingException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlTypeMappingException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlTypeMappingException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public SqlTypeMappingException( Throwable rootCause ) {
        super( rootCause );
    }

}
