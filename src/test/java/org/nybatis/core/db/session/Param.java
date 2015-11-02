package org.nybatis.core.db.session;

import org.nybatis.core.reflection.Reflector;

public class Param {

    public String listId;
	public String prodId;
	public int    price;
	public byte[] image;

	public float rowNum;
	public String keyValue;

	public Param() {}

	public Param( String listId, String prodId, int price ) {

		this.listId = listId;
		this.prodId = prodId;
		this.price  = price;

	}

	public Param( String listId, String prodId, int price, byte[] image ) {

		this.listId = listId;
		this.prodId = prodId;
		this.price  = price;
		this.image  = image;

	}

	public Param( String listId ) {
		this.listId = listId;
	}

	public String toString() {
		return new Reflector().getFieldReport( this );
	}


}
