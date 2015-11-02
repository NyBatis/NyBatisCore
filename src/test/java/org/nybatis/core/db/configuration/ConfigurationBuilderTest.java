package org.nybatis.core.db.configuration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.configuration.builder.ConfigurationBuilder;
import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

public class ConfigurationBuilderTest {

	@Test
	public void test() {

		new ConfigurationBuilder( new File( Const.path.getConfigDatabase() + "/config.xml") );

		try {

			DataSource dataSource = DatasourceManager.get();

			Connection conn = dataSource.getConnection();

	        PreparedStatement psmt = conn.prepareStatement( "SELECT * FROM TB_DP_LIST_PROD WHERE tenant_id = ? AND rownum < 10" );

	        ResultSet rs = psmt.executeQuery();

	        while( rs.next() ) {
	        	NLogger.debug( rs.getString( "PROD_ID" ) );
	        }

	        rs.close();
	        psmt.close();
	        conn.close();


        } catch( SQLException e ) {
        	NLogger.error( e );
        }

	}

}
