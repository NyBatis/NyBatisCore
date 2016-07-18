package org.nybatis.core.file.vo;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.nybatis.core.file.annotation.ExcelHeader;

/**
 * User Entity to convert as excel data
 *
 * @author nayasis@gmail.com
 * @since 2016-03-11
 */
@JsonPropertyOrder({ "address", "name", "id" })
public class User {

    @ExcelHeader( "아이디" )
    private String id;

    private String name;

    @ExcelHeader( "주소" )
    private String address;

    public User() {}

//    @JsonCreator
//    public User( @JsonProperty("아이디") String id, @JsonProperty("이름") String name, @JsonProperty("주소") String address ) {
//        this.id = id;
//        this.name = name;
//        this.address = address;
//    }

    public User( String id, String name, String address ) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @ExcelHeader( "이름" )
    public String getName() {
        return name;
    }

    @ExcelHeader( "이름" )
    public void setName( String name ) {
        this.name = name;
    }

    public void setAddress( String address ) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String toString() {
        return String.format( "{id:%s, name:%s, address:%s}", id, name, address );
    }

}
