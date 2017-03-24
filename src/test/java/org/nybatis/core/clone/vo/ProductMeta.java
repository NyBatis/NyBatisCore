package org.nybatis.core.clone.vo;

import java.util.List;

/**
 * @author nayasis@onestorecorp.com
 * @since 2017-03-24
 */
public class ProductMeta {

    private String description;
    private List<String> author;

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor( List<String> author ) {
        this.author = author;
    }
}
