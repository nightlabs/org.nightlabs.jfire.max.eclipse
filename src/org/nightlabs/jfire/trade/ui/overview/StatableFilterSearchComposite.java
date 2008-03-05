package org.nightlabs.jfire.trade.ui.overview;

import java.util.Collections;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.jbpm.graph.def.Statable;
import org.nightlabs.jfire.jbpm.query.StatableQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class StatableFilterSearchComposite
	extends AbstractQueryFilterComposite<Statable, StatableQuery>
{

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param queryProvider
	 */
	public StatableFilterSearchComposite(Composite parent, int style, LayoutMode layoutMode,
		LayoutDataMode layoutDataMode, QueryProvider<Statable, ? super StatableQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 * @param queryProvider
	 */
	public StatableFilterSearchComposite(Composite parent, int style,
		QueryProvider<Statable, ? super StatableQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	private StatableFilterComposite statableComposite;
	private Class<? extends Statable> statableClass;
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite#createContents()
	 */
	@Override
	protected void createContents()
	{
		statableComposite = new StatableFilterComposite(this, SWT.NONE,
			LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		statableComposite.setStatableClass(getQueryProvider().getBaseViewerClass());
	}

	@Override
	protected Class<StatableQuery> getQueryClass()
	{
		return StatableQuery.class;
	}

	@Override
	protected List<JDOQueryComposite<Statable, StatableQuery>> registerJDOQueryComposites()
	{
		return Collections.singletonList((JDOQueryComposite<Statable, StatableQuery>) statableComposite);
	}

	/**
	 * Sets the class implementing {@link Statable} for which the states shall be retrieved and used
	 * for filtering.
	 *  
	 * @param statableClass a class implementing Statable
	 */
	public void setStatableClass(Class<? extends Statable> statableClass)
	{
//		assert statableClass != null;
//		if (statableComposite == null)
//		{
//			throw new IllegalStateException("Trying to the statable class though no " +
//			"StatableFilterSection has been created!");
//		}
//		
//		statableComposite.setStatableClass(statableClass);
		this.statableClass = statableClass;
	}
	
}
