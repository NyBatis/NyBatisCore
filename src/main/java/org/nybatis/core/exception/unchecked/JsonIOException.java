package org.nybatis.core.exception.unchecked;


/**
 * This exception is raised when Format of Json was invalid so unable to read an input stream or write to one.
 *
 * @author nayasis@gmail.com
 *
 */
public class JsonIOException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public JsonIOException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public JsonIOException( Throwable rootCause ) {
        super( rootCause );
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public JsonIOException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public JsonIOException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

}
