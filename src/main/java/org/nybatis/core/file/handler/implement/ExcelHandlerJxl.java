package org.nybatis.core.file.handler.implement;

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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExcelHandlerJxl extends ExcelHandler {

    @Override
    public void writeNListTo( OutputStream outputStream, Map<String, NList> data, boolean isXlsx ) throws IoException {

        WritableWorkbook workbook = null;

        try {

            workbook = Workbook.createWorkbook( outputStream );

            for( String sheetName : data.keySet() ) {
                writeTo( workbook, sheetName, data.get( sheetName ) );
            }

            workbook.write();

        } catch( IOException | WriteException e ) {
            throw new IoException( e, "Error on writing excel to output stream." );

        } finally {

            if( workbook != null ) {
                try { workbook.close(); } catch( WriteException | IOException e ) {}
            }

            if( outputStream != null ) {
                try { outputStream.close(); } catch( IOException e ) {}
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
    public NList readFrom( InputStream inputStream, String sheetName ) throws IoException {
        return readFrom( inputStream, ( workbook, result ) -> {
            result.put( sheetName, readFrom( workbook, sheetName ) );
        } ).get( sheetName );
    }

    @Override
    public NList readFirstSheetFrom( InputStream inputStream ) throws IoException {
        return readFrom( inputStream, ( workbook, result ) -> {
            Sheet sheet = workbook.getSheet( 0 );
            if( sheet != null ) {
                result.put( "FirstSheet", readFrom( workbook, sheet.getName() ) );
            }
        } ).get( 0 );
    }

    @Override
    public Map<String, NList> readFrom( InputStream inputStream ) throws IoException {
        return readFrom( inputStream, ( workbook, result ) -> {
            for( String sheetName : workbook.getSheetNames() ) {
                result.put( sheetName, readFrom(workbook, sheetName) );
            }
        } );
    }

    private interface Reader {
        void read( Workbook workbook, Map<String,NList> result );
    }

    private Map<String, NList> readFrom( InputStream inputStream, Reader reader ) throws IoException {

        Map<String, NList> result   = new LinkedHashMap<>();
        Workbook           workBook = null;

        try {

            workBook = Workbook.getWorkbook( inputStream );
            reader.read( workBook, result );
            return result;

        } catch( IOException | BiffException e ) {
            throw new IoException( e, "Error on reading excel data from input stream." );
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
