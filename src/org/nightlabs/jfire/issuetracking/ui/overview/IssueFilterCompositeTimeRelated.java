package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.ui.composite.DateTimeEdit;
import org.nightlabs.base.ui.composite.XComposite;
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
public class IssueFilterCompositeTimeRelated
	extends AbstractQueryFilterComposite<IssueQuery>
{
	private DateTimeEdit createdTimeEdit;
	private DateTimeEdit updatedTimeEdit;

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
	public IssueFilterCompositeTimeRelated(Composite parent, int style,
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

		createdTimeEdit = new DateTimeEdit(
				this,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				new Date(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeTimeRelated.dateTimeEdit.createTime.text"), //$NON-NLS-1$
				true);
		createdTimeEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
				getQuery().setCreateTimestamp(createdTimeEdit.getDate());
			}
		});
		createdTimeEdit.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(IssueQuery.FieldName.createTimestamp, active);
			}
		});

		updatedTimeEdit = new DateTimeEdit(
				this,
				DateFormatter.FLAGS_DATE_SHORT_TIME_HMS_WEEKDAY + DateTimeEdit.FLAGS_SHOW_ACTIVE_CHECK_BOX,
				new Date(),
				Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositeTimeRelated.dateTimeEdit.updatedAfter.text"), //$NON-NLS-1$
				true);
		updatedTimeEdit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

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
				getQuery().setUpdateTimestamp(updatedTimeEdit.getDate());
			}
		});
		updatedTimeEdit.addActiveChangeListener(new ButtonSelectionListener()
		{
			@Override
			protected void handleSelection(boolean active)
			{
				getQuery().setFieldEnabled(IssueQuery.FieldName.updateTimestamp, active);
			}
		});
	}

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.createTimestamp.equals(changedField.getPropertyName()))
			{
				final Date newCreateDate = (Date) changedField.getNewValue();
				if (newCreateDate == null) {
					createdTimeEdit.setActive(false);
				}
				else {
					createdTimeEdit.setDate(newCreateDate);
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.createTimestamp).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				if (createdTimeEdit.isActive() != active)
				{
					createdTimeEdit.setActive(active);
					setSearchSectionActive(active);
				}
			}
			else if (IssueQuery.FieldName.updateTimestamp.equals(changedField.getPropertyName()))
			{
				final Date newUpdateDate = (Date) changedField.getNewValue();
				if (newUpdateDate == null) {
					updatedTimeEdit.setActive(false);
				}
				else {
					updatedTimeEdit.setDate(newUpdateDate);
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.updateTimestamp).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				if (updatedTimeEdit.isActive() != active)
				{
					updatedTimeEdit.setActive(active);
					setSearchSectionActive(active);
				}
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(2);
		fieldNames.add(IssueQuery.FieldName.createTimestamp);
		fieldNames.add(IssueQuery.FieldName.updateTimestamp);
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
