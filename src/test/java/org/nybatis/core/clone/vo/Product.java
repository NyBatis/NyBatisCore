package org.nybatis.core.clone.vo;

import org.nybatis.core.reflection.Reflector;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-24
 */
public class Product extends AbstractProduct {

    private String prodId;
    private Integer price;
    private ProductMeta meta;

    public Product() {}

    public Product( String prodId ) {
        setProdId( prodId );
    }

    public Product( String prodId, String prodNm ) {
        setProdId( prodId );
        setProdName( prodNm );
    }

    public String getProdId() {
        return prodId;
    }

    public void setProdId( String prodId ) {
        this.prodId = prodId;
    }

    public void setProdId( String prodId, String dummyParameter ) {
        setProdId( prodId );
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice( Integer price ) {
        this.price = price;
    }

    public ProductMeta getMeta() {
        return meta;
    }

    public void setMeta( ProductMeta meta ) {
        this.meta = meta;
    }

    public String toString(){
        return Reflector.toJson( this, true );
    }

}
