package org.nybatis.core.file.handler;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.model.NList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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

	/**
	 * Write data to excelFile
	 *
	 * @param outputStream	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param data			grid data
	 * @throws IoException	File I/O Exception
	 */
	public void writeTo( OutputStream outputStream, String sheetName, NList data ) throws IoException {
		Map<String, NList> worksheets = new HashMap<>();
		worksheets.put( sheetName, data );
		writeTo( outputStream, worksheets );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param data      grid data
	 * @throws IoException file I/O exception
	 */
	public void writeTo( OutputStream outputStream, NList data ) throws IoException {
		writeTo( outputStream, DEFAULT_SHEET_NAME, data );
	}

	/**
	 * Write data to excel file
	 *
	 * @param outputStream	output stream to write data
	 * @param data      key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public abstract void writeTo( OutputStream outputStream, Map<String, NList> data ) throws IoException;

	/**
	 * Read sheet from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public abstract NList readFrom( InputStream inputStream, String sheetName ) throws IoException;

	/**
	 * Read first sheet from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return grid data from first sheet
	 * @throws IoException file I/O exception
	 */
	public abstract NList readFirstSheetFrom( InputStream inputStream ) throws IoException;

	/**
	 * Read all sheets from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public abstract Map<String, NList> readFrom( InputStream inputStream ) throws IoException;

    protected String toExcelText( Object object ) {

        if( object == null ) return "";

        String txt = object.toString();

        if( txt.length() > 32_707 ) {
            txt = txt.substring( 0, 32_707 );
        }

        return txt;

    }

	protected FileInputStream getInputStream( File excelFile ) {
		try {
			return new FileInputStream( excelFile );
		} catch( FileNotFoundException e ) {
			throw new IoException( e, "Excel File to read is not found. ({})", excelFile );
		}
	}

	protected FileOutputStream getFileOutputStream( File excelFile ) {
		try {
			return new FileOutputStream( excelFile );
		} catch( FileNotFoundException e ) {
			throw new IoException( "ExcelFile[{}] to write is not exist.", excelFile );
		}
	}


}
