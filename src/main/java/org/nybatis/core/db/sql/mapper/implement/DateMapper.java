package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class DateMapper implements TypeMapperIF<Date>{

	@Override
    public void setParameter( PreparedStatement statement, int index, Date param ) throws SQLException {
		statement.setDate( index, new java.sql.Date(param.getTime()) );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Date.class).toCode() );
	}

	@Override
    public Date getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return resultSet.getDate( columnName );
    }

	@Override
    public Date getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return resultSet.getDate( columnIndex );
    }

	@Override
    public Date getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return statement.getDate( columnIndex );
    }

}
