package org.nybatis.core.db.orm.table.create.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.db.annotation.Index;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.model.NDate;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
@Table( value = "Tb_tAbLe", indices = {
    @Index( name = "test1", columns = {"age","lastName"} ),
    @Index( name = "test2", columns = {"age","income","key"} )
})
public class Person {

    @Pk
    private String key;
    private String lastName;
    private int    age;
    @Column( length = 10, precision = 3 )
    private double income;
    private String id;
    private NDate  birthDay;

    @JsonIgnore
    private String dummy;

    public String getKey() {
        return key;
    }

    public Person() {}

    public Person( String key, String lastName, int age, double income, String id, String dummy ) {
        this.key    = key;
        this.lastName = lastName;
        this.age    = age;
        this.income = income;
        this.id     = id;
        this.dummy  = dummy;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge( int age ) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome( double income ) {
        this.income = income;
    }

    public String getDummyMerong() {
        return dummy;
    }

    public void setDummyMerong( String dummy ) {
        this.dummy = dummy;
    }

    @JsonProperty( "dummy" )
    public String dummy() {
        return this.dummy;
    }

    public NDate getBirthDay() {
        return birthDay;
    }

    public void setBirthDay( NDate birthDay ) {
        this.birthDay = birthDay;
    }
}
