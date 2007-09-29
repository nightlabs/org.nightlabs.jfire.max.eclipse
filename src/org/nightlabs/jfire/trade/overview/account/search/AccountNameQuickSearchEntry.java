/**
 * 
 */
package org.nightlabs.jfire.trade.overview.account.search;

import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.jfire.trade.resource.Messages;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class AccountNameQuickSearchEntry 
extends AbstractQuickSearchEntry 
{
	public AccountNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	public Object search(ProgressMonitor monitor) 
	{
		AccountQuery query = new AccountQuery();
		query.setName(getSearchText());
		query.setFromInclude(getMinIncludeRange());		
		query.setToExclude(getMaxExcludeRange());		
		return query;
	}
}
