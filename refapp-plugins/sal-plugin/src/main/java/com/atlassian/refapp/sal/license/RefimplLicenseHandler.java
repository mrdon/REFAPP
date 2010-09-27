package com.atlassian.refapp.sal.license;

import com.atlassian.license.LicenseException;
import com.atlassian.license.ng.LicenseManager;
import com.atlassian.sal.api.license.LicenseHandler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple implementation of license handler
 */
public class RefimplLicenseHandler implements LicenseHandler
{
    private static final Log log = LogFactory.getLog(RefimplLicenseHandler.class);

    /**
     * Sets the license, going through the regular validation steps as if you used the web UI
     *
     * @param license The license string
     */
    public void setLicense(String license)
    {
        log.info("Setting license "+license);
        try
        {
            LicenseManager.registerLicense(license);
        }
        catch (LicenseException e)
        {
            throw new IllegalArgumentException(e);
        }

    }
}
