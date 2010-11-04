package com.atlassian.refapp.ctk.version;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.plugin.util.ClassLoaderUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.Validate;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.twdata.pkgscanner.DefaultOsgiVersionConverter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.fail;

public class PlatformVersionTest extends SpringAwareTestCase
{
    private static final String PLATFORM_VERSION_PATH = "com/atlassian/refapp/ctk/version/platformversions.xml";

    private PackageAdmin packageAdmin;

    public void setPackageAdmin(PackageAdmin packageAdmin)
    {
        this.packageAdmin = packageAdmin;
    }

    @Test
    public void testAtlassianPlatformModulesSuppliedAtCorrectVersions()
    {
        final String platformVersion = getPlatformVersion();
        List<VersionCheck> versionChecks = getVersionChecks();

        // this keeps all the errors found.
        StringBuilder sb = new StringBuilder();

        for (VersionCheck check : versionChecks)
        {
            if (check instanceof ExportVersionCheck)
            {
                ExportVersionCheck exportCheck = (ExportVersionCheck) check;
                final ExportedPackage export = packageAdmin.getExportedPackage(exportCheck.getPkg());
                if (export == null || !export.getVersion().toString().equals(getOsgiVersion(exportCheck.getVersion())))
                {
                    sb.append("Atlassian Platform ");
                    sb.append(platformVersion);
                    sb.append(" must have ");
                    sb.append(exportCheck.getModuleName());
                    sb.append("  version:");
                    sb.append(exportCheck.getVersion());

                    if (export != null)
                    {
                        sb.append("  current version:");
                        sb.append(export.getVersion());
                    }
                    sb.append("\n");
                }
            }
            else if (check instanceof BundleVersionCheck)
            {
                BundleVersionCheck bundleCheck = (BundleVersionCheck) check;
                Bundle[] bundles = packageAdmin.getBundles(bundleCheck.getBundleName(), null);

                boolean found = false;

                if (bundles != null)
                {
                    // the version we expect must be found.
                    for(Bundle bundle:bundles)
                    {
                        if (bundle.getVersion().toString().equals(getOsgiVersion(bundleCheck.getVersion())))
                        {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found)
                {
                    sb.append("Atlassian Platform ");
                    sb.append(platformVersion);
                    sb.append(" must have ");
                    sb.append(bundleCheck.getModuleName());
                    sb.append("  version:");
                    sb.append(bundleCheck.getVersion());
                    sb.append("\n");
                }
            }
        }

        if (sb.length() > 0)
        {
            fail(sb.toString());
        }
    }

    /**
     * Converts maven versions to OSGi format.
     */
    private static String getOsgiVersion(String version)
    {
        DefaultOsgiVersionConverter converter = new DefaultOsgiVersionConverter();
        return converter.getVersion(version);
    }

    private String getPlatformVersion()
    {
        InputStream in = null;

        try
        {
            in = ClassLoaderUtils.getResourceAsStream(PLATFORM_VERSION_PATH, PlatformVersionTest.class);

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

    private List<VersionCheck> getVersionChecks()
    {
        final List<VersionCheck> versionChecks = new ArrayList<VersionCheck>();
        InputStream in = null;

        try
        {
            in = ClassLoaderUtils.getResourceAsStream(PLATFORM_VERSION_PATH, PlatformVersionTest.class);

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

    private abstract static class VersionCheck
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

    private static class BundleVersionCheck extends VersionCheck
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

    private static class ExportVersionCheck extends VersionCheck
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
