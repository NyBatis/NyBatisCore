package org.nybatis.core.reflection.vo;


import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author nayasis
 * @since 2016-12-26
 */
public class TestRes {

    private String name;
    private Integer count;
    private Date date;
    private Map etcProp;
    private List<TestSubItem> productList;

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount( Integer count ) {
        this.count = count;
    }

    public Date getDate() {
        return date;
    }

    public void setDate( Date date ) {
        this.date = date;
    }

    public Map getEtcProp() {
        return etcProp;
    }

    public void setEtcProp( Map etcProp ) {
        this.etcProp = etcProp;
    }

    public List<TestSubItem> getProductList() {
        return productList;
    }

    public void setProductList( List<TestSubItem> productList ) {
        this.productList = productList;
    }
}
