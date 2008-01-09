package org.nightlabs.jfire.issuetracking.trade.ui.accounting.issuelink;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.JDOQuery;
import org.nightlabs.jfire.accounting.AccountingManager;
import org.nightlabs.jfire.accounting.AccountingManagerUtil;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.id.InvoiceID;
import org.nightlabs.jfire.accounting.query.InvoiceQuery;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.jbpm.graph.def.StatableLocal;
import org.nightlabs.jfire.jbpm.graph.def.State;
import org.nightlabs.jfire.jbpm.graph.def.StateDefinition;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.articlecontainer.InvoiceDAO;
import org.nightlabs.progress.NullProgressMonitor;

public class IssueLinkTable extends AbstractTableComposite<String> {

	protected static final String[] FETCH_GROUPS_INVOICE = new String[]{
		Invoice.FETCH_GROUP_CUSTOMER,
		Invoice.FETCH_GROUP_CURRENCY,
		Article.FETCH_GROUP_ORDER,
		Order.FETCH_GROUP_CUSTOMER_GROUP,
		Invoice.FETCH_GROUP_ARTICLES,
		Article.FETCH_GROUP_PRODUCT_TYPE, // for delivery
		ProductType.FETCH_GROUP_NAME, // for delivery
		Article.FETCH_GROUP_PRODUCT, // for delivery
		Article.FETCH_GROUP_DELIVERY_NOTE_ID,
		Article.FETCH_GROUP_INVOICE_ID,
		Article.FETCH_GROUP_PRICE, // for payment

		Invoice.FETCH_GROUP_INVOICE_LOCAL, // for finding out the amountToPay
		Invoice.FETCH_GROUP_PRICE, // for finding out the amountToPay

		FetchPlan.DEFAULT
	};

	public IssueLinkTable(Composite parent, int style)
	{
		super(parent, style);

		loadObjects();
	}

	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn tc;
		TableLayout layout = new TableLayout();

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Object ID");
		layout.addColumnData(new ColumnWeightData(30));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setMoveable(true);
		tc.setText("Type");
		layout.addColumnData(new ColumnWeightData(40));

		table.setLayout(layout);
	}

	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new IssueListLabelProvider());
		tableViewer.setContentProvider(new TableContentProvider());
	}

	private void loadObjects(){
		try {
			AccountingManager accountingManager = AccountingManagerUtil.getHome(Login.getLogin().getInitialContextProperties()).create();		

			List<JDOQuery> queries = new ArrayList<JDOQuery>();
			queries.add(new InvoiceQuery());
			Set<InvoiceID> invoiceIDs = accountingManager.getInvoiceIDs(queries);
			setInput(InvoiceDAO.sharedInstance().getInvoices(invoiceIDs, FETCH_GROUPS_INVOICE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()));
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	class IssueListLabelProvider
	extends TableLabelProvider
	{
		public String getColumnText(Object element, int columnIndex) 
		{
			if (element instanceof Offer) {
				Offer offer = (Offer) element;
				switch (columnIndex) 
				{
				case(0):
					return JDOHelper.getObjectId(offer).toString();
				case(1):
					return "Offer";
				default:
					return ""; //$NON-NLS-1$
				}
			}
			return null;
		}		
	}
}