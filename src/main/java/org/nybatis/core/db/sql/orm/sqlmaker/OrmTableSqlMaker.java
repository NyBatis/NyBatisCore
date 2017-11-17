package org.nybatis.core.db.sql.orm.sqlmaker;

import org.nybatis.core.db.datasource.DatasourceManager;
import org.nybatis.core.db.sql.orm.reader.EntityLayoutReader;
import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;
import org.nybatis.core.util.StringUtil;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class OrmTableSqlMaker {

    private String environmentId;

    public OrmTableSqlMaker( String environmentId ) {
        this.environmentId = environmentId;
    }

    private TableLayout getTableLayout( Class klass ) {
        try {
            return TableLayoutRepository.getLayout( environmentId, EntityLayoutReader.getTableName(klass) );
        } catch( SqlConfigurationException e ) {
            return null;
        }
    }

    private TableLayout getEntityLayout( Class klass ) {
        EntityLayoutReader reader = new EntityLayoutReader();
        return reader.getTableLayout( klass );
    }

    private String getCreateTable( TableLayout tableLayout ) {

        StringBuilder sql = new StringBuilder();

        sql.append( "CREATE TABLE " );

        return sql.toString();

    }

    private String getDatabset() {
        return DatasourceManager.getAttributes( environmentId ).getDatabase();
    }

    private String uncamel( String value ) {
        return StringUtil.toUncamel( value );
    }

}
