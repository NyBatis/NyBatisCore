package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When sql configuration error is occured.
 *
 * @author nayasis@gmail.com
 *
 */
public class SqlConfigurationException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public SqlConfigurationException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlConfigurationException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlConfigurationException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public SqlConfigurationException( Throwable rootCause ) {
    	super( rootCause );
    }

}
