package org.nybatis.core.clone.vo;

import java.util.List;
import java.util.Set;

/**
 * @author nayasis@gmail.com.com
 * @since 2017-03-24
 */
public class ProductMeta {

    private String description;
    private List<String> author;
    private Set<String> newAuthor;

    private static String descriptionStatic;
    private static String nameStatic;

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public void setDescription( String description, String name ) {
        this.description = description;
    }

    public List<String> getAuthor() {
        return author;
    }

    public void setAuthor( List<String> author ) {
        this.author = author;
    }

    public Set<String> getNewAuthor() {
        return newAuthor;
    }

    public void setNewAuthor( Set<String> newAuthor ) {
        this.newAuthor = newAuthor;
    }

    public static void setDescriptionStaticaly( String description, String name ) {
        descriptionStatic = description;
        nameStatic = name;
    }

    public String toString() {
        return String.format( "{description:'%s', author:%s}", description, author );
    }
}
