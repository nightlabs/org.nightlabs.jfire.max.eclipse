package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.nightlabs.base.ui.timelength.TimeLengthComposite;
import org.nightlabs.base.ui.timelength.TimeUnit;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.l10n.DateFormatter;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositeWorkTimeRelated
	extends AbstractQueryFilterComposite<IssueQuery>
{
	private DateTimeEdit startTimeEdit;
	private DateTimeEdit endTimeEdit;
	
	private TimeLengthComposite withinTimeLengthComposite; 
//	private DateTimeEdit deadlineDateAfter;
//	private DateTimeEdit deadlineDateBefore;
	
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
	public IssueFilterCompositeWorkTimeRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
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
	public IssueFilterCompositeWorkTimeRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected void createComposite()
	{
		GridLayout gridLayout = new GridLayout(2, true);
		this.setLayout(gridLayout);
		this.setLayoutData(new GridData(GridData.FILL_BOTH));

		startTimeEdit = new DateTimeEdit(
				this,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				new Date(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeWorkTimeRelated.dateTimeEdit.startTime.text"), //$NON-NLS-1$
				true);
		startTimeEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		startTimeEdit.setDate(cal.getTime());
		startTimeEdit.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				getQuery().setIssueWorkTimeRangeFrom(startTimeEdit.getDate());
			}
		});
		startTimeEdit.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(IssueQuery.FieldName.issueWorkTimeRangeFrom, active);
			}
		});

		endTimeEdit = new DateTimeEdit(
				this,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				new Date(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeWorkTimeRelated.dateTimeEdit.endTime.text"), //$NON-NLS-1$
				true);
		endTimeEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cal.getActualMinimum(Calendar.MILLISECOND));
		endTimeEdit.setDate(cal.getTime());
		endTimeEdit.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				getQuery().setIssueWorkTimeRangeTo(endTimeEdit.getDate());
			}
		});
		endTimeEdit.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(IssueQuery.FieldName.issueWorkTimeRangeTo, active);
			}
		});
		
		Group deadlineGroup = new Group(this, SWT.NONE);
		deadlineGroup.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeWorkTimeRelated.deadlineGroup.text"));
		deadlineGroup.setLayout(new GridLayout(2, true));
		
		new Label(deadlineGroup, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeWorkTimeRelated.withinLabel.text"));
		withinTimeLengthComposite = new TimeLengthComposite(deadlineGroup);
		withinTimeLengthComposite.setTimeUnits(new TimeUnit[] {TimeUnit.month, TimeUnit.day, TimeUnit.hour});
		withinTimeLengthComposite.setTimeLength(0);
		withinTimeLengthComposite.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				changeDeadlineDuration(withinTimeLengthComposite.getTimeLength());
			}
		});
		
		//////////////////
//		Group deadlineGroup = new Group(this, SWT.NONE);
//		deadlineGroup.setText("Deadline");
//		deadlineGroup.setLayout(new GridLayout(2, true));
//		long dateTimeEditStyle = DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX;
//		deadlineDateAfter = new DateTimeEdit(deadlineGroup, dateTimeEditStyle, "After");
//		deadlineDateAfter.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		deadlineDateAfter.setActive(false);
//		deadlineDateAfter.addModifyListener(new ModifyListener()
//		{
//			@Override
//			public void modifyText(ModifyEvent e)
//			{
//				getQuery().setDeadlineAfterTimestamp(deadlineDateAfter.getDate());
//			}
//		});
//		deadlineDateAfter.addActiveChangeListener(new ButtonSelectionListener()
//		{
//			@Override
//			protected void handleSelection(boolean active)
//			{
//				if (getQuery().getDeadlineAfterTimestamp() == null) {
//					getQuery().setDeadlineAfterTimestamp(deadlineDateAfter.getDate());
//				}
//				getQuery().setFieldEnabled(IssueQuery.FieldName.deadlineAfterTimestamp, active);
//			}
//		});
//		
//		deadlineDateBefore = new DateTimeEdit(deadlineGroup, dateTimeEditStyle, "Before");
//		deadlineDateBefore.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		deadlineDateBefore.setActive(false);
//		deadlineDateBefore.addModifyListener(new ModifyListener()
//		{
//			@Override
//			public void modifyText(ModifyEvent e)
//			{
//				getQuery().setDeadlineBeforeTimestamp(deadlineDateBefore.getDate());
//			}
//		});
//		deadlineDateBefore.addActiveChangeListener(new ButtonSelectionListener()
//		{
//			@Override
//			protected void handleSelection(boolean active)
//			{
//				if (getQuery().getDeadlineBeforeTimestamp() == null) {
//					getQuery().setDeadlineBeforeTimestamp(deadlineDateBefore.getDate());
//				}
//				getQuery().setFieldEnabled(IssueQuery.FieldName.deadlineBeforeTimestamp, active);
//			}
//		});
//		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//		gd.horizontalSpan = 2;
//		deadlineGroup.setLayoutData(gd);
	}

	private void changeDeadlineDuration(long duration) {
		getQuery().setDeadlineTimePeriod(duration);
		getQuery().setFieldEnabled(IssueQuery.FieldName.deadlineTimePeriod, ! (duration == 0));
	}
	
	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.issueWorkTimeRangeFrom.equals(changedField.getPropertyName()))
			{
				final Date tmpCreateDate = (Date) changedField.getNewValue();
				startTimeEdit.setDate(tmpCreateDate);
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueWorkTimeRangeFrom).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				if (startTimeEdit.isActive() != active)
				{
					startTimeEdit.setActive(active);
					setSearchSectionActive(active);
				}
			}
			else if (IssueQuery.FieldName.issueWorkTimeRangeTo.equals(changedField.getPropertyName()))
			{
				final Date tmpUpdateDate = (Date) changedField.getNewValue();
				endTimeEdit.setDate(tmpUpdateDate);
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueWorkTimeRangeTo).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				if (endTimeEdit.isActive() != active)
				{
					endTimeEdit.setActive(active);
					setSearchSectionActive(active);
				}
			}
			else if (IssueQuery.FieldName.deadlineTimePeriod.equals(changedField.getPropertyName()))
			{
				Long newValue = (Long)changedField.getNewValue();
				if (newValue == null)
				{
//					durationComposite.setTimeLength(0);
				}
				else
				{
					withinTimeLengthComposite.setTimeLength(newValue);
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.deadlineTimePeriod).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().setDeadlineTimePeriod(null);
					withinTimeLengthComposite.setTimeLength(0);
				}
			}
//			else if (IssueQuery.FieldName.deadlineAfterTimestamp.equals(changedField.getPropertyName()))
//			{
//				final Date tmpDeadlineDate = (Date) changedField.getNewValue();
//				deadlineDateAfter.setDate(tmpDeadlineDate);
//			}
//			else if (getEnableFieldName(IssueQuery.FieldName.deadlineAfterTimestamp).equals(changedField.getPropertyName()))
//			{
//				final boolean active = (Boolean) changedField.getNewValue();
//				if (deadlineDateAfter.isActive() != active)
//				{
//					deadlineDateAfter.setActive(active);
//					setSearchSectionActive(active);
//				}
//			}
//			else if (IssueQuery.FieldName.deadlineBeforeTimestamp.equals(changedField.getPropertyName()))
//			{
//				final Date tmpDeadlineDate = (Date) changedField.getNewValue();
//				deadlineDateBefore.setDate(tmpDeadlineDate);
//			}
//			else if (getEnableFieldName(IssueQuery.FieldName.deadlineBeforeTimestamp).equals(changedField.getPropertyName()))
//			{
//				final boolean active = (Boolean) changedField.getNewValue();
//				if (deadlineDateBefore.isActive() != active)
//				{
//					deadlineDateBefore.setActive(active);
//					setSearchSectionActive(active);
//				}
//			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(2);
		fieldNames.add(IssueQuery.FieldName.issueWorkTimeRangeFrom);
		fieldNames.add(IssueQuery.FieldName.issueWorkTimeRangeTo);
		fieldNames.add(IssueQuery.FieldName.deadlineTimePeriod);
//		fieldNames.add(IssueQuery.FieldName.deadlineAfterTimestamp);
//		fieldNames.add(IssueQuery.FieldName.deadlineBeforeTimestamp);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositeTimeRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
}
