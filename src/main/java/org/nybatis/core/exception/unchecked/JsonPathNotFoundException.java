package org.nybatis.core.exception.unchecked;

import org.nybatis.core.exception.checked.BaseException;

/**
 * RuntimeException When parsing sql test expression
 *
 * @author nayasis@gmail.com
 *
 */
public class JsonPathNotFoundException extends BaseException {

    private static final long serialVersionUID = 7461498821447251256L;

    /**
     * Exception Constructor
     */
    public JsonPathNotFoundException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public JsonPathNotFoundException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public JsonPathNotFoundException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public JsonPathNotFoundException( Throwable rootCause ) {
        super( rootCause );
    }

}
