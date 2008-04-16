package org.nightlabs.jfire.trade.ui.overview.search;

import org.nightlabs.jdo.query.AbstractSearchQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public abstract class AbstractCreatorNameQuickSearchEntryFactory<Q extends AbstractSearchQuery>
	extends AbstractQuickSearchEntryFactory<Q>
{
	@Override
	public String getName()
	{
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.search.AbstractCreatorNameQuickSearchEntryFactory.name"); //$NON-NLS-1$
	}
}
