/**
 * 
 */
package org.nightlabs.jfire.trade.ui.overview.deliverynote.search;

import org.nightlabs.jfire.base.ui.overview.search.QuickSearchEntry;
import org.nightlabs.jfire.trade.ui.overview.search.AbstractArticleContainerIDQuickSearchEntryFactory;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryNoteIDQuickSearchEntryFactory
extends AbstractArticleContainerIDQuickSearchEntryFactory
{
	public QuickSearchEntry createQuickSearchEntry() {
		return new DeliveryNoteIDQuickSearchEntry(this);
	}
}
