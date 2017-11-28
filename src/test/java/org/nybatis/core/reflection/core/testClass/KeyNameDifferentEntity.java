package org.nybatis.core.reflection.core.testClass;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.annotation.Table;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-29
 */
@Table( "TB_KEY_DIFFERENT")
public class KeyNameDifferentEntity {

    @Pk @Column(length=20) @JsonProperty
    private String name;
    private Integer age;

    public KeyNameDifferentEntity() {}

    public KeyNameDifferentEntity( String name, Integer age ) {
        this.name = name;
        this.age = age;
    }

    public String getNameAnother() {
        return name;
    }

    public void setNameAnother( String name ) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge( Integer age ) {
        this.age = age;
    }
}
