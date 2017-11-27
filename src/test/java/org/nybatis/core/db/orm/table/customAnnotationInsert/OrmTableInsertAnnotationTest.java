package org.nybatis.core.db.orm.table.customAnnotationInsert;

import java.util.List;
import org.nybatis.core.db.configuration.builder.DatabaseConfigurator;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.Person;
import org.nybatis.core.db.orm.table.customAnnotationInsert.entity.PersonProperty;
import org.nybatis.core.db.session.SessionManager;
import org.nybatis.core.db.session.type.orm.OrmSession;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.reflection.Reflector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
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
    public void jsonTest() {

        // TODO

        String json = Reflector.toJson( getSampleData(), true );
        NLogger.debug( json );

        Person person = Reflector.toBeanFrom( json, Person.class );
        NLogger.debug( Reflector.toJson( person, true ) );

        json  = "{\"id\" : \"01001\",\"name\" : \"Jhon Dow\",\"age\" : 21,\"used\" : \"N\",\"property\" : [{\"id\":\"01001\",\"nation\":\"America\",\"location\":\"Delaware\"}] }";

        person = Reflector.toBeanFrom( json, Person.class );
        NLogger.debug( Reflector.toJson( person, true ) );

    }

    @Test
    public void test() {

        OrmSession<Person> session = SessionManager.openOrmSession( Person.class );
        session.table().drop().set();
        session.insert( getSampleData() );

        List<Person> list = session.list().select();

        NLogger.debug( list );

    }

    private Person getSampleData() {
        Person person = new Person( "01001", "Jhon Dow", 21  );
        person.getProperty().add( new PersonProperty( "01001", "America", "Delaware" ) );
        return person;
    }


}
