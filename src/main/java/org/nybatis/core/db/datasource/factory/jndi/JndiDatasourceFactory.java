package org.nybatis.core.db.datasource.factory.jndi;

import org.nybatis.core.db.datasource.DatasourceFactory;
import org.nybatis.core.db.datasource.factory.parameter.JndiConnectionProperties;
import org.nybatis.core.db.datasource.proxy.ProxyDataSource;
import org.nybatis.core.exception.unchecked.DatabaseConfigurationException;
import org.nybatis.core.util.StringUtil;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

public class JndiDatasourceFactory implements DatasourceFactory {

	private JndiConnectionProperties connectionProperties = null;

	public JndiDatasourceFactory( JndiConnectionProperties connectionProperties ) {
		this.connectionProperties = connectionProperties;
	}

	public DataSource getDataSource() {

		try {
			InitialContext initialContext = getInitialContext();
			DataSource datasource = (DataSource) initialContext.lookup( connectionProperties.getJndiName() );
			return new ProxyDataSource( datasource ).getDataSource();
		} catch( NamingException e ) {
        	throw new DatabaseConfigurationException( e, "There is no context about {}. If you use WAS, check jndi option in [server.xml] or [web.xml].", connectionProperties.getJndiName() );
        }

	}


	private InitialContext getInitialContext() throws NamingException {

        InitialContext ctx;

        if( StringUtil.isNotEmpty( connectionProperties.getInitialContext() ) && StringUtil.isNotEmpty( connectionProperties.getProviderUrl() ) ) {

        	Properties props = new Properties();
        	props.put( Context.INITIAL_CONTEXT_FACTORY, connectionProperties.getInitialContext() );
        	props.put( Context.PROVIDER_URL, connectionProperties.getProviderUrl() );

        	ctx = new InitialContext( props );

        } else {
        	ctx = new InitialContext();

        }

        return ctx;

	}

}
