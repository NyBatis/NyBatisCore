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
    private String patternToMatchClassName     = "";

    private boolean enableToGetParameterType   = true;
    private boolean enableToGetBLob            = true;
    private boolean enableToDoLobPrefetch      = true;

    private String  pageSqlPre                 = "";
    private String  pageSqlPost                = String.format( " LIMIT #{%s}, #{%s}", PAGE_PARAM_START, PAGE_PARAM_END );
    private String  countSqlPre                = "SELECT COUNT(1) AS CNT FROM ( ";
    private String  countSqlPost               = " )";

    /**
     * Driver Attributes
     */
    public DatabaseAttribute() {}

    /**
     * Driver Attributes
     *
     * @param database Driver name (oracle, mysql, etc...)
     * @param patternToMatchClassName Driver name pattern to match with Database name. It must be regular expression.
     */
    public DatabaseAttribute( String database, String patternToMatchClassName ) {
        setDatabase( database );
        setPatternToMatchClassName( patternToMatchClassName );
    }

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
        return Validator.isFinded( driverName, patternToMatchClassName );
    }

    public boolean enableToGetParameterType() {
        return enableToGetParameterType;
    }
    public DatabaseAttribute enableToGetParameterType( boolean enable ) {
        this.enableToGetParameterType = enable;
        return this;
    }
    public boolean enableToGetBlob() {
        return enableToGetBLob;
    }
    public DatabaseAttribute enableToGetBlob( boolean enable ) {
        this.enableToGetBLob = enable;return this;
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

    public String toString() {

        return String.format(
                "Database\n" +
                " - name : [%s]\n" +
                " - Pattern to match with class name: [%s]\n" +
                "Page Sql\n" +
                " - pre : [%s]\n" +
                " - post: [%s]"
                ,
                getDatabase(),
                patternToMatchClassName,
                getPageSqlPre(),
                getPageSqlPost()
        );

    }

    public DatabaseAttribute clone() {

        DatabaseAttribute attribute = new DatabaseAttribute( database, patternToMatchClassName );

        attribute.enableToGetParameterType = enableToGetParameterType;
        attribute.enableToGetBLob          = enableToGetBLob;
        attribute.enableToDoLobPrefetch    = enableToDoLobPrefetch;
        attribute.pageSqlPre               = pageSqlPre;
        attribute.pageSqlPost              = pageSqlPost;
        attribute.countSqlPre              = countSqlPre;
        attribute.countSqlPost             = countSqlPost;

        return attribute;

    }

}
