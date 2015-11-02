package org.nybatis.core.file.handler.implement;

import java.io.File;
import java.io.IOException;

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

public class ExcelHandlerJxl extends ExcelHandler {

	@Override
    public void writeTo( File excelFile, NList data, String sheetName ) throws IoException {

		excelFile = FileUtil.makeFile( excelFile );

		if( FileUtil.isNotExist(excelFile) ) {
			throw new IoException( "ExcelFile[{}] to write is not exist.", excelFile );
		}

        WritableWorkbook workbook = null;
        WritableSheet    sheet    = null;

        try {

            workbook = Workbook.createWorkbook( excelFile );
            sheet    = workbook.createSheet( sheetName, 0 );

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

	@Override
    public NList readFrom( File excelFile, String sheetName ) throws IoException {

		if( FileUtil.isNotExist(excelFile) ) {
			throw new IoException( "ExcelFile[{}] to read is not exist.", excelFile );
		}

        NList result = new NList();

        Workbook workBook = null;

        try {

            workBook = Workbook.getWorkbook( excelFile );

            Sheet sheet = workBook.getSheet( sheetName );

            if( sheet == null ) throw new IOException( String.format("There is no sheet name [%s]", sheetName) );

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

        } catch( IOException | BiffException e ) {
            throw new IoException( e, "Error on reading excel file[{}].", excelFile );

        } finally {
            if( workBook != null ) {
                workBook.close();
            }
        }

        return result;

    }

}
