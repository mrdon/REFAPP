package com.atlassian.refapp.ctk;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang.StringUtils;

import java.util.Set;

/**
 * Default implementation of AppSpecificInfoProvider which allows values to be set through system properties.
 */
public class DefaultAppSpecificInfoProvider implements AppSpecificInfoProvider
{
    public String getAdminUsername()
    {
        return getRequiredProperty("platform.ctk.test.admin.username");
    }

    public String getAdminPassword()
    {
        return getRequiredProperty("platform.ctk.test.admin.password");
    }

    public String getAdminFullname()
    {
        return getRequiredProperty("platform.ctk.test.admin.fullname");
    }

    public String getMatchingSearchTerm()
    {
        return System.getProperty("platform.ctk.test.search.term");
    }

    /**
     * Matching strings can be supplied as comma separated values.
     */
    public Set<String> getExpectedMatchingContents()
    {
        String matches = System.getProperty("platform.ctk.test.search.matches");
        if (StringUtils.isNotBlank(matches))
        {
            return ImmutableSet.of(StringUtils.split(matches, ','));
        }

        return null;
    }

    public String getValidLicense()
    {
        return getRequiredProperty("platform.ctk.test.validlicense");
    }

    private String getRequiredProperty(String key)
    {
        final String value = System.getProperty(key);

        if (value == null)
        {
            throw new IllegalStateException("System property [" + key + "] must be supplied for platform Ctk execution.");
        }

        return value;
    }
}