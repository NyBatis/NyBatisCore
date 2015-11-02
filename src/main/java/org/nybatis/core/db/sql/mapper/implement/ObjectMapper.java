package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ObjectMapper implements TypeMapperIF<Object>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Object param ) throws SQLException {
		statement.setObject( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Object.class).toCode() );
	}

	@Override
    public Object getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getObject( columnName );
    }

	@Override
    public Object getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getObject( columnIndex );
    }

	@Override
    public Object getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getObject( columnIndex );
    }

}
