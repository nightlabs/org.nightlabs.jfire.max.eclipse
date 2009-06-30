package org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.nightlabs.jfire.trade.deliverydate.ArticleContainerDeliveryDateDTO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.jfire.trade.ui.TradePlugin;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class DeliveryDateTable extends AbstractTableComposite<ArticleContainer>
{
	class LabelProvider extends TableLabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex)
		{
			if (element != null && element instanceof ArticleContainerDeliveryDateDTO) {
				ArticleContainerDeliveryDateDTO dto = (ArticleContainerDeliveryDateDTO) element;
				ArticleContainer ac = dto.getArticleContainer();
				switch(columnIndex) {
					case 0:
						return ""; //$NON-NLS-1$
					case 1:
						return ArticleContainerUtil.getArticleContainerID(ac);
					case 2:
						return ac.getCustomer().getPerson().getDisplayName();
					case 3:
						Set<String> endCustomers = new HashSet<String>();
//						Collection<Article> articles = ac.getArticles();
						Collection<Article> articles = dto.getArticleID2Article().values();
						for (Article article : articles) {
							if (article.getEndCustomer() != null){
								String endCustomer = article.getEndCustomer().getPerson().getDisplayName();;
								endCustomers.add(endCustomer);
							}
						}
						StringBuilder sb = new StringBuilder();
						int counter = 0;
						for (String endCustomer : endCustomers) {
							if (counter != 0)
								sb.append("/"); //$NON-NLS-1$

							sb.append(endCustomer);
							++counter;
						}
						return sb.toString();
					case 4:
						Set<Date> deliveryDates = new HashSet<Date>();
//						articles = ac.getArticles();
						articles = dto.getArticleID2Article().values();
						if (ac instanceof Offer || ac instanceof Order) {
							for (Article article : articles) {
								if (article.getDeliveryDateOffer() != null)
									deliveryDates.add(article.getDeliveryDateOffer());
							}
						}
						else if (ac instanceof DeliveryNote) {
							for (Article article : articles) {
								if (article.getDeliveryDateDeliveryNote() != null)
									deliveryDates.add(article.getDeliveryDateDeliveryNote());
							}
						}
						List<Date> dates = new ArrayList<Date>(deliveryDates);
						Collections.sort(dates);
						sb = new StringBuilder();
						counter = 0;
						for (Date deliveryDate : dates) {
							if (counter != 0)
								sb.append("/"); //$NON-NLS-1$

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
			if (element != null && element instanceof ArticleContainerDeliveryDateDTO) {
				ArticleContainerDeliveryDateDTO dto= (ArticleContainerDeliveryDateDTO) element;
				ArticleContainer ac = dto.getArticleContainer();
				switch(columnIndex) {
					case 0:
						String suffix = null;
						if (ac instanceof Offer) {
							suffix = "Offer";
						}
						else if (ac instanceof Order) {
							suffix = "Order"; //$NON-NLS-1$
						}
						else if (ac instanceof DeliveryNote) {
							suffix = "DeliveryNote"; //$NON-NLS-1$
						}
						else if (ac instanceof Invoice) {
							suffix = "Invoice"; //$NON-NLS-1$
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
//		iconColumn.setText("Icon");
		TableColumn idColumn = new TableColumn(table, SWT.NONE);
		idColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateTable.column.id.name")); //$NON-NLS-1$
		TableColumn customerColumn = new TableColumn(table, SWT.NONE);
		customerColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateTable.column.customer.name")); //$NON-NLS-1$
		TableColumn endCustomerColumn = new TableColumn(table, SWT.NONE);
		endCustomerColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateTable.column.endcustomer.name")); //$NON-NLS-1$
		TableColumn deliveryDateColumn = new TableColumn(table, SWT.NONE);
		deliveryDateColumn.setText(Messages.getString("org.nightlabs.jfire.trade.ui.articlecontainer.deliverydate.DeliveryDateTable.column.deliverydate.name")); //$NON-NLS-1$

		TableLayout tableLayout = new TableLayout();
		tableLayout.addColumnData(new ColumnPixelData(20));
		tableLayout.addColumnData(new ColumnPixelData(75));
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
		tableViewer.setSorter(new ViewerSorter() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
			 */
			@Override
			public int compare(Viewer viewer, Object e1, Object e2)
			{
				if (e1 instanceof ArticleContainerDeliveryDateDTO && e2 instanceof ArticleContainerDeliveryDateDTO) {
					ArticleContainerDeliveryDateDTO a1 = (ArticleContainerDeliveryDateDTO) e1;
					ArticleContainerDeliveryDateDTO a2 = (ArticleContainerDeliveryDateDTO) e2;
					Date a1DeliveryDate = getEarliestDeliveryDate(a1);
					Date a2DeliveryDate = getEarliestDeliveryDate(a2);

					if (a1DeliveryDate == null && a2DeliveryDate == null)
						return 0;
					else if (a1DeliveryDate == null && a2DeliveryDate != null)
						return -1;
					else if (a1DeliveryDate != null && a2DeliveryDate == null)
						return 1;
					else
						return a1DeliveryDate.compareTo(a2DeliveryDate);
				}
				return super.compare(viewer, e1, e2);
			}
		});
	}

	public Date getEarliestDeliveryDate(ArticleContainerDeliveryDateDTO dto) {
		Date deliveryDate = null;
		for (Map.Entry<ArticleID, Article> entry : dto.getArticleID2Article().entrySet()) {
			Article article = entry.getValue();
			Date articleDeliveryDate = null;
			if (dto.getArticleContainer() instanceof Offer) {
				 articleDeliveryDate = article.getDeliveryDateOffer();
			}
			else if (dto.getArticleContainer() instanceof DeliveryNote) {
				 articleDeliveryDate = article.getDeliveryDateDeliveryNote();
			}

			if (deliveryDate == null && articleDeliveryDate != null) {
				deliveryDate = articleDeliveryDate;
			}
			else if (deliveryDate.compareTo(articleDeliveryDate) > 0) {
				deliveryDate = articleDeliveryDate;
			}
		}
		return deliveryDate;
	}

}
