package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Calendar;
import java.util.Date;

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
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.l10n.DateFormatter;
import org.nightlabs.util.Util;

public class IssueFilterCompositeTimeRelated 
	extends AbstractQueryFilterComposite<Issue, IssueQuery> 
{	
	private DateTimeEdit createdTimeEdit;
	private DateTimeEdit updatedTimeEdit;
	
	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueFilterCompositeTimeRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<Issue, ? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite(this);
	}

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
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				getQuery().setCreateTimestamp(createdTimeEdit.getDate());
				setUIChangedQuery(false);
			}
		});
		createdTimeEdit.addActiveChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button) e.getSource()).getSelection(); 
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setCreateTimestamp(createdTimeEdit.getDate());
				}
				else
				{
					getQuery().setCreateTimestamp(null);
				}
				setUIChangedQuery(false);
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
				
				setUIChangedQuery(true);
				getQuery().setUpdateTimestamp(updatedTimeEdit.getDate());
				setUIChangedQuery(false);
			}
		});
		updatedTimeEdit.addActiveChangeListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean active = ((Button) e.getSource()).getSelection(); 
				setSearchSectionActive(active);
				if (isUpdatingUI())
					return;
				
				setUIChangedQuery(true);
				if (active)
				{
					getQuery().setUpdateTimestamp(updatedTimeEdit.getDate());
				}
				else
				{
					getQuery().setUpdateTimestamp(null);
				}
				setUIChangedQuery(false);
			}
		});
	}
	
	@Override
	protected void resetSearchQueryValues(IssueQuery query)
	{
		if (createdTimeEdit.isActive())
		{
			query.setCreateTimestamp(createdTimeEdit.getDate());
		}
		else
		{
			query.setCreateTimestamp(null);
		}
		
		if (updatedTimeEdit.isActive())
		{
			query.setUpdateTimestamp(updatedTimeEdit.getDate());
		}
		else
		{
			query.setUpdateTimestamp(null);
		}
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query)
	{
		query.setCreateTimestamp(null);
		query.setUpdateTimestamp(null);
	}

	@Override
	protected void doUpdateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			createdTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
			createdTimeEdit.setActive(false);
			updatedTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
			updatedTimeEdit.setActive(false);
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				if (IssueQuery.PROPERTY_CREATE_TIMESTAMP.equals(changedField.getPropertyName()))
				{
					final Date tmpCreateDate = (Date) changedField.getNewValue();
					if (! Util.equals(createdTimeEdit.getDate(), tmpCreateDate) )
					{
						if (tmpCreateDate == null)
						{
							createdTimeEdit.setActive(false);
							createdTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
						}
						else
						{
							createdTimeEdit.setActive(true);
							createdTimeEdit.setDate(tmpCreateDate);
						}
					}
				}
				
				if (IssueQuery.PROPERTY_UPDATE_TIMESTAMP.equals(changedField.getPropertyName()))
				{
					final Date tmpUpdateDate = (Date) changedField.getNewValue();
					if (! Util.equals(updatedTimeEdit.getDate(), tmpUpdateDate) )
					{
						if (tmpUpdateDate == null)
						{
							updatedTimeEdit.setActive(false);
							updatedTimeEdit.setTimestamp(Calendar.getInstance().getTimeInMillis());
						}
						else
						{
							updatedTimeEdit.setActive(true);
							updatedTimeEdit.setDate(tmpUpdateDate);
						}
					}
				}				
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}

}
