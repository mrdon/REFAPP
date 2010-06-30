package com.atlassian.sal.refimpl.search.query;

import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;


/**
 * Factory for creating SearchQueries
 */
public class DefaultSearchQueryParser implements SearchQueryParser
{
	public SearchQuery parse(String query)
	{
		return new DefaultSearchQuery(query);
	}
}
