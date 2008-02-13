/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OrderCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new OrderCustomerNameQuickSearchEntry(this);
	}
}
