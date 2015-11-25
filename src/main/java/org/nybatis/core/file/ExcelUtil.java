package org.nybatis.core.file;

import java.io.File;
import java.util.Map;

import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.handler.ExcelHandler;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.file.handler.implement.ExcelHandlerApachePoi;
import org.nybatis.core.file.handler.implement.ExcelHandlerJxl;

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
			throw new IoException( "There is no excel libaray like ApachePoi or Jxl." );
		}

		return excelHandler;

	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile excel file to write data
	 * @param data      key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( File excelFile, Map<String, NList> worksheets ) throws IoException {
		getHandler().writeTo( excelFile, worksheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param worksheet		grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( File excelFile, String sheetName, NList worksheet ) throws IoException {
		getHandler().writeTo( excelFile, sheetName, worksheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param worksheet grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( File excelFile, NList worksheet ) throws IoException {
		getHandler().writeTo( excelFile, worksheet );
	}

	/**
	 * Write data to excel file
	 *
	 * @param excelFile  excel file to write data
	 * @param worksheets key is sheetName and value is grid data.
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( String excelFile, Map<String, NList> worksheets ) throws IoException {
	    writeTo( new File( excelFile ), worksheets );
	}

	/**
	 * Write data to excelFile
	 *
	 * @param excelFile		excel file to write data
	 * @param sheetName		sheet name of excel file to write
	 * @param worksheet		grid data
	 * @throws IoException	File I/O Exception
	 */
	public static void writeTo( String excelFile, String sheetName, NList worksheet ) throws IoException {
		writeTo( new File( excelFile ), sheetName, worksheet );
	}

	/**
	 * Write data to excel file in sheet named 'Sheet1'
	 *
	 * @param excelFile excel file to write
	 * @param worksheet grid data
	 * @throws IoException file I/O exception
	 */
	public static void writeTo( String excelFile, NList worksheet ) throws IoException {
		writeTo( new File( excelFile ), worksheet );
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
	 * Read first sheet from excel file
	 *
	 * @param excelFile excel file to read
	 * @return grid data from first sheet
	 * @throws IoException file I/O exception
	 */
	public static NList readFirstSheetFrom( String excelFile ) throws IoException {
		return readFirstSheetFrom( new File( excelFile ) );
	}

}
