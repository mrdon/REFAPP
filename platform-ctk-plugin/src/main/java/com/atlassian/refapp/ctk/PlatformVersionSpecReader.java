package com.atlassian.refapp.ctk;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.plugin.util.ClassLoaderUtils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public final class PlatformVersionSpecReader
{
    private PlatformVersionSpecReader()
    {
    }

    private static final String PLATFORM_VERSION_PATH = "com/atlassian/refapp/ctk/version/platformversions.xml";

    static Document readPlatformVersionDocument() throws RuntimeException
    {
        InputStream in = ClassLoaderUtils.getResourceAsStream(PLATFORM_VERSION_PATH, PlatformVersionSpecReader.class);

        try
        {
            SAXReader reader = new SAXReader();
            return reader.read(in);

        }
        catch (DocumentException e)
        {
            throw new RuntimeException("Cannot read the platform version definition", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }
    
    /**
     * Get the platform version.
     *
     * @return the platform version.
     */
    public static String getPlatformVersion() throws RuntimeException
    {
        Document document = readPlatformVersionDocument();

        String xPath = "/platform";
        List<Node> nodes = document.selectNodes(xPath);

        return nodes.get(0).valueOf("@version");
    }

    /**
     * Get the VersionChecks of modules.
     *
     * @return list of checks.
     */
    public static List<VersionCheck> getVersionChecks()
    {
        final List<VersionCheck> versionChecks = new ArrayList<VersionCheck>();
        
        Document document = readPlatformVersionDocument();

        List<Node> nodes = document.selectNodes("/platform/export-version-check");
        for (Node node : nodes)
        {
            String pkg = node.valueOf("@package");
            String version = node.valueOf("@version");
            String modulename = node.valueOf("@modulename");
            boolean optional = Boolean.parseBoolean(node.valueOf("@optional"));
            versionChecks.add(new ExportVersionCheck(pkg, version, modulename, optional));
        }

        List<Node> bundleCheckNodes = document.selectNodes("/platform/bundle-version-check");
        for (Node node : bundleCheckNodes)
        {
            String bundlename = node.valueOf("@bundlename");
            String version = node.valueOf("@version");
            String modulename = node.valueOf("@modulename");
            versionChecks.add(new BundleVersionCheck(bundlename, version, modulename));
        }
        
        return versionChecks;
    }

    public abstract static class VersionCheck
    {
        protected final String version;
        protected final String moduleName;

        VersionCheck(String version, String moduleName)
        {
            Validate.notEmpty(version);
            Validate.notEmpty(moduleName);
            this.version = version;
            this.moduleName = moduleName;
        }
        
        public String getVersion()
        {
            return version;
        }

        public String getModuleName()
        {
            return moduleName;
        }
    }

    public static class BundleVersionCheck extends VersionCheck
    {
        private final String bundleName;

        private BundleVersionCheck(String bundleName, String version, String moduleName)
        {
            super(version, moduleName);
            Validate.notEmpty(bundleName);

            this.bundleName = bundleName;
        }

        public String getBundleName()
        {
            return bundleName;
        }
    }

    public static class ExportVersionCheck extends VersionCheck
    {
        private final String pkg;
        private final boolean optional;

        private ExportVersionCheck(String pkg, String version, String moduleName, boolean optional)
        {
            super(version, moduleName);
            Validate.notEmpty(pkg);

            this.pkg = pkg;
            this.optional = optional;
        }

        public String getPkg()
        {
            return pkg;
        }

        public boolean isOptional()
        {
            return optional;
        }
    }
}
