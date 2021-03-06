package org.nybatis.core.db.sql.mapper.implement;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class ByteBoxedArrayMapper implements TypeMapperIF<Byte[]> {

	@Override
    public void setParameter( PreparedStatement statement, int index, Byte[] param ) throws SQLException {
		statement.setBytes( index, toPrimitiveArray(param) );
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Byte[].class).toCode() );
	}

	@Override
    public Byte[] getResult( ResultSet resultSet, String columnName ) throws SQLException {
		return getBytes( resultSet.getBytes(columnName) );
    }

	@Override
    public Byte[] getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		return getBytes( resultSet.getBytes(columnIndex) );
    }

	@Override
    public Byte[] getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		return getBytes( statement.getBytes(columnIndex) );
    }

	private byte[] toPrimitiveArray( Byte[] objects ) {

		final byte[] bytes = new byte[ objects.length ];

		for( int i = 0, iCnt = objects.length; i < iCnt; i++) {
		    bytes[i] = objects[i].byteValue();
		}

		return bytes;

	}

	private Byte[] toObjectArray( byte[] bytes ) {

		final Byte[] objects = new Byte[ bytes.length ];

		for( int i = 0, iCnt = bytes.length; i < iCnt; i++) {
			objects[i] = Byte.valueOf(bytes[i]);
		}

		return objects;

	}

	private Byte[] getBytes( byte[] bytes ) throws SQLException {
	    return bytes == null ? new Byte[] {} : toObjectArray( bytes );
    }

}
