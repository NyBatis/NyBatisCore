package org.nybatis.core.db.sqlRepository;

import java.io.IOException;
import java.nio.file.Paths;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.fail;

public class SqlRepositoryTest {

	SqlRepository repository = new SqlRepository();

	@BeforeClass
	public void before() {
		repository.readFrom( Paths.get(Const.path.getBase(), "/config/db/grammer" ), "testDbResource" );
	}

	@Test
	public void load() {
		NLogger.debug( repository.toString() );
	}

    @Test
	public void readTest() throws IOException, ParseException {

		NMap param = new NMap();

		param.put( "id", "1" );

		SqlNode sql = SqlRepository.get( "Grammer.Merong" );

		if( sql == null ) {
			fail( "Sql is not extracted." );
		}

		NLogger.debug( sql.getText( param ) );

	}

    @Test
    public void refValidTest() {

		NMap param = new NMap();

		param.put( "id", "1" );


		try {

			String sql = SqlRepository.get( "Grammer.refTestValid" ).getText( param );

			NLogger.debug( sql );

		} catch( SqlConfigurationException e ) {
			NLogger.error( e.getMessage() );
		}

    }

    @Test( expectedExceptions = SqlConfigurationException.class )
    public void refInalidTest() {

    	NMap param = new NMap();

    	param.put( "id", "1" );

    	try {

    		SqlRepository.get( "Grammer.refTestInvalid" ).getText( param );

    		fail( "SqlConfigurationException is not raised." );

    	} catch( SqlConfigurationException e ) {
    		throw e;
    	}

    }

}
