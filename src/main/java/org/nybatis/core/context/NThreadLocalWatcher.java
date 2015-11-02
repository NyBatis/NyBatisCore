package org.nybatis.core.context;

import java.util.Observable;

public class NThreadLocalWatcher extends Observable {

	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	public void notifyObservers( String deliveredParameter ) {
		setChanged();
		super.notifyObservers( deliveredParameter );
	}

}
