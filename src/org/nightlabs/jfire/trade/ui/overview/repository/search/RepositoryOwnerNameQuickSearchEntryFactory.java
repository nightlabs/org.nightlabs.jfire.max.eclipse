/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class RepositoryOwnerNameQuickSearchEntryFactory 
extends AbstractQuickSearchEntryFactory 
{
	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.search.RepositoryOwnerNameQuickSearchEntry.name"); //$NON-NLS-1$
	}
	
	public QuickSearchEntry createQuickSearchEntry() {
		return new RepositoryOwnerNameQuickSearchEntry(this);
	}
}
