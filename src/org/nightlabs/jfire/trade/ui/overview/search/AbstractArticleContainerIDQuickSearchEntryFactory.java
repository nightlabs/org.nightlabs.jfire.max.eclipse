package org.nightlabs.jfire.trade.ui.overview.search;

import org.nightlabs.base.ui.validation.InputValidator;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.base.ui.overview.search.StringIDStringValidator;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuery;
import org.nightlabs.jfire.trade.ui.resource.Messages;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public abstract class AbstractArticleContainerIDQuickSearchEntryFactory<Q extends AbstractArticleContainerQuery>
	extends AbstractQuickSearchEntryFactory<Q>
{
	@Override
	public String getName()
	{
		return Messages.getString("org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntry.name"); //$NON-NLS-1$
	}

	@Override
	protected InputValidator<?> createInputValidator()
	{
		return new StringIDStringValidator();
	}
}
