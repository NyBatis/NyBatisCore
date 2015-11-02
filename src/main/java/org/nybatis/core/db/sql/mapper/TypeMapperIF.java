package org.nybatis.core.db.sql.mapper;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeMapperIF<T> {

    void setParameter( PreparedStatement statement, int index, T param ) throws SQLException;

    void setOutParameter( CallableStatement statement, int index ) throws SQLException;

    T getResult( ResultSet resultSet, String columnName ) throws SQLException;

    T getResult( ResultSet resultSet, int columnIndex ) throws SQLException;

    T getResult( CallableStatement statement, int columnIndex ) throws SQLException;

}