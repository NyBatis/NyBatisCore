package org.nybatis.core.db.sql.sqlNode.element.abstracts;

/**
 * @author Administrator
 * @since 2015-10-23
 */
public class ElementText {

    private Class  klass;
    private String text;

    public ElementText( Class klass, String text ) {
        this.klass = klass;
        this.text  = text;
    }

    public Class getKlass() {
        return klass;
    }

    public String getText() {
        return text;
    }

    public String toString() {
        return text;
    }

}
