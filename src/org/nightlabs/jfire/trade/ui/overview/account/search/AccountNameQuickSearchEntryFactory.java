/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.account.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class AccountNameQuickSearchEntryFactory 
extends AbstractQuickSearchEntryFactory 
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new AccountNameQuickSearchEntry(this);
	}

	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.account.search.AccountNameQuickSearchEntry.name"); //$NON-NLS-1$
	}
}
