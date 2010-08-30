package com.atlassian.activeobjects.refimpl;


import com.atlassian.activeobjects.spi.DataSourceProvider;
import com.atlassian.activeobjects.spi.DatabaseType;

import javax.sql.DataSource;

import static com.google.common.base.Preconditions.checkNotNull;

public class RefimplDataSourceProvider implements DataSourceProvider
{
    private final DataSource dataSource;

    public RefimplDataSourceProvider(DataSource dataSource)
    {
        this.dataSource = checkNotNull(dataSource);
    }

    public DataSource getDataSource()
    {
        return dataSource;
    }

    public DatabaseType getDatabaseType()
    {
        return DatabaseType.HSQL;
    }
}
