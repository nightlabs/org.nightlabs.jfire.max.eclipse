package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteSearchFilterFactory
	extends AbstractQueryFilterFactory<ReceptionNoteQuery>
{

	@Override
	public AbstractQueryFilterComposite<ReceptionNoteQuery> createQueryFilter(
		Composite parent, int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super ReceptionNoteQuery> queryProvider)
	{
		return new ReceptionNoteFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
