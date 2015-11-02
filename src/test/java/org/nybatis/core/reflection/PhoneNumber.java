package org.nybatis.core.reflection;

public class PhoneNumber {

	public int code;
	
	public String number;
	
	public String toString() {
		return new Reflector().toJson( this );
	}
	
}