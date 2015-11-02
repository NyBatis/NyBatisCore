package org.nybatis.core.context;

import org.nybatis.core.util.StringUtil;

public class TempInheritableThreadLocal {

	private static final ThreadLocal<String> parentThreadName = new InheritableThreadLocal<String>() {
	    public String initialValue() {
	        return Thread.currentThread().getName();
	    }
		protected String childValue( String parentValue ) {
	    	return StringUtil.isEmpty( parentValue ) ? Thread.currentThread().getName() : parentValue;
	    }
	};

	public static String getParentThreadName() {
		return parentThreadName.get();
	}

	public static void setParentThreadName() {
		parentThreadName.set( Thread.currentThread().getName() );
	}

	public static void init() {
		parentThreadName.remove();
	}

}
