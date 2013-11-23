package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.ArticleContainer;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerListComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class ReceptionNoteListComposite
extends AbstractArticleContainerListComposite<ReceptionNote>
{
	public ReceptionNoteListComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class<? extends ArticleContainer> getArticleContainerClass() {
		return ReceptionNote.class;
	}

	@Override
	protected void createAdditionalTableColumns(TableViewer tableViewer,
			Table table)
	{
	}

	@Override
	protected String getAdditionalColumnText(Object element,
			int additionalColumnIndex, int firstAdditionalColumnIndex, int columnIndex)
	{
		return null;
	}
}
