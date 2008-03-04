package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuickSearchQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class AdvancedDeliveryNoteSearchFilterFactory
	extends AbstractQueryFilterFactory<DeliveryNote, DeliveryNoteQuickSearchQuery>
{

	@Override
	public AbstractQueryFilterComposite<DeliveryNote, DeliveryNoteQuickSearchQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<DeliveryNote, ? super DeliveryNoteQuickSearchQuery> queryProvider)
	{
		return new DeliveryNoteFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
