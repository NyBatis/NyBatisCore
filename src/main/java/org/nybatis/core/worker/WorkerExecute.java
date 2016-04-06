package org.nybatis.core.worker;


/**
 * 처리할 작업을 파라미터화 시키기 위한 Worker 클래스
 *
 * @param <T> 작업결과를 임시저장할 오브젝트의 타입
 */
@FunctionalInterface
public interface WorkerExecute {

	void execute();

}