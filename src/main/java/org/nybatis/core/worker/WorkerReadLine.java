package org.nybatis.core.worker;

import java.io.IOException;

/**
 * Worker class have readLine do something.
 */
@FunctionalInterface
public interface WorkerReadLine {

	/**
	 * do something with readLine
	 *
	 * @param readLine text line in Piles
	 * @throws IOException if an I/O exception occurs.
	 */
	void execute( String readLine ) throws IOException;

}