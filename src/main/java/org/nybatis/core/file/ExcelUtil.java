package org.nybatis.core.file;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.handler.ExcelHandler;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.file.handler.implement.ExcelHandlerApachePoi;
import org.nybatis.core.file.handler.implement.ExcelHandlerJxl;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

/**
 * Excel Utility to read or write
 *
 * @author nayasis@gmail.com
 *
 */
public class ExcelUtil {

	private static ExcelHandler excelHandler = null;

	private static ExcelHandler getHandler() {

		if( excelHandler != null ) return excelHandler;

		try {

			excelHandler = new ExcelHandlerApachePoi();
			NLogger.info( "ExcelUtil use [Apache Poi Library]" );
			return excelHandler;

		} catch( Throwable e ) {
			String errorMessage =
					"ExcelUtil can not use [Apache Poi Library] because it is not imported.\n" +
					"\t- Maven dependency is like below.\n" +
					"\t\t<dependency>\n" +
					"\t\t  <groupId>org.apache.poi</groupId>\n" +
					"\t\t  <artifactId>poi-ooxml</artifactId>\n" +
					"\t\t  <version>3.12</version>\n" +
					"\t\t</dependency>\n";
			NLogger.warn( errorMessage );
		}

		try {

			excelHandler = new ExcelHandlerJxl();
			NLogger.info( "ExcelUtil use [JExcel Library]" );
			return excelHandler;

		} catch( Throwable e ) {
			String errorMessage =
					"ExcelUtil can not use [JExcel Library] because it is not imported.\n" +
					"\t- Maven dependency is like below.\n" +
					"\t\t<dependency>\n" +
					"\t\t  <groupId>net.sourceforge.jexcelapi</groupId>\n" +
					"\t\t  <artifactId>jxl</artifactId>\n" +
					"\t\t  <version>2.6.12</version>\n" +
					"\t\t</dependency>\n";
			NLogger.warn( errorMessage );
		}

		if( excelHandler == null ) {
			throw new NoClassDefFoundError( "There is no excel libaray like ApachePoi or Jxl." );
		}

		return excelHandler;

	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param sheets    key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( File excelFile, Map<String, ?> sheets ) throws IoException {
		getHandler().writeTo( excelFile, sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( File excelFile, String sheetName, NList sheet ) throws IoException {
		getHandler().writeTo( excelFile, sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( File excelFile, String sheetName, List<?> sheet ) throws IoException {
		getHandler().writeTo( excelFile, sheetName, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( File excelFile, NList sheet ) throws IoException {
		getHandler().writeTo( excelFile, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( File excelFile, List sheet ) throws IoException {
		getHandler().writeTo( excelFile, sheet );
	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param sheets	key is sheetName and value is grid data.<br>
	 *                  value type is allowed only List or NList.
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( String excelFile, Map<String, ?> sheets ) throws IoException {
	    writeTo( new File( excelFile ), sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( String excelFile, String sheetName, NList sheet ) throws IoException {
		writeTo( new File( excelFile ), sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( String excelFile, String sheetName, List sheet ) throws IoException {
		writeTo( new File( excelFile ), sheetName, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( String excelFile, NList sheet ) throws IoException {
		writeTo( new File( excelFile ), sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param sheet grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( String excelFile, List sheet ) throws IoException {
		writeTo( new File( excelFile ), sheet );
	}

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
    public static NList readFrom( File excelFile, String sheetName ) throws IoException {
    	return getHandler().readFrom( excelFile, sheetName );
    }

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public static <T> List<T> readFrom( File excelFile, String sheetName, Class<T> toClass ) throws IoException {
		return getHandler().readFrom( excelFile, sheetName, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param excelFile excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
    public static Map<String, NList> readFrom( File excelFile ) throws IoException {
    	return getHandler().readFrom( excelFile );
    }

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public static <T> Map<String, List<T>> readFrom( File excelFile, Class<T> toClass ) throws IoException {
		return getHandler().readFrom( excelFile, toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile excel file to read
	 * @return grid data from first sheet
	 * @throws IoException file I/O exception
	 */
	public static NList readFirstSheetFrom( File excelFile ) throws IoException {
		return getHandler().readFirstSheetFrom( excelFile );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public static <T> List<T> readFirstSheetFrom( File excelFile, Class<T> toClass ) throws IoException {
		return getHandler().readFirstSheetFrom( excelFile, toClass );
	}

	/**
	 * Read data from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
    public static NList readFrom( String excelFile, String sheetName ) throws IoException {
        return readFrom( new File(excelFile), sheetName );
    }

	/**
	 * Read sheet from excel file
	 *
	 * @param excelFile		excel file to read
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public <T> List<T> readFrom( String excelFile, String sheetName, Class<T> toClass ) throws IoException {
		return readFrom( new File(excelFile), sheetName, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param excelFile excel file to read.
	 * @return key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
    public static Map<String, NList> readFrom( String excelFile ) throws IoException {
    	return readFrom( new File(excelFile) );
    }

	/**
	 * Read all sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( String excelFile, Class<T> toClass ) throws IoException {
		return readFrom( new File(excelFile), toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile excel file to read
	 * @return grid data from first sheet
	 * @throws IoException file I/O exception
	 */
	public static NList readFirstSheetFrom( String excelFile ) throws IoException {
		return readFirstSheetFrom( new File( excelFile ) );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param excelFile 	excel file to read
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public <T> List<T> readFirstSheetFrom( String excelFile, Class<T> toClass ) throws IoException {
		return readFirstSheetFrom( new File(excelFile), toClass );
	}


	// -----

	/**
	 * Write data to excel file
	 *
	 * @param outputStream output stream to write data
	 * @param sheets      	key is sheetName and value is grid data.<br>
	 *                      value type is allowed only List or NList.
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( OutputStream outputStream, Map<String, ?> sheets ) throws IoException {
		getHandler().writeTo( outputStream, sheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param outputStream 	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( OutputStream outputStream, String sheetName, NList sheet ) throws IoException {
		getHandler().writeTo( outputStream, sheetName, sheet );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param outputStream 	output stream to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param sheet			grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( OutputStream outputStream, String sheetName, List<?> sheet ) throws IoException {
		getHandler().writeTo( outputStream, sheetName, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param sheet		grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( OutputStream outputStream, NList sheet ) throws IoException {
		getHandler().writeTo( outputStream, sheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param outputStream	output stream to write data
	 * @param sheet			grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( OutputStream outputStream, List<?> sheet ) throws IoException {
		getHandler().writeTo( outputStream, sheet );
	}

	/**
	 * Read data from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public static NList readFrom( InputStream inputStream, String sheetName ) throws IoException {
		return getHandler().readFrom( inputStream, sheetName );
	}

	/**
	 * Read sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param sheetName		sheet name of excel file to read
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public <T> List<T> readFrom( InputStream inputStream, String sheetName, Class<T> toClass ) throws IoException {
		return getHandler().readFrom( inputStream, sheetName, toClass );
	}

	/**
	 * Read all sheets from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public static Map<String, NList> readFrom( InputStream inputStream ) throws IoException {
		return getHandler().readFrom( inputStream );
	}

	/**
	 * Read all sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public <T> Map<String, List<T>> readFrom( InputStream inputStream, Class<T> toClass ) throws IoException {
		return getHandler().readFrom( inputStream, toClass );
	}

	/**
	 * Read first sheet from excel file
	 *
	 * @param inputStream	input stream to read data
	 * @return grid data from first sheet
	 * @throws IoException file I/O exception
	 */
	public static NList readFirstSheetFrom( InputStream inputStream ) throws IoException {
		return getHandler().readFirstSheetFrom( inputStream );
	}

	/**
	 * Read sheet from input stream
	 *
	 * @param inputStream	input stream to read data
	 * @param toClass		generic type of list's class
	 * @return grid data
	 * @throws IoException  File I/O Exception
	 */
	public static <T> List<T> readFirstSheetFrom( InputStream inputStream, Class<T> toClass ) throws IoException {
		return getHandler().readFirstSheetFrom( inputStream, toClass );
	}

	/**
	 * Convert data to NList
	 *
	 * @param data data for excel
	 * @return data as NList type
	 */
	public static Map<String, NList> toNList( Map<String, ?> data ) {
		return getHandler().toNList( data );
	}

	/**
	 * Convert data to bean list
	 *
	 * @param data data for excel
	 * @param toClass generic type of list
	 * @return data as toClass generic type
	 */
	public static <T> Map<String, List<T>> toBeanList( Map<String, NList> data, Class<T> toClass ) {
		return getHandler().toBeanList( data, toClass );
	}

}
