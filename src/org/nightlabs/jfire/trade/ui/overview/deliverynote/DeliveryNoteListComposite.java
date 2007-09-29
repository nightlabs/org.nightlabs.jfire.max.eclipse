package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteListComposite 
extends AbstractArticleContainerListComposite
{
	public DeliveryNoteListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createArticleContainerIDPrefixTableColumn(
			TableViewer tableViewer, Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite.deliveryNoteIDPrefixTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}
	
	@Override
	protected void createArticleContainerIDTableColumn(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.RIGHT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite.deliveryNoteIDTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite.finalizeDateTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite.finalizeUserTableColumn.text")); //$NON-NLS-1$
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Override
	protected String getAdditionalColumnText(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex)
	{
		if (!(element instanceof DeliveryNote))
			return ""; //$NON-NLS-1$

		DeliveryNote deliveryNote = (DeliveryNote) element;

		switch (additionalColumnIndex) {
			case 0:
				if (deliveryNote.getFinalizeDT() != null)
					return DateFormatter.formatDateShort(deliveryNote.getFinalizeDT(), false);
			break;
			case 1:
				if (deliveryNote.getFinalizeUser() != null)
					return deliveryNote.getFinalizeUser().getName();
			break;
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return DeliveryNote.class;
	}
}
