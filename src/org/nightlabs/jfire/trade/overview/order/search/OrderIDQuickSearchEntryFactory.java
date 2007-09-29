/**
 * 
 */
package org.nightlabs.jfire.trade.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OrderIDQuickSearchEntryFactory 
extends AbstractArticleContainerIDQuickSearchEntryFactory 
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new OrderIDQuickSearchEntry(this);
	}
}
