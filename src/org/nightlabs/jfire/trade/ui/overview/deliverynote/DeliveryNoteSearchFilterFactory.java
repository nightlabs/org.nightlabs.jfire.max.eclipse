package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.query.DeliveryNoteQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class DeliveryNoteSearchFilterFactory
	extends AbstractQueryFilterFactory<DeliveryNoteQuery>
{

	@Override
	public AbstractQueryFilterComposite<DeliveryNoteQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super DeliveryNoteQuery> queryProvider)
	{
		return new DeliveryNoteFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}
	
}
