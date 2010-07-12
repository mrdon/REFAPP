package com.atlassian.sal.refimpl.sql;

import com.atlassian.sal.api.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * {@code DataSource} implementation that backs onto a HSQL instance.
 */
public final class HsqlDataSource implements DataSource
{
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String DEFAULT_BASE_DIR = "data/plugins/activeobjects";
    private static final String DEFAULT_USERNAME = "sa";
    private static final String DEFAULT_PASSWORD = "";

    private final ApplicationProperties applicationProperties;

    private PrintWriter logWriter;
    private int loginTimeout;
    private String jdbcUrl;

    public HsqlDataSource(ApplicationProperties applicationProperties)
    {
        this.applicationProperties = checkNotNull(applicationProperties);
    }

    public Connection getConnection() throws SQLException
    {
        return getConnection(DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public Connection getConnection(String username, String password) throws SQLException
    {
        if (jdbcUrl == null)
        {
            jdbcUrl = constructJdbcUrl();
        }

        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public PrintWriter getLogWriter() throws SQLException
    {
        return logWriter;
    }

    public void setLogWriter(PrintWriter logWriter) throws SQLException
    {
        this.logWriter = logWriter;
    }

    public void setLoginTimeout(int loginTimeout) throws SQLException
    {
        this.loginTimeout = loginTimeout;
    }

    public int getLoginTimeout() throws SQLException
    {
        return loginTimeout;
    }

    // Wrapper methods required for JDK 1.6; no one cares

    public boolean isWrapperFor(Class iface) throws SQLException
    {
        return false;
    }

    public Object unwrap(Class iface) throws SQLException
    {
        throw new UnsupportedOperationException();
    }

    private String constructJdbcUrl()
    {
        File productHome = applicationProperties.getHomeDirectory();

        if (productHome == null)
        {
            throw new RuntimeException("no home directory defined by product");
        }

        if (!productHome.exists() || !productHome.isDirectory())
        {
            throw new RuntimeException("Couldn't find product home directory at '" + productHome.getAbsolutePath() + "'");
        }

        final File dbDirectory = new File(productHome, DEFAULT_BASE_DIR);

        if (dbDirectory.exists() && dbDirectory.isFile())
        {
            throw new RuntimeException("Database directory already exists, but is a file, at <" + dbDirectory.getPath() + ">");
        }

        if (!dbDirectory.exists() && !dbDirectory.mkdirs())
        {
            throw new RuntimeException("Could not create directory for database at <" + dbDirectory.getPath() + ">");
        }

        log.debug("ActiveObjects databases directory {} initialized", dbDirectory.getAbsolutePath());

        StringBuilder url = new StringBuilder("jdbc:hsqldb:");
        url.append(dbDirectory.getAbsolutePath());
        url.append(";hsqldb.default_table_type=cached");

        return url.toString();
    }
}
