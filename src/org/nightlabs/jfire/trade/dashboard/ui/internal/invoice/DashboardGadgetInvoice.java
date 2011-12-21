package org.nightlabs.jfire.trade.dashboard.ui.internal.invoice;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryCollection;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.Price;
import org.nightlabs.jfire.accounting.dao.InvoiceDAO;
import org.nightlabs.jfire.base.dashboard.ui.AbstractDashboardGadget;
import org.nightlabs.jfire.dashboard.DashboardGadgetLayoutEntry;
import org.nightlabs.jfire.prop.PropertySet;
import org.nightlabs.jfire.prop.StructLocal;
import org.nightlabs.jfire.prop.dao.StructLocalDAO;
import org.nightlabs.jfire.query.store.BaseQueryStore;
import org.nightlabs.jfire.query.store.QueryStore;
import org.nightlabs.jfire.query.store.dao.QueryStoreDAO;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.OrganisationLegalEntity;
import org.nightlabs.jfire.trade.dashboard.DashboardGadgetInvoiceConfig;
import org.nightlabs.jfire.trade.query.InvoiceQuery;
import org.nightlabs.l10n.GlobalNumberFormatter;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;
import org.nightlabs.progress.SubProgressMonitor;

/**
 * @author sschefczyk
 * @author Frederik Loeser <!-- frederik [AT] nightlabs [DOT] de -->
 */
public class DashboardGadgetInvoice extends AbstractDashboardGadget {

	private InvoiceTable invoiceTable;

	public DashboardGadgetInvoice() {}

	@Override
	public Composite createControl(Composite parent) {
		XComposite invoiceGadget = createDefaultWrapper(parent);
		invoiceTable = new InvoiceTable(invoiceGadget, SWT.NONE);
		return invoiceGadget;
	}
	
	@Override
	public void refresh() {
		getGadgetContainer().setTitle(getGadgetContainer().getLayoutEntry().getName());
		Job refreshJob = new RefreshGadgetJob("Load invoices Job");
		refreshJob.schedule();	
	}
	
	class RefreshGadgetJob extends Job {
		
		private RefreshGadgetJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(ProgressMonitor monitor) {
			monitor.beginTask("Retrieving invoices", 100);
			try {
				displayLoadingMessage();
				DashboardGadgetLayoutEntry<?> layoutEntry = getGadgetContainer().getLayoutEntry();
				DashboardGadgetInvoiceConfig config = getConfig(layoutEntry);
				QueryCollection<? extends InvoiceQuery> queryCollection = getConfiguredQueryCollection(config, new SubProgressMonitor(monitor, 50));
				Collection<Invoice> invoices = getInvoicesForQueryCollection(queryCollection, new SubProgressMonitor(monitor, 50));
				List<InvoiceTableItem> invoiceList = createInvoiceTableItems(invoices);
				updateTableInput(invoiceList);
			} finally {
				monitor.done();
			}
			
			return Status.OK_STATUS;
		}

		private DashboardGadgetInvoiceConfig getConfig(
				DashboardGadgetLayoutEntry<?> layoutEntry) {
			return (DashboardGadgetInvoiceConfig) (layoutEntry.getConfig() != null ? layoutEntry.getConfig() : new DashboardGadgetInvoiceConfig());
		}

		@SuppressWarnings("unchecked")
		private QueryCollection<? extends InvoiceQuery> getConfiguredQueryCollection(DashboardGadgetInvoiceConfig config, ProgressMonitor monitor) {
			
			monitor.beginTask("Loading query", 1);
			try {
				QueryCollection<InvoiceQuery> queryCollection = null;
				if (config.getInvoiceQueryItemId() != null) {
					QueryStore queryStore = QueryStoreDAO.sharedInstance().getQueryStore(
									config.getInvoiceQueryItemId(),
									new String[] { BaseQueryStore.FETCH_GROUP_SERIALISED_QUERIES },
									NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
									monitor);
					queryCollection = (QueryCollection<InvoiceQuery>) queryStore.getQueryCollection();
				} else {
					queryCollection = new QueryCollection(Invoice.class);
					queryCollection.add(new InvoiceQuery());
				}
				
				queryCollection.setFromInclude(0);
				queryCollection.setToExclude(Math.max(1, config.getAmountOfInvoices()));
				
				return queryCollection;

			} finally {
				monitor.done();
			}
		}
		
		private final String[] INVOICE_FETCH_GROUPS = new String[] { FetchPlan.DEFAULT,
				Invoice.FETCH_GROUP_PRICE,
				Price.FETCH_GROUP_CURRENCY,
				Invoice.FETCH_GROUP_CUSTOMER,
				Invoice.FETCH_GROUP_VENDOR,
				LegalEntity.FETCH_GROUP_PERSON,
				PropertySet.FETCH_GROUP_FULL_DATA };

		private Collection<Invoice> getInvoicesForQueryCollection(
				QueryCollection<? extends InvoiceQuery> queryCollection,
				ProgressMonitor monitor) {
			Collection<Invoice> invoices = InvoiceDAO.sharedInstance().getInvoices(
					queryCollection, INVOICE_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new SubProgressMonitor(monitor, 50));
			List<Invoice> invoiceList = new LinkedList<Invoice>(invoices);
			Collections.sort(invoiceList, Collections.reverseOrder(ArticleContainerUtil.ARTICLE_CONTAINER_COMPARATOR));
			monitor.done();
			return invoiceList;
		}


		private List<InvoiceTableItem> createInvoiceTableItems(
				Collection<Invoice> invoices) {
			final List<InvoiceTableItem> tableItems = new LinkedList<InvoiceTableItem>();
			for (Invoice invoice : invoices) {
				InvoiceTableItem tableItem = new InvoiceTableItem();
				tableItem.invoiceId = ArticleContainerUtil.getArticleContainerID(invoice);
				tableItem.customerName = getBusinessPartnerName(invoice);
				tableItem.invoiceAmount = formatInvoiceAmount(invoice);
				tableItems.add(tableItem);
			}
			return tableItems;
		}

		private String formatInvoiceAmount(Invoice invoice) {
			return GlobalNumberFormatter.sharedInstance().formatCurrency(invoice.getPrice().getAmount(), invoice.getPrice().getCurrency());
		}

		private String getBusinessPartnerName(Invoice invoice) {
			LegalEntity businessPartner = invoice.getCustomer();
			boolean isPurchaseInvoice = !(invoice.getVendor() instanceof OrganisationLegalEntity);
			if (isPurchaseInvoice) {
				businessPartner = invoice.getVendor();
			}
			String name = businessPartner.getPerson().getDisplayName();
			if (name == null || name.isEmpty()) {
				StructLocal structure = StructLocalDAO.sharedInstance().getStructLocal(businessPartner.getPerson().getStructLocalObjectID(), new NullProgressMonitor());
				name = structure.createDisplayName(businessPartner.getPerson());
			}
			return name;
		}

		private void displayLoadingMessage() {
			invoiceTable.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					invoiceTable.setLoadingMessage("Loading...");
				}
			});
		}
		
		private void updateTableInput(final List<InvoiceTableItem> invoiceList) {
			invoiceTable.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					invoiceTable.setInput(invoiceList);
				}
			});
		}
	}

	static class InvoiceTableItem {
		String invoiceId;
		String customerName;
		String invoiceAmount;
	}
	
	static class InvoiceTable extends AbstractTableComposite<InvoiceTableItem> {
		
		public InvoiceTable(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected void createTableColumns(TableViewer tableViewer, final Table table) 
		{
			TableColumn col0 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col0.setText("ID");
			TableColumn col1 = new TableColumn(tableViewer.getTable(), SWT.LEFT);
			col1.setText("Customer");
			TableColumn col2 = new TableColumn(tableViewer.getTable(), SWT.RIGHT);
			col2.setText("Amount");
			
			final TableLayout tableLayout = new TableLayout();
			tableLayout.addColumnData(new ColumnWeightData(30));
			tableLayout.addColumnData(new ColumnWeightData(60));
			tableLayout.addColumnData(new ColumnWeightData(30));
			table.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!table.isDisposed()) {
						table.setLayout(tableLayout);
						table.layout(true, true);
					}
				}
			});
		}

		@Override
		protected void setTableProvider(TableViewer tableViewer) {
			tableViewer.setContentProvider(new ArrayContentProvider());
			tableViewer.setLabelProvider(new TableLabelProvider() {
				
				@Override
				public String getColumnText(Object element, int columnIndex) {
					if (columnIndex == 0)
						return ((InvoiceTableItem) element).invoiceId;
					if (columnIndex == 1)
						return ((InvoiceTableItem) element).customerName;
					if (columnIndex == 2)
						return ((InvoiceTableItem) element).invoiceAmount;
					return "";
				}
			});
		}
		
	}
	
}
