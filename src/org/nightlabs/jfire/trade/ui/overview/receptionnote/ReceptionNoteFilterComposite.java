package org.nightlabs.jfire.trade.ui.overview.receptionnote;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.trade.query.ReceptionNoteQuery;
import org.nightlabs.jfire.trade.ui.overview.AbstractArticleContainerFilterComposite;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class ReceptionNoteFilterComposite
	extends AbstractArticleContainerFilterComposite<ReceptionNoteQuery>
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
		QueryProvider<? super ReceptionNoteQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public ReceptionNoteFilterComposite(Composite parent, int style,
		QueryProvider<? super ReceptionNoteQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	public Class<ReceptionNoteQuery> getQueryClass() {
		return ReceptionNoteQuery.class;
	}

	@Override
	public void resetData() {
		// TODO Auto-generated method stub
		
	}
	
}
