/**
 * 
 */
package org.nightlabs.jfire.trade.overview.repository.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class RepositoryNameQuickSearchEntryFactory 
extends AbstractQuickSearchEntryFactory 
{
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.overview.repository.search.RepositoryNameQuickSearchEntry.name"); //$NON-NLS-1$
	}

	public QuickSearchEntry createQuickSearchEntry() {
		return new RepositoryNameQuickSearchEntry(this);
	}
}
