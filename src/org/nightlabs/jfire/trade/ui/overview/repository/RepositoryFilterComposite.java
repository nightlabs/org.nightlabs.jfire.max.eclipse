package org.nightlabs.jfire.trade.ui.overview.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.store.Repository;
import org.nightlabs.jfire.store.query.RepositoryQuery;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class RepositoryFilterComposite
	extends AbstractQueryFilterComposite<Repository, RepositoryQuery>
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public RepositoryFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Repository, ? super RepositoryQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public RepositoryFilterComposite(Composite parent, int style,
		QueryProvider<Repository, ? super RepositoryQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	@Override
	protected Class<RepositoryQuery> getQueryClass() {
		return RepositoryQuery.class;
	}

	private RepositorySearchComposite repositorySearchComposite;
	
	@Override
	protected List<JDOQueryComposite<Repository, RepositoryQuery>> registerJDOQueryComposites()
	{
		List<JDOQueryComposite<Repository, RepositoryQuery>> queryComps =
			new ArrayList<JDOQueryComposite<Repository, RepositoryQuery>>();
		
		queryComps.add(repositorySearchComposite);
		return queryComps;
	}

	@Override
	protected void createContents() {
		repositorySearchComposite = new RepositorySearchComposite(
				this, SWT.NONE, LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		repositorySearchComposite.setToolkit(getToolkit());
	}

}
