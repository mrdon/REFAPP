package com.atlassian.plugin.refimpl;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for system exports.
 *
 * @since 2.14.0
 */
public class SystemExportVersionUtils
{
    private SystemExportVersionUtils()
    {
    }

    private static final String GUAVA_PROPERTY_PATH = "META-INF/maven/com.google.guava/guava/pom.properties";

    /**
     * Retrieves version number of the bundled google guava.
     *
     * @return version of google guava
     */
    public static String getGoogleGuavaVersion()
    {
        InputStream inputStream = null;

        try
        {
            inputStream = SystemExportVersionUtils.class.getClassLoader().getResourceAsStream(GUAVA_PROPERTY_PATH);

            if (inputStream == null)
            {
                throw new IllegalStateException("google guava is required in classpath for refapp to function");
            }

            Properties props = new Properties();
            try
            {
                props.load(inputStream);
            }
            catch (IOException e)
            {
                throw new IllegalStateException("cannot read the bundled google guava version file");
            }

            final String guavaVersion = props.getProperty("version");
            if (guavaVersion == null)
            {
                throw new IllegalStateException("cannot extract google guava version from the bundled version file");
            }

            return guavaVersion;
        }
        finally
        {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
