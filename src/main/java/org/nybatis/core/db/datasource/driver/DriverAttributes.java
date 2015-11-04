package org.nybatis.core.db.datasource.driver;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;

/**
 * Driver Attrubites
 *
 * @author nayasis@gmail.com
 * @since 2015-10-29
 */
public class DriverAttributes {

    public static final String PAGE_PARAM_START = "NybatisPagebuilder.START";
    public static final String PAGE_PARAM_END   = "NybatisPagebuilder.END";
    public static final String DATABASE_UNKOWN  = "unknown";

    private String  database                   = DATABASE_UNKOWN;
    private String  connectionClassNamePattern = "";

    private boolean enableGetParameterType     = true;
    private boolean enableGetBLob              = true;

    private String  pageSqlPre                 = "";
    private String  pageSqlPost                = String.format( " LIMIT #{%s}, #{%s}", PAGE_PARAM_START, PAGE_PARAM_END );
    private String  countSqlPre                = "SELECT COUNT(1) AS CNT FROM ( ";
    private String  countSqlPost               = " )";

    /**
     * Driver Attributes
     */
    public DriverAttributes() {}

    /**
     * Driver Attributes
     *
     * @param database Driver name (oracle, mysql, etc...)
     * @param connectionClassNamePattern Driver name pattern to match with Database name. It must be regular expression.
     */
    public DriverAttributes( String database, String connectionClassNamePattern ) {
        setDatabase( database );
        setConnectionClassNamePattern( connectionClassNamePattern );
    }

    public String getDatabase() {
        return database;
    }

    public DriverAttributes setDatabase( String database ) {
        this.database = StringUtil.compressSpaceOrEnter( database ).toLowerCase();
        return this;
    }

    public String getConnectionClassNamePattern() {
        return connectionClassNamePattern;
    }

    public DriverAttributes setConnectionClassNamePattern( String connectionClassNamePattern ) {
        this.connectionClassNamePattern = StringUtil.compressSpaceOrEnter( connectionClassNamePattern );
        return this;
    }

    /**
     * Check whether connection class name is matched with setted pattern.
     *
     * @param connectionClassName class name of Connection instance
     * @return if matched with pattern, return true
     */
    public boolean isMatched( String connectionClassName ) {

        if( NLogger.isTraceEnabled() ) {
            NLogger.trace(
                "Database type    : {}\n" +
                "Pattern to match : {}\n" +
                "Match result     : {}\n",
                    database, connectionClassNamePattern, Validator.isFinded(connectionClassName, connectionClassNamePattern)
            );
        }

        return Validator.isFinded( connectionClassName, connectionClassNamePattern );
    }

    public boolean enableGetParameterType() {
        return enableGetParameterType;
    }
    public DriverAttributes enableGetParameterType( boolean enable ) {
        this.enableGetParameterType = enable;
        return this;
    }
    public boolean enableBlobGet() {
        return enableGetBLob;
    }
    public DriverAttributes enableBlobGet( boolean enable ) {
        this.enableGetBLob = enable;return this;
    }

    public String getPageSqlPre() {
        return pageSqlPre;
    }
    public DriverAttributes setPageSqlPre( String sql ) {
        pageSqlPre = StringUtil.compressSpaceOrEnter( sql ) + " ";
        pageSqlPre = pageSqlPre.replaceAll( "(?i)#\\{start\\}", "#{" + PAGE_PARAM_START + "}" ).replaceAll( "(?i)#\\{end\\}", "#{" + PAGE_PARAM_END + "}" );
        return this;
    }
    public String getPageSqlPost() {
        return pageSqlPost;
    }
    public DriverAttributes setPageSqlPost( String sql ) {
        pageSqlPost = StringUtil.compressSpaceOrEnter( sql ) + " ";
        pageSqlPost = pageSqlPost.replaceAll( "(?i)#\\{start\\}", "#{" + PAGE_PARAM_START + "}" ).replaceAll( "(?i)#\\{end\\}", "#{" + PAGE_PARAM_END + "}" );
        return this;
    }

    public String getCountSqlPre() {
        return countSqlPre;
    }
    public DriverAttributes setCountSqlPre( String sql ) {
        this.countSqlPre = StringUtil.compressSpaceOrEnter( sql ) + " ";
        return this;
    }
    public String getCountSqlPost() {
        return countSqlPost;
    }
    public DriverAttributes setCountSqlPost( String sql ) {
        this.countSqlPost = StringUtil.compressSpaceOrEnter( sql ) + " ";
        return this;
    }

}
