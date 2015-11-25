package org.nybatis.core.file.handler.implement;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.file.handler.ExcelHandler;
import org.nybatis.core.model.NList;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.validation.Validator;

public class ExcelHandlerJxl extends ExcelHandler {

    @Override
    public void writeTo( File excelFile, Map<String, NList> data ) throws IoException {

		excelFile = FileUtil.makeFile( excelFile );

		if( FileUtil.isNotExist(excelFile) ) {
			throw new IoException( "ExcelFile[{}] to write is not exist.", excelFile );
		}

        WritableWorkbook workbook = null;

        try {

            workbook = Workbook.createWorkbook( excelFile );

            for( String sheetName : data.keySet() ) {
                writeTo( workbook, sheetName, data.get(sheetName) );
            }

            workbook.write();

        } catch( IOException | WriteException e ) {
        	throw new IoException( e, "Error on writing excel file[{}].", excelFile );

        } finally {

            if( workbook != null ) {
                try {
                    workbook.close();
                } catch( WriteException | IOException e ) {}
            }

        }

    }

    private void writeTo( WritableWorkbook workbook, String sheetName, NList data ) throws WriteException {

        WritableSheet sheet = workbook.createSheet( sheetName, workbook.getNumberOfSheets() );

        // Write Header

        WritableCellFormat headerCellFormat = new WritableCellFormat();

        headerCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );
        headerCellFormat.setBackground( Colour.GRAY_25 );

        int column = 0;

        for( String alias : data.getAliases() ) {
            sheet.addCell( new Label( column++, 0, toExcelText( alias ), headerCellFormat ) );
        }

        // Write Body

        WritableCellFormat bodyCellFormat = new WritableCellFormat();

        bodyCellFormat.setBorder( Border.ALL, BorderLineStyle.THIN );

        int rowCnt = data.size();
        int colCnt = data.keySize();

        for( int row = 0; row < rowCnt; row++ ) {

            for( int col = 0; col < colCnt; col++ ) {
                sheet.addCell( new Label( col, row + 1, toExcelText( data.getBy( col, row ) ), bodyCellFormat ) );
            }

        }


    }

    @Override
    public NList readFrom( File excelFile, String sheetName ) throws IoException {
        return readFrom( excelFile, ( workbook, result ) -> {
            result.put( sheetName, readFrom( workbook, sheetName ) );
        } ).get( sheetName );
    }

    @Override
    public NList readFirstSheetFrom( File excelFile ) throws IoException {
        return readFrom( excelFile, ( workbook, result ) -> {
            Sheet sheet = workbook.getSheet( 0 );
            if( sheet != null ) {
                result.put( "FirstSheet", readFrom( workbook, sheet.getName() ) );
            }
        } ).get( 0 );
    }

    @Override
    public Map<String, NList> readFrom( File excelFile ) throws IoException {
        return readFrom( excelFile, ( workbook, result ) -> {
            for( String sheetName : workbook.getSheetNames() ) {
                result.put( sheetName, readFrom(workbook, sheetName) );
            }
        } );
    }

    private interface Reader {
        void read( Workbook workbook, Map<String,NList> result );
    }

    private Map<String, NList> readFrom( File excelFile, Reader reader ) throws IoException {

        if( FileUtil.isNotExist(excelFile) ) {
            throw new IoException( "ExcelFile[{}] to read is not exist.", excelFile );
        }

        Map<String, NList> result = new LinkedHashMap<>();

        Workbook workBook = null;

        try {

            workBook = Workbook.getWorkbook( excelFile );

            reader.read( workBook, result );

            return result;

        } catch( IOException | BiffException e ) {
            throw new IoException( e, "Error on reading excel file[{}].", excelFile );
        } finally {
            if( workBook != null ) {
                workBook.close();
            }
        }

    }

    private NList readFrom( Workbook workBook, String sheetName ) {

        NList result = new NList();

        Sheet sheet = workBook.getSheet( sheetName );

        if( sheet == null ) return result;

        int colCnt = sheet.getColumns();
        int rowCnt = sheet.getRows();

        for( int idxCol = 0; idxCol < colCnt; idxCol++ ) {

            String columnName = null;

            for( int idxRow = 0; idxRow < rowCnt; idxRow++ ) {

                Cell cell = sheet.getCell( idxCol, idxRow );

//                    NLogger.debug( "key : {}, val : {}, format : {}", columnName, cell.toString(), cell.getCellFormat() );

                if( idxRow == 0 ) {
                    columnName = cell.getContents();

                } else {
                    result.addRow( columnName, cell.getContents() );
                }

            }
        }

        return result;

    }

}
