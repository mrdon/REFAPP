package com.atlassian.plugin.refimpl.servlet;

import com.atlassian.plugin.refimpl.ConfigParser;
import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;

import java.util.Collections;

public class ConfigParserTest extends TestCase
{
    public void testParseMap()
    {
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3", "org.xyz", "2.3.4-alpha1"), ConfigParser.parseMap("com.abc.def=1.2.3,org.xyz=2.3.4-alpha1"));
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3", "org.xyz", "2.3.4-alpha1"), ConfigParser.parseMap("com.abc.def = 1.2.3,org.xyz=2.3.4-alpha1"));
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3", "org.xyz", "2.3.4-alpha1"), ConfigParser.parseMap("com.abc.def = 1.2.3, org.xyz=2.3.4-alpha1"));
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3", "org. xyz", "2.3.4-alpha1"), ConfigParser.parseMap("com.abc.def = 1.2.3, org. xyz=2.3.4-alpha1"));

        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3"), ConfigParser.parseMap("com.abc.def = 1.2.3"));
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3"), ConfigParser.parseMap("com.abc.def = 1.2.3,"));
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3"), ConfigParser.parseMap(",com.abc.def = 1.2.3"));
        assertEquals(ImmutableMap.of("com.abc.def", "1.2.3"), ConfigParser.parseMap("com.abc.def = 1.2.3, wahaha"));

        assertEquals(Collections.emptyMap(), ConfigParser.parseMap(""));
        assertEquals(Collections.emptyMap(), ConfigParser.parseMap("(blah)"));
    }
}
