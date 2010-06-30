package com.atlassian.sal.refimpl.search.query;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atlassian.sal.api.search.parameter.SearchParameter;
import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.refimpl.search.parameter.BasicSearchParameter;

/**
 * Copied from sal-core for now so we can remove the dependency on Apache HTTP client.
 */
public class DefaultSearchQuery implements SearchQuery
{
    private StringBuffer searchString = new StringBuffer();
    private Map<String, SearchParameter> parameters = new LinkedHashMap<String, SearchParameter>();

    public DefaultSearchQuery(String query)
	{
        append(query);
	}

   
    public SearchQuery setParameter(String name, String value)
    {
    	parameters.put(name, new BasicSearchParameter(name, value));
        return this;
    }

	public String getParameter(String name)
	{
		final SearchParameter value = parameters.get(name);
		
		return value==null?null:value.getValue();
	}

	public String buildQueryString()
	{
		StringBuilder builder = new StringBuilder(searchString);
		for (SearchParameter parameter : parameters.values())
		{
			builder.append('&');
			builder.append(parameter.buildQueryString());
		}
		return builder.toString();
	}

	public SearchQuery append(String query)
	{
        if (StringUtils.isEmpty(query))
        {
            throw new IllegalArgumentException("Cannot parse empty query string!");
        }
        if (query.indexOf(SearchQuery.PARAMETER_SEPARATOR) == -1)
        {
            //looks like there's no params.
            searchString.append(query);
            return this;
        }
        
        final String[] strings = query.split(SearchQuery.PARAMETER_SEPARATOR);
        searchString.append(strings[0]);
        for (int i = 1; i < strings.length; i++)
        {
            String string = strings[i];
            BasicSearchParameter searchParam = new BasicSearchParameter(string);
            parameters.put(searchParam.getName(), searchParam);
        }
		return this;
	}


	public String getSearchString()
	{
		try
		{
			return URLDecoder.decode(searchString.toString(), "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("You're JVM doesn't appear to support UTF-8", e);
		}
	}


	public int getParameter(String name, int defaultValue)
	{
		final String value = getParameter(name);
		try
		{
			return Integer.parseInt(value);
		} catch (NumberFormatException e)
		{
			// ignore
		}
		return defaultValue;
	}
	
}
