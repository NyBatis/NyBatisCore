package org.nybatis.core.util;

import org.testng.annotations.Test;

public class ValidatorTest {

    @Test
    public void isFinded() {
        
        String val = "AAAA(1,2,3,4,5)";
        
        System.out.println( StringUtil.capturePatterns( val, "(?i)(A)").size() );
        
        System.out.println(  getParamCount(val) );
        
    }

    private int getParamCount( String param ) {
        
        param = param.substring( param.indexOf( '(' ) + 1, param.indexOf( ')')  );

        int count = 0;
        
        int index = -1;
        
        while( true ) {
            
            index = param.indexOf( ',', index + 1 );
            
            if( index < 0 ) break;
            
            count++;
            
        }
        
        return count;
        
    }
    
}
