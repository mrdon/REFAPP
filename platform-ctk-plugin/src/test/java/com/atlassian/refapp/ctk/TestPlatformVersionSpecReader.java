package com.atlassian.refapp.ctk;

import java.util.Iterator;
import java.util.List;

import com.atlassian.refapp.ctk.PlatformVersionSpecReader.BundleVersionCheck;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.ExportVersionCheck;
import com.atlassian.refapp.ctk.PlatformVersionSpecReader.VersionCheck;

import com.google.common.collect.Iterables;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestPlatformVersionSpecReader
{
    @Test
    public void testPlatformVersionXmlParsing() throws Exception
    {
        assertEquals("1.2.3", PlatformVersionSpecReader.getPlatformVersion());
    }

    @Test
    public void testSpecMustBeParsedFromElements() throws Exception
    {
        List<VersionCheck> checks = PlatformVersionSpecReader.getVersionChecks();
        assertEquals(3, checks.size());
    }

    @Test
    public void testExportVersionCheckMustBeParsedCorrectly() throws Exception
    {
        List<VersionCheck> checks = PlatformVersionSpecReader.getVersionChecks();

        assertTrue(checks.get(0) instanceof ExportVersionCheck);
        ExportVersionCheck check0 = (ExportVersionCheck)checks.get(0);
        assertEquals("package1", check0.getPkg());
        assertEquals("5.5.5", check0.getVersion());
        assertEquals("package1name", check0.getModuleName());
        assertFalse(check0.isOptional());
    }

    @Test
    public void testBundleVersionCheckMustBeParsedCorrectly() throws Exception
    {
        Iterable<? extends VersionCheck> checks = PlatformVersionSpecReader.getVersionChecks();

        Iterator<BundleVersionCheck> it = Iterables.filter(checks, BundleVersionCheck.class).iterator();
        
        assertTrue(it.hasNext());
        BundleVersionCheck check1 = it.next();
        assertEquals("bundle1", check1.getBundleName());
        assertEquals("6.6.6", check1.getVersion());
        assertEquals("bundle1name", check1.getModuleName());
        assertFalse(it.hasNext());
    }
    
    VersionCheck checkWithName(String moduleName)
    {
        for (VersionCheck vc : PlatformVersionSpecReader.getVersionChecks())
        {
            if (vc.getModuleName().equals(moduleName))
            {
                return vc;
            }
        }
        
        return null;
    }
    
    @Test
    public void optionalExportVersionChecksMustBeParsed() throws Exception
    {
        VersionCheck vc = checkWithName("Optional Package");
        assertNotNull("Unexpected missing Optional Package module in test versions", vc);

        assertEquals("1.2.4", vc.getVersion());
        
        assertTrue(vc instanceof ExportVersionCheck);
        
        assertTrue(((ExportVersionCheck) vc).isOptional());
    }
}
