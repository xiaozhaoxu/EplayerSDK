package org.xmlsoft;

/**
 * <p>Encapsulate an XML parse error or warning.</p>
 *
 * <p>This exception may include information for locating the error in the original XML document,
 * as if it came from a Locator object.</p>
 *
 * @author Jang-Ho Hwang, rath@xrath.com
 */
public class LibXmlException extends RuntimeException {
	private int code;
	private int lineNumber;
	private int columnNumber;

	/**
	 * Create a new LibXmlException.
	 */
	public LibXmlException() {

	}

	/**
	 * Create a new LibXmlException from a message.
	 * @param message The error or warning message.
	 */
	public LibXmlException(String message) {
		super(message.trim());
	}

	/**
	 * Create a new LibXmlException from a code and a message.
	 * @param code The internal libxml2 error code.
	 * @param message The error or warning message.
	 */
	public LibXmlException(int code, String message) {
		this(message);
		setCode(code);
	}

	/**
	 * Create a new LibXmlException from a code and a message and locator information.
	 * @param code The internal libxml2 error code.
	 * @param message The error or warning message.
	 * @param line The line number of a document that error or warning occurs.
	 * @param column The column number of a document that error or warning occurs.
	 */
	public LibXmlException(int code, String message, int line, int column) {
		this(code, message);
		this.lineNumber = line;
		this.columnNumber = column;
	}

	/**
	 * Set error code generated by native libxml2.
	 * @param code error code.
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * Return error code generated by native libxml2.
	 * @return error code.
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * Return the line number of a document with this exception object.
	 * @return line number of a document.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * Return the column number of a document with this exception object.
	 * @return column number of a document.
	 */
	public int getColumnNumber() {
		return columnNumber;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("LibXmlException {code=");
		sb.append(code);
		sb.append(", message=");
		sb.append(getMessage());
		if( lineNumber!=0 || columnNumber!=0 ) {
			sb.append(", line=").append(lineNumber);
			sb.append(", column=").append(columnNumber);
		}
		sb.append("}");
		return sb.toString();
	}
}