/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractCustomerNameQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceCustomerNameQuickSearchEntryFactory
extends AbstractCustomerNameQuickSearchEntryFactory
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new InvoiceCustomerNameQuickSearchEntry(this);
	}
}
