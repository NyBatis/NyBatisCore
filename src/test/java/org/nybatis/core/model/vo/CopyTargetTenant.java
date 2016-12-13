package org.nybatis.core.model.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.nybatis.core.util.StringUtil;

/**
 * @author nayasis
 * @since 2016-12-13
 */
public class CopyTargetTenant {

    private String S02 = "N";
    private String S03 = "N";

    @JsonProperty( "S02" )
    public String getS02() {
        return S02;
    }

    @JsonProperty( "S03" )
    public String getS03() {
        return S03;
    }

    @JsonProperty( "S02" )
    public void setS02( String yn ) {
        S02 = StringUtil.toYn( yn );
    }

    @JsonProperty( "S03" )
    public void setS03( String yn ) {
        S03 = StringUtil.toYn( yn );
    }

}
