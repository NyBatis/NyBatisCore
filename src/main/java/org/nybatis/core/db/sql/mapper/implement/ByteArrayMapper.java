package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ByteArrayMapper implements TypeMapperIF<byte[]> {

	@Override
    public void setParameter( PreparedStatement statement, int index, byte[] param ) throws SQLException {
		statement.setBytes( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(byte[].class).toCode() );
	}

	@Override
    public byte[] getResult( ResultSet resultSet, String columnName ) throws SQLException {
		return resultSet.getBytes( columnName );
    }

	@Override
    public byte[] getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getBytes( columnIndex );
    }

	@Override
    public byte[] getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		return statement.getBytes( columnIndex );
    }

}
