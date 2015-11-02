package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class IntegerMapper implements TypeMapperIF<Integer>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Integer param ) throws SQLException {
		statement.setInt( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Integer.class).toCode() );
	}

	@Override
    public Integer getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getInt( columnName );
    }

	@Override
    public Integer getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getInt( columnIndex );
    }

	@Override
    public Integer getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getInt( columnIndex );
    }

}
