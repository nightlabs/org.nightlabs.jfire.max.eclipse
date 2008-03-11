package org.nightlabs.jfire.issuetracking.ui.overview;

import org.apache.log4j.Logger;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

/**
 * @author Chairat Kongarayawetchakun 
 */
public class IssueSearchCompositeDocumentRelated
extends JDOQueryComposite<Issue, IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueSearchCompositeDocumentRelated.class);
	private Object mutex = new Object();

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueSearchCompositeDocumentRelated(AbstractQueryFilterComposite<Issue, IssueQuery> parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);

		createComposite(this);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public IssueSearchCompositeDocumentRelated(AbstractQueryFilterComposite<Issue, IssueQuery> parent, int style)
	{
		super(parent, style);

		createComposite(this);
	}

	/**************Document Related Section************/
	private IssueLinkAdderComposite issueLinkAdderComposite;

	@Override
	protected void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

	}

	@Override
	protected void resetSearchQueryValues() {
		IssueQuery issueQuery = getQuery();
	}

	@Override
	protected void unsetSearchQueryValues() {
		IssueQuery issueQuery = getQuery();
	}
}
