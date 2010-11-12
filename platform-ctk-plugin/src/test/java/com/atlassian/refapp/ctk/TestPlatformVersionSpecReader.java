package com.atlassian.refapp.ctk;

import junit.framework.TestCase;

import java.util.List;

import com.atlassian.refapp.ctk.PlatformVersionSpecReader.VersionCheck;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.ExportVersionCheck;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.BundleVersionCheck;

public class TestPlatformVersionSpecReader extends TestCase
{
    public void testPlatformVersionXmlParsing()
    {
        assertEquals("1.2.3", PlatformVersionSpecReader.getPlatformVersion());
    }

    public void testSpecMustBeParsedFromElements()
    {
        List<VersionCheck> checks = PlatformVersionSpecReader.getVersionChecks();
        assertEquals(2, checks.size());
    }

    public void testExportVersionCheckMustBeParsedCorrectly()
    {
        List<VersionCheck> checks = PlatformVersionSpecReader.getVersionChecks();

        assertTrue(checks.get(0) instanceof ExportVersionCheck);
        ExportVersionCheck check0 = (ExportVersionCheck)checks.get(0);
        assertEquals("package1", check0.getPkg());
        assertEquals("5.5", check0.getVersion());
        assertEquals("package1name", check0.getModuleName());
    }

    public void testBundleVersionCheckMustBeParsedCorrectly()
    {
        List<VersionCheck> checks = PlatformVersionSpecReader.getVersionChecks();

        assertTrue(checks.get(1) instanceof BundleVersionCheck);
        BundleVersionCheck check1 = (BundleVersionCheck)checks.get(1);
        assertEquals("bundle1", check1.getBundleName());
        assertEquals("6.6", check1.getVersion());
        assertEquals("bundle1name", check1.getModuleName());
    }
}
