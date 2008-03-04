package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.store.ReceptionNote;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuickSearchQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteSearchFilterFactory
	extends AbstractQueryFilterFactory<ReceptionNote, ReceptionNoteQuickSearchQuery>
{

	@Override
	public AbstractQueryFilterComposite<ReceptionNote, ReceptionNoteQuickSearchQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<ReceptionNote, ? super ReceptionNoteQuickSearchQuery> queryProvider)
	{
		return new ReceptionNoteFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
