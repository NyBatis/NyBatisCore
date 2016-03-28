package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class TimeMapper implements TypeMapperIF<Date> {

	@Override
    public void setParameter( PreparedStatement statement, int index, Date param ) throws SQLException {
		statement.setTime( index, new Time(param.getTime()) );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.TIME.toCode() );
	}

	@Override
    public Date getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return new Date( resultSet.getTime( columnName ).getTime() );
    }

	@Override
    public Date getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return new Date( resultSet.getTime( columnIndex ).getTime() );
    }

	@Override
    public Date getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return new Date( statement.getTime( columnIndex ).getTime() );
    }

}
