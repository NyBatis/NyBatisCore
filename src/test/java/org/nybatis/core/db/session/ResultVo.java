package org.nybatis.core.db.session;

import org.nybatis.core.reflection.Reflector;

public class ResultVo {

	public String listId;
	public String prodId;
	public int    price;
	public byte[] image;

	public String toString() {
		return Reflector.toString( this );
	}

}