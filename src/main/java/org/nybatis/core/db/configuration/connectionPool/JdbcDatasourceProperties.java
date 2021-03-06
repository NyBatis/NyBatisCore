package org.nybatis.core.db.configuration.connectionPool;

import org.nybatis.core.util.StringUtil;

public class JdbcDatasourceProperties {

	private String  id          = "";
	private boolean isPooled    = true;

	private int     poolMin     = 1;
	private int     poolMax     = 20;
	private int     poolStep    = 5;

	private boolean pingEnable  = false;
	private String  pingQuery   = null;
	private int     pingCycle   = 60_000;   // mili-sec

	public JdbcDatasourceProperties() {}

	public JdbcDatasourceProperties( String id ) {
		this.id = id;
	}

	public int getPoolMin() {
		return poolMin;
	}

	public void setPoolMin( int count ) {
		this.poolMin = count;
	}

	public void setPoolMin( String count ) {
		try {
			this.poolMin = Integer.parseInt( count );
		} catch( NumberFormatException e ) {}
	}

	public int getPoolMax() {
		return poolMax;
	}

	public void setPoolMax( int count ) {
		this.poolMax = count;
	}

	public void setPoolMax( String count ) {
		try {
			this.poolMax = Integer.parseInt( count );
		} catch( NumberFormatException e ) {}
	}

	public int getPoolStep() {
		return poolStep;
	}

	public void setPoolStep( int count ) {
		this.poolStep = count;
	}

	public void setPoolStep( String count ) {
		try {
			this.poolStep = Integer.parseInt( count );
		} catch( NumberFormatException e ) {}
	}

	public String getPingQuery() {
		return pingQuery;
	}

	public void setPingQuery( String query ) {
		pingQuery = query;
	}

	/**
	 * Get Ping cycle time
	 *
	 * @return mili-second
	 */
	public long getPingCycle() {
		return pingCycle;
	}

	public void setPingCycle( int seconds ) {
		this.pingCycle = seconds * 1_000;
	}

	public void setPingCycle( String seconds ) {
		try {
			this.pingCycle = Integer.parseInt( seconds ) * 1_000;
		} catch( NumberFormatException e ) {}
	}

    public boolean isPingEnable() {
	    return pingEnable;
    }

    public void setPingEnable( boolean enable ) {
	    pingEnable = enable;
    }

    public void setPingEnable( String enable ) {
    	pingEnable = StringUtil.isTrue( enable );
    }

	public boolean isPooled() {
	    return isPooled;
    }

	public void setPooled( boolean isPooled ) {
	    this.isPooled = isPooled;
    }

	public void setPooled( String isPooled ) {
		this.isPooled = StringUtil.isEmpty( isPooled ) || StringUtil.isTrue( isPooled );
	}

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}
}
