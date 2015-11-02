package org.nybatis.core.worker;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * 처리할 작업을 파라미터화 시키기 위한 Worker 클래스
 *
 */
public interface WorkerWriteBuffer {

	void execute( BufferedWriter writer ) throws IOException;

}