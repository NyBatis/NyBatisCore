package org.nybatis.core.util;

import java.io.IOException;

import org.nybatis.core.conf.Const;
import org.nybatis.core.file.ExcelUtil;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.testng.annotations.Test;

public class ExcelFileUtilTest {

	@Test
	public void readFromExcel() throws IOException {

		String xlsFileToRead   = Const.path.getBase() + "/org/nybatis/core/util/Test.xls";
		String xlsFileToWrite  = Const.path.getBase() + "/nayasis/common/util/TestWrite.xls";
		String xlsxFileToRead  = Const.path.getBase() + "/org/nybatis/core/util/Test.xlsx";
		String xlsxFileToWrite = Const.path.getBase() + "/nayasis/common/util/TestWrite.xlsx";

		NLogger.debug( ">> Read Xls : [{}]", xlsFileToRead );
		NList tableXls  = ExcelUtil.readFrom( xlsFileToRead, "Sheet1" );
		NLogger.debug( tableXls  );

		NLogger.debug( ">> Write Xls : [{}]", xlsFileToWrite );
		ExcelUtil.writeTo( xlsFileToWrite, tableXls  );
		tableXls  = ExcelUtil.readFrom( xlsFileToWrite );
		NLogger.debug( tableXls );

//		FileUtil.delete( xlsFileToWrite  );

		NLogger.debug( ">> Read Xlsx : [{}]", xlsxFileToRead );
		NList tableXlsx = ExcelUtil.readFrom( xlsxFileToRead, "Sheet1" );
		NLogger.debug( tableXlsx );

		NLogger.debug( ">> Write Xlsx : [{}]", xlsxFileToWrite );
		ExcelUtil.writeTo( xlsxFileToWrite, tableXlsx );
		tableXlsx = ExcelUtil.readFrom( xlsxFileToWrite );
		NLogger.debug( tableXlsx );

		FileUtil.delete( xlsxFileToWrite );

	}

}
