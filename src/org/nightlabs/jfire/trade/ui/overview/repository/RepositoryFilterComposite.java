package org.nightlabs.jfire.trade.ui.overview.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.store.Repository;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class RepositoryFilterComposite 
extends AbstractQueryFilterComposite 
{
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public RepositoryFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public RepositoryFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Class getQueryClass() {
		return Repository.class;
	}

	@Override
	protected List<JDOQueryComposite> registerJDOQueryComposites() 
	{
		List<JDOQueryComposite> queryComps = new ArrayList<JDOQueryComposite>();
		queryComps.add(repositorySearchComposite);
		return queryComps;
	}

	@Override
	protected void createContents(Composite parent) {
		repositorySearchComposite = new RepositorySearchComposite(
				parent, SWT.NONE, LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		repositorySearchComposite.setToolkit(getToolkit());
	}

	private RepositorySearchComposite repositorySearchComposite;
}
