package com.atlassian.refapp.ctk.version;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.VersionCheck;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.ExportVersionCheck;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.BundleVersionCheck;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;
import org.twdata.pkgscanner.DefaultOsgiVersionConverter;

import java.util.List;

import static org.junit.Assert.fail;

public class PlatformVersionTest extends SpringAwareTestCase
{
    private PackageAdmin packageAdmin;

    public void setPackageAdmin(PackageAdmin packageAdmin)
    {
        this.packageAdmin = packageAdmin;
    }

    @Test
    public void testAtlassianPlatformModulesSuppliedAtCorrectVersions()
    {
        final String platformVersion = PlatformVersionSpecReader.getPlatformVersion();

        List<VersionCheck> versionChecks = PlatformVersionSpecReader.getVersionChecks();

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
}
