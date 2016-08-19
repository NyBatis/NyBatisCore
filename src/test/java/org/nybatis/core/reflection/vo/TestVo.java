package org.nybatis.core.reflection.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.nybatis.core.reflection.Reflector;

/**
 * Test VO
 *
 * @author nayasis
 * @since 2016-08-19
 */
public class TestVo {

    @JsonProperty( "S01" )
    private String S01;
    @JsonProperty( "S02" )
    private String S02;

    @JsonIgnore
    public String getS01() {
        return S01;
    }

    public void setS01( String s01 ) {
        S01 = s01;
    }

    @JsonIgnore
    public String getS02() {
        return S02;
    }

    public void setS02( String s02 ) {
        S02 = s02;
    }

    public String toString() {
        return Reflector.toJson( this );
    }
}
