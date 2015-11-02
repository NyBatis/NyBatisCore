package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ResultsetMapper implements TypeMapperIF<ResultSet>{

	@Override
    public void setParameter( PreparedStatement statement, int index, ResultSet param ) throws SQLException {
		statement.setObject( index, param );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.CURSOR.toCode() );
	}

	@Override
    public ResultSet getResult( ResultSet resultSet, String columnName ) throws SQLException {
	    return (ResultSet) resultSet.getObject( columnName );
    }

	@Override
    public ResultSet getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return (ResultSet) resultSet.getObject( columnIndex );
    }

	@Override
    public ResultSet getResult( CallableStatement statement, int columnIndex ) throws SQLException {
	    return (ResultSet) statement.getObject( columnIndex );
    }

}
