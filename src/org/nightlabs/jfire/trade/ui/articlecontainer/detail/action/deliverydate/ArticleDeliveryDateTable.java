package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Date;
import java.util.Map;

import javax.jdo.FetchPlan;

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
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.store.ProductType;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleDeliveryDateSet;
import org.nightlabs.jfire.trade.dao.ArticleDAO;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleDeliveryDateTable
extends AbstractTableComposite<Article>
{
	class LabelProvider extends TableLabelProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Map.Entry) {
				Map.Entry<ArticleID, Date> entry = (Map.Entry<ArticleID, Date>) element;
				Article article = ArticleDAO.sharedInstance().getArticle(entry.getKey(),
						new String[] {FetchPlan.DEFAULT, Article.FETCH_GROUP_PRODUCT_TYPE, ProductType.FETCH_GROUP_NAME},
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
				Date deliveryDate = entry.getValue();
				switch (columnIndex) {
				case 0:
					return article.getProductType().getName().getText();
				case 1:
					if (deliveryDate != null)
						return DateFormatter.formatDateShort(deliveryDate, true);
				}
				return "";
			}
			return "";
		}
	}

	class ContentProvider extends ArrayContentProvider {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ArrayContentProvider#getElements(java.lang.Object)
		 */
		@Override
		public Object[] getElements(Object inputElement)
		{
			ArticleDeliveryDateSet articleDeliveryDateSet = (ArticleDeliveryDateSet) inputElement;
			if (articleDeliveryDateSet != null && articleDeliveryDateSet.getArticleID2DeliveryDate() != null)
				return articleDeliveryDateSet.getArticleID2DeliveryDate().entrySet().toArray();
			return new Object[] {};
		}
	}

	public ArticleDeliveryDateTable(Composite parent, int style) {
		super(parent, style, false);
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
		deliveryDateViewerColumn.setEditingSupport(new ArticleDeliveryDateEditingSupport(getTableViewer()));

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
		tableViewer.setContentProvider(new ContentProvider());
	}

	public void setArticleDeliveryDateSet(ArticleDeliveryDateSet articleDeliveryDateSet) {
		setInput(articleDeliveryDateSet);
	}
}
