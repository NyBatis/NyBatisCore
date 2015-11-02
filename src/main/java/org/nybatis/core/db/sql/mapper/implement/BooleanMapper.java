package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class BooleanMapper implements TypeMapperIF<Boolean>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Boolean param ) throws SQLException {
		statement.setBoolean( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Boolean.class).toCode() );
	}

	@Override
    public Boolean getResult( ResultSet resultSet, String columnName ) throws SQLException {
		return resultSet.getBoolean( columnName );
    }

	@Override
    public Boolean getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getBoolean( columnIndex );
    }

	@Override
    public Boolean getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		return statement.getBoolean( columnIndex );
    }

}
