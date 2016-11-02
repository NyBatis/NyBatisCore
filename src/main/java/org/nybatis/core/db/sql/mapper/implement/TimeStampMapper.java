package org.nybatis.core.db.sql.mapper.implement;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * TimeStampMapper
 *
 * java.sql.Date handle data as Date only and miss time information(HH:MI:SS).<br>
 * So It is necessary to handle data as TimeStamp
 */
public class TimeStampMapper implements TypeMapperIF<Date> {

	@Override
    public void setParameter( PreparedStatement statement, int index, Date param ) throws SQLException {
		statement.setTimestamp( index, new Timestamp(param.getTime()) );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.TIMESTAMP.toCode() );
	}

	@Override
    public Date getResult( ResultSet resultSet, String columnName ) throws SQLException {
		Timestamp timestamp = resultSet.getTimestamp( columnName );
		return timestamp == null ? null : new Date( timestamp.getTime() );
    }

	@Override
    public Date getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		Timestamp timestamp = resultSet.getTimestamp( columnIndex );
		return timestamp == null ? null : new Date( timestamp.getTime() );
    }

	@Override
    public Date getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		Timestamp timestamp = statement.getTimestamp( columnIndex );
		return timestamp == null ? null : new Date( timestamp.getTime() );
    }

}
