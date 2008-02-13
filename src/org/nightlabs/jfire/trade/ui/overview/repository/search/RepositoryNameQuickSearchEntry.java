/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.store.query.RepositoryQuery;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class RepositoryNameQuickSearchEntry
extends AbstractQuickSearchEntry
{
	public RepositoryNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	public Object search(ProgressMonitor monitor) {
		RepositoryQuery query = new RepositoryQuery();
		query.setName(getSearchText());
		query.setFromInclude(getMinIncludeRange());
		query.setToExclude(getMaxExcludeRange());
		return query;
	}
}
