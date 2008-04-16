package org.nightlabs.jfire.trade.ui.overview;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.query.StatableQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class StatableSearchFilterFactory
	extends AbstractQueryFilterFactory<StatableQuery>
{

	@Override
	public AbstractQueryFilterComposite<StatableQuery> createQueryFilter(Composite parent,
		int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<? super StatableQuery> queryProvider)
	{
		StatableFilterSearchComposite filterComposite =
			new StatableFilterSearchComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
		
		// set the class with which the JDOQuery will be instantiated and for which the 
		// selectable states shall be retrieved.
		filterComposite.setStatableClass((Class<? extends Statable>) getTargetClass());
		
		return filterComposite;
	}

}
