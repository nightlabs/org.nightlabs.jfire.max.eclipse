package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Collection;
import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.deliverydate.ArticleDeliveryDateCarrier;
import org.nightlabs.jfire.trade.deliverydate.DeliveryDateMode;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.tableprovider.ui.TableProviderTable;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleDeliveryDateTable
extends TableProviderTable<Article>
{
	private Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers;
	private ArticleDeliveryDateCarrierEditingSupport articleDeliveryDateCarrierEditingSupport;
	private DeliveryDateMode mode;

	/**
	 * @param parent
	 * @param style
	 * @param elementClass
	 * @param scope
	 */
	public ArticleDeliveryDateTable(Composite parent, int style,
			String elementClass, String scope, DeliveryDateMode mode) {
		super(parent, style, elementClass, scope);
		this.mode = mode;
	}

	public void setArticleDeliveryDateCarriers(Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers) {
		this.articleDeliveryDateCarriers = articleDeliveryDateCarriers;
		articleDeliveryDateCarrierEditingSupport.setArticleDeliveryDateCarriers(articleDeliveryDateCarriers);
	}

	protected ArticleDeliveryDateCarrier getArticleDeliveryDateCarrier(Article article) {
		ArticleID articleID = (ArticleID) JDOHelper.getObjectId(article);
		for (ArticleDeliveryDateCarrier articleDeliveryDateCarrier : articleDeliveryDateCarriers) {
			if (articleDeliveryDateCarrier.getArticleID().equals(articleID)) {
				return articleDeliveryDateCarrier;
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderTable#createTableColumns(org.eclipse.jface.viewers.TableViewer, org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void createTableColumns(TableViewer tableViewer, Table table) {
		super.createTableColumns(tableViewer, table);
		TableColumn deliveryDateColumn = new TableColumn(table, SWT.NONE);
		deliveryDateColumn.setText("Delivery Date");

		articleDeliveryDateCarrierEditingSupport = new ArticleDeliveryDateCarrierEditingSupport(tableViewer);
		TableViewerColumn tvc = new TableViewerColumn(tableViewer, deliveryDateColumn);
		tvc.setEditingSupport(articleDeliveryDateCarrierEditingSupport);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			/* (non-Javadoc)
			 * @see org.eclipse.jface.viewers.ColumnLabelProvider#getText(java.lang.Object)
			 */
			@Override
			public String getText(Object element)
			{
				if (element instanceof Article) {
					Article article = (Article) element;
					Date deliverDate = null;
					ArticleDeliveryDateCarrier carrier = getArticleDeliveryDateCarrier(article);
					deliverDate = carrier.getDeliveryDate();

					if (deliverDate != null) {
						return DateFormatter.formatDateShort(deliverDate, false);
					}
				}
				return "";
			}
		});
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.tableprovider.ui.TableProviderTable#configureTableLayout(org.eclipse.swt.widgets.Table)
	 */
	@Override
	protected void configureTableLayout(Table table, TableLayout tableLayout) {
		super.configureTableLayout(table, tableLayout);
		tableLayout.addColumnData(new ColumnWeightData(1));
	}

}
