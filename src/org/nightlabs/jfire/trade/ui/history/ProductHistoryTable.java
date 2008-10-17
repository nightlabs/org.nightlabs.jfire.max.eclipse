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
import org.nightlabs.jfire.accounting.pay.Payment;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.store.deliver.ModeOfDeliveryFlavour;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.LegalEntity;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.history.ProductHistoryItem;
import org.nightlabs.jfire.trade.history.ProductHistoryItem.ProductHistoryItemType;
import org.nightlabs.jfire.trade.ui.resource.Messages;
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
//		Person.FETCH_GROUP_FULL_DATA,
		Offer.FETCH_GROUP_CREATE_USER,
		Order.FETCH_GROUP_CREATE_USER,
		Invoice.FETCH_GROUP_CREATE_USER,
		DeliveryNote.FETCH_GROUP_CREATE_USER,
		Offer.FETCH_GROUP_CUSTOMER,
		Order.FETCH_GROUP_CUSTOMER,
		Invoice.FETCH_GROUP_CUSTOMER,
		DeliveryNote.FETCH_GROUP_CUSTOMER,
		ModeOfDeliveryFlavour.FETCH_GROUP_NAME,
		ModeOfPaymentFlavour.FETCH_GROUP_NAME,
		LegalEntity.FETCH_GROUP_PERSON,
		Payment.FETCH_GROUP_MODE_OF_PAYMENT_FLAVOUR,
		Payment.FETCH_GROUP_USER,
		ModeOfDeliveryFlavour.FETCH_GROUP_NAME,
		ModeOfPaymentFlavour.FETCH_GROUP_NAME,
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
								return Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.allocation"); //$NON-NLS-1$
							case DELIVERY:
								return Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.delivery"); //$NON-NLS-1$
							case DELIVERY_NOTE:
								return Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.deliveryNote"); //$NON-NLS-1$
							case INVOICE:
								return Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.invoice");								 //$NON-NLS-1$
							case OFFER:
								return Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.offer"); //$NON-NLS-1$
							case PAYMENT:
								return Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.payment"); //$NON-NLS-1$
							default:
								return ""; //$NON-NLS-1$
						}
					// name
					case 3:
						return item.getName();
					// id
					case 4:
//						return item.getArticleContainer().getArticleContainerIDAsString();
						String id = ArticleContainerUtil.getArticleContainerID(item.getArticleContainer());
						return id != null ? id : ""; //$NON-NLS-1$
					// payment
					case 5:
						ModeOfPaymentFlavour paymentFlavour = item.getModeOfPaymentFlavour();
						if (paymentFlavour != null) {
							return paymentFlavour.getName().getText();
						}
						return ""; //$NON-NLS-1$
					// delivery
					case 6:
						ModeOfDeliveryFlavour deliveryFlavour = item.getModeOfDeliveryFlavour();
						if (deliveryFlavour != null) {
							return deliveryFlavour.getName().getText();
						}
						return ""; //$NON-NLS-1$
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
		createDTColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.createDate.text")); //$NON-NLS-1$
		createDTColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.createDate.tooltip")); //$NON-NLS-1$

		TableColumn userColumn = new TableColumn(table, SWT.LEFT);
		userColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.user.text")); //$NON-NLS-1$
		userColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.user.tooltip")); //$NON-NLS-1$

		TableColumn historyTypeColumn = new TableColumn(table, SWT.LEFT);
		historyTypeColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.type.text")); //$NON-NLS-1$
		historyTypeColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.type.tooltip")); //$NON-NLS-1$

		TableColumn nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.name.text")); //$NON-NLS-1$
		nameColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.name.tooltip")); //$NON-NLS-1$

		TableColumn articleContainerIDColumn = new TableColumn(table, SWT.LEFT);
		articleContainerIDColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.id.text")); //$NON-NLS-1$
		articleContainerIDColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.id.tooltip"));		 //$NON-NLS-1$

		TableColumn paymentColumn = new TableColumn(table, SWT.LEFT);
		paymentColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.payment.text")); //$NON-NLS-1$
		paymentColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.payment.tooltip"));		 //$NON-NLS-1$

		TableColumn deliveryColumn = new TableColumn(table, SWT.LEFT);
		deliveryColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.delivery.text")); //$NON-NLS-1$
		deliveryColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.delivery.tooltip"));		 //$NON-NLS-1$

		TableColumn customerColumn = new TableColumn(table, SWT.LEFT);
		customerColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.customer.text")); //$NON-NLS-1$
		customerColumn.setToolTipText(Messages.getString("org.nightlabs.jfire.trade.ui.history.ProductHistoryTable.column.customer.tooltip")); //$NON-NLS-1$

		WeightedTableLayout layout = new WeightedTableLayout(new int [] {20, 15, 15, 15, 20, 20, 20, 40});
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
