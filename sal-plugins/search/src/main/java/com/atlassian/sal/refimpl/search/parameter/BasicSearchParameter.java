package com.atlassian.sal.refimpl.search.parameter;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;

import com.atlassian.sal.api.search.parameter.SearchParameter;

/**
 * Basic name value pair search parameter.
 * 
 * Copied from sal-core so we can remove the dep on Apache HTTP Client
 */
public class BasicSearchParameter implements SearchParameter
{
    private String name;
    private String value;

    public BasicSearchParameter(String queryString)
    {
        initFromQueryString(queryString);
    }

    public BasicSearchParameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public String buildQueryString()
    {
        try
        {
            return URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
        }
    	catch (UnsupportedEncodingException e)
    	{
    		throw new RuntimeException("You're JVM doesn't support UTF-8", e);
		}
    }

    private void initFromQueryString(String queryString)
    {
        if (StringUtils.isEmpty(queryString) || queryString.indexOf("=") == -1)
        {
            throw new IllegalArgumentException("QueryString '" + queryString + "' does not appear to be a valid query string");
        }

        final String[] strings;
        try
        {
            strings = URLDecoder.decode(queryString, "UTF-8").split("=");
        }
    	catch (UnsupportedEncodingException e)
    	{
    		throw new RuntimeException("You're JVM doesn't support UTF-8", e);
		}
        this.name = strings[0];
        this.value = strings[1];
    }

    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        BasicSearchParameter that = (BasicSearchParameter) o;

        if (!name.equals(that.name))
        {
            return false;
        }
        if (!value.equals(that.value))
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        int result;
        result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
