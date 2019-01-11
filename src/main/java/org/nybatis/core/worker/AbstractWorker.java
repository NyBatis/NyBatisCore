package org.nybatis.core.worker;

import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.ClassUtil;


/**
 * 처리할 작업을 파라미터화 시키기 위한 Worker 클래스
 *
 * @param <T> 작업결과를 임시저장할 오브젝트의 타입
 */
public abstract class AbstractWorker<T> {

	protected T pipe;

    public AbstractWorker() {

    	try {
			Class genericClass = ClassUtil.getGenericClass( this.getClass() );
			pipe = ClassUtil.createInstance( (Class<T>) genericClass );
    	} catch( ClassCastingException e ) {
    		NLogger.warn( e );
        }

    }

	/**
	 * 기본 생성자
	 *
	 * @param pipe 작업결과를 저장할 인스턴스
	 */
	public AbstractWorker( T pipe ) {
		this.pipe = pipe;
	}

	/**
	 * 작업결과를 가져온다.
	 *
	 * @return 작업결과
	 */
    public T getPipe() {
		return pipe;
	}

}