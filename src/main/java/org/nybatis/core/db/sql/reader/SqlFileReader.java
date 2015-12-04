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

				checkEnvironmentDuplication( sqlId );

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

	private void checkEnvironmentDuplication( String sqlId ) {

		if( ! SqlRepository.isExist( sqlId ) ) return;

		SqlRepository.get( sqlId ).addEnvironmentId( xmlParser.getEnvironmentId() );

    	LoadedFile existedFile = getLoadedFileInfo( sqlId );

		NLogger.trace( "Sql({}) has multiple environment({}) because of sql mapper file({}).", sqlId, existedFile.getEnvironmentId(), existedFile.getFilePath() );

	}

	private LoadedFile getLoadedFileInfo( String sqlId ) {

		LoadedFile existedFile = SqlRepository.sqlRepositoryFiles.get( sqlId );

		return existedFile == null ? new LoadedFile() : existedFile;

	}

	private String getMainId( File file ) {
		return FileUtil.removeExtention( file.getName() );
	}

}
