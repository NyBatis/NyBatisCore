package org.nybatis.core.db.session.executor.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.nybatis.core.db.sql.mapper.SqlType;

public class Header {

	private List<String>  name  = new ArrayList<>();
	private List<SqlType> type  = new ArrayList<>();

	public int size() {
		return name.size();
	}

	public Header add( String name, SqlType sqlType ) {
		this.name.add( name    );
		this.type.add( sqlType );
		return this;
	}

	public boolean contains( String name ) {
		return this.name.contains( name );
	}

	public Header add( String name, int sqlType ) {
		return add( name, SqlType.find(sqlType) );
	}

	public String getName( int index ) {
		return name.get( index );
	}

	public SqlType getType( int index ) {
		return type.get( index );
	}

	public Set<String> keySet() {
		return new LinkedHashSet<>( name );
	}

}
