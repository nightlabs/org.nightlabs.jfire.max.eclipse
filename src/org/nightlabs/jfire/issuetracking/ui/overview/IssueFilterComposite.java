package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.ui.JDOQueryComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.Issue;

public class IssueFilterComposite 
extends AbstractQueryFilterComposite 
{	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}
	
	public IssueFilterComposite(Composite parent, int style) {
		super(parent, style);
	}

	private IssueSearchComposite issueSearchComposite;
	
	@Override
	protected void createContents(Composite parent) {
		issueSearchComposite = new IssueSearchComposite(parent, SWT.NONE, 
				LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		issueSearchComposite.setToolkit(getToolkit());
	}

	@Override
	protected Class getQueryClass() {
		return Issue.class;
	}

	@Override
	protected List<JDOQueryComposite> registerJDOQueryComposites() {
		List<JDOQueryComposite> queryComps = new ArrayList<JDOQueryComposite>();
		queryComps.add(issueSearchComposite);
		return queryComps;
	}
}
