package org.nightlabs.jfire.trade.ui.overview.invoice;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.query.InvoiceQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceSearchFilterFactory
	extends AbstractQueryFilterFactory<InvoiceQuery>
{

	@Override
	public AbstractQueryFilterComposite<InvoiceQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super InvoiceQuery> queryProvider)
	{
		return new InvoiceFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
