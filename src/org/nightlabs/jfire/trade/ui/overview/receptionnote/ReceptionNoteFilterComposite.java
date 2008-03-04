package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.query.AbstractArticleContainerQuickSearchQuery;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuickSearchQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteFilterComposite
	extends AbstractArticleContainerFilterComposite<ReceptionNote, ReceptionNoteQuickSearchQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param queryProvider
	 */
	public ReceptionNoteFilterComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode,
		QueryProvider<ReceptionNote, ? super ReceptionNoteQuickSearchQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ReceptionNoteFilterComposite(Composite parent, int style,
		QueryProvider<ReceptionNote, AbstractArticleContainerQuickSearchQuery<ReceptionNote>> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	protected Class<ReceptionNoteQuickSearchQuery> getQueryClass() {
		return ReceptionNoteQuickSearchQuery.class;
	}
	
}
