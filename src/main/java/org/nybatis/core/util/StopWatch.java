package org.nybatis.core.util;

import java.util.ArrayList;
import java.util.List;
import org.nybatis.core.model.NList;

public class StopWatch {

	private long         initTime = System.nanoTime();
	private List<Log>    logs     = new ArrayList<>();

	public long elapsedNanoSeconds() {
		return System.nanoTime() - initTime;
	}

	public long elapsedMiliSeconds() {
		return elapsedNanoSeconds() / 1_000_000;
	}

	public double elapsedSeconds() {
		return elapsedNanoSeconds() / 1_000_000_000.0;
	}

	public StopWatch reset() {
	    initTime = System.nanoTime();
		logs.clear();
	    return this;
	}

	public StopWatch tick( String message ) {

		Log log = new Log( message);

		if( logs.size() == 0 ) {
			log.lastNonoSec = initTime;
			log.durationMiliSec = elapsedMiliSeconds();
		} else {
			log.lastNonoSec = System.nanoTime();
			log.durationMiliSec = ( log.lastNonoSec - logs.get(logs.size() - 1).lastNonoSec ) / 1_000_000;
		}

		logs.add( log );

		return this;
	}

	public String toString() {

		double total = 0.;

		for( Log log : logs )
			total += log.durationMiliSec;

		int remainPercent = 100;

		for( int i = 0, last = logs.size() - 1; i <= last; i++ ) {
			Log log = logs.get( i );
			if( i == last ) {
				log.percent = remainPercent;
			} else {
				log.percent = (int) ( log.durationMiliSec / total * 100 );
				remainPercent -= log.percent;
			}
		}

		NList list = new NList();
		for( Log log : logs ) {
			list.add( "ms",   String.format( "%6d", log.durationMiliSec ) );
			list.add( "%",    String.format("%3d", log.percent )          );
			list.add( "Task", log.message                                 );
		}

		list.add( "ms",   String.format( "%6d", (long) total ) );
		list.add( "%",    ""                                   );
		list.add( "Task", "TOTAL"                              );

		return list.toDebugString();

	}

	private static class Log {
		public long   lastNonoSec;
		public long   durationMiliSec;
		public String message;
		public int    percent;
		public Log( String message ) {
			this.message = message;
		}
	}

}
