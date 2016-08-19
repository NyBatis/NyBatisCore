package org.nybatis.core.reflection.vo;

import org.nybatis.core.model.NDate;
import org.nybatis.core.reflection.Reflector;

public class ToVo {

	public String name;
	public NDate birth;
	public int    age;

	public ToVo() {}

	public ToVo( String name ) {
		this( name, null );
	}

	public ToVo( String name, String birth ) {
		this.name = name;
		this.birth = new NDate( birth );
	}

	public String toString() {
		return Reflector.toString( this );
	}

	public void setAge() {
		age = 99;
	}

	public void setAge( int age ) {
		this.age = age * 2;
	}

	public void setBirth( String date ) {
		birth = new NDate( date );
	}

}
