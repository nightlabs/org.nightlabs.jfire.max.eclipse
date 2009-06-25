package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Collection;
import java.util.Date;

import javax.jdo.JDOHelper;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.celleditor.DateTimeCellEditor;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.ArticleDeliveryDateCarrier;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleDeliveryDateCarrierEditingSupport extends EditingSupport
{
	private Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers;

	/**
	 * @param viewer
	 */
	public ArticleDeliveryDateCarrierEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	public void setArticleDeliveryDateCarriers(Collection<ArticleDeliveryDateCarrier> articleDeliveryDateCarriers) {
		this.articleDeliveryDateCarriers = articleDeliveryDateCarriers;
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
	 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
	 */
	@Override
	protected boolean canEdit(Object element)
	{
		if (element instanceof Article) {
			Article article = (Article) element;
			return getArticleDeliveryDateCarrier(article) != null;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#getCellEditor(java.lang.Object)
	 */
	@Override
	protected CellEditor getCellEditor(Object element)
	{
		if (element instanceof Article){
			return new DateTimeCellEditor((Composite)getViewer().getControl(), DateFormatter.FLAGS_DATE_SHORT, true);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#getValue(java.lang.Object)
	 */
	@Override
	protected Object getValue(Object element)
	{
		if (element instanceof Article) {
			Article article = (Article) element;
			ArticleDeliveryDateCarrier carrier = getArticleDeliveryDateCarrier(article);
			if (carrier != null)
				return carrier.getDeliveryDate();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setValue(Object element, Object value)
	{
		if (element instanceof Article) {
			Article article = (Article) element;
			ArticleDeliveryDateCarrier carrier = getArticleDeliveryDateCarrier(article);

			Date deliveryDate = null;
			if (value instanceof Date) {
				deliveryDate = (Date) value;
			}
			else if (value == null) {
				deliveryDate = null;
			}
			else {
				throw new IllegalArgumentException("Param value is neither a Date nor null!");
			}
			if (carrier != null)
				carrier.setDeliveryDate(deliveryDate);
		}
		getViewer().refresh(true);
	}

}
