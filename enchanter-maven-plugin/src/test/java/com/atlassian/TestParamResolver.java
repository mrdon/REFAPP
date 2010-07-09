package com.atlassian;

import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestParamResolver extends TestCase
{

    public void testResolveSingleLine()
    {
        String input = "this is my ${variable1}string.";
        Map<String, String> params = new HashMap<String, String>();
        params.put("variable1", "cool");
        ParamResolver resolver = new ParamResolver();
        Assert.assertEquals("this is my coolstring.", resolver.resolve(input, params));
    }

    public void testResolveSingleLineNoVariableMatch()
    {
        String input = "this is my ${variable1}string";
        ParamResolver resolver = new ParamResolver();
        Assert.assertEquals(input, resolver.resolve(input, Collections.<String, String>emptyMap()));
    }

    public void testResolveMultiLineAllResolved()
    {
        String input = "there is a cool man called ${name}.\n ${name} is a software engineer.\n"
                       + "everybody loves ${name} except mr.${opponent}.\n mr.${opponent} is hated by everyone.";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Pi");
        params.put("opponent", "DevilMan");
        ParamResolver resolver = new ParamResolver();

        String expectedOutput = "there is a cool man called Pi.\n Pi is a software engineer.\n"
                                + "everybody loves Pi except mr.DevilMan.\n mr.DevilMan is hated by everyone.";

        Assert.assertEquals(expectedOutput, resolver.resolve(input, params));
    }

    public void testResolveMultiLineSomeResolved()
    {
        String input = "there is a cool man called ${name}.\n ${name2} is a software engineer.\n"
                       + "everybody loves ${name} except mr.${opponent}.\n mr.${opponent2} is hated by everyone.";
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "Pi");
        params.put("opponent", "DevilMan");
        ParamResolver resolver = new ParamResolver();

        String expectedOutput = "there is a cool man called Pi.\n ${name2} is a software engineer.\n"
                                + "everybody loves Pi except mr.DevilMan.\n mr.${opponent2} is hated by everyone.";

        Assert.assertEquals(expectedOutput, resolver.resolve(input, params));
    }

    public void testResolveStartCorner()
    {
        String input = "${key} is cool.";
        Map<String, String> params = new HashMap<String, String>();
        params.put("key", "Pi");
        ParamResolver resolver = new ParamResolver();
        Assert.assertEquals("Pi is cool.", resolver.resolve(input, params));
    }

    public void testResolveEndCorner()
    {
        String input = "Pi is ${rating}";
        Map<String, String> params = new HashMap<String, String>();
        params.put("rating", "cool");
        ParamResolver resolver = new ParamResolver();
        Assert.assertEquals("Pi is cool", resolver.resolve(input, params));
    }
}
