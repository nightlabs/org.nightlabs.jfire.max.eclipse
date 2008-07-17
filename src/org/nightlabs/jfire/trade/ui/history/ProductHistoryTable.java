package org.nightlabs.jfire.trade.ui.history;

import javax.jdo.FetchPlan;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.layout.WeightedTableLayout;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableContentProvider;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.accounting.pay.ModeOfPaymentFlavour;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.history.ProductHistoryItem;
import org.nightlabs.jfire.trade.history.ProductHistoryItem.ProductHistoryItemType;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ProductHistoryTable 
extends AbstractTableComposite<ProductHistoryItem> 
{
	public static final String[] FETCH_GROUPS_PRODUCT_HISTORY = new String[] {
		FetchPlan.DEFAULT,
		User.FETCH_GROUP_PERSON,
		Offer.FETCH_GROUP_CREATE_USER,
		Order.FETCH_GROUP_CREATE_USER,
		Invoice.FETCH_GROUP_CREATE_USER,
		DeliveryNote.FETCH_GROUP_CREATE_USER,
		Offer.FETCH_GROUP_CUSTOMER,
		Order.FETCH_GROUP_CUSTOMER,
		Invoice.FETCH_GROUP_CUSTOMER,
		DeliveryNote.FETCH_GROUP_CUSTOMER,
		ModeOfDeliveryFlavour.FETCH_GROUP_NAME,
		ModeOfPaymentFlavour.FETCH_GROUP_NAME
	};
	
	class ProductHistoryTableLabelProvider
	extends TableLabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int index) 
		{
			if (element != null && element instanceof ProductHistoryItem) 
			{
				ProductHistoryItem item = (ProductHistoryItem) element;
				switch (index) {
					// createDT
					case 0:
						return DateFormatter.formatDate(item.getCreateDT(), DateFormatter.FLAGS_DATE_SHORT_TIME_HMS);
					// user
					case 1:
						return item.getUser().getName();
					// type
					case 2:
						ProductHistoryItemType type = item.getType();
						switch (type) {
							case ALLOCATION:
								return "Allocation";
							case DELIVERY:
								return "Delivery";
							case DELIVERY_NOTE_FINALIZED:
								return "Delivery Note";
							case INVOICE_FINALIZED:
								return "Invoice";
							case OFFER_ACCEPTED:
								return "Offer";
							case PAYMENT:
								return "Payment";								
						}
					// name
					case 3:
						return item.getName();
					// id
					case 4:
						return item.getArticleContainer().getArticleContainerIDAsString();
					// payment
					case 5:
						ModeOfPaymentFlavour paymentFlavour = item.getModeOfPaymentFlavour();
						if (paymentFlavour != null) {
							return paymentFlavour.getName().getText();
						}
						return "";
					// delivery
					case 6:
						ModeOfDeliveryFlavour deliveryFlavour = item.getModeOfDeliveryFlavour();
						if (deliveryFlavour != null) {
							return deliveryFlavour.getName().getText();
						}
						return "";
					// customer
					case 7:
						return item.getCustomer().getPerson().getDisplayName();
				}
			}
			return null;
		}
	}
	
	/**
	 * @param parent
	 * @param style
	 */
	public ProductHistoryTable(Composite parent, int style) {
		super(parent, style);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 */
	public ProductHistoryTable(Composite parent, int style, boolean initTable) {
		super(parent, style, initTable);
	}

	/**
	 * @param parent
	 * @param style
	 * @param initTable
	 * @param viewerStyle
	 */
	public ProductHistoryTable(Composite parent, int style, boolean initTable,
			int viewerStyle) {
		super(parent, style, initTable, viewerStyle);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) 
	{
		TableColumn createDTColumn = new TableColumn(table, SWT.LEFT);
		createDTColumn.setText("Create Date");
		createDTColumn.setToolTipText("Create Date");
		
		TableColumn userColumn = new TableColumn(table, SWT.LEFT);
		userColumn.setText("User");
		userColumn.setToolTipText("User");

		TableColumn historyTypeColumn = new TableColumn(table, SWT.LEFT);
		historyTypeColumn.setText("Type");
		historyTypeColumn.setToolTipText("Type");
		
		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		nameColumn.setToolTipText("Name");
		
		TableColumn articleContainerIDColumn = new TableColumn(table, SWT.LEFT);
		articleContainerIDColumn.setText("ID");
		articleContainerIDColumn.setToolTipText("ID");		

		TableColumn paymentColumn = new TableColumn(table, SWT.LEFT);
		paymentColumn.setText("Payment");
		paymentColumn.setToolTipText("Payment");		

		TableColumn deliveryColumn = new TableColumn(table, SWT.LEFT);
		deliveryColumn.setText("Delivery");
		deliveryColumn.setToolTipText("Delivery");		

		TableColumn customerColumn = new TableColumn(table, SWT.LEFT);
		customerColumn.setText("Customer");
		customerColumn.setToolTipText("Customer");
		
		WeightedTableLayout layout = new WeightedTableLayout(new int [] {1, 1, 1, 1, 1, 1, 1, 1});
		table.setLayout(layout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new TableContentProvider());
		tableViewer.setLabelProvider(new ProductHistoryTableLabelProvider());
	}

}
