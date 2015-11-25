package org.nybatis.core.file.handler;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.model.NList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Excel Writer
 *
 * @author nayasis@gmail.com
 */
public abstract class ExcelHandler {

	private static final String DEFAULT_SHEET_NAME = "Sheet1";

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param data			grid data
	 * @throws IoException	File I/O Exception
	 */
	public void writeTo( File excelFile, String sheetName, NList data ) throws IoException {

		Map<String, NList> worksheets = new HashMap<>();

		worksheets.put( sheetName, data );

		writeTo( excelFile, worksheets );

	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param data      grid data
	 * @throws IoException file I/O exception
	 */
	public void writeTo( File excelFile, NList data ) throws IoException {
		writeTo( excelFile, DEFAULT_SHEET_NAME, data );
	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param data      key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public abstract void writeTo( File excelFile, Map<String, NList> data ) throws IoException;

	/**
	 * Read sheet from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public abstract NList readFrom( File excelFile, String sheetName ) throws IoException;

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile excel file to read
	 * @return grid data from first sheet
	 * @throws IoException file I/O exception
	 */
	public abstract NList readFirstSheetFrom( File excelFile ) throws IoException;

	/**
	 * Read all sheets from excel file
	 *
	 * @param excelFile excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public abstract Map<String, NList> readFrom( File excelFile ) throws IoException;

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
