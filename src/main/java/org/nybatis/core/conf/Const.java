package org.nybatis.core.conf;

import org.nybatis.core.log.NLogger;
import org.nybatis.core.file.FileUtil;
import org.nybatis.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Place to pile up common constants or directory
 *
 * @author nayasis@gmail.com
 *
 */
public class Const {

    /**
     * Constants for developing WEB
     */
    public static final class web {

		private static String rootFilePath = "";

		private static String javascriptMessageObjectName  = "nayasis.common.msg.pool";

		public static void setRootFilePath( String rootFilePath ) {
			web.rootFilePath = rootFilePath;
		}

		public static String getRootFilePath() {
			return web.rootFilePath;
		}

		public static String getJavascriptMessageObjectName() {
			return javascriptMessageObjectName;
		}

		public static void setJavascriptMessageObjectName( String javascriptMessageObjectName ) {
			web.javascriptMessageObjectName = javascriptMessageObjectName;
		}
	}

	/**
	 * Profile Controller to specify envorionment for example loacl, stage, product
	 *
	 */
	public static final class profile {

		private static String name = null;

		static {
			name = System.getProperty( "nayasis.common.profile" );
		}

		/**
		 * set profile
		 *
		 * default value is
		 *
		 * @param name profile name
		 */
		public static void set( String name ) {
			profile.name = StringUtil.trim( name );
		}

		/**
		 * get profile
		 *
		 * @return profile name
		 */
		public static String get() {
			return name;
		}

		/**
		 * get filename applied profile setting
		 *
		 * <pre>
		 *
		 *     Const.profile.set( "local" )
		 *
		 *     Const.profile.getFileName( "/app/webapp/config.prop" )
		 *     --> "/app/webapp/config<font color=red>-local</font>.prop"
		 *
		 * </pre>
		 *
		 *
		 * @param file filename
		 * @return filename applied profile setting
		 */
		public static String getFileName( String file ) {
			if( StringUtil.isEmpty(name) || StringUtil.isBlank(file) ) return file;
			return String.format( "%s-%s.%s", FileUtil.removeExtention(file), profile.name, FileUtil.getExtention(file) );
		}

	}

	/**
	 * Path to use by NayasisCore
	 *
	 * @author nayasis@gmail.com
	 *
	 */
	public abstract static class path {

		private static final String root = new ConstHelper().getRoot();
		private static       String base = root;

		/**
		 * Get NayasisCore's base path. <br><br>
		 *
		 * It is similar with root path. but can be changable.<br>
		 * It's default value is root path.
		 *
		 * @return base path
		 */
		public static String getBase() {
	        return base;
        }

		/**
		 * Set base path of NayasisCore
		 *
		 * @param path base path of Nayasis Core
		 */
		public static void setBase( String path ) {

			if( ! FileUtil.isDirectory(path) ) {
				NLogger.info( "base path({}) to change does not exists. current base path({}) is not changed.", path, base );
				return;
			}

			base = path;

		}

		/**
		 * Set base path of NayasisCore to unit test in Junit or TestNg
		 *
		 */
		public static void setBaseForUnitTest() {
			setBase( getBase().replaceFirst( "/test-classes", "/classes" ) );
		}

		/**
		 * Get root directory where program runs. <br><br>
		 *
		 * It is similar with base path but immutable.
		 *
		 * @return root directory
		 */
		public static String getRoot() {
			return root;
		}

		/**
		 * Get configuration directory
		 *
		 * @return Configuration directory
		 */
		public static String getConfig() {
	        return getBase() + "/config";
        }

		/**
		 * Get Database configuration directory
		 *
		 * @return Database configuration directory
		 */
		public static String getConfigDatabase() {
	        return getConfig() + "/db";
        }

		/**
		 * Get Logger configuration directory
		 *
		 * @return Logger configuration directory
		 */
		public static String getConfigLogger() {
	        return getConfig() + "/log";
        }

		/**
		 * Get Message configuration directory
		 *
		 * @return Message configuration directory
		 */
		public static String getConfigMessage() {
	        return getConfig() + "/message";
        }

		/**
		 * Get local DB directory
		 *
		 * @return local DB directory
		 */
		public static String getLocalDatabase() {
	        return getBase() + "/localDb";
        }

	}

	/**
	 * Constants for determine platform
	 *
	 * @author nayasis@gmail.com
	 *
	 */
	public abstract static class platform {

		/** O/S name */
		public static final String  osName          = System.getProperty( "os.name"    );
		/** O/S architect */
		public static final String  osArchitecture  = System.getProperty( "os.arch"    );
		/** O/S version */
		public static final String  osVersion       = System.getProperty( "os.version" );
		/** O/S character set */
		public static final String  osCharset       = System.getProperty( "sun.jnu.encoding" );
		/** Java Virtual Machine architect */
		public static final String  jvmArchitecture = System.getProperty( "sun.arch.data.model" );
		/** is WINDOWS O/S */
		public static final boolean isWindows       = osName.startsWith( "Windows" );
		/** is LINUX O/S */
		public static final boolean isLinux         = osName.startsWith( "Linux"   );
		/** is MAC O/S */
		public static final boolean isMac           = osName.startsWith( "Mac OS"  );

	}

	/**
	 * Constants for Nybatis
	 */
	public static class db {

		public static final String ORM_SQL_PREFIX                = "NybatisOrm";
		public static final String ORM_PARAMETER_ENTITY          = ORM_SQL_PREFIX + "-Entity-";
		public static final String ORM_PARAMETER_USER            = ORM_SQL_PREFIX + "-User-";
		public static final String ORM_PARAMETER_WHERE           = ORM_SQL_PREFIX + "-DynamicSqlWhere";
		public static final String ORM_PARAMETER_ORDER_BY        = ORM_SQL_PREFIX + "-DynamicSqlOrderBy";
		public static final String ORM_SQL_SELECT_PK             = ".select.record";
		public static final String ORM_SQL_SELECT                = ".select";
		public static final String ORM_SQL_UPDATE_PK             = ".update.record";
		public static final String ORM_SQL_INSERT_PK             = ".insert.record";
		public static final String ORM_SQL_DELETE                = ".delete";
		public static final String ORM_SQL_DELETE_PK             = ".delete.record";


		public static final int    DEFAULT_CACHE_FLUSH_CYCLE     = Integer.MAX_VALUE;
		public static final int    DEFAULT_CACHE_CAPACITY        = 5120;

		public static final String DEFAULT_TABLE_NAME            = ORM_SQL_PREFIX + "-DEFAULT_TABLE_NAME";
		public static final String DEFAULT_ENVIRONMENT_ID        = ORM_SQL_PREFIX + "-DEFAULT_ENVIRONMENT_ID";
		public static final String PARAMETER_INNER_FOR_EACH      = "NyBatisInnerParamForEach";
		public static final String PARAMETER_SINGLE              = "NybatisSingleParameter";
		public static final String PARAMETER_DATABASE            = "nybatis.database";

		public static String getOrmSqlIdPrefix( String environmentId, String tableName ) {
			return String.format( "%s%s.%s", ORM_SQL_PREFIX, environmentId, tableName );
		}

		public static final String LOG_SQL                       = "nybatis.sql";
		public static final String LOG_BATCH                     = "nybatis.batch";



	}

}
