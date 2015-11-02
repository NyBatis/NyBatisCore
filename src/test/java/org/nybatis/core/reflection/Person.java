package org.nybatis.core.reflection;

import java.util.ArrayList;
import java.util.List;

public class Person {

	public String firstName;
	public String lastName;

	public PhoneNumber phone = new PhoneNumber();
	public PhoneNumber fax   = new PhoneNumber();

	public List<PhoneNumber> phoneList = new ArrayList<>();
	
	public String toString() {
		return new Reflector().getFieldReport( this );
	}
	
}

