package org.nybatis.core.db.session.executor.util;

import org.nybatis.core.db.session.handler.RowHandler;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;

public class CacheResultsetController {

    public void toList( NList cacheResult, RowHandler rowHandler ) {

    	if( cacheResult == null ) return;

		rowHandler.setHeader( cacheResult.keySet() );

    	for( NMap row : cacheResult ) {
    		rowHandler.handle( row );
    		if( rowHandler.isBreak() ) break;
    	}

    }

}
