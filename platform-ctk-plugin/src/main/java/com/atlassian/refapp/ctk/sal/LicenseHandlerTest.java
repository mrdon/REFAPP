package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;
import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.sal.api.license.LicenseHandler;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class LicenseHandlerTest extends SpringAwareTestCase
{
    static final String INVALID_LICENSE = StringUtils.repeat("Helloworld ", 50);

    private AppSpecificInfoProvider infoProvider;
    private LicenseHandler handler;

    public void setHandler(LicenseHandler handler)
    {
        this.handler = handler;
    }

    public void setInfoProvider(AppSpecificInfoProvider infoProvider)
    {
        this.infoProvider = infoProvider;
    }

    @Test
    public void testLicenseHandlerAvailable()
    {
        assertNotNull("License handler must be available to plugins", handler);
    }

    @Test
    public void testValidLicenseShouldNotThrowException()
    {
        // this should not throw any exception.
        handler.setLicense(infoProvider.getValidLicense());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLicenseShouldthrowIAE()
    {
        // Should throw IllegalArgumentException for invalid license.
        handler.setLicense(INVALID_LICENSE);
    }

    @Test
    public void testGetServerIdReturnsValidID()
    {
        String acceptedFormat = "[A-Z0-9]{4}(-[A-Z0-9]{4}){3}";
        String serverId = handler.getServerId();
        assertTrue("server id [" + serverId + "] does not conform to the format [" + acceptedFormat + "]", serverId.matches(acceptedFormat));
    }
}