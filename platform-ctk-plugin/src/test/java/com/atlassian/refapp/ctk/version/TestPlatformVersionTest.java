package com.atlassian.refapp.ctk.version;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnit44Runner;
import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnit44Runner.class)
public class TestPlatformVersionTest
{
    @Mock PackageAdmin mockedPackageAdmin;
    
    @Test
    public void testAllVersionsMatchShouldPass()
    {
        mockExportedPackage("package1", "5.5.5");
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }

    @Test(expected = AssertionError.class)
    public void shouldFailWhenNothingPresent()
    {
        executeVersionTest();
    }
    
    @Test(expected = AssertionError.class)
    public void shouldFailWhenRequiredExportMissing()
    {
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }
    
    @Test(expected = AssertionError.class)
    public void shouldFailWhenRequiredBundleMissing()
    {
        mockExportedPackage("package1", "5.5.5");
        executeVersionTest();
    }
    
    @Test(expected = AssertionError.class)
    public void testOlderBundleVersionShouldFail()
    {
        mockExportedPackage("package1", "5.5.5");
        mockBundle("bundle1", "6.6.5");
        executeVersionTest();
    }

    @Test
    public void testNewerBundleVersionShouldPass()
    {
        mockExportedPackage("package1", "5.5.5");
        mockBundle("bundle1", "7.1.1");
        executeVersionTest();
    }

    @Test(expected = AssertionError.class)
    public void testOlderExportVersionShouldFail()
    {
        mockExportedPackage("package1", "5.5.1");
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }

    @Test
    public void testNewerExportVersionShouldPass()
    {
        mockExportedPackage("package1", "5.5.8");
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }

    @Test
    public void testNewerAlphaExportVersionShouldPass()
    {
        mockExportedPackage("package1", "5.5.8.alpha1");
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }

    @Test(expected = AssertionError.class)
    public void testOlderAlphaExportVersionShouldFail()
    {
        mockExportedPackage("package1", "5.5.5.alpha1");
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }
    
    @Test
    public void anOptionalPackageIsOptional()
    {
        mockExportedPackage("package1", "5.5.5");
        mockBundle("bundle1", "6.6.6");
        executeVersionTest();
    }

    @Test(expected = AssertionError.class)
    public void anOptionalPackageIsSubjectToVersionChecks()
    {
        mockExportedPackage("package1", "5.5.5");
        mockBundle("bundle1", "6.6.6");
        mockExportedPackage("optionalpackage", "1.0.0");
        executeVersionTest();
    }
    
    private void executeVersionTest()
    {
        // actual test execution.
        PlatformVersionTest test = new PlatformVersionTest();
        test.setPackageAdmin(mockedPackageAdmin);
        test.testAtlassianPlatformModulesSuppliedAtCorrectVersions();
    }

    private void mockExportedPackage(String name, String version)
    {
        ExportedPackage mockedExportedPackage = Mockito.mock(ExportedPackage.class);
        when(mockedExportedPackage.getName()).thenReturn(name);
        when(mockedExportedPackage.getVersion()).thenReturn(Version.parseVersion(version));
        when(mockedPackageAdmin.getExportedPackage(name)).thenReturn(mockedExportedPackage);
    }

    private void mockBundle(String name, String version)
    {
        Bundle mockedBundle = Mockito.mock(Bundle.class);
        when(mockedBundle.getSymbolicName()).thenReturn(name);
        when(mockedBundle.getVersion()).thenReturn(Version.parseVersion(version));

        when(mockedPackageAdmin.getBundles(name, null)).thenReturn(new Bundle[] { mockedBundle });
    }
}
