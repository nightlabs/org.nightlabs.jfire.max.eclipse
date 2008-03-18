package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItemChangedListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangedEvent;
import org.nightlabs.util.Util;

public class IssueFilterCompositeDocumentRelated 
	extends AbstractQueryFilterComposite<Issue, IssueQuery> 
{	
	private IssueLinkAdderComposite issueLinkAdderComposite;
	private Set<IssueLink> issueLinks;

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueFilterCompositeDocumentRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Issue, ? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

	public IssueFilterCompositeDocumentRelated(Composite parent, int style,
			QueryProvider<Issue, ? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite(this);
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected void createComposite(Composite parent)
	{
		issueLinkAdderComposite = new IssueLinkAdderComposite(parent, SWT.NONE, true, null);
		issueLinkAdderComposite.addIssueLinkTableItemListener(new IssueLinkTableItemChangedListener()
		{
			@Override
			public void issueLinkItemChanged(IssueLinkItemChangedEvent itemChangedEvent)
			{
				if (isUpdatingUI())
					return;
				
				issueLinks = issueLinkAdderComposite.getItems();
				setUIChangedQuery(true);
				getQuery().setIssueLinks( issueLinks );
				setUIChangedQuery(false);
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

	@SuppressWarnings("unchecked")
	@Override
	protected void doUpdateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			issueLinks = null;
			issueLinkAdderComposite.setIssueLinks(null);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				if (IssueQuery.PROPERTY_ISSUE_LINKS.equals(changedField.getPropertyName()))
				{
					Set<IssueLink> tmpIssueLinks = (Set<IssueLink>) changedField.getNewValue();
					if (! Util.equals(issueLinks, tmpIssueLinks))
					{
						issueLinks = tmpIssueLinks;
						issueLinkAdderComposite.setIssueLinks(tmpIssueLinks);
					}
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}
	
}
