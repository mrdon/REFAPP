package com.atlassian.refapp.ctk.version;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import static org.mockito.Mockito.when;

public class TestPlatformVersionTest
{
    @Test
    public void testAllVersionsMatchShouldPass()
    {
        executeVersionTest("package1", "5.5.5", "bundle1", "6.6.6");
    }

    @Test(expected = AssertionError.class)
    public void testOlderBundleVersionShouldFail()
    {
        executeVersionTest("package1", "5.5.5", "bundle1", "6.6.5");
    }

    @Test
    public void testNewerBundleVersionShouldPass()
    {
        executeVersionTest("package1", "5.5.5", "bundle1", "7.1.1");
    }

    @Test(expected = AssertionError.class)
    public void testOlderExportVersionShouldFail()
    {
        executeVersionTest("package1", "5.5.1", "bundle1", "6.6.6");
    }

    @Test
    public void testNewerExportVersionShouldPass()
    {
        executeVersionTest("package1", "5.5.8", "bundle1", "6.6.6");
    }

    @Test
    public void testNewerAlphaExportVersionShouldPass()
    {
        executeVersionTest("package1", "5.5.8.alpha1", "bundle1", "6.6.6");
    }

    @Test(expected = AssertionError.class)
    public void testOlderAlphaExportVersionShouldFail()
    {
        executeVersionTest("package1", "5.5.5.alpha1", "bundle1", "6.6.6");
    }

    private void executeVersionTest(String packageName, String packageVersion, String bundleName, String bundleVersion)
    {
        // mocks.
        ExportedPackage mockedExportPackage = getMockedExportedPackage(packageName, packageVersion);
        Bundle mockedBundle = getMockedBundle(bundleName, bundleVersion);

        PackageAdmin mockedPackageAdmin = Mockito.mock(PackageAdmin.class);

        when(mockedPackageAdmin.getExportedPackage(packageName)).thenReturn(mockedExportPackage);
        when(mockedPackageAdmin.getBundles(bundleName, null)).thenReturn(new Bundle[] { mockedBundle });

        // actual test execution.
        PlatformVersionTest test = new PlatformVersionTest();
        test.setPackageAdmin(mockedPackageAdmin);
        test.testAtlassianPlatformModulesSuppliedAtCorrectVersions();
    }

    private ExportedPackage getMockedExportedPackage(String name, String version)
    {
        ExportedPackage mockedExportedPackage = Mockito.mock(ExportedPackage.class);
        when(mockedExportedPackage.getName()).thenReturn(name);
        when(mockedExportedPackage.getVersion()).thenReturn(Version.parseVersion(version));

        return mockedExportedPackage;
    }

    private Bundle getMockedBundle(String name, String version)
    {
        Bundle mockedBundle = Mockito.mock(Bundle.class);
        when(mockedBundle.getSymbolicName()).thenReturn(name);
        when(mockedBundle.getVersion()).thenReturn(Version.parseVersion(version));

        return mockedBundle;
    }
}