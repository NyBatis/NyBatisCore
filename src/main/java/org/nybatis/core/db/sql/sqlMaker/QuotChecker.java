package org.nybatis.core.db.sql.sqlMaker;


public class QuotChecker {

    private boolean onQuot       = false;
    private boolean onDoubleQuot = false;
    
    public void init() {
        onQuot       = false;
        onDoubleQuot = false;
    }
    
    public void check( char character ) {

        if( onQuot == true ) {
            if( character == '\'' ) onQuot = false;
            
        } else if( onDoubleQuot == true ) {
            if( character == '"' ) onDoubleQuot = false;
            
        } else {
            if( character == '\'' ) {
                onQuot = true;
            } else if( character == '"' ) {
                onDoubleQuot = true;
            }
        }
    }
    
    public boolean isOn() {
        return onQuot || onDoubleQuot;
    }
    
}
