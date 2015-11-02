package org.nybatis.core.db.sql.mapper.implement;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class BigDecimalMapper implements TypeMapperIF<BigDecimal>{

	@Override
    public void setParameter( PreparedStatement statement, int index, BigDecimal param ) throws SQLException {
		statement.setBigDecimal( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(BigDecimal.class).toCode() );
	}

	@Override
    public BigDecimal getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getBigDecimal( columnName );
    }

	@Override
    public BigDecimal getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getBigDecimal( columnIndex );
    }

	@Override
    public BigDecimal getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getBigDecimal( columnIndex );
    }

}
