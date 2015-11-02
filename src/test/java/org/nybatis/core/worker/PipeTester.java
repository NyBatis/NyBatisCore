package org.nybatis.core.worker;

import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class PipeTester {


    @SuppressWarnings( "rawtypes" )
    @Test
	public void basicTest() {

		NLogger.debug( new Pipe<Object>( new Object() ).get().getClass() );
		NLogger.debug( new Pipe<List>( new ArrayList() ).get().getClass() );
		NLogger.debug( new Pipe<String>( "" ).get().getClass() );

		NLogger.debug( new Pipe<Object>( Object.class ).get().getClass() );
		NLogger.debug( new Pipe<List>( List.class ).get().getClass() );
		NLogger.debug( new Pipe<String>( String.class ).get().getClass() );

	}

}
