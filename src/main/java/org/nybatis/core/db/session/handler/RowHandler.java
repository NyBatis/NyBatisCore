package org.nybatis.core.db.session.handler;

import org.nybatis.core.model.NMap;
import org.nybatis.core.validation.Validator;

import java.util.HashSet;
import java.util.Set;

public abstract class RowHandler {

	private boolean stop = false;

	private Set<?> header;

	public abstract void handle( NMap row );

	protected void stop() {
		stop = true;
	}

	public boolean isBreak() {
		return stop;
	}

	public Set<?> getHeader() {
		return Validator.nvl( header, new HashSet() );
	}

	public void setHeader( Set<?> header ) {
		this.header = header;
	}
}
