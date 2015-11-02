package org.nybatis.core.file.handler;

import java.io.File;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.model.NList;

/**
 * Abstract Excel Writer
 *
 * @author nayasis@gmail.com
 */
public abstract class ExcelHandler {

	private static final String DEFAULT_SHEET_NAME = "Sheet1";

	/**
	 * Write data to excelFile at 'Sheet1'
	 *
	 * @param excelFile excel file to write data
	 * @param data 		grid data
	 * @throws IoException    File I/O Exception
	 */
	public void writeTo( File excelFile, NList data ) throws IoException {
		writeTo( excelFile, data, DEFAULT_SHEET_NAME );
	}

	/**
	 * Read data from excel file at 'Sheet1'
	 *
	 * @param excelFile excel file to read
	 * @return grid data
	 * @throws IoException	File I/O Exception
	 */
	public NList readFrom( File excelFile ) throws IoException {
		return readFrom( excelFile, DEFAULT_SHEET_NAME );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param data			grid data
	 * @param sheetName		sheet name of excel file to write
	 * @throws IoException	File I/O Exception
	 */
	public abstract void writeTo( File excelFile, NList data, String sheetName ) throws IoException;

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public abstract NList readFrom( File excelFile, String sheetName ) throws IoException;

    protected String toExcelText( Object object ) {

        if( object == null ) {
            return "";
        }

        String txt = object.toString();

        if( txt.length() > 32_707 ) {
            txt = txt.substring( 0, 32_707 );
        }

        return txt;

    }

}
