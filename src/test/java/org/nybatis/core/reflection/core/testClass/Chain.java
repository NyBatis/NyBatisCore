package org.nybatis.core.reflection.core.testClass;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nybatis.core.db.annotation.Column;
import org.nybatis.core.db.annotation.Pk;
import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.model.NDate;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.util.StringUtil;

/**
 * Chain Entity
 *
 * @author nayasis@gmail.com
 */
@Table( "TB_CHAIN" )
public class Chain {

    @Pk @Column(length=10)
    private String projectId;
    @Pk
    private NDate  completeDate;
    @Pk @Column(length=20)
    private String chainId;
    @Column(length=1000)
    private String chainName;
    @Column(length=20)
    private String chainType;
    @Column(type=Types.CLOB)
    private NMap   chainProp = new NMap();
    @Column(type=Types.CLOB)
    private Map    etcProp = new HashMap();
    @Column(type=Types.CHAR, length=1 )
    private String skipYn    = "N";
    @Column(type=Types.CHAR,length=1)
    private String existYn   = "Y";
    @Column(type=Types.CLOB)
    private Map<String,List<String>> possibleChains = new HashMap<>();

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId( String projectId ) {
        this.projectId = projectId;
    }

    public NDate getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate( NDate completeDate ) {
        this.completeDate = completeDate;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId( String chainId ) {
        this.chainId = chainId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName( String name ) {
        chainName = name;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType( String chainType ) {
        this.chainType = chainType;
    }

    @JsonIgnore
    public boolean isSkippable() {
        return StringUtil.toBoolean( skipYn );
    }

    @JsonIgnore
    public void isSkippable( boolean skippable ) {
        skipYn = StringUtil.toYn( skippable );
    }

    @JsonProperty
    private String getSkipYn() {
        return skipYn;
    }

    @JsonProperty
    private void setSkipYn( String skipYn ) {
        this.skipYn = skipYn;
    }

    @JsonIgnore
    public boolean isExist() {
        return StringUtil.toBoolean( existYn );
    }

    @JsonIgnore
    public void isExist( boolean flag ) {
        existYn = StringUtil.toYn( flag );
    }

    @JsonProperty
    private String getExistYn() {
        return existYn;
    }

    @JsonProperty
    private void setExistYn( String existYn ) {
        this.existYn = existYn;
    }

    @JsonIgnore
    public NMap getChainProp() {
        return chainProp;
    }

    @JsonIgnore
    public void setChainProp( NMap properties ) {
        chainProp = properties;
    }

    @JsonProperty( "chainProp" )
    public void setChainProperties( String properties ) {
        chainProp.bind( properties );
    }

    @JsonProperty( "chainProp" )
    public String toChainProp() {
        return Reflector.toJson( chainProp );
    }

    @JsonIgnore
    public Map<String, List<String>> getPossibleChains() {
        return possibleChains;
    }

    @JsonIgnore
    public void setPossibleChains( Map<String, List<String>> possibleChains ) {
        this.possibleChains = possibleChains;
    }

    @JsonProperty( "possibleChains" )
    public void setPossibleChains( String text ) {
        possibleChains = Reflector.toBeanFrom( text, possibleChains.getClass() );
    }

    @JsonProperty( "possibleChains" )
    public String toPossibleChains() {
        return Reflector.toJson( possibleChains );
    }

    public Chain clone() {
        return Reflector.clone( this );
    }

}
