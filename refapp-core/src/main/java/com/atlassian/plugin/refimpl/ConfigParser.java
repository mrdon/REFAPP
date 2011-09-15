package com.atlassian.plugin.refimpl;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;


/**
 * Helps with configuration parsing.
 *
 * @since 2.7.0
 */
public class ConfigParser
{
    /**
     * Not for instantiation.
     */
    private ConfigParser() {}

    private static final Logger LOG = Logger.getLogger(ConfigParser.class);

    /**
     * Parses the configuration string in the form of "com.abc.def=1.2.3, org.xyz=4.5.6"
     *
     * @param input the configuration string to parse, cannot be null.
     * @return (key, value) map parsed from the given input string.
     */
    public static Map<String, String> parseMap(final String input)
    {
        final Map<String, String> output = new HashMap<String, String>();

        final String[] items = input.split("[,]");
        for(String item:items)
        {
            String[] parts = item.split("[=]");
            if (parts.length == 2)
            {
                output.put(parts[0].trim(), parts[1].trim());
            }
            else
            {
                LOG.warn("Ignored unparsable item in configuration:" + item);
            }
        }

        return ImmutableMap.copyOf(output);
    }
}
