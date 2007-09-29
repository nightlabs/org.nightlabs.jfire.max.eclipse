/**
 * 
 */
package org.nightlabs.jfire.trade.overview.order.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OrderVendorNameQuickSearchEntryFactory 
extends AbstractVendorNameQuickSearchEntryFactory 
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new OrderVendorNameQuickSearchEntry(this);
	}
}
