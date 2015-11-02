package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class FloatMapper implements TypeMapperIF<Float> {

	@Override
    public void setParameter( PreparedStatement statement, int index, Float param ) throws SQLException {
		statement.setFloat( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find( Float.class ).toCode() );
	}

	@Override
    public Float getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getFloat( columnName );
    }

	@Override
    public Float getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getFloat( columnIndex );
    }

	@Override
    public Float getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getFloat( columnIndex );
    }

}
