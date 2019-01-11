package org.nybatis.core.conf;

import org.nybatis.core.file.FileUtil;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.validation.Validator;


/**
 * Place to pile up common constants or directory
 *
 * @author nayasis@gmail.com
 *
 */
public class Const {

	/**
	 * Profile Controller to specify envorionment for example loacl, stage, product
	 *
	 */
	public static final class profile {

		private static String name = null;

		static {
			name = StringUtil.nvl( System.getProperty("nayasis.common.profile") );
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
		 *     Const.profile.apply( "/app/webapp/config.prop" )
		 *     -&gt; "/app/webapp/config<font style="color:red">.local</font>.prop"
		 *
		 * </pre>
		 *
		 *
		 * @param file filename
		 * @return filename applied profile setting
		 */
		public static String apply( String file ) {
			if( StringUtil.isEmpty(name) || StringUtil.isBlank(file) ) return file;
			return String.format( "%s%s.%s",
					FileUtil.removeExtention(file),
					Validator.isEmpty(profile.name) ? "" : "-" + profile.name,
					FileUtil.getExtention(file)
			);
		}

	}

	/**
	 * Path to use by NayasisCore
	 *
	 * @author nayasis@gmail.com
	 *
	 */
	public abstract static class path {

		private static final String  root          = new ConstHelper().getRoot();
		private static       boolean isWebinfExist = new ConstHelper().isWebInfExist();
		private static       String  base          = root;

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
				NLogger.warn( "base path({}) does not exists in file system.", path );
			}

			base = FileUtil.nomalizeSeparator( path );

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
			if( isWebInfExist() && ! getBase().endsWith( "/WEB-INF/classes" ) ) {
				return getBase() + "/WEB-INF/classes/config";
			}
			return getBase() + "/config";
        }

		/**
		 * check whether '/WEB-INF' directory resource exists.
		 * @return true if 'WEB-INF' directory exists.
         */
		public static boolean isWebInfExist() {
			return isWebinfExist;
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
		 * Convert file path to resource path. <br>
		 *
		 * (remove root path from file path.)
		 *
		 * @param filePath file path
		 * @return resource path
		 */
		public static String toResourceName( String filePath ) {
			return FileUtil.nomalizeSeparator( filePath ).replaceFirst( "^" + base, "" ).replaceFirst( "^/+", "" );
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

		public static final String DEFAULT_TABLE_NAME            = ORM_SQL_PREFIX + "-DEFAULT_TABLE_NAME";
		public static final String DEFAULT_ENVIRONMENT_ID        = ORM_SQL_PREFIX + "-DEFAULT_ENVIRONMENT_ID";
		public static final String PARAMETER_INNER_FOR_EACH      = "NyBatisInnerParamForEach";
		public static final String PARAMETER_SINGLE              = "NybatisSingleParameter";
		public static final String PARAMETER_DATABASE            = "nybatis.database";

		public static String getOrmSqlIdPrefix( String environmentId, String tableName ) {
			return String.format( "%s.%s.%s", ORM_SQL_PREFIX, environmentId, tableName );
		}

		public static final String LOG_SQL                       = "nybatis.sql";
		public static final String LOG_BATCH                     = "nybatis.batch";

	}

}
