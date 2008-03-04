package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.config.StoredIssueQuery;
import org.nightlabs.jfire.issue.query.IssueQuery;

public class IssueFilterComposite 
	extends AbstractQueryFilterComposite<Issue, IssueQuery> 
{	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueFilterComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Issue, ? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
	}
	
	public IssueFilterComposite(Composite parent, int style,
		QueryProvider<Issue, ? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
	}

	private IssueSearchComposite issueSearchComposite;
	
	@Override
	protected Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected List<JDOQueryComposite<Issue, IssueQuery>> registerJDOQueryComposites() {
		List<JDOQueryComposite<Issue, IssueQuery>> queryComps =
			new ArrayList<JDOQueryComposite<Issue, IssueQuery>>();
		
		queryComps.add(issueSearchComposite);
		return queryComps;
	}
	
	
	public void setStoredIssueQuery(StoredIssueQuery query) {
		issueSearchComposite.setStoredIssueQuery(query);
	}
	
	public void setSearchInvoker(IIssueSearchInvoker searchInvoker) {
		issueSearchComposite.setSearchInvoker(searchInvoker);
	}

	@Override
	protected void createContents()
	{
		issueSearchComposite = new IssueSearchComposite(this, SWT.NONE, 
			LayoutMode.TOTAL_WRAPPER, LayoutDataMode.GRID_DATA);
		issueSearchComposite.setToolkit(getToolkit());
	}
}
