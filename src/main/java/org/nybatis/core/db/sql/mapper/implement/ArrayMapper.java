package org.nybatis.core.db.sql.mapper.implement;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings( "rawtypes" )
public class ArrayMapper implements TypeMapperIF<Object> {

    @Override
    public void setParameter( PreparedStatement statement, int index, Object param ) throws SQLException {
		// It is not possible without making Collection Type in Databse.
		statement.setArray( index, (Array) param );
    }

	@Override
    public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.ARRAY.toCode() );
    }

	@Override
    public Object getResult( ResultSet resultSet, String columnName ) throws SQLException {
		return getList( resultSet.getArray(columnName) );
	}

    @Override
    public Object getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return getList( resultSet.getArray(columnIndex) );
	}

	@Override
    public Object getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		return getList( statement.getArray(columnIndex) );
	}

	@SuppressWarnings( "unchecked" )
	private List getList( Array array ) throws SQLException {

		List result = new ArrayList();

		if( array == null ) return result;

		for( Object e : (Object[]) array.getArray() ) {
			result.add( e );
		}

		return result;

	}

}
