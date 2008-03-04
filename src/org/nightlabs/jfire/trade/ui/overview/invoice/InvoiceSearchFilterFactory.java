package org.nightlabs.jfire.trade.ui.overview.invoice;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.query.InvoiceQuickSearchQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceSearchFilterFactory
	extends AbstractQueryFilterFactory<Invoice, InvoiceQuickSearchQuery>
{

	@Override
	public AbstractQueryFilterComposite<Invoice, InvoiceQuickSearchQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<Invoice, ? super InvoiceQuickSearchQuery> queryProvider)
	{
		return new InvoiceFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
