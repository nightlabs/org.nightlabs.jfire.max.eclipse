/**
 * 
 */
package org.nightlabs.jfire.trade.overview.account.search;

import org.nightlabs.jfire.accounting.query.AccountQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntry;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntryFactory;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public class AccountOwnerNameQuickSearchEntry 
extends AbstractQuickSearchEntry 
{
	public AccountOwnerNameQuickSearchEntry(QuickSearchEntryFactory factory) {
		super(factory);
	}

	public Object search(ProgressMonitor monitor) {
		AccountQuery query = new AccountQuery();
		query.setOwnerName(getSearchText());
		query.setFromInclude(getMinIncludeRange());		
		query.setToExclude(getMaxExcludeRange());		
		return query;
	}
}
