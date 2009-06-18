package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Date;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.celleditor.DateTimeCellEditor;
import org.nightlabs.jfire.trade.Article;
import org.nightlabs.jfire.trade.DeliveryDateMode;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleDeliveryDateEditingSupport extends EditingSupport
{
	private DeliveryDateMode mode;

	/**
	 * @param viewer
	 */
	public ArticleDeliveryDateEditingSupport(ColumnViewer viewer, DeliveryDateMode mode) {
		super(viewer);
		this.mode = mode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
	 */
	@Override
	protected boolean canEdit(Object element)
	{
		if (element instanceof Article && mode != null) {
			return true;
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
			return new DateTimeCellEditor((Composite)getViewer().getControl(), DateFormatter.FLAGS_DATE_SHORT, false);
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
			switch (mode){
				case OFFER:
					return article.getDeliveryDateOffer();
				case DELIVERY_NOTE:
					return article.getDeliveryDateDeliveryNote();
			}
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
			Date deliveryDate = null;
			if (value instanceof Date) {
				deliveryDate = (Date) value;
			}
			else if (value == null) {
				deliveryDate = null;
			}
			else {
				throw new IllegalArgumentException("Param value in neither a Date nor null!");
			}
			switch (mode){
				case OFFER:
					article.setDeliveryDateOffer(deliveryDate);
					break;
				case DELIVERY_NOTE:
					article.setDeliveryDateDeliveryNote(deliveryDate);
					break;
			}
		}
		getViewer().refresh(true);
	}

}
