package org.nybatis.core.db.sql.reader;

import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.exception.unchecked.UncheckedIOException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SqlParseException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.NXml;
import org.nybatis.core.xml.NXmlDeformed;
import org.nybatis.core.xml.node.Node;

public class SqlFileReader {

	private String       file;
	private XmlSqlParser xmlParser;

	public SqlFileReader( String file, String environmentId ) {
		this.file      = FileUtil.nomalizeSeparator( file );
		this.xmlParser = new XmlSqlParser( environmentId );
	}

	public void read() {

		String baseId = getMainId( file );

		try {

			NXml xmlReader = new NXmlDeformed( FileUtil.readResourceFrom(file) );

			for( Node node : xmlReader.getChildElements("sql") ) {

				String id = node.getAttr( "id" );

				if( StringUtil.isEmpty( id ) ) {
					NLogger.error( "Sql Id is missing in file[{}]\n{}", file, node );
					continue;
				}

				String sqlId = String.format( "%s.%s", baseId, id );

				SqlNode sql = xmlParser.parse( sqlId, node );

				if( sql == null ) continue;

				sql.setMainId( baseId );

				SqlRepository.put( sqlId, sql, file );

			}

		} catch( ParseException | UncheckedIOException | SqlParseException | DatabaseConfigurationException e ) {
			NLogger.error( "Error on reading file({}).. {}", file, e );
		}

	}

	private String getMainId( String file ) {
		int index = file.lastIndexOf( "/" );
		if( index >= 0 ) {
			file = file.substring( index + 1 );
		}
		return FileUtil.removeExtention( file );
	}

}
