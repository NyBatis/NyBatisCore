package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.xml.node.Node;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class SqlBuilder {

	private PropertiesBuilder prop;
	private String            basePath;

	public SqlBuilder( Node environment, PropertiesBuilder propertiesBuilder, File basePath ) {
		this.prop     = propertiesBuilder;
		this.basePath = basePath.getAbsolutePath();
		setSql( environment );
	}

	private void setSql( Node environment) {

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
        Path   realPath  = FileUtil.isExist( inputPath ) ? Paths.get( inputPath ) : Paths.get( basePath, inputPath );
		new SqlRepository().readFrom( realPath, environmentId );
	}

}
