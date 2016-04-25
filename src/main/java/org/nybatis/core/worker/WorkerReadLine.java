package org.nybatis.core.worker;

import java.io.IOException;

/**
 * 처리할 작업을 파라미터화 시키기 위한 Worker 클래스
 *
 * @param <T> 작업결과를 임시저장할 오브젝트의 타입
 */
@FunctionalInterface
public interface WorkerReadLine {

	/**
	 * 실행할 로직을 구현한다.
	 *
	 * @param readLine Command 출력결과 1줄
	 */
	void execute( String readLine ) throws IOException;

}