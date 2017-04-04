package org.nybatis.core.reflection.core;

import org.adrianwalker.multilinestring.Multiline;
import org.nybatis.core.reflection.Reflector;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.*;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-31
 */
public class BeanMergerTest {

    private BeanMerger merger = new BeanMerger();

    @Test
    public void testMergeMap() throws Exception {

        Map a = Reflector.toMapFrom( json1 );
        Map b = Reflector.toMapFrom( json2 );
        Map merge = merger.merge( b, a );

        System.out.println( merge );
        assertEquals( merge.toString(), "{name=another name, age=18, profile=[{name=q1, age=1}, {name=q2, age=2}, {name=q3, age=3}, {name=p4, age=4}], meta={key=meta1, id=1234, locale=korean}, dept=No Dept}" );

    }

    @Test
    public void testMergeBean() throws Exception {

        A a = Reflector.toBeanFrom( json1, A.class );
        B b = Reflector.toBeanFrom( json2, B.class );

        System.out.println( a );
        A merge = merger.merge( b, a );

        System.out.println( merge );
        assertEquals( merge.toString(), "{\"name\":\"another name\",\"age\":18,\"dept\":\"No Dept\",\"profile\":[{\"name\":\"q1\",\"age\":1},{\"name\":\"q2\",\"age\":2},{\"name\":\"q3\",\"age\":3},{\"name\":\"p4\",\"age\":4}],\"meta\":{\"key\":\"meta1\",\"id\":1234,\"locale\":\"korean\"}}" );

    }

    @Test
    public void testMergeJsonToBean() throws Exception {

        A a = Reflector.toBeanFrom( json1, A.class );

        System.out.println( a );
        A merge = merger.merge( json2, a );

        System.out.println( merge );
        assertEquals( merge.toString(), "{\"name\":\"another name\",\"age\":18,\"dept\":\"No Dept\",\"profile\":[{\"name\":\"q1\",\"age\":1},{\"name\":\"q2\",\"age\":2},{\"name\":\"q3\",\"age\":3},{\"name\":\"p4\",\"age\":4}],\"meta\":{\"key\":\"meta1\",\"id\":1234,\"locale\":\"korean\"}}" );

    }

    @Test
    public void testMergeCollection() throws Exception {

        List a = new ArrayList();
        List b = new ArrayList();

        a.add( "A" );
        a.add( "B" );
        a.add( "C" );
        a.add( "D" );

        b.add( "1" );
        b.add( "" );
        b.add( "3" );

        List merge01 = (List) merger.merge( b, a );
        List merge02 = (List) merger.merge( b, null );

        System.out.println( merge01 );
        System.out.println( merge02 );

        assertEquals( "[1, B, 3, D]", merge01.toString() );
        assertEquals( "[1, , 3]", merge02.toString() );

    }

    @Test
    public void testMergeArray() {

        List a = Arrays.asList( "","B","","D" );
        String[] b = new String[] { "1", "", "3" };

        System.out.println( Arrays.toString( b ) );

        String[] merge = merger.merge( a, b );

        System.out.println( Arrays.toString( merge ) );


    }

    /**
    {
        "name" : "name",
        "age"  : 18,
        "profile" : [{
            "name" : "p1",
            "age"  : 1
        },{
            "name" : "p2",
            "age"  : 2
        },{
            "name" : "p3",
            "age"  : 3
        },{
            "name" : "p4",
            "age"  : 4
        }],
        "meta" : {
            "key" : "meta1",
            "id"  : 1234
        }
    }
     */
    @Multiline
    private String json1;

    /**
    {
        "name" : "another name",
        "dept" : "No Dept",
        "profile" : [{
            "name" : "q1"
        },{
            "name" : "q2"
        },{
            "name" : "q3"
        }],
        "meta" : {
            "locale" : "korean"
        }
    }
     */
    @Multiline
    private String json2;

    private static class A {
        private String name;
        private int    age;
        private String dept;
        private List<Profile> profile;
        private Meta meta;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge( int age ) {
            this.age = age;
        }

        public String getDept() {
            return dept;
        }

        public void setDept( String dept ) {
            this.dept = dept;
        }

        public List<Profile> getProfile() {
            return profile;
        }

        public void setProfile( List<Profile> profile ) {
            this.profile = profile;
        }

        public Meta getMeta() {
            return meta;
        }

        public void setMeta( Meta meta ) {
            this.meta = meta;
        }
        public String toString() {
            return Reflector.toJson( this );
        }
    }

    private static class B {
        private String name;
        private String dept;
        private List<Profile> profile;
        private AnotherMeta meta;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public List<Profile> getProfile() {
            return profile;
        }

        public void setProfile( List<Profile> profile ) {
            this.profile = profile;
        }

        public String getDept() {
            return dept;
        }

        public void setDept( String dept ) {
            this.dept = dept;
        }

        public AnotherMeta getMeta() {
            return meta;
        }

        public void setMeta( AnotherMeta meta ) {
            this.meta = meta;
        }
        public String toString() {
            return Reflector.toJson( this );
        }
    }


    private static class Profile {
        private String  name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName( String name ) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge( Integer age ) {
            this.age = age;
        }
    }

    private static class Meta {
        private String key;
        private Integer id;
        private String locale;

        public String getKey() {
            return key;
        }

        public void setKey( String key ) {
            this.key = key;
        }

        public Integer getId() {
            return id;
        }

        public void setId( Integer id ) {
            this.id = id;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale( String locale ) {
            this.locale = locale;
        }
    }

    private static class AnotherMeta {
        private String locale;

        public String getLocale() {
            return locale;
        }

        public void setLocale( String locale ) {
            this.locale = locale;
        }
    }

}