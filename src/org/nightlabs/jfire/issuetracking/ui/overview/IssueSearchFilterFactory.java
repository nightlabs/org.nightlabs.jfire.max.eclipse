package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueSearchFilterFactory
	extends AbstractQueryFilterFactory<Issue, IssueQuery>
{

	@Override
	public AbstractQueryFilterComposite<Issue, IssueQuery> createQueryFilter(Composite parent,
		int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
		QueryProvider<Issue, ? super IssueQuery> queryProvider)
	{
		return new IssueFilterComposite(parent, style, layoutMode, layoutDataMode, queryProvider);
	}

}
