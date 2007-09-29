/**
 * 
 */
package org.nightlabs.jfire.trade.overview.search;

import org.nightlabs.jfire.base.ui.overview.search.AbstractQuickSearchEntryFactory;
import org.nightlabs.jfire.trade.resource.Messages;

/**
 * @author Daniel Mazurek - daniel <at> nightlabs <dot> de
 *
 */
public abstract class AbstractCustomerNameQuickSearchEntryFactory
extends AbstractQuickSearchEntryFactory  
{
	public String getName() {
		return Messages.getString("org.nightlabs.jfire.trade.overview.search.AbstractCustomerNameQuickSearchEntry.name"); //$NON-NLS-1$
	}
}
