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
public class RepositoryNameQuickSearchEntryFactory 
extends AbstractQuickSearchEntryFactory 
{
	@Override
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.repository.search.RepositoryNameQuickSearchEntry.name"); //$NON-NLS-1$
	}

	public QuickSearchEntry createQuickSearchEntry() {
		return new RepositoryNameQuickSearchEntry(this);
	}
}
