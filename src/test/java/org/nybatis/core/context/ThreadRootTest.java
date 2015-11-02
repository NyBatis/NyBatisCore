package org.nybatis.core.context;

import org.nybatis.core.log.NLogger;

public class ThreadRootTest {

	public static void main( String[] args ) {

		NLogger.debug( "T1 {} - {}", TempInheritableThreadLocal.getParentThreadName(), Thread.currentThread().getName() );
		NLogger.debug( "N1 {} - {}", ThreadRoot.getKey(), Thread.currentThread().getName() );

		new Thread( new Runnable() {
			public void run() {


				NLogger.debug( "T2 {} - {}", TempInheritableThreadLocal.getParentThreadName(), Thread.currentThread().getName() );
				NLogger.debug( "N2 {} - {}", ThreadRoot.getKey(), Thread.currentThread().getName() );

				TempInheritableThreadLocal.init();


				new Thread( new Runnable() {
					public void run() {

						NLogger.debug( "T3 {} - {}", TempInheritableThreadLocal.getParentThreadName(), Thread.currentThread().getName() );
						NLogger.debug( "N3 {} - {}", ThreadRoot.getKey(), Thread.currentThread().getName() );

					}

				}).start();

				NLogger.debug( "T2 {} - {}", TempInheritableThreadLocal.getParentThreadName(), Thread.currentThread().getName() );
				NLogger.debug( "N2 {} - {}", ThreadRoot.getKey(), Thread.currentThread().getName() );

			}
		}).start();


		NLogger.debug( "T1 {} - {}", TempInheritableThreadLocal.getParentThreadName(), Thread.currentThread().getName() );
		NLogger.debug( "N1 {} - {}", ThreadRoot.getKey(), Thread.currentThread().getName() );

	}

}
