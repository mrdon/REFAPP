package com.atlassian.sal.refimpl.sql;

import com.atlassian.sal.api.sql.DataSourceProvider;

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
}
