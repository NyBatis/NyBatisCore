package org.nybatis.core.db.session.type.orm;

import org.nybatis.core.db.annotation.Table;
import org.nybatis.core.db.session.SqlSessionSqliteTest;
import org.nybatis.core.reflection.Reflector;

/**
 * @author nayasis@gmail.com
 * @since 2015-09-16
 */
@Table( name = SqlSessionSqliteTest.TABLE_NAME )
public class TestDomain {

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
        return Reflector.toString( this );
    }
}
