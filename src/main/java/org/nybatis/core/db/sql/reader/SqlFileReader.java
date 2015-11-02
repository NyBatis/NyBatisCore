package org.nybatis.core.db.sql.reader;

import org.nybatis.core.db.sql.repository.LoadedFile;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.IoException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;

import java.io.File;

public class SqlFileReader {

	private File                     file;
	private XmlSqlParser             xmlParser;

	public SqlFileReader( File file, String environmentId ) {
		this.file      = file;
		this.xmlParser = new XmlSqlParser( environmentId );
	}

	public void read() {

		String baseId = getMainId( file );

		try {

			NXml xmlReader = new NXmlDeformed( file );

			for( Node node : xmlReader.getChildElements("sql") ) {

				String id = node.getAttr( "id" );

				if( StringUtil.isEmpty( id ) ) {
					NLogger.error( "Sql Id is missing in file[{}]\n{}", file, node );
					continue;
				}

				String sqlId = String.format( "%s.%s", baseId, id );

				checkSqlIdDuplication( sqlId );

				SqlNode sql = xmlParser.parse( sqlId, node );

				if( sql == null ) continue;

				sql.setMainId( baseId );

				SqlRepository.put( sqlId, sql );

				LoadedFile loadedFile = new LoadedFile( xmlParser.getEnvironmentId(), file );

				SqlRepository.sqlRepositoryFiles.put( sqlId, loadedFile );

			}

		} catch( ParseException | IoException | SqlParseException | DatabaseConfigurationException e ) {
			NLogger.error( "Error on reading file({}).. {}", file, e );
		}

	}

	private void checkSqlIdDuplication( String sqlId ) {

		if( ! SqlRepository.sqlRepository.containsKey( sqlId ) ) return;

    	LoadedFile existedFile = getLoadedFileInfo( sqlId );

		SqlRepository.sqlRepository.clear();
		SqlRepository.sqlRepositoryFiles.clear();

    	String errorGuideMessage = null;

    	if( file.toString().equals( existedFile.getFilePath() ) ) {
    		if( ! xmlParser.getEnvironmentId().equals( existedFile.getEnvironmentId() )) {
    			errorGuideMessage = StringUtil.format( "Same SqlId({}) existed in environment({}). One SqlId must be affilated with One unique environment.", sqlId, existedFile.getEnvironmentId() );
    		}
    	}

    	if( errorGuideMessage == null ) {
    		errorGuideMessage = "FileName itself is used as SqlPath's main key. So rename other file to avoid conflict.";
    	}

    	throw new DatabaseConfigurationException(
    			"There is duplicated sql (id:{})."
    			+ "\n\t- environmentId        : {}"
    			+ "\n\t- file                 : {}"
    			+ "\n\t- existedEnvironmentId : {}"
    			+ "\n\t- existedFile          : {}"
    			+ "\n{}",
    			sqlId, xmlParser.getEnvironmentId(), file, existedFile.getEnvironmentId(), existedFile.getFilePath(), errorGuideMessage );

	}

	private LoadedFile getLoadedFileInfo( String sqlId ) {

		LoadedFile existedFile = SqlRepository.sqlRepositoryFiles.get( sqlId );

		return existedFile == null ? new LoadedFile() : existedFile;

	}

	private String getMainId( File file ) {
		return FileUtil.removeExtention( file.getName() );
	}

}
