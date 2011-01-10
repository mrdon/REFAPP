package com.atlassian.refapp.sal.license;

import com.atlassian.extras.api.AtlassianLicense;
import com.atlassian.extras.api.LicenseException;
import com.atlassian.extras.api.LicenseManager;
import com.atlassian.extras.core.LicenseManagerFactory;
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
        final LicenseManager licenseManager = LicenseManagerFactory.getLicenseManager();
        final AtlassianLicense atlassianLicense;
        try {
            atlassianLicense = licenseManager.getLicense(license);
        } catch (final LicenseException exception) {
            throw new IllegalArgumentException("Invalid license", exception );
        }
        if (atlassianLicense == null) {
            throw new IllegalArgumentException("Missing license");
        }
    }

}
