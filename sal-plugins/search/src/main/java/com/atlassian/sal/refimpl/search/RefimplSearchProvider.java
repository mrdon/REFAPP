package com.atlassian.sal.refimpl.search;

import java.util.*;


import org.apache.log4j.Logger;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.search.*;
import com.atlassian.sal.core.search.BasicSearchMatch;
import com.atlassian.sal.core.search.BasicResourceType;

/**
 *
 */
public class RefimplSearchProvider implements com.atlassian.sal.api.search.SearchProvider
{
    private static final Logger log = Logger.getLogger(RefimplSearchProvider.class);
    private ApplicationProperties applicationProperties;

    public RefimplSearchProvider(ApplicationProperties props)
    {
        this.applicationProperties = props;
    }

    public SearchResults search(String username, String searchString)
    {
        final List<SearchMatch> matches = new ArrayList<SearchMatch>();
        matches.add(new BasicSearchMatch("http://foo.com", "My Foo", "This is the excerpt", new BasicResourceType(applicationProperties, "someType")));
        matches.add(new BasicSearchMatch("http://bar.com", "My Bar", "This is the bar excerpt", new BasicResourceType(applicationProperties, "someType2")));
        return new SearchResults(matches, 2, 666);
    }
}
