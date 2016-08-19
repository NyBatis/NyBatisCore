package org.nybatis.core.reflection.vo;

import java.text.ParseException;

import org.nybatis.core.model.NDate;

public class FromVo {

	public String name;
	public int    age;
	public NDate  birth;

	public FromVo( String name, int age, String birth ) throws ParseException {

		this.name = name;
		this.age  = age;
		this.birth = new NDate( birth );

	}

}
