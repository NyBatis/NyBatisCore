package org.nybatis.core.exception.unchecked;


/**
 * RuntimeException When Excel sheet has no header to read
 *
 * @author nayasis@gmail.com
 *
 */
public class ExcelNoHeadException extends BaseRuntimeException {

    private static final long serialVersionUID = -2193252285540217341L;

    /**
     * Exception Constructor
     */
    public ExcelNoHeadException() {
        super();
    }

    /**
     * Exception Constructor
     *
     * @param message   error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public ExcelNoHeadException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     * @param message error message or code
     * @param messageParam parameter to replace '{}' character
     */
    public ExcelNoHeadException( Throwable rootCause, String message, Object... messageParam ) {
    	super( rootCause, message, messageParam );
    }

    /**
     * Exception Constructor
     *
     * @param rootCause root cause for this exception
     */
    public ExcelNoHeadException( Throwable rootCause ) {
        super( rootCause );
    }

}
