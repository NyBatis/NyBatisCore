package org.nybatis.core.db.sql.mapper.implement;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class BigIntegerMapper implements TypeMapperIF<BigInteger>{

	@Override
    public void setParameter( PreparedStatement statement, int index, BigInteger param ) throws SQLException {
		statement.setBigDecimal( index, new BigDecimal(param) );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(BigInteger.class).toCode() );
	}

	@Override
    public BigInteger getResult( ResultSet resultSet, String columnName ) throws SQLException {
		return resultSet.getBigDecimal( columnName ).toBigInteger();
    }

	@Override
    public BigInteger getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getBigDecimal( columnIndex ).toBigInteger();
    }

	@Override
    public BigInteger getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getBigDecimal( columnIndex ).toBigInteger();
    }

}
