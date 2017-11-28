package org.nybatis.core.db.orm.table.customAnnotationInsert;

import java.util.List;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.Person;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.PersonProperty;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.core.JsonConverter;
import org.nybatis.core.reflection.mapper.NObjectSqlMapper;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Column annotation test in ORM sql
 *
 * boolean   to  Y/N
 * Map,List  to  JsonText
 *
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class OrmTableInsertAnnotationTest {

    public static final String envirionmentId = "h2";

    @BeforeClass
    public void init() {
        DatabaseConfigurator.build( "/config/dbLogicTest/tableCreator/configTableCreation.xml");
    }

    @Test
    public void basicJsonBind() {

        JsonConverter $ = new JsonConverter( new NObjectSqlMapper() );

        String json = $.toJson( getSampleData(), true );
        NLogger.debug( json );

        NLogger.debug( ">> convert json from ListString" );
        Person person = $.toBeanFrom( json, Person.class );
        NLogger.debug( Reflector.toJson( person, true ) );

        // convert json from List (not list string)
        json  = "{\"id\" : \"01001\",\"name\" : \"Jhon Dow\",\"used\" : \"N\",\"age\" : 21,\"property\" : [{\"id\":\"01001\",\"nation\":\"America\",\"location\":\"Delaware\"}], \"mixedProperty\" : {\"test\":{\"id\":\"01001\",\"nation\":\"America\",\"location\":\"Delaware\"}}, \"nmap\" : {\"ichigo\":\"yata\"} }";
        NLogger.debug( json );
        person = $.toBeanFrom( json, Person.class );

        NLogger.debug( ">> convert json from List" );
        NLogger.debug( Reflector.toJson( person, true ) );

    }

    @Test
    public void test() {

        OrmSession<Person> session = SessionManager.openOrmSession( Person.class );
        session.table().drop().set();

        Person sampleData = getSampleData();
        NLogger.debug( Reflector.toJson( sampleData ) );

        session.insert( sampleData );
        List<Person> list = session.list().select();
        NLogger.debug( list );

        Assert.assertEquals( list.size(), 1 );
        Assert.assertTrue( list.get(0).getProperty().iterator().next() instanceof PersonProperty );

    }

    private Person getSampleData() {
        PersonProperty property = new PersonProperty( "01001", "America", "Delaware" );

        Person person = new Person( "01001", "Jhon Dow", 21  );
        person.getProperty().add( property );
        person.setNmap( new NMap("{'ichigo':'yata'}") );
        person.addMixedProperty( "test", property );
        return person;
    }

}
