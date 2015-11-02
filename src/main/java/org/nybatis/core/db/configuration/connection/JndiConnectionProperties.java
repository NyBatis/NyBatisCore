package org.nybatis.core.db.configuration.connection;

import org.nybatis.core.util.StringUtil;

public class JndiConnectionProperties {

	private String initialContext;
	private String providerUrl;
	private String jndiName;

	public JndiConnectionProperties( String initialContext, String providerUrl, String jndiName ) {

		if( StringUtil.isNotEmpty( initialContext ) ) this.initialContext = initialContext;
		if( StringUtil.isNotEmpty( providerUrl )    ) this.providerUrl    = providerUrl;
		
		setJndiName( jndiName );
	
	}
	
	public JndiConnectionProperties( String jndiName ) {
		setJndiName( jndiName );
	}

    public String getInitialContext() {
	    return initialContext;
    }
    
    public String getProviderUrl() {
    	return providerUrl;
    }
    
    private void setJndiName( String jndiName ) {
    	
    	this.jndiName = String.format( "java:comp/env/%s", jndiName.replaceFirst("(?i)java:comp/env", "") ).replaceAll( "/+", "/" );
    	
    }
    
    public String getJndiName() {
    	return jndiName;
    }

    public String toString() {
    	
    	String format = null;
    	
    	if( StringUtil.isNotEmpty( initialContext) && StringUtil.isNotEmpty( providerUrl ) ) {
    		format = "{ initialContext : \"%s\", providerUrl : \"%s\", jndiName : \"%s\" }";
    		
    	} else {
    		format = "{ jndiName : \"%s\" }";
    		
    	}
    	
    	return String.format( format, getJndiName() );
    	
    }
	
}
