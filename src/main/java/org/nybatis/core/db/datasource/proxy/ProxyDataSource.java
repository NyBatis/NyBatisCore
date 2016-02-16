package org.nybatis.core.db.datasource.proxy;

import java.lang.reflect.Method;
import java.sql.Connection;

import javax.sql.DataSource;

import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.MethodInvocator;

public class ProxyDataSource {

    private int             hashCode = 0;
    private DataSource      proxyDataSource;

    public ProxyDataSource( DataSource dataSource ) {
    	this.hashCode        = dataSource.hashCode();
    	this.proxyDataSource = invoke( dataSource );
    }

    public int hashCode() {
        return hashCode;
    }

    public boolean equals( Object object ) {

        if ( object instanceof ProxyDataSource || object instanceof DataSource ) {
            return hashCode == object.hashCode();
        }

        return false;

    }

    public DataSource getDataSource() {
        return proxyDataSource;
    }

	private DataSource invoke( DataSource dataSource ) {

		return new Reflector().wrapProxy( dataSource, new Class<?>[] {DataSource.class}, new MethodInvocator() {
            public Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable {

                switch( method.getName() ) {

                    case "getConnection":
                        Connection connection = (Connection) method.invoke( dataSource, arguments );
                        return new ProxyConnection( connection ).getConnection();

                }

                return method.invoke( dataSource, arguments );

            }
        } );

	}

}
