package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.node.Node;

import java.util.List;

public class SqlBuilder {

	private PropertyResolver prop = new PropertyResolver();
	private String           basePath;

	public SqlBuilder( String basePath ) {
		this.basePath = basePath;
	}

	public SqlBuilder( PropertyResolver propertyResolver, String basePath ) {
		this.prop     = propertyResolver;
		this.basePath = basePath;
	}

	public void setSql( Node environment ) {

		String environmentId = environment.getAttrIgnoreCase( "id" );

		if( StringUtil.isEmpty( environmentId ) ) return;

		List<Node> paths = environment.getChildElements( "sqlPath" );
		paths.addAll( environment.getChildElements( "sqlpath" ) );

		for( Node sqlPath : paths ) {
			for( Node path : sqlPath.getChildElements( "path" ) ) {
				readSql( path, environmentId );
			}
		}

	}

	private void readSql( Node path, String environmentId ) {

		String inputPath = prop.getPropValue(path.getText());
		String realPath;

		if( FileUtil.exists( inputPath ) ) {
			realPath = inputPath;
		} else {
			realPath = FileUtil.getPath( basePath, inputPath );
		}

		if( realPath.endsWith(".xml") ) {
			new SqlRepository().readFromFile( realPath, environmentId );
		} else {
			new SqlRepository().readFromDirectory( realPath, environmentId );
		}

	}

}
