package org.nybatis.core.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

import java.util.Map;

/**
 * @author nayasis
 * @since 2016-11-04
 */
public class Card {


    public String  cardId;
    public Integer expoOrd;

    private CopyTargetTenant copyTargetTenant;

    @JsonIgnore
    public CopyTargetTenant getCopyTargetTenant() {
        if( copyTargetTenant == null ) {
            copyTargetTenant = new CopyTargetTenant();
        }
        return copyTargetTenant;
    }

    @JsonProperty( "copyTargetTenant" )
    private String copyTargetTenant() {

        return Reflector.toJson( copyTargetTenant );
    }

    @JsonProperty( "copyTargetTenant" )
    private void copyTargetTenant( Object copyTargetTenant ) {
        if( Validator.isEmpty( copyTargetTenant ) ) {
            this.copyTargetTenant = new CopyTargetTenant();
        } else {
            if( copyTargetTenant instanceof CopyTargetTenant ) {
                this.copyTargetTenant = (CopyTargetTenant) copyTargetTenant;
            } else if( copyTargetTenant instanceof Map ) {
                this.copyTargetTenant = Reflector.toBeanFrom( copyTargetTenant, CopyTargetTenant.class );
            } else if( Types.isString( copyTargetTenant ) ) {
                this.copyTargetTenant = Reflector.toBeanFrom( copyTargetTenant.toString(), CopyTargetTenant.class );
            }
        }
    }

}
