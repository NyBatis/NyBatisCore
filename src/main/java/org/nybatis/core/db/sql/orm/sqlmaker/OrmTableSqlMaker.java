package org.nybatis.core.db.sql.orm.sqlmaker;

import org.nybatis.core.db.sql.orm.vo.TableLayout;
import org.nybatis.core.db.sql.repository.TableLayoutRepository;
import org.nybatis.core.exception.unchecked.SqlConfigurationException;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-16
 */
public class OrmTableSqlMaker {

    private String environmentId;

    public OrmTableSqlMaker( String environmentId ) {
        this.environmentId = environmentId;
    }

    private TableLayout getTableLayout( String environmentId, String tableName ) {
        try {
            return TableLayoutRepository.getLayout( environmentId, tableName );
        } catch( SqlConfigurationException e ) {
            return null;
        }
    }

}
