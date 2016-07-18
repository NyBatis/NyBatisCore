package org.nybatis.core.file.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.nybatis.core.file.annotation.ExcelHeader;
import org.nybatis.core.reflection.Reflector;

/**
 * User Entity to convert as excel data
 *
 * @author nayasis@gmail.com
 * @since 2016-03-11
 */
@JsonPropertyOrder({ "address", "name", "id" })
public class UserForExcelReader {

    @ExcelHeader( "아이디" )
    @JsonProperty( "아이디" )
    private String id;

    private String name;

    @ExcelHeader( "주소" )
    @JsonProperty( "주소" )
    @JsonIgnore
    private String address;

    public UserForExcelReader() {}

    public UserForExcelReader( String id, String name, String address ) {
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

    @JsonProperty( "이름" )
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
