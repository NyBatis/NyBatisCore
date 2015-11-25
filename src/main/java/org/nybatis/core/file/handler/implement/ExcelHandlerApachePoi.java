package org.nybatis.core.file.handler.implement;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.format.CellDateFormatter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.file.handler.ExcelHandler;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelHandlerApachePoi extends ExcelHandler {

	@Override
	public void writeTo( File excelFile, Map<String, NList> data ) throws IoException {

		excelFile = FileUtil.makeFile( excelFile );

		if( FileUtil.isNotExist(excelFile) ) {
			throw new IoException( "ExcelFile[{}] to write is not exist.", excelFile );
		}

		Workbook workbook = ( "xls".equalsIgnoreCase(FileUtil.getExtention(excelFile )) ) ? new HSSFWorkbook() : new XSSFWorkbook();

		for( String sheetName : data.keySet() ) {
			writeTo( workbook, sheetName, data.get(sheetName) );
		}

		try (
			FileOutputStream fos = new FileOutputStream( excelFile )
		) {
			workbook.write( fos );
		} catch( IOException e ) {
			throw new IoException( e, "Error on writing excel file[{}].", excelFile );
		}

	}

	private void writeTo( Workbook workbook, String sheetName, NList data ) {

		int idxColumn = 0, idxRow = 0;

		Sheet    sheet    = workbook.createSheet( sheetName );
		Row      row      = sheet.createRow( idxRow++ );

		for( String alias : data.getAliases() ) {
			row.createCell( idxColumn++ ).setCellValue( alias );
		}

		for( NMap nrow : data ) {

			row = sheet.createRow( idxRow++ );

			idxColumn = 0;

			for( Object key : data.keySet() ) {

				Object val = nrow.get( key );

				if( val == null ) {
					row.createCell( idxColumn++, HSSFCell.CELL_TYPE_BLANK );
				} else if( Validator.isNumericClass( val ) ) {
					row.createCell( idxColumn++, HSSFCell.CELL_TYPE_NUMERIC ).setCellValue( nrow.getDouble(key) );
				} else if( Validator.isBooleanClass( val) ) {
					row.createCell( idxColumn++, HSSFCell.CELL_TYPE_BOOLEAN ).setCellValue( (boolean) nrow.get( key ) );
				} else {
					row.createCell( idxColumn++, HSSFCell.CELL_TYPE_STRING ).setCellValue( toExcelText(nrow.get(key)) );
				}

			}

		}

	}

	@Override
	public Map<String, NList> readFrom( File excelFile ) throws IoException {

		Map<String, NList> result = new LinkedHashMap<>();

		try (
			FileInputStream fis      = new FileInputStream( excelFile );
			Workbook        workbook = new HSSFWorkbook( fis )
		) {

			for( int sheetIndex = 0, limit = workbook.getNumberOfSheets(); sheetIndex < limit; sheetIndex++ ) {
				result.put( workbook.getSheetName( sheetIndex ), readFrom(workbook, sheetIndex) );
			}

		} catch( IOException e ) {
			throw new IoException( e, "Error on reading excel file[{}].", excelFile );
		}

		return result;

	}


	private NList readFrom( Workbook workbook, int sheetIndex ) {

		NList result = new NList();

		Sheet sheet = workbook.getSheetAt( sheetIndex );

		if( sheet == null ) return result;

		NMap header = getExcelColumnHeader( sheet );

		if( header.size() == 0 ) return result;

		FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

		for( int idxRow = 1, maxRowCnt = sheet.getPhysicalNumberOfRows(); idxRow < maxRowCnt; idxRow++ ) {

			Row  row  = sheet.getRow( idxRow );

			NMap data = new NMap();

			for( int idxColumn = 0, maxColumnCnt = row.getPhysicalNumberOfCells(); idxColumn < maxColumnCnt; idxColumn++ ) {

				Cell cell = row.getCell( idxColumn );

				if( cell == null ) continue;

				String key = header.getString( idxColumn );

				switch( cell.getCellType() ) {

					case Cell.CELL_TYPE_FORMULA :

						if( StringUtil.isNotEmpty( cell ) ) {

							switch( evaluator.evaluateFormulaCell(cell) ) {
								case Cell.CELL_TYPE_NUMERIC :
									data.put( key, getNumericCellValue(cell) );
									break;
								case Cell.CELL_TYPE_BOOLEAN :
									data.put( key, cell.getBooleanCellValue() );
									break;
								case Cell.CELL_TYPE_STRING  :
									data.put( key, cell.getStringCellValue() );
									break;
							}

						} else {
							data.put( key, cell.getStringCellValue() );
						}

						break;

					case Cell.CELL_TYPE_NUMERIC :
						data.put( key, getNumericCellValue(cell) );
						break;

					case Cell.CELL_TYPE_BOOLEAN :
						data.put( key, cell.getBooleanCellValue() );
						break;
					default :
						data.put( key, cell.getStringCellValue() );

				}


			}

			result.addRow( data );

		}

		return result;

	}

	private NMap getExcelColumnHeader( Sheet sheet ) {

    	NMap result = new NMap();

    	if( sheet == null || sheet.getPhysicalNumberOfRows() == 0 ) return result;

    	Row row = sheet.getRow( 0 );

    	for( int i = 0, iCnt = row.getPhysicalNumberOfCells(); i < iCnt; i++ ) {

    		Cell cell = row.getCell( i );

    		result.put( i, cell.getStringCellValue() );

    	}

    	return result;

    }

    private Object getNumericCellValue( Cell cell ) {

		double val = cell.getNumericCellValue();

		if( isCellDateFormatted(cell) ) {

			String dateFormat = cell.getCellStyle().getDataFormatString();

			return new CellDateFormatter(dateFormat).format( HSSFDateUtil.getJavaDate(val) );

		} else {

			long fixedVal = (long) val;

			if( val - fixedVal == 0 ) {

				if( fixedVal < Integer.MAX_VALUE ) {
					return (int) fixedVal;
				} else {
					return fixedVal;
				}

			} else {
				return cell.getNumericCellValue();
			}
		}

    }


    private boolean isCellDateFormatted( Cell cell ) {

    	if (cell == null) return false;

        if ( ! DateUtil.isValidExcelDate(cell.getNumericCellValue()) ) return false;

        CellStyle style = cell.getCellStyle();
        if( style == null ) return false;

        int    formatIndex = style.getDataFormat();
        String format      = style.getDataFormatString();

        // Apache poi's missing logic
        format = format.replaceAll( "([^\\\\])\".*?[^\\\\]\"", "$1" );

        return DateUtil.isADateFormat( formatIndex, format) ;

    }

}
