package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When Cipher has no such algorithm
 *
 * @author nayasis@gmail.com
 *
 */
public class NoSuchAlgorithmException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public NoSuchAlgorithmException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public NoSuchAlgorithmException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public NoSuchAlgorithmException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public NoSuchAlgorithmException( Throwable rootCause ) {
        super( rootCause );
    }

}
