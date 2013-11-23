package org.nightlabs.jfire.trade.ui.overview.deliverynote;

import java.util.Comparator;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.store.DeliveryNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;
import org.nightlabs.jfire.trade.ui.resource.Messages;
import org.nightlabs.util.BaseComparator;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class DeliveryNoteListComposite
	extends AbstractArticleContainerListComposite<DeliveryNote>
{
	public static final Comparator<DeliveryNote> DELIVERY_NOTE_FINALZE_DT_COMPARATOR = new Comparator<DeliveryNote>(){
		@Override
		public int compare(DeliveryNote o1, DeliveryNote o2)
		{
			int result = BaseComparator.comparatorNullCheck(o1, o2);
			if (result == BaseComparator.COMPARE_RESULT_NOT_NULL) {
				int result2 = BaseComparator.comparatorNullCheck(o1.getFinalizeDT(), o2.getFinalizeDT());
				if (result2== BaseComparator.COMPARE_RESULT_NOT_NULL) {
					return o1.getFinalizeDT().compareTo(o2.getFinalizeDT());
				}
				return result2;
			}
			return result;
		}
	};

	public DeliveryNoteListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite.finalizeDateTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);

		tc = new TableColumn(table, SWT.LEFT);
		tc.setText(Messages.getString("org.nightlabs.jfire.trade.ui.overview.deliverynote.DeliveryNoteListComposite.finalizeUserTableColumn.text")); //$NON-NLS-1$
//		tableLayout.setColumnData(tc, new ColumnWeightData(10));
		addWeightedColumn(10);
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
					return formatDate(deliveryNote.getFinalizeDT());
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

	@Override
	protected Comparator<?> getAdditionalColumnComparator(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex,
			int columnIndex) {
		
		if (additionalColumnIndex == 0) {
			return DELIVERY_NOTE_FINALZE_DT_COMPARATOR;
		}
		return null;
	}
}
