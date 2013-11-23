package org.nightlabs.jfire.trade.ui.overview.invoice;

import java.util.Collection;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.dao.InvoiceDAO;
import org.nightlabs.jfire.base.ui.overview.Entry;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.jfire.trade.ui.overview.ArticleContainerEntryViewer;
import org.nightlabs.jfire.trade.ui.overview.invoice.action.EditInvoiceAction;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class InvoiceEntryViewer
	extends ArticleContainerEntryViewer<Invoice, InvoiceQuery>
{
	public static final String ID = InvoiceEntryViewer.class.getName();

	public InvoiceEntryViewer(Entry entry) {
		super(entry);
	}

	@Override
	public AbstractTableComposite<Invoice> createListComposite(Composite parent) {
		final InvoiceListComposite invoiceListComposite = new InvoiceListComposite(parent, SWT.NONE);
		return invoiceListComposite;
	}

	@Override
	protected void addResultTableListeners(final AbstractTableComposite<Invoice> tableComposite) {
		super.addResultTableListeners(tableComposite);
		tableComposite.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				EditInvoiceAction editAction = new EditInvoiceAction();
				editAction.setSelection(tableComposite.getSelection());
				editAction.run();
			}
		});
	}

	public String getID() {
		return ID;
	}

	@Override
	protected Collection<Invoice> doSearch(
		QueryCollection<? extends InvoiceQuery> queryMap, ProgressMonitor monitor)
	{
		return InvoiceDAO.sharedInstance().getInvoices(
			queryMap,
			InvoiceListComposite.FETCH_GROUPS_INVOICES,
			NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
			monitor);
	}

	@Override
	public Class<Invoice> getTargetType()
	{
		return Invoice.class;
	}

	/**
	 * The ID for the Quick search registry.
	 */
	public static final String QUICK_SEARCH_REGISTRY_ID = InvoiceEntryViewer.class.getName();

	@Override
	protected String getQuickSearchRegistryID()
	{
		return QUICK_SEARCH_REGISTRY_ID;
	}
}