package org.nybatis.core.db.orm.table.customAnnotationInsert.entity;

import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.annotation.Table;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
@Table( "TB_USER" )
public class Person {

    @Pk @Column(length=20 )                  private String               id;
        @Column(length=100 )                 private String               name;
                                             private int                  age;
        @Column(type=Types.VARCHAR,length=1) private boolean              used;
        @Column(type=Types.CLOB )            private List<PersonProperty> property = new ArrayList<>();

    public Person() {}

    public Person( String id, String name, int age ) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

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

    public boolean isUsed() {
        return used;
    }

    public void setUsed( boolean used ) {
        this.used = used;
    }

    public List<PersonProperty> getProperty() {
        return property;
    }

    public void setProperty( List<PersonProperty> property ) {
        this.property = property;
    }

}
