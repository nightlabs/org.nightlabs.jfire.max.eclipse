package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueLink;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTable;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItem;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLinkTableItemChangeListener;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkItemChangeEvent;
import org.nightlabs.jfire.issuetracking.ui.issuelink.IssueLinkObjectChooserComposite;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeLinkedObjectRelated
extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositeLinkedObjectRelated.class);

	private Set<IssueLink> issueLinks = new HashSet<IssueLink>();
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
	public IssueFilterCompositeLinkedObjectRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
	}

	/**
	 * @param parent
	 *          The this to instantiate this filter into.
	 * @param style
	 *          The style to apply.
	 * @param queryProvider
	 *          The queryProvider to use. It may be <code>null</code>, but the caller has to
	 *          ensure, that it is set before {@link #getQuery()} is called!
	 */
	public IssueFilterCompositeLinkedObjectRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	private IssueLinkObjectChooserComposite issueLinkObjectChooserComposite;
	@Override
	protected void createComposite()
	{
		this.setLayout(new GridLayout(3, false));

		XComposite mainComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 2;

		new Label(mainComposite, SWT.NONE).setText("Linked Object: ");
		issueLinkObjectChooserComposite = new IssueLinkObjectChooserComposite(mainComposite, SWT.NONE);
		issueLinkObjectChooserComposite.getIssueLinkTable().addIssueLinkTableItemChangeListener(new IssueLinkTableItemChangeListener() {
			@Override
			public void issueLinkItemChanged(IssueLinkItemChangeEvent itemChangedEvent) {
				issueLinks.clear();
				for (IssueLinkTableItem item : issueLinkObjectChooserComposite.getIssueLinkTable().getIssueLinkTableItems()) {
					issueLinks.add(item.getIssueLink());
				}
				getQuery().setIssueLinks(issueLinks);
				boolean enable = !issueLinks.isEmpty();
				getQuery().setFieldEnabled(IssueQuery.FieldName.issueLinks, enable);
			}
		});
	}

	private Set<IssueLinkTableItem> issueLinkTableItems;
	private IssueLinkTable issueLinkTable;
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.issueLinks.equals(changedField.getPropertyName()))
			{
				Set<IssueLink> tmpIssueLinks = (Set<IssueLink>) changedField.getNewValue();
				if (tmpIssueLinks == null)
				{
				}
				else
				{
					issueLinkTable = issueLinkObjectChooserComposite.getIssueLinkTable();
					issueLinkTable.getIssueLinkTableItems().clear();
					
					issueLinkTableItems = new HashSet<IssueLinkTableItem>();
					for (IssueLink issueLink : tmpIssueLinks) {
						IssueLinkTableItem linkedTableItem = new IssueLinkTableItem(issueLink.getLinkedObjectID(), issueLink.getIssueLinkType());
						issueLinkTableItems.add(linkedTableItem);
					}
					
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							issueLinkTable.addIssueLinkTableItems(issueLinkTableItems);
						}
					});
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueLinks).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().getIssueLinks().clear();
				}
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(1);
		fieldNames.add(IssueQuery.FieldName.issueLinks);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositeLinkedObjectRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
}