package org.nybatis.core.db.sqlRepository;

import org.nybatis.core.conf.Const;
import org.nybatis.core.db.session.executor.util.QueryParameter;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.db.sql.sqlMaker.QueryResolver;
import org.nybatis.core.db.sql.sqlNode.SqlNode;
import org.nybatis.core.exception.unchecked.JsonPathNotFoundException;
import org.nybatis.core.exception.unchecked.ParseException;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.file.vo.User;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.util.StringUtil;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class SqlGrammerTest {

	SqlRepository repository = new SqlRepository();

	@BeforeClass
	public void before() {
		repository.readFromDirectory( Paths.get( Const.path.getBase(), "/config/db/grammer" ).toString(), "testDbResource" );
	}

	@Test
	public void load() {
		NLogger.debug( repository.toString() );
	}

    @Test
	public void readTest() throws IOException, ParseException {

		QueryParameter param = new QueryParameter();

		param.put( "id", "2" );

		SqlNode sql = SqlRepository.get( "Grammer.Merong" );

		if( sql == null ) {
			fail( "Sql is not extracted." );
		}

		NLogger.debug( sql.getText( param ) );

	}

    @Test
    public void refValidTest() {

		QueryParameter param = new QueryParameter();

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

		QueryParameter param = new QueryParameter();

    	param.put( "id", "1" );

    	try {

    		SqlRepository.get( "Grammer.refTestInvalid" ).getText( param );

    		fail( "SqlConfigurationException is not raised." );

    	} catch( SqlConfigurationException e ) {
    		throw e;
    	}

    }



	@Test
	public void forEach() {

		QueryParameter param = new QueryParameter();

		param.put( "age", 39 );
		param.put( "names", Arrays.asList("hwasu", "hwajong") );

		String sql = getSql( "Grammer.forEach", param );

		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND age > #{age} AND ( names LIKE '%' || #{names[0]} || '%' || #{names[0].index} -- #{index} OR names LIKE '%' || #{names[1]} || '%' || #{names[1].index} -- #{index} ) ORDER BY title" );
		assertEquals( param.toString(), "{age=39, names=[hwasu, hwajong], names[0]=hwasu, names[0].index=0, names[1]=hwajong, names[1].index=1}" );

	}

	@Test
	public void forEachPrimitiveParam() {

		QueryParameter param = new QueryParameter();

		param.put( "names", Arrays.asList("hwasu", "hwajong") );

		String sql = getSql( "Grammer.forEachPrimitiveParam", param );

		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND ( SELECT #{names[0].index} AS index, #{names[0]} AS name FROM DUAL UNION ALL SELECT #{names[1].index} AS index, #{names[1]} AS name FROM DUAL ) ORDER BY title" );
		assertEquals( param.toString(), "{names=[hwasu, hwajong], names[0]=hwasu, names[0].index=0, names[1]=hwajong, names[1].index=1}" );

	}

	@Test
	public void forEachAttributeFromParam() {

		QueryParameter param = new QueryParameter();

		param.put( "names", Arrays.asList("hwasu", "hwajong") );
		param.put( "AND", "AND MERONG" );
		param.put( "CLOSE", "CLOSE MERONG" );
		param.put( "DELIMETER", "DELIMETER MERONG" );

		String sql = getSql( "Grammer.forEachAttributeFromParam", param );


	}


	@Test
	public void forEachForVoParam() {

		QueryParameter param = new QueryParameter();

		param.put( "age", 39 );

		List children = new ArrayList<>();

		children.add( new NMap( "{ 'name' : 'juho',    'age' : 5 }" ) );
		children.add( new NMap( "{ 'name' : 'hwasu',   'age' : 39 }") );
		children.add( new NMap( "{ 'name' : 'hwajong', 'age' : 38 }" ) );

		param.put( "children", children );

		String sql = getSql( "Grammer.forEachForVoParam", param );

		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND age > #{age} AND ( names LIKE '%' || #{children[1].name} || '%' || #{children[1].index} -- #{index} OR ) AND ( names LIKE '%' || #{children[1].name} || '%' || #{children[1].index} OR age > #{children[1].age} -- #{index} OR names LIKE '%' || #{children[2].name} || '%' || #{children[2].index} OR age > #{children[2].age} -- #{index} ) ORDER BY title" );
		assertEquals( param.toString(), "{age=39, children=[{name=juho, age=5}, {name=hwasu, age=39}, {name=hwajong, age=38}], children[1].name=hwasu, children[1].index=1, children[1].age=39, children[2].name=hwajong, children[2].age=38, children[2].index=2}" );

	}

	@Test
	public void groupTest() {

		QueryParameter param = new QueryParameter();

//		param.put( "name", "google" );
		param.put( "age", "36" );
//		param.put( "job", "student" );


		String sql = SqlRepository.get( "Grammer.group" ).getText( param );

		NLogger.debug( sql );
		NLogger.debug( param );

	}

	@Test
	public void ifElse() {

		QueryParameter param = new QueryParameter();

		String sql = null;

		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = 'DANTE' AND age = 'DEFAULT' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.put( "name", "A" );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = #{name} AND age = 'DEFAULT' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.put( "age", "15" );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = #{name} AND age = 'TEENAGE' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.put( "age", "41" );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = #{name} AND age = 'RATHER OLD' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.put( "age", "60" );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = #{name} AND age = 'DEFAULT' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.remove( "name" );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = 'DANTE' AND age = 'DEFAULT' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.put( "key", 1 );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = 'DANTE' AND age = 'DEFAULT' AND key = 'DEFAULT' AND val = 'DEFAULT'" );

		param.put( "key", 2 );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = 'DANTE' AND age = 'DEFAULT' AND key = '3' AND val = 'DEFAULT'" );

		param.put( "key", 4 );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = 'DANTE' AND age = 'DEFAULT' AND key = '5' AND val = 'DEFAULT'" );

		param.put( "val", 5 );
		sql = getSql( "Grammer.ifElseSwitch", param );
		assertEquals( sql, "SELECT * FROM TABLE_SAMPLE WHERE 1=1 AND name = 'DANTE' AND age = 'DEFAULT' AND key = '5' AND val = '4'" );


	}

	@Test
	public void nestedForLoop() {

		String json = FileUtil.readFrom( Const.path.getConfigDatabase() +  "/grammer/NestedLoopTestParameter.json" );

		QueryParameter param = new QueryParameter();

		param.bind( json );

//		param = new NMap();

		getQueryResolver( "Grammer.nestedForLoop", param );

	}

	@Test
	public void nestedForLoopAboutSingleParameter() throws JsonPathNotFoundException {

		String json = FileUtil.readFrom( Const.path.getConfigDatabase() +  "/grammer/NestedLoopTestParameter.json" );

		NMap map = new NMap( json );

		QueryParameter param = new QueryParameter( map.getByJsonPath("user") );

		getQueryResolver( "Grammer.nestedForLoop", param );

	}


	private String getSql( String sqlId, QueryParameter param ) {


		String sql = SqlRepository.get( sqlId ).getText( param );

		sql = StringUtil.compressSpaceOrEnter( sql );

		NLogger.debug( sql );
		NLogger.debug( param );

		return sql;

	}

	private QueryResolver getQueryResolver( String sqlId, QueryParameter param ) {

		String sql = SqlRepository.get( sqlId ).getText( param );

		String line = "----------------------------------------------------------";
		NLogger.debug( line );
		NLogger.debug( sql );

		QueryResolver queryResolver = new QueryResolver( sql, param );

		NLogger.debug( ">> getDebugSql " + line );
		NLogger.debug( queryResolver.getDebugSql() );
		NLogger.debug( ">> getSql " + line );
		NLogger.debug( queryResolver.getSql() );
		NLogger.debug( ">> getBindParam " + line );
		NLogger.debug( queryResolver.getBindParams() );
		NLogger.debug( line );

		return queryResolver;


	}

}
