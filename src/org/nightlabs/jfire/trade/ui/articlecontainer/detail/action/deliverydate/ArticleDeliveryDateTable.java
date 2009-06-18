package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.table.TableLabelProvider;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.DeliveryDateMode;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleDeliveryDateTable
extends AbstractTableComposite<Article>
{
	class LabelProvider extends TableLabelProvider
	{
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Article) {
				Article article = (Article) element;
				switch (columnIndex) {
				case 0:
					return article.getProductType().getName().getText();
				case 1:
					switch (mode) {
					case OFFER:
						if (article.getDeliveryDateOffer() != null)
							return DateFormatter.sharedInstance().formatDateShort(article.getDeliveryDateOffer(), true);
					case DELIVERY_NOTE:
						if (article.getDeliveryDateDeliveryNote() != null)
							return DateFormatter.sharedInstance().formatDateShort(article.getDeliveryDateDeliveryNote(), true);
					}
				}
				return "";
			}
			return "";
		}
	}

	private DeliveryDateMode mode;

	public ArticleDeliveryDateTable(Composite parent, int style, DeliveryDateMode mode) {
		super(parent, style, false);
		this.mode = mode;
		initTable();
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		TableColumn nameColumn = new TableColumn(table, SWT.NONE);
		nameColumn.setText("Article");
		TableColumn deliveryDateColumn = new TableColumn(table, SWT.NONE);
		deliveryDateColumn.setText("Estimated Delivery Date");

		TableViewerColumn deliveryDateViewerColumn = new TableViewerColumn(getTableViewer(), deliveryDateColumn);
		deliveryDateViewerColumn.setEditingSupport(new ArticleDeliveryDateEditingSupport(getTableViewer(), mode));

		TableLayout tl = new TableLayout();
		tl.addColumnData(new ColumnWeightData(1, true));
		tl.addColumnData(new ColumnWeightData(1, true));
		table.setLayout(tl);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.ui.table.AbstractTableComposite#setTableProvider(org.eclipse.jface.viewers.TableViewer)
	 */
	@Override
	protected void setTableProvider(TableViewer tableViewer) {
		tableViewer.setLabelProvider(new LabelProvider());
		tableViewer.setContentProvider(new ArrayContentProvider());
	}

}
