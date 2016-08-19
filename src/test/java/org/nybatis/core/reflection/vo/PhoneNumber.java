package org.nybatis.core.reflection.vo;

import org.nybatis.core.reflection.Reflector;

import java.util.Objects;

public class PhoneNumber {

	public int code;
	public String number;

	public PhoneNumber() {}

	public PhoneNumber( int code, String number ) {
		this.code = code;
		this.number = number;
	}

	public String toString() {
		return Reflector.toJson( this );
	}

//	public boolean equals( Object value ) {
//		return false;
//	}


	@Override
	public boolean equals( Object o ) {
		if( this == o ) return true;
		if( o == null || getClass() != o.getClass() ) return false;
		PhoneNumber that = (PhoneNumber) o;
		return Objects.equals( code, that.code ) &&
				Objects.equals( number, that.number );
	}

	@Override
	public int hashCode() {
		return Objects.hash( code, number );
	}
}