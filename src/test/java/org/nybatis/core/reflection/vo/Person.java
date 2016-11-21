package org.nybatis.core.reflection.vo;

import org.nybatis.core.model.NDate;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Person {

	public String   firstName;
	public String   lastName;
	public Integer  age;
	public Long     weight;
	public NDate    birthNDate        = new NDate();
	public Date     birthDate         = new Date();
	public String[] previousAddresses = { "granada",   "zeon", "earth defense federation" };
	public Set      profileSet        = new HashSet<>();
	public NList    profileNList      = new NList();


	public PhoneNumber phone = new PhoneNumber();
	public PhoneNumber fax   = new PhoneNumber();

	public List<PhoneNumber> phoneList = new ArrayList<>();
	
	public String toString() {
		return Reflector.toString( this );
	}
	
}

