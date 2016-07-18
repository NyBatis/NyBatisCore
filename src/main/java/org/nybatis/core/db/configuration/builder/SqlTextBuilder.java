package org.nybatis.core.db.configuration.builder;

import org.nybatis.core.db.sql.reader.SqlReader;
import org.nybatis.core.db.sql.repository.SqlRepository;
import org.nybatis.core.validation.Assertion;

/**
 * Sql builder from text
 *
 * @author nayasis@gmail.com
 * @since 2016-03-18
 */
public class SqlTextBuilder {

    private String environmentId;

    public SqlTextBuilder( String environmentId ) {
        Assertion.isNotEmpty( environmentId, "Environment id is missing." );
        this.environmentId = environmentId;
    }

    public SqlTextBuilder set( String id, String xmlSql ) {
        SqlRepository.put( id, new SqlReader().read( environmentId, id, xmlSql ) );
        return this;

    }

}
