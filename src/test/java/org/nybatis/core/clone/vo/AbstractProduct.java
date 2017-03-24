package org.nybatis.core.clone.vo;

/**
 * @author nayasis@onestorecorp.com
 * @since 2017-03-24
 */
public class AbstractProduct {

    private final static String SECURE_CODE = "1234";

    private String prodId;
    private String prodName;

    public String getProdId() {
        return prodId;
    }

    public void setProdId( String prodId ) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName( String prodName ) {
        this.prodName = prodName;
    }

}
