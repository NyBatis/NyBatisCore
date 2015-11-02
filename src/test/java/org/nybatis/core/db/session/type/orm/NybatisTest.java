package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.reflection.Reflector;

/**
 * Test Domain Class for representing class name as table name
 *
 * @author nayasis@gmail.com
 * @since 2015-09-21
 */
public class NybatisTest {

    private String  listId;
    private String  prodId;
    private Integer price;
    private String  prodName;

    public String getListId() {
        return listId;
    }

    public void setListId( String listId ) {
        this.listId = listId;
    }

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

    public String getProdName() {
        return prodName;
    }

    public void setProdName( String prodName ) {
        this.prodName = prodName;
    }

    public String toString() {
        return new Reflector().getFieldReport( this );
    }

}
