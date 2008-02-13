package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReceptionNoteListComposite
extends AbstractArticleContainerListComposite
{
	public ReceptionNoteListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return ReceptionNote.class;
	}

	@Override
	protected void createArticleContainerIDPrefixTableColumn(
			TableViewer tableViewer, Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText("Prefix");
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Override
	protected void createArticleContainerIDTableColumn(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
		TableColumn tc = new TableColumn(table, SWT.LEFT);
		tc.setText("RepositoryID");
		tableLayout.addColumnData(new ColumnWeightData(10));
	}

	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table, TableLayout tableLayout)
	{
//		throw new UnsupportedOperationException("NYI!"); //$NON-NLS-1$
	}
	
	@Override
	protected String getAdditionalColumnText(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex)
	{
//		throw new UnsupportedOperationException("NYI!"); //$NON-NLS-1$
		return null;
	}
}
