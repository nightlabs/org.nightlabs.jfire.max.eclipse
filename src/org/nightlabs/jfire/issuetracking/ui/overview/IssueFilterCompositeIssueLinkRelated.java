package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkAdderComposite;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeIssueLinkRelated 
	extends AbstractQueryFilterComposite<IssueQuery> 
{	
	private IssueLinkAdderComposite issueLinkAdderComposite;
	private Set<IssueLink> issueLinks;

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param layoutMode
	 *          The layout mode to use. See {@link XComposite.LayoutMode}.
	 * @param layoutDataMode
	 *          The layout data mode to use. See {@link XComposite.LayoutDataMode}.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeIssueLinkRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

	/**
	 * @param parent
	 *          The parent to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeIssueLinkRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
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
		Group issueLinkGroup = new Group(parent, SWT.NONE);
		issueLinkGroup.setText("Issue Link");
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		issueLinkGroup.setLayout(gridLayout);
		issueLinkGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(issueLinkGroup, SWT.NONE).setText("Type: ");
		
		
//		issueLinkAdderComposite = new IssueLinkAdderComposite(parent, SWT.NONE, true, null);
////		issueLinkAdderComposite.addIssueLinkTableItemChangeListener(new IssueLinkTableItemChangeListener()
////		{
////			@Override
////			public void issueLinkItemChanged(IssueLinkItemChangeEvent itemChangedEvent)
////			{
////				issueLinks = issueLinkAdderComposite.getItems();
////				getQuery().setIssueLinks( issueLinks );
////			}
////		});
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
	protected void updateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			issueLinks = null;
//			issueLinkAdderComposite.setIssueLinks(null);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				if (IssueQuery.PROPERTY_ISSUE_LINKS.equals(changedField.getPropertyName()))
				{
					issueLinks = (Set<IssueLink>) changedField.getNewValue();
//					issueLinkAdderComposite.setIssueLinks(issueLinks);
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}
	
}
