package com.atlassian.refapp.ctk;

import org.apache.commons.lang.StringUtils;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helps determine which OSGi version is newer.
 *
 * This is based on com.atlassian.plugin.util.VersionStringComparator r109520 with some modified rules.
 */
public class VersionStringComparator
{
    private static final VersionStringComparator VERSION_COMPARATOR = new VersionStringComparator();
    private static final Pattern MODIFIER_PATTERN = Pattern.compile("([a-zA-Z]+)([0-9]*)");

    /**
     * Compares OSGi versions by:-
     *
     * - each component is compared numerically.
     * - modifier gets split into alpha part and numeric part. The alpha part gets compared by string comparison while the numeric part gets compared numerically.
     *
     */
    private int compare(final String version1, final String version2)
    {
        // Get the version numbers, remove all whitespaces
        String thisVersion = "0";
        if (StringUtils.isNotEmpty(version1))
        {
            thisVersion = version1.replaceAll(" ", "");
        }
        String compareVersion = "0";
        if (StringUtils.isNotEmpty(version2))
        {
            compareVersion = version2.replaceAll(" ", "");
        }

        // Split the version numbers
        final String[] v1 = StringUtils.split(thisVersion, '.');
        final String[] v2 = StringUtils.split(compareVersion, '.');

        final Comparator<String> componentComparator = new VersionStringComponentComparator();

        // Compare each place, until we find a difference and then return. If empty, assume zero.
        for (int i = 0; i < (v1.length > v2.length ? v1.length : v2.length); i++)
        {
            final String component1 = i >= v1.length ? "0" : v1[i];
            final String component2 = i >= v2.length ? "0" : v2[i];

            if (componentComparator.compare(component1, component2) != 0)
            {
                return componentComparator.compare(component1, component2);
            }
        }

        return 0;
    }

    private class VersionStringComponentComparator implements Comparator<String>
    {
        public static final int FIRST_GREATER = 1;
        public static final int SECOND_GREATER = -1;

        public int compare(final String component1, final String component2)
        {
            if (component1.equalsIgnoreCase(component2))
            {
                return 0;
            }

            if (StringUtils.isNumeric(component1) && StringUtils.isNumeric(component2))
            {
                // both numbers -- parse and compare
                if (Integer.parseInt(component1) > Integer.parseInt(component2))
                {
                    return FIRST_GREATER;
                }
                if (Integer.parseInt(component2) > Integer.parseInt(component1))
                {
                    return SECOND_GREATER;
                }
                return 0;
            }

            // 2.3.1.alpha1 < 2.3.1
            if (StringUtils.isNumeric(component1))
            {
                return FIRST_GREATER;
            }
            if (StringUtils.isNumeric(component2))
            {
                return SECOND_GREATER;
            }

            // 2.3.1.a9 < 2.3.1.a11
            Matcher matcherComponent1 = MODIFIER_PATTERN.matcher(component1);
            Matcher matcherComponent2 = MODIFIER_PATTERN.matcher(component2);

            if (!matcherComponent1.matches())
            {
                throw new IllegalArgumentException("modifier not in supported format:" + matcherComponent1);
            }

            if (!matcherComponent2.matches())
            {
                throw new IllegalArgumentException("modifier not in supported format:" + matcherComponent2);
            }

            int alphaCompareResult = matcherComponent1.group(1).compareToIgnoreCase(matcherComponent2.group(1));
            if (alphaCompareResult != 0)
            {
                return alphaCompareResult;
            }

            int numeric1 = StringUtils.isEmpty(matcherComponent1.group(2)) ? 0: Integer.parseInt(matcherComponent1.group(2));
            int numeric2 = StringUtils.isEmpty(matcherComponent2.group(2)) ? 0: Integer.parseInt(matcherComponent2.group(2));

            return new Integer(numeric1).compareTo(numeric2);
        }
    }

    /**
     * Returns true if the actualVersion is same or newer than the specVersion.
     * The versions here are assumed to be in OSGi format.
     */
    public static boolean isSameOrNewerVersion(String specVersion, String actualVersion)
    {
        return VERSION_COMPARATOR.compare(specVersion, actualVersion) <= 0;
    }
}