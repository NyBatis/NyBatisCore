package org.nybatis.core.worker;


/**
 * 처리할 작업을 파라미터화 시키기 위한 Worker 클래스
 */
@FunctionalInterface
public interface WorkerExecute {
	void execute();
}