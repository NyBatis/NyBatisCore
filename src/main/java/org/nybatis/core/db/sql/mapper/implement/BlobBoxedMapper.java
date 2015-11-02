package org.nybatis.core.db.sql.mapper.implement;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;

public class BlobBoxedMapper implements TypeMapperIF<Byte[]> {

    @Override
    public void setParameter( PreparedStatement statement, int index, Byte[] param ) throws SQLException {

        ByteArrayInputStream bis = new ByteArrayInputStream( toPrimitiveArray(param) );

        statement.setBinaryStream( index, bis, param.length );

    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(Byte[].class).toCode() );
	}

	@Override
    public Byte[] getResult( ResultSet resultSet, String columnName ) throws SQLException {

		Blob blob = resultSet.getBlob( columnName );

		return getBytes( blob );

    }

	@Override
    public Byte[] getResult( ResultSet resultSet, int columnIndex ) throws SQLException {

		Blob blob = resultSet.getBlob( columnIndex );

		return getBytes( blob );

	}

	@Override
    public Byte[] getResult( CallableStatement statement, int columnIndex ) throws SQLException {

		Blob blob = statement.getBlob( columnIndex );

		return getBytes( blob );

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

	private Byte[] getBytes( Blob blob ) throws SQLException {
	    return blob == null ? new Byte[] {} : toObjectArray( blob.getBytes(1, (int) blob.length()) );
    }


}
