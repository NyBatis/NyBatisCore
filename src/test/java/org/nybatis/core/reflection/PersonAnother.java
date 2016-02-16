package org.nybatis.core.reflection;

import java.util.ArrayList;
import java.util.List;

public class PersonAnother {

	public String lastName;
	public String prefix;

	public PhoneNumber fax   = new PhoneNumber();

	public List<PhoneNumber> phoneList = new ArrayList<>();
	
	public String toString() {
		return new Reflector().getFieldReport( this );
	}

	

}

