package com.atlassian.refapp.ctk.exports;

import com.atlassian.functest.junit.SpringAwareTestCase;
import org.junit.Test;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Tests that atlassian events is exported in the system bundle, since it is optional we should know that it is the
 * correct version specified in the framework.
 */
public class EventImportTest extends SpringAwareTestCase
{

    private PackageAdmin packageAdmin;

    public void setPackageAdmin(PackageAdmin packageAdmin)
    {
        this.packageAdmin = packageAdmin;
    }

    @Test
    public void testAtlassianEventExportedAtCorrectVersion()
    {
        final ExportedPackage eventPackage = packageAdmin.getExportedPackage("com.atlassian.event");
        assertNotNull("We are hoping that atlassian event is exported from the system bundle", eventPackage);
        assertEquals("We are hoping that atlassian event is exported from the system bundle", eventPackage.getExportingBundle().getBundleId(), 0);
    }

}