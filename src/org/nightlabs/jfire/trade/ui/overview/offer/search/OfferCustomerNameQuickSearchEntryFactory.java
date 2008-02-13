/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new OfferCustomerNameQuickSearchEntry(this);
	}
}
