package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.overview.search.JDOQueryComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItemChangedListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangedEvent;

/**
 * @author Chairat Kongarayawetchakun 
 */
@Deprecated
public class IssueSearchCompositeDocumentRelated
extends JDOQueryComposite<Issue, IssueQuery>
{
//	private static final Logger logger = Logger.getLogger(IssueSearchCompositeDocumentRelated.class);

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

	private IssueLinkAdderComposite issueLinkAdderComposite;
	private Set<IssueLink> issueLinks;

	@Override
	protected void createComposite(Composite parent) {
		issueLinkAdderComposite = new IssueLinkAdderComposite(parent, SWT.NONE, true, null);
		issueLinkAdderComposite.addIssueLinkTableItemListener(new IssueLinkTableItemChangedListener()
		{
			@Override
			public void issueLinkItemChanged(IssueLinkItemChangedEvent itemChangedEvent)
			{
				if (isUpdatingUI())
					return;
				
				issueLinks = issueLinkAdderComposite.getItems();
				getQuery().setIssueLinks( issueLinks );
			}
		});
	}

	@Override
	protected void resetSearchQueryValues(IssueQuery query)
	{
		query.setIssueLinks(issueLinks);
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query)
	{
		query.setIssueLinks(null);
	}

	@Override
	protected void updateUI(QueryEvent event)
	{
		final IssueQuery changedQuery = (IssueQuery) event.getChangedQuery();
		if (changedQuery == null)
		{
			issueLinks = null;
		}
		else
		{
			issueLinks = changedQuery.getIssueLinks();
		}
		
		issueLinkAdderComposite.setIssueLinks(issueLinks);
	}
}
