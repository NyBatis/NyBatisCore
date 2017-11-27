package org.nybatis.core.db.orm.table.customAnnotationInsert.entity;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class PersonProperty {

    private String id;
    private String nation;
    private String location;

    public PersonProperty() {
    }

    public PersonProperty( String id, String nation, String location ) {
        this.id = id;
        this.nation = nation;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public String getNation() {
        return nation;
    }

    public void setNation( String nation ) {
        this.nation = nation;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation( String location ) {
        this.location = location;
    }

}