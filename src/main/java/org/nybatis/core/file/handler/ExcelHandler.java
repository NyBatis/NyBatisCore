package org.nybatis.core.file.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.file.annotation.ExcelReadAnnotationInspector;
import org.nybatis.core.file.annotation.ExcelWriteAnnotationInspector;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.mapper.NObjectMapper;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
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
	private static NObjectMapper excelMapper  = null;

	static {
		excelMapper = new NObjectMapper( false );
		excelMapper.setAnnotationIntrospectors(
				new ExcelReadAnnotationInspector(),
				new ExcelWriteAnnotationInspector()
		);
	}

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


	protected NList toExcelNListFromBean( List<?> fromList ) throws JsonIOException {

		NList result = new NList();

		if( hasRow(fromList) ) {
			ObjectWriter writer = excelMapper.writer();
			try {
				for( Object bean : fromList ) {
					result.addRow( writer.writeValueAsString(bean) );
				}
			} catch( JsonProcessingException e ) {
				throw new JsonIOException( e );
			}
		}

		return result;

	}

	protected <T> List<T> toBeanFromExcelNList( NList fromList, Class<T> toClass ) throws JsonIOException {

		List<T> list = new ArrayList<>();

		if( fromList == null || fromList.size() == 0 ) return list;

		for( NMap map : fromList ) {

			String json = map.toJson();

			try {

				T bean = excelMapper.readValue( json, toClass );
				list.add( bean );

			} catch( IOException e ) {
				throw new JsonIOException( e, "JsonParseException : {}\n\t- json string :\n{}\n\t- target class : {}", e.getMessage(), json, toClass );
			}

		}

		return list;

	}

	private boolean hasRow( List<?> list ) {
		if( Validator.isEmpty(list) ) return false;
		return ! Types.isPrimitive( list.get(0) );
	}

}
