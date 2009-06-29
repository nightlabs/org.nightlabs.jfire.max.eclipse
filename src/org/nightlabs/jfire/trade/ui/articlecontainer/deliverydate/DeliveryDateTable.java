package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.accounting.Invoice;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ArticleContainerUtil;
import org.nightlabs.jfire.trade.Offer;
import org.nightlabs.jfire.trade.Order;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryDateTable extends AbstractTableComposite<ArticleContainer>
{
	// TODO use new DTO to optimize fetching of data
	class LabelProvider extends TableLabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			if (element != null && element instanceof ArticleContainer) {
				ArticleContainer ac = (ArticleContainer) element;
				switch(columnIndex) {
					case 0:
						return "";
					case 1:
						return ArticleContainerUtil.getArticleContainerID(ac);
					case 2:
						return ac.getCustomer().getPerson().getDisplayName();
					case 3:
						Set<String> endCustomers = new HashSet<String>();
						for (Article article : ac.getArticles()) {
							if (article.getEndCustomer() != null){
								String endCustomer = article.getEndCustomer().getPerson().getDisplayName();;
								endCustomers.add(endCustomer);
							}
						}
						StringBuilder sb = new StringBuilder();
						int counter = 0;
						for (String endCustomer : endCustomers) {
							if (counter != 0)
								sb.append("/");

							sb.append(endCustomer);
							++counter;
						}
						return sb.toString();
					case 4:
						Set<Date> deliveryDates = new HashSet<Date>();
						if (ac instanceof Offer || ac instanceof Order) {
							for (Article article : ac.getArticles()) {
								if (article.getDeliveryDateOffer() != null)
									deliveryDates.add(article.getDeliveryDateOffer());
							}
						}
						else if (ac instanceof DeliveryNote) {
							for (Article article : ac.getArticles()) {
								if (article.getDeliveryDateDeliveryNote() != null)
									deliveryDates.add(article.getDeliveryDateDeliveryNote());
							}
						}
						sb = new StringBuilder();
						counter = 0;
						for (Date deliveryDate : deliveryDates) {
							if (counter != 0)
								sb.append("/");

							sb.append(DateFormatter.formatDateShort(deliveryDate, true));
							++counter;
						}
						return sb.toString();
				}
			}
			return null;
		}

		/* (non-Javadoc)
		 * @see org.nightlabs.base.ui.table.TableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (element != null && element instanceof ArticleContainer) {
				ArticleContainer ac = (ArticleContainer) element;
				switch(columnIndex) {
					case 0:
						String suffix = null;
						if (ac instanceof Offer) {
							suffix = "Offer";
						}
						else if (ac instanceof Order) {
							suffix = "Order";
						}
						else if (ac instanceof DeliveryNote) {
							suffix = "DeliveryNote";
						}
						else if (ac instanceof Invoice) {
							suffix = "Invoice";
						}

						return SharedImages.getSharedImage(TradePlugin.getDefault(), DeliveryDateTable.class, suffix);
				}
			}
			return null;
		}

	}

	/**
	 * @param parent
	 * @param style
	 */
	public DeliveryDateTable(Composite parent, int style) {
		super(parent, style);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table)
	{
		TableColumn iconColumn = new TableColumn(table, SWT.NONE);
		iconColumn.setText("Icon");
		TableColumn idColumn = new TableColumn(table, SWT.NONE);
		idColumn.setText("ID");
		TableColumn customerColumn = new TableColumn(table, SWT.NONE);
		customerColumn.setText("Customer");
		TableColumn endCustomerColumn = new TableColumn(table, SWT.NONE);
		endCustomerColumn.setText("End Customer");
		TableColumn deliveryDateColumn = new TableColumn(table, SWT.NONE);
		deliveryDateColumn.setText("Delivery Date");

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnPixelData(20));
		tableLayout.addColumnData(new ColumnPixelData(100));
		tableLayout.addColumnData(new ColumnWeightData(1));
		tableLayout.addColumnData(new ColumnWeightData(1));
		tableLayout.addColumnData(new ColumnWeightData(1));

		table.setLayout(tableLayout);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new LabelProvider());
	}

}
