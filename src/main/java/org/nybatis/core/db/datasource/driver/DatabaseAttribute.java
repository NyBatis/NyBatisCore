package org.nybatis.core.db.datasource.driver;

import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

/**
 * Driver Attrubites
 *
 * @author nayasis@gmail.com
 * @since 2015-10-29
 */
public class DatabaseAttribute {

    public static final String PAGE_PARAM_START = "NybatisPagebuilder.START";
    public static final String PAGE_PARAM_END   = "NybatisPagebuilder.END";
    public static final String DATABASE_UNKOWN  = "unknown";

    private String  database                   = DATABASE_UNKOWN;
    private String  patternToMatchClassName    = "";

    private boolean enableToGetParameterType   = true;
    private boolean enableToDoLobPrefetch      = true;

    private String  pageSqlPre                 = "";
    private String  pageSqlPost                = String.format( "\nLIMIT #{%s}, #{%s}", PAGE_PARAM_START, PAGE_PARAM_END );
    private String  countSqlPre                = "SELECT COUNT(1) AS CNT FROM (\n";
    private String  countSqlPost               = "\n) NybatisCountQuery";

    private String  pingQuery                  = "SELECT 1";

    /**
     * Driver Attributes
     */
    public DatabaseAttribute() {}

    /**
     * Driver Attributes
     *
     * @see DatabaseName
     * @param database Driver name (oracle, mysql, maria, sqlite, h2, hsqldb, mssql, postgresql, sybase, db2, odbc ...)
     * @param patternToMatchClassName Driver name pattern to match with Database name. It must be regular expression.
     */
    public DatabaseAttribute( String database, String patternToMatchClassName ) {
        setDatabase( database );
        setPatternToMatchClassName( patternToMatchClassName );
    }

    /**
     * Driver Attributes
     *
     * @see DatabaseName
     * @param database Driver name (oracle, mysql, maria, sqlite, h2, hsqldb, mssql, postgresql, sybase, db2, odbc ...)
     */
    public DatabaseAttribute( DatabaseName database ) {
        setDatabase( database.name );
        setPatternToMatchClassName( database.driverNamePattern );
    }


    /**
     * get database type
     *
     * @see DatabaseName
     * @return database type (oracle, mysql, maria, sqlite, h2, hsqldb, mssql, postgresql, sybase, db2, odbc ...)
     */
    public String getDatabase() {
        return database;
    }

    public DatabaseAttribute setDatabase( String database ) {
        this.database = StringUtil.compressSpaceOrEnter( database );
        return this;
    }

    public String getPatternToMatchClassName() {
        return patternToMatchClassName;
    }

    public DatabaseAttribute setPatternToMatchClassName( String pattern ) {
        this.patternToMatchClassName = StringUtil.compressSpaceOrEnter( pattern );
        return this;
    }

    /**
     * Check whether connection class name is matched with setted pattern.
     *
     * @param driverName driver name
     * @return if matched with pattern, return true
     */
    public boolean isMatched( String driverName ) {
        return Validator.isFound( driverName, patternToMatchClassName );
    }

    public boolean enableToGetParameterType() {
        return enableToGetParameterType;
    }
    public DatabaseAttribute enableToGetParameterType( boolean enable ) {
        this.enableToGetParameterType = enable;
        return this;
    }

    public boolean enableToDoLobPrefetch() {
        return enableToDoLobPrefetch;
    }

    public DatabaseAttribute enableToDoLobPrefetch( boolean enable ) {
        this.enableToDoLobPrefetch = enable;
        return this;
    }

    public String getPageSqlPre() {
        return pageSqlPre;
    }
    public DatabaseAttribute setPageSqlPre( String sql ) {
        pageSqlPre = StringUtil.compressSpaceOrEnter( sql ) + " ";
        pageSqlPre = pageSqlPre.replaceAll( "(?i)#\\{start\\}", "#{" + PAGE_PARAM_START + "}" ).replaceAll( "(?i)#\\{end\\}", "#{" + PAGE_PARAM_END + "}" );
        return this;
    }
    public String getPageSqlPost() {
        return pageSqlPost;
    }
    public DatabaseAttribute setPageSqlPost( String sql ) {
        pageSqlPost = StringUtil.compressSpaceOrEnter( sql ) + " ";
        pageSqlPost = pageSqlPost.replaceAll( "(?i)#\\{start\\}", "#{" + PAGE_PARAM_START + "}" ).replaceAll( "(?i)#\\{end\\}", "#{" + PAGE_PARAM_END + "}" );
        return this;
    }

    public String getCountSqlPre() {
        return countSqlPre;
    }
    public DatabaseAttribute setCountSqlPre( String sql ) {
        this.countSqlPre = StringUtil.compressSpaceOrEnter( sql ) + " ";
        return this;
    }
    public String getCountSqlPost() {
        return countSqlPost;
    }
    public DatabaseAttribute setCountSqlPost( String sql ) {
        this.countSqlPost = StringUtil.compressSpaceOrEnter( sql ) + " ";
        return this;
    }

    public String getPingQuery() {
        return pingQuery;
    }
    public DatabaseAttribute setPingQuery( String query ) {
        pingQuery = StringUtil.compressSpaceOrEnter( query );
        return this;
    }

    public String toString() {

        return String.format(
                "Database\n" +
                " - name : [%s]\n" +
                " - Pattern to match with class name: [%s]\n" +
                "Page Sql\n" +
                " - pre :\n%s\n" +
                " - post:\n%s\n" +
                "Ping Query:\n" +
                " - %s"
                ,
                getDatabase(),
                patternToMatchClassName,
                getPageSqlPre(),
                getPageSqlPost(),
                getPingQuery()
        );

    }

    public DatabaseAttribute clone() {

        DatabaseAttribute attribute = new DatabaseAttribute();

        attribute.database                 = database;
        attribute.patternToMatchClassName  = patternToMatchClassName;
        attribute.enableToGetParameterType = enableToGetParameterType;
        attribute.enableToDoLobPrefetch    = enableToDoLobPrefetch;
        attribute.pageSqlPre               = pageSqlPre;
        attribute.pageSqlPost              = pageSqlPost;
        attribute.countSqlPre              = countSqlPre;
        attribute.countSqlPost             = countSqlPost;
        attribute.pingQuery                = pingQuery;

        return attribute;

    }

}
