/**
 * 
 */
package org.nightlabs.jfire.trade.overview.offer.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.overview.search.AbstractVendorNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class OfferVendorNameQuickSearchEntryFactory 
extends AbstractVendorNameQuickSearchEntryFactory 
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new OfferVendorNameQuickSearchEntry(this);
	}
}
