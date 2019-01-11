package org.nybatis.core.db.orm.table.customAnnotationInsert.entity;

import java.util.*;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.annotation.Table;

import java.sql.Types;
import org.nybatis.core.model.NMap;

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
        @Column(type=Types.CLOB )            private Set<PersonProperty>  property = new LinkedHashSet<>();
        @Column(type=Types.CLOB )            private NMap                 nmap     = new NMap();
        @Column(type=Types.CLOB )            private Map<String,PersonProperty> mixedProperty = new HashMap<>();

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

    public Set<PersonProperty> getProperty() {
        return property;
    }

    public void setProperty( Set<PersonProperty> property ) {
        this.property = property;
    }

    public NMap getNmap() {
        return nmap;
    }

    public void setNmap( NMap nmap ) {
        this.nmap = nmap;
    }

    public Map<String, PersonProperty> getMixedProperty() {
        return mixedProperty;
    }

    public void setMixedProperty( Map<String, PersonProperty> mixedProperty ) {
        this.mixedProperty = mixedProperty;
    }

    public void addMixedProperty( String key, PersonProperty personProperty ) {
        mixedProperty.put( key, personProperty );
    }

}
