package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class DoubleMapper implements TypeMapperIF<Double>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Double param ) throws SQLException {
		statement.setDouble( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Double.class).toCode() );
	}

	@Override
    public Double getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getDouble( columnName );
    }

	@Override
    public Double getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getDouble( columnIndex );
    }

	@Override
    public Double getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getDouble( columnIndex );
    }

}
