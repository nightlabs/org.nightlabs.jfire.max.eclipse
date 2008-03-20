package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.resource.Messages;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.l10n.DateFormatter;

public class IssueFilterCompositeTimeRelated 
	extends AbstractQueryFilterComposite<Issue, IssueQuery> 
{	
	private DateTimeEdit createdTimeEdit;
	private Date createDate;
	private DateTimeEdit updatedTimeEdit;
	private Date updateDate;
	
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
	public IssueFilterCompositeTimeRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Issue, ? super IssueQuery> queryProvider)
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
	public IssueFilterCompositeTimeRelated(Composite parent, int style,
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
				createDate = createdTimeEdit.getDate();
				getQuery().setCreateTimestamp(createDate);
			}
		});
		createdTimeEdit.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (createDate == null)
					{
						initialValue = true;
						// for consistency we need to update the field according to the initial value of
						// the date edit composites.
						createDate = createdTimeEdit.getDate();
						getQuery().setCreateTimestamp(createDate);
						initialValue = false;
					}
					else
					{
						getQuery().setCreateTimestamp(createDate);
					}
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
				updateDate = updatedTimeEdit.getDate();
				getQuery().setUpdateTimestamp(updateDate);
			}
		});
		updatedTimeEdit.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				if (active)
				{
					if (updateDate == null)
					{
						initialValue = true;
						// for consistency we need to update the field according to the initial value of
						// the date edit composites.
						updateDate = updatedTimeEdit.getDate();
						getQuery().setUpdateTimestamp(updateDate);
						initialValue = false;
					}
					else
					{
						getQuery().setUpdateTimestamp(updateDate);
					}
				}
				else
				{
					getQuery().setUpdateTimestamp(null);
				}
			}
		});
	}
	
	@Override
	protected void resetSearchQueryValues(IssueQuery query)
	{
		query.setCreateTimestamp(createDate);
		query.setUpdateTimestamp(updateDate);		
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query)
	{
		if (! createdTimeEdit.isActive())
		{
			createDate = null;
		}
		if (! updatedTimeEdit.isActive())
		{
			updateDate = null;
		}
		
		query.setCreateTimestamp(null);
		query.setUpdateTimestamp(null);
	}

	@Override
	protected void updateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			createDate = null;
			createdTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
			if (createdTimeEdit.isActive())
			{
				createdTimeEdit.setActive(false);
				setSearchSectionActive(false);
			}

			updateDate = null;
			updatedTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
			if (updatedTimeEdit.isActive())
			{
				updatedTimeEdit.setActive(false);
				setSearchSectionActive(false);
			}
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				boolean active = initialValue;
				if (IssueQuery.PROPERTY_CREATE_TIMESTAMP.equals(changedField.getPropertyName()))
				{
					final Date tmpCreateDate = (Date) changedField.getNewValue();
					createdTimeEdit.setDate(tmpCreateDate);
					active |= tmpCreateDate != null;
					if (createdTimeEdit.isActive() != active)
					{
						createdTimeEdit.setActive(active);
						setSearchSectionActive(active);
					}
				}
				
				if (IssueQuery.PROPERTY_UPDATE_TIMESTAMP.equals(changedField.getPropertyName()))
				{
					final Date tmpUpdateDate = (Date) changedField.getNewValue();
					updatedTimeEdit.setDate(tmpUpdateDate);
					active |= tmpUpdateDate != null;
					if (updatedTimeEdit.isActive() != active)
					{
						updatedTimeEdit.setActive(active);
						setSearchSectionActive(active);
					}
				}
				
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}

}
