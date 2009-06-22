package org.nightlabs.jfire.trade.ui.articlecontainer.detail.action.deliverydate;

import java.util.Date;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.celleditor.DateTimeCellEditor;
import org.nightlabs.jfire.trade.id.ArticleID;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class ArticleDeliveryDateEditingSupport extends EditingSupport
{
	/**
	 * @param viewer
	 */
	public ArticleDeliveryDateEditingSupport(ColumnViewer viewer) {
		super(viewer);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#canEdit(java.lang.Object)
	 */
	@Override
	protected boolean canEdit(Object element)
	{
		if (element instanceof Map.Entry<?, ?>) {
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
		if (element instanceof Map.Entry<?, ?>){
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
		if (element instanceof Map.Entry<?, ?>) {
			Map.Entry<ArticleID, Date> entry = (Map.Entry<ArticleID, Date>) element;
			return entry.getValue();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.EditingSupport#setValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void setValue(Object element, Object value)
	{
		if (element instanceof Map.Entry<?, ?>) {
			Map.Entry<ArticleID, Date> entry = (Map.Entry<ArticleID, Date>) element;
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
			entry.setValue(deliveryDate);
		}
		getViewer().refresh(true);
	}

}
