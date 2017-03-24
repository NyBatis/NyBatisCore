package org.nybatis.core.clone.vo;

/**
 * @author nayasis@onestorecorp.com
 * @since 2017-03-24
 */
public class Product extends AbstractProduct {

    private String prodId;
    private Integer price;
    private ProductMeta meta;

    public String getProdId() {
        return prodId;
    }

    public void setProdId( String prodId ) {
        this.prodId = prodId;
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
}
