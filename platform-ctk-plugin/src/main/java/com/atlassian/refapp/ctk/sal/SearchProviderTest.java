package com.atlassian.refapp.ctk.sal;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.refapp.ctk.AppSpecificInfoProvider;
import com.atlassian.refapp.ctk.AppSpecificInfoProviderFactory;
import com.atlassian.sal.api.search.SearchMatch;
import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SearchProviderTest extends SpringAwareTestCase
{
    private SearchProvider searchProvider;
    private AppSpecificInfoProvider infoProvider = AppSpecificInfoProviderFactory.create();

    public void setSearchProvider(SearchProvider searchProvider)
    {
        this.searchProvider = searchProvider;
    }

    @Test
    public void testSearchProviderAvailable()
    {
        assertNotNull("SearchProvider must be available to plugins", searchProvider);
    }

    @Test
    public void testSearchButNoMatch()
    {
        final SearchResults sresults = searchProvider.search(null, "superweirdsearchtermwahaha");

        assertNotNull("Should never return null", sresults);
        assertTrue("Search time should be greater than zero", sresults.getSearchTime() > 0);
    }

    @Test
    public void testSearchWithMatch()
    {
        final SearchResults sresults = searchProvider.search(infoProvider.getAdminUsername(),
                                                             infoProvider.getMatchingSearchTerm());

        assertNotNull("Should never return null", sresults);
        assertTrue("We expect matches in search", sresults.getMatches().size() > 0);

        for(String content:infoProvider.getExpectedMatchingContents())
        {
            boolean found = false;
            for(SearchMatch match:sresults.getMatches())
            {
                // if any of these match, then we're good.
                if (match.getTitle().contains(content) ||
                    match.getUrl().contains(content) ||
                    match.getExcerpt().contains(content))
                {
                    found = true;
                    break;
                }
            }
            assertTrue("the expected content should exist in the search results:" + content, found);
        }
    }
}
