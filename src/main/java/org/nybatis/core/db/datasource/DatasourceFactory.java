package org.nybatis.core.db.datasource;

import javax.sql.DataSource;

public interface DatasourceFactory {
	DataSource getDataSource();
}
