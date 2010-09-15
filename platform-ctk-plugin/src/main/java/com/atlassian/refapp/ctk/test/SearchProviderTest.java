package com.atlassian.refapp.ctk.test;

import com.atlassian.functest.junit.SpringAwareTestCase;

import com.atlassian.sal.api.search.SearchProvider;
import com.atlassian.sal.api.search.SearchResults;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class SearchProviderTest extends SpringAwareTestCase
{
    private SearchProvider searchProvider;

    public void setSearchProvider(SearchProvider searchProvider)
    {
        this.searchProvider = searchProvider;
    }

    @Test
    public void testInjection()
    {
        assertTrue("SearchProvider should be injectable", searchProvider != null);
    }

    @Test
    public void testSearch()
    {
        final SearchResults sresults = searchProvider.search(null, "the");

        assertTrue("Should always return results", sresults != null);
        assertTrue("Search time should be greater than zero", sresults.getSearchTime() > 0);
    }
}