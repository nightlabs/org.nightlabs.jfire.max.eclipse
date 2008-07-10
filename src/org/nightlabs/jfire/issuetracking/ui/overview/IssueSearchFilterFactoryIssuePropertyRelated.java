package org.nightlabs.jfire.issuetracking.ui.overview;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterFactory;
import org.nightlabs.jfire.issue.query.IssueQuery;

/**
 * 
 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
 */
public class IssueSearchFilterFactoryIssuePropertyRelated
extends AbstractQueryFilterFactory<IssueQuery>
{
	@Override
	public AbstractQueryFilterComposite<IssueQuery> createQueryFilter(Composite parent,
			int style, LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		return new IssueFilterCompositePropertyRelated(parent, style, layoutMode, layoutDataMode, queryProvider);
	}
}
