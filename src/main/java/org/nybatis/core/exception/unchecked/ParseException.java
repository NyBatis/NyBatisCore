package org.nybatis.core.exception.unchecked;



/**
 * 업무예외 발생시 사용하는 Exception
 *
 * @author 정화수
 *
 */
public class ParseException extends BaseRuntimeException {

  private static final long serialVersionUID = 8134903392363981978L;

  private Integer lineNumber;
  private Integer columnNumber;

	/**
     * Exception 생성자
     */
    public ParseException() {
        super();
    }

    /**
     * Exception 생성자
     *
     * @param message   에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public ParseException( String message, Object... messageParam ) {
        super( message, messageParam );
    }

    /**
     * Exception 생성자
     *
     * @param rootCause 원인이 되는 예외
     * @param message 에러메세지(또는 메세지코드)
     * @param messageParam '@' 문자를 치환할 파라미터
     */
    public ParseException( Throwable rootCause, String message, Object... messageParam ) {
        super( rootCause, message, messageParam );
    }

    /**
    * Exception Constructor
    *
    * @param rootCause root cause for this exception
    */
    public ParseException( Throwable rootCause ) {
    super( rootCause );
    }

    public Integer getLineNumber() {
    return lineNumber;
}

    public void setLineNumber( Integer lineNumber ) {
    this.lineNumber = lineNumber;
}

    public Integer getColumnNumber() {
    return columnNumber;
}

    public void setColumnNumber( Integer columnNumber ) {
    this.columnNumber = columnNumber;
}

}
