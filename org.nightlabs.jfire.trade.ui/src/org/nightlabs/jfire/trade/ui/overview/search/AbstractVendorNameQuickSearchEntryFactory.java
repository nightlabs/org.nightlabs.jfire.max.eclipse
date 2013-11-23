package org.nightlabs.jfire.trade.ui.overview.search;

import org.nightlabs.base.ui.validation.InputValidator;
import org.nightlabs.jdo.query.AbstractSearchQuery;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.StringIDStringValidator;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public abstract class AbstractVendorNameQuickSearchEntryFactory<Q extends AbstractSearchQuery>
	extends AbstractQuickSearchEntryFactory<Q>
{
	@Override
	public String getName()
	{
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.search.AbstractVendorNameQuickSearchEntry.name"); //$NON-NLS-1$
	}

	@Override
	protected InputValidator<?> createInputValidator()
	{
		return new StringIDStringValidator();
	}
}
