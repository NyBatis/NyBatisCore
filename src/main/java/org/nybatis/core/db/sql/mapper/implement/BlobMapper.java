package org.nybatis.core.db.sql.mapper.implement;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.nybatis.core.db.sql.mapper.SqlType;
import org.nybatis.core.db.sql.mapper.TypeMapperIF;
import org.nybatis.core.exception.unchecked.JdbcImplementException;
import org.nybatis.core.log.NLogger;

public class BlobMapper implements TypeMapperIF<byte[]>{

	private static final String ERROR_MSG = "Blob is not implemented. {}\nBlobMapper will be changed to ByteMapper.";

	@Override
    public void setParameter( PreparedStatement statement, int index, byte[] param ) throws SQLException {
		ByteArrayInputStream bis = new ByteArrayInputStream( param );
		try {
	        statement.setBinaryStream( index, bis, param.length );
        } catch( SQLException e ) {
			NLogger.info( ERROR_MSG, getErrorDetail( statement ) );
        	throw new JdbcImplementException( e );
        }
    }

	@Override
	public void setOutParameter( CallableStatement statement, int index ) throws SQLException {
		statement.registerOutParameter( index, SqlType.find(byte[].class).toCode() );
	}

	@Override
    public byte[] getResult( ResultSet resultSet, String columnName ) throws SQLException {
		try {
	        return getBytes( resultSet.getBlob(columnName) );
        } catch( SQLException e ) {
        	NLogger.info( ERROR_MSG, getErrorDetail( resultSet ) );
	        throw new JdbcImplementException( e );
        }
    }

	@Override
    public byte[] getResult( ResultSet resultSet, int columnIndex ) throws SQLException {
		try {
	        return getBytes( resultSet.getBlob(columnIndex) );
        } catch( SQLException e ) {
        	NLogger.info( ERROR_MSG, getErrorDetail( resultSet ) );
        	throw new JdbcImplementException( e );
        }
    }

	@Override
    public byte[] getResult( CallableStatement statement, int columnIndex ) throws SQLException {
		try {
	        return getBytes( statement.getBlob(columnIndex) );
        } catch( SQLException e ) {
        	NLogger.info( ERROR_MSG, getErrorDetail( statement ) );
        	throw new JdbcImplementException( e );
        }
    }

	private byte[] getBytes( Blob blob ) throws SQLException {
		if( blob == null ) return new byte[] {};
		return blob.getBytes( 1, (int) blob.length() );
	}

	private String getErrorDetail( Connection connection ) {

		String driverInfo = "";

		if( connection != null ) {
			try {
				DatabaseMetaData dbMeta = connection.getMetaData();
	            driverInfo = String.format( "(Driver : %s(%s), URL : %s)", dbMeta.getDriverName(),  connection.getClass().getName(), dbMeta.getURL() );
			} catch( SQLException e ) {
	            NLogger.error( e );
            }
		}

		return driverInfo;

	}

	private String getErrorDetail( Statement statement ) {
		try {
	        return statement == null ? "" : getErrorDetail( statement.getConnection() );
        } catch( SQLException e ) {
        	return "";
        }
	}

	private String getErrorDetail( ResultSet resultSet ) {
		try {
			return resultSet == null ? "" : getErrorDetail( resultSet.getStatement() );
		} catch( SQLException e ) {
			return "";
		}
	}

}
