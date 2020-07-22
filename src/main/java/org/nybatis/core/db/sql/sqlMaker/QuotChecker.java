package org.nybatis.core.db.sql.sqlMaker;


public class QuotChecker {

    private boolean quot       = false;
    private boolean doubleQuot = false;
    
    public void init() {
        quot       = false;
        doubleQuot = false;
    }

    public QuotChecker clear() {
        quot       = false;
        doubleQuot = false;
        return this;
    }

    public void check( char character ) {
        if( quot == true ) {
            if( character == '\'' ) quot = false;
        } else if( doubleQuot == true ) {
            if( character == '"' ) doubleQuot = false;
        } else {
            if( character == '\'' ) {
                quot = true;
            } else if( character == '"' ) {
                doubleQuot = true;
            }
        }
    }
    
    public boolean isIn() {
        return quot || doubleQuot;
    }
    
}
