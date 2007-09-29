/**
 * 
 */
package org.nightlabs.jfire.trade.overview.invoice.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class InvoiceIDQuickSearchEntryFactory 
extends AbstractArticleContainerIDQuickSearchEntryFactory 
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new InvoiceIDQuickSearchEntry(this);
	}
}
