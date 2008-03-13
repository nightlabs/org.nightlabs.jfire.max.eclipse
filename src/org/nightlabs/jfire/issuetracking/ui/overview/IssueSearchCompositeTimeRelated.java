package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Calendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.l10n.DateFormatter;

public class IssueSearchCompositeTimeRelated
extends JDOQueryComposite<Issue, IssueQuery>
{
	public IssueSearchCompositeTimeRelated(
			AbstractQueryFilterComposite<Issue, IssueQuery> filterComposite,
			int style) {
		super(filterComposite, style);
	}
	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueSearchCompositeTimeRelated(AbstractQueryFilterComposite<Issue, IssueQuery> parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);

		createComposite(this);
	}
	
	private DateTimeEdit createdTimeEdit;
	private DateTimeEdit updatedTimeEdit;
	
	@Override
	protected void createComposite(Composite parent) {
		Group timeGroup = new Group(parent, SWT.NONE);
		timeGroup.setText("Time Related");
		
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		timeGroup.setLayout(gridLayout);
		timeGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(timeGroup, SWT.NONE).setText("Created Time: ");
		createdTimeEdit = new DateTimeEdit(
				timeGroup,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		createdTimeEdit.setDate(cal.getTime());
		createdTimeEdit.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (isUpdatingUI())
					return;
				
				getQuery().setCreateTimestamp(createdTimeEdit.getDate());
			}
		});
		createdTimeEdit.addActiveChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (isUpdatingUI())
					return;
				
				if (((Button) e.getSource()).getSelection())
				{
					getQuery().setCreateTimestamp(createdTimeEdit.getDate());
				}
				else
				{
					getQuery().setCreateTimestamp(null);
				}
			}
		});
		
		new Label(timeGroup, SWT.NONE).setText("Updated Time: ");
		updatedTimeEdit = new DateTimeEdit(
				timeGroup,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				Messages.getString("org.nightlabs.jfire.trade.ui.overview.StatableFilterComposite.createDateMin.caption")); //$NON-NLS-1$
		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		updatedTimeEdit.setDate(cal.getTime());
		updatedTimeEdit.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				if (isUpdatingUI())
					return;
				
				getQuery().setUpdateTimestamp(updatedTimeEdit.getDate());
			}
		});
		updatedTimeEdit.addActiveChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (isUpdatingUI())
					return;
				
				if (((Button) e.getSource()).getSelection())
				{
					getQuery().setUpdateTimestamp(updatedTimeEdit.getDate());
				}
				else
				{
					getQuery().setUpdateTimestamp(null);
				}
			}
		});
	}

	@Override
	protected void resetSearchQueryValues(IssueQuery query) {
		if (createdTimeEdit.getDate() != null) {
			query.setCreateTimestamp(createdTimeEdit.getDate());
		}
		
		if (updatedTimeEdit.getDate() != null) {
			query.setUpdateTimestamp(updatedTimeEdit.getDate());
		}
//		if (selectedIssueResolution != null && !selectedIssueResolution.equals(ISSUE_RESOLUTION_ALL)) {
//			query.setIssueResolutionID((IssueResolutionID)JDOHelper.getObjectId(selectedIssueResolution));
//		}
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query) {
		query.setCreateTimestamp(null);
		query.setUpdateTimestamp(null);
	}

	@Override
	protected void doUpdateUI(QueryEvent event)
	{
		boolean wholeQueryChanged = isWholeQueryChanged(event);
		final IssueQuery changedQuery = (IssueQuery) event.getChangedQuery();
		if (changedQuery == null)
		{
			createdTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
			createdTimeEdit.setActive(false);
			updatedTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
			updatedTimeEdit.setActive(false);
		}
		else
		{
			if (wholeQueryChanged || IssueQuery.PROPERTY_CREATE_TIMESTAMP.equals(event.getPropertyName()))
			{
				if (changedQuery.getCreateTimestamp() == null)
				{
					createdTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());	
					createdTimeEdit.setActive(false);
				}
				else
				{
					createdTimeEdit.setDate(changedQuery.getCreateTimestamp());
					createdTimeEdit.setActive(true);
				}
			}
			
			if (wholeQueryChanged || IssueQuery.PROPERTY_UPDATE_TIMESTAMP.equals(event.getPropertyName()))
			{
				if (changedQuery.getUpdateTimestamp() == null)
				{
					updatedTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
					updatedTimeEdit.setActive(false);
				}
				else
				{
					updatedTimeEdit.setDate(changedQuery.getUpdateTimestamp());
					updatedTimeEdit.setActive(true);
				}
			}
		}
	}

}
