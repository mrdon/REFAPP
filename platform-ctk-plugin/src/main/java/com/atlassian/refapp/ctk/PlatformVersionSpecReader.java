package com.atlassian.refapp.ctk;

import com.atlassian.plugin.util.ClassLoaderUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PlatformVersionSpecReader
{
    private PlatformVersionSpecReader()
    {
    }

    private static final String PLATFORM_VERSION_PATH = "com/atlassian/refapp/ctk/version/platformversions.xml";

    /**
     * Get the platform version.
     *
     * @return the platform version.
     */
    public static String getPlatformVersion()
    {
        InputStream in = null;

        try
        {
            in = ClassLoaderUtils.getResourceAsStream(PLATFORM_VERSION_PATH, PlatformVersionSpecReader.class);

            SAXReader reader = new SAXReader();
            Document document = reader.read(in);

            String xPath = "//platform";
            List<Node> nodes = document.selectNodes(xPath);

            return nodes.get(0).valueOf("@version");
        }
        catch (DocumentException e)
        {
            throw new IllegalStateException("cannot read the platform version definition", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }
    }

    /**
     * Get the VersionChecks of modules.
     *
     * @return list of checks.
     */
    public static List<VersionCheck> getVersionChecks()
    {
        final List<VersionCheck> versionChecks = new ArrayList<VersionCheck>();
        InputStream in = null;

        try
        {
            in = ClassLoaderUtils.getResourceAsStream(PLATFORM_VERSION_PATH, PlatformVersionSpecReader.class);

            SAXReader reader = new SAXReader();
            Document document = reader.read(in);

            List<Node> nodes = document.selectNodes("//export-version-check");
            for (Node node : nodes)
            {
                String pkg = node.valueOf("@package");
                String version = node.valueOf("@version");
                String modulename = node.valueOf("@modulename");
                versionChecks.add(new ExportVersionCheck(pkg, version, modulename));
            }

            List<Node> bundleCheckNodes = document.selectNodes("//bundle-version-check");
            for (Node node : bundleCheckNodes)
            {
                String bundlename = node.valueOf("@bundlename");
                String version = node.valueOf("@version");
                String modulename = node.valueOf("@modulename");
                versionChecks.add(new BundleVersionCheck(bundlename, version, modulename));
            }
        }
        catch (DocumentException e)
        {
            throw new IllegalStateException("cannot read the platform version definition", e);
        }
        finally
        {
            IOUtils.closeQuietly(in);
        }

        return Collections.unmodifiableList(versionChecks);
    }

    public abstract static class VersionCheck
    {
        protected String version;
        protected String moduleName;

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
        private String bundleName;

        private BundleVersionCheck(String bundleName, String version, String moduleName)
        {
            Validate.notEmpty(bundleName);
            Validate.notEmpty(version);
            Validate.notEmpty(moduleName);

            this.bundleName = bundleName;
            this.version = version;
            this.moduleName = moduleName;
        }

        public String getBundleName()
        {
            return bundleName;
        }
    }

    public static class ExportVersionCheck extends VersionCheck
    {
        private String pkg;

        private ExportVersionCheck(String pkg, String version, String moduleName)
        {
            Validate.notEmpty(pkg);
            Validate.notEmpty(version);
            Validate.notEmpty(moduleName);

            this.pkg = pkg;
            this.version = version;
            this.moduleName = moduleName;
        }

        public String getPkg()
        {
            return pkg;
        }
    }
}
