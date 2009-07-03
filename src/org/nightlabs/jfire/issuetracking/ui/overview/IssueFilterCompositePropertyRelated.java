package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.XComboComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.exceptionhandler.ExceptionHandlerRegistry;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.issue.IssuePriority;
import org.nightlabs.jfire.issue.IssueResolution;
import org.nightlabs.jfire.issue.IssueSeverityType;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issue.dao.IssuePriorityDAO;
import org.nightlabs.jfire.issue.dao.IssueResolutionDAO;
import org.nightlabs.jfire.issue.dao.IssueSeverityTypeDAO;
import org.nightlabs.jfire.issue.dao.IssueTypeDAO;
import org.nightlabs.jfire.issue.id.IssuePriorityID;
import org.nightlabs.jfire.issue.id.IssueResolutionID;
import org.nightlabs.jfire.issue.id.IssueSeverityTypeID;
import org.nightlabs.jfire.issue.id.IssueTypeID;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.issue.IssueLabelProvider;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositePropertyRelated
	extends AbstractQueryFilterComposite<IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueFilterCompositePropertyRelated.class);
	private Object mutex = new Object();

	private XComboComposite<IssueType> issueTypeCombo;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private XComboComposite<IssuePriority> issuePriorityCombo;
	private XComboComposite<IssueResolution> issueResolutionCombo;

	private volatile IssueType selectedIssueType;
	private volatile IssueSeverityType selectedIssueSeverityType;
	private volatile IssuePriority selectedIssuePriority;
	private volatile IssueResolution selectedIssueResolution;

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
	public IssueFilterCompositePropertyRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		prepareIssueProperties();
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
	public IssueFilterCompositePropertyRelated(Composite parent, int style,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, queryProvider);
		prepareIssueProperties();
		createComposite();
	}

	@Override
	public Class<IssueQuery> getQueryClass() {
		return IssueQuery.class;
	}

	@Override
	protected void createComposite()
	{
		this.setLayout(new GridLayout(3, false));

		XComposite issueTypeComposite = new XComposite(this, SWT.NONE,
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		issueTypeComposite.getGridLayout().numColumns = 4;

		new Label(issueTypeComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.label.issueType.text")); //$NON-NLS-1$
		issueTypeCombo = new XComboComposite<IssueType>(issueTypeComposite, getBorderStyle() | SWT.READ_ONLY);
		issueTypeCombo.setLabelProvider(labelProvider);
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				final IssueType selectedIssueType = issueTypeCombo.getSelectedElement();

				boolean selectAll = selectedIssueType.equals(ISSUE_TYPE_ALL);

				if (selectAll)
					getQuery().setIssueTypeID(null);
				else
					getQuery().setIssueTypeID((IssueTypeID)JDOHelper.getObjectId(selectedIssueType));

				getQuery().setFieldEnabled(IssueQuery.FieldName.issueTypeID, ! selectAll);
			}
		});

		new Label(issueTypeComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.label.severity.text")); //$NON-NLS-1$
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(issueTypeComposite, getBorderStyle() | SWT.READ_ONLY);
		issueSeverityCombo.setLabelProvider(labelProvider);
		issueSeverityCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				final IssueSeverityType selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();

				boolean selectAll = selectedIssueSeverityType.equals(ISSUE_SEVERITY_TYPE_ALL);
				if (selectAll)
					getQuery().setIssueSeverityTypeID(null);
				else
					getQuery().setIssueSeverityTypeID(
							(IssueSeverityTypeID) JDOHelper.getObjectId(selectedIssueSeverityType));

				getQuery().setFieldEnabled(IssueQuery.FieldName.issueSeverityTypeID, ! selectAll);
			}
		});

		new Label(issueTypeComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.label.priority.text")); //$NON-NLS-1$
		issuePriorityCombo = new XComboComposite<IssuePriority>(issueTypeComposite, getBorderStyle() | SWT.READ_ONLY);
		issuePriorityCombo.setLabelProvider(labelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				final IssuePriority selectedIssuePriority = issuePriorityCombo.getSelectedElement();

				boolean selectAll = selectedIssuePriority.equals(ISSUE_PRIORITY_ALL);
				if (selectAll)
					getQuery().setIssuePriorityID(null);
				else
					getQuery().setIssuePriorityID(
							(IssuePriorityID) JDOHelper.getObjectId(selectedIssuePriority));

				getQuery().setFieldEnabled(IssueQuery.FieldName.issuePriorityID, ! selectAll);
			}
		});

		new Label(issueTypeComposite, SWT.NONE).setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.label.resolution.text")); //$NON-NLS-1$
		issueResolutionCombo = new XComboComposite<IssueResolution>(issueTypeComposite, getBorderStyle() | SWT.READ_ONLY);
		issueResolutionCombo.setLabelProvider(labelProvider);
		issueResolutionCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				final IssueResolution selectedIssueResolution = issueResolutionCombo.getSelectedElement();

				boolean selectAll = ISSUE_RESOLUTION_ALL.equals(selectedIssueResolution);
				if (selectAll)
					getQuery().setIssueResolutionID(null);
				else
					getQuery().setIssueResolutionID((IssueResolutionID) JDOHelper.getObjectId(selectedIssueResolution));

				getQuery().setFieldEnabled(IssueQuery.FieldName.issueResolutionID, ! selectAll);
			}
		});

		loadProperties();
	}

	@Override
	protected void updateUI(QueryEvent event, List<FieldChangeCarrier> changedFields)
	{
		for (FieldChangeCarrier changedField : event.getChangedFields())
		{
			if (IssueQuery.FieldName.issuePriorityID.equals(changedField.getPropertyName()))
			{
				IssuePriorityID tmpPriorityID = (IssuePriorityID) changedField.getNewValue();
				if (tmpPriorityID == null)
				{
					issuePriorityCombo.setSelection(ISSUE_PRIORITY_ALL);
				}
				else
				{
					final IssuePriority newIssuePriority = IssuePriorityDAO.sharedInstance().getIssuePriority(
							tmpPriorityID, new String[] { IssuePriority.FETCH_GROUP_NAME },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);
					issuePriorityCombo.setSelection(newIssuePriority);
					if (! newIssuePriority.equals(issuePriorityCombo.getSelectedElement()))
						selectedIssuePriority = newIssuePriority;

				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issuePriorityID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().setIssuePriorityID(null);
					issuePriorityCombo.setSelection(ISSUE_PRIORITY_ALL);
				}
			}
			else if (IssueQuery.FieldName.issueResolutionID.equals(changedField.getPropertyName()))
			{
				final IssueResolutionID tmpResolutionID = (IssueResolutionID) changedField.getNewValue();
				if (tmpResolutionID == null)
				{
					issueResolutionCombo.setSelection(ISSUE_RESOLUTION_ALL);
				}
				else
				{
					final IssueResolution newIssueResolution = IssueResolutionDAO.sharedInstance().getIssueResolution(
							tmpResolutionID,
							new String[] { IssueResolution.FETCH_GROUP_NAME},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					issueResolutionCombo.setSelection(newIssueResolution);
					if (! newIssueResolution.equals(issueResolutionCombo.getSelectedElement()))
						selectedIssueResolution = newIssueResolution;

				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueResolutionID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().setIssueResolutionID(null);
					issueResolutionCombo.setSelection(ISSUE_RESOLUTION_ALL);
				}
			}
			else if (IssueQuery.FieldName.issueSeverityTypeID.equals(changedField.getPropertyName()))
			{
				IssueSeverityTypeID tmpSeverityID = (IssueSeverityTypeID) changedField.getNewValue();
				if (tmpSeverityID == null)
				{
					issueSeverityCombo.setSelection(ISSUE_SEVERITY_TYPE_ALL);
				}
				else
				{
					final IssueSeverityType newIssueSeverityType = IssueSeverityTypeDAO.sharedInstance().getIssueSeverityType(
							tmpSeverityID,
							new String[] { IssueSeverityType.FETCH_GROUP_NAME },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					issueSeverityCombo.setSelection(newIssueSeverityType);
					if (! newIssueSeverityType.equals(issueSeverityCombo.getSelectedElement()))
						selectedIssueSeverityType = newIssueSeverityType;
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueSeverityTypeID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().setIssueSeverityTypeID(null);
					issueSeverityCombo.setSelection(ISSUE_SEVERITY_TYPE_ALL);
				}
			}
			else if (IssueQuery.FieldName.issueTypeID.equals(changedField.getPropertyName()))
			{
				IssueTypeID tmpTypeID = (IssueTypeID) changedField.getNewValue();
				if (tmpTypeID == null)
				{
					issueTypeCombo.setSelection(ISSUE_TYPE_ALL);
				}
				else
				{
					final IssueType newIssueType = IssueTypeDAO.sharedInstance().getIssueType(
							tmpTypeID,
							new String[] { IssueType.FETCH_GROUP_NAME },
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);

					issueTypeCombo.setSelection(newIssueType);
					if (! newIssueType.equals(issueTypeCombo.getSelectedElement()))
						selectedIssueType = newIssueType;
				}
			}
			else if (getEnableFieldName(IssueQuery.FieldName.issueTypeID).equals(
					changedField.getPropertyName()))
			{
				Boolean active = (Boolean) changedField.getNewValue();
				setSearchSectionActive(active);
				if (!active) {
					getQuery().setIssueTypeID(null);
					issueTypeCombo.setSelection(ISSUE_TYPE_ALL);
				}
			}
		} // for (FieldChangeCarrier changedField : event.getChangedFields())
	}

	private static IssueType ISSUE_TYPE_ALL = new IssueType(Organisation.DEV_ORGANISATION_ID, "Issue_Type_All"); //$NON-NLS-1$
	private static IssueSeverityType ISSUE_SEVERITY_TYPE_ALL = new IssueSeverityType(Organisation.DEV_ORGANISATION_ID, "Issue_Severity_Type_All"); //$NON-NLS-1$
	private static IssuePriority ISSUE_PRIORITY_ALL = new IssuePriority(Organisation.DEV_ORGANISATION_ID, "Issue_Priority_All"); //$NON-NLS-1$
	private static IssueResolution ISSUE_RESOLUTION_ALL = new IssueResolution(Organisation.DEV_ORGANISATION_ID, "Issue_Resolution_All"); //$NON-NLS-1$

	private void prepareIssueProperties(){
		ISSUE_TYPE_ALL.getName().setText(Locale.ENGLISH.getLanguage(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.issueType.all.text")); //$NON-NLS-1$
		ISSUE_SEVERITY_TYPE_ALL.getIssueSeverityTypeText().setText(Locale.ENGLISH.getLanguage(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.severity.all.text")); //$NON-NLS-1$
		ISSUE_PRIORITY_ALL.getIssuePriorityText().setText(Locale.ENGLISH.getLanguage(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.priority.all.text")); //$NON-NLS-1$
		ISSUE_RESOLUTION_ALL.getName().setText(Locale.ENGLISH.getLanguage(), Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.resolution.all.text")); //$NON-NLS-1$
	}

	private static final String[] FETCH_GROUPS_ISSUE_TYPE = {
		IssueType.FETCH_GROUP_NAME,
		IssueType.FETCH_GROUP_ISSUE_PRIORITIES,
		IssueType.FETCH_GROUP_ISSUE_RESOLUTIONS,
		IssueType.FETCH_GROUP_ISSUE_SEVERITY_TYPES,
		IssueSeverityType.FETCH_GROUP_NAME,
		IssuePriority.FETCH_GROUP_NAME,
		IssueResolution.FETCH_GROUP_NAME, FetchPlan.DEFAULT
		};
	private IssueLabelProvider labelProvider = new IssueLabelProvider();

	private void loadProperties(){
		Job loadJob = new Job(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePropertyRelated.job.loadingProperties.text")) { //$NON-NLS-1$
			@Override
			protected IStatus run(final ProgressMonitor monitor) {
				synchronized (mutex) {
//					loadJobRunning = true;
					logger.debug("Load Job running...."); //$NON-NLS-1$
				}
				try {
					try {
						final List<IssueType> issueTypeList = new ArrayList<IssueType>(IssueTypeDAO.sharedInstance().getAllIssueTypes(FETCH_GROUPS_ISSUE_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
						final List<IssuePriority> issuePriorityList = new ArrayList<IssuePriority>();
						final List<IssueSeverityType> issueSeverityTypeList = new ArrayList<IssueSeverityType>();
						final List<IssueResolution> issueResolutionList = new ArrayList<IssueResolution>();

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								issueTypeCombo.removeAll();
								issueTypeCombo.addElement(ISSUE_TYPE_ALL);
								for (IssueType issueType : issueTypeList) {
									issueTypeCombo.addElement(issueType);
									for (IssuePriority p : issueType.getIssuePriorities())
										issuePriorityList.add(p);
									for (IssueSeverityType s : issueType.getIssueSeverityTypes())
										issueSeverityTypeList.add(s);
									for (IssueResolution r : issueType.getIssueResolutions())
										issueResolutionList.add(r);
								}
								if (selectedIssueType == null)
									selectedIssueType = ISSUE_TYPE_ALL;

								issueTypeCombo.setSelection(selectedIssueType);

								/**************************************************/
								ISSUE_TYPE_ALL.getIssuePriorities().addAll(issuePriorityList);
								ISSUE_TYPE_ALL.getIssueSeverityTypes().addAll(issueSeverityTypeList);
								ISSUE_TYPE_ALL.getIssueResolutions().addAll(issueResolutionList);
								/**************************************************/

								issueSeverityCombo.removeAll();
								issueSeverityCombo.addElement(ISSUE_SEVERITY_TYPE_ALL);
								for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
									if (!issueSeverityCombo.contains(is))
										issueSeverityCombo.addElement(is);
								}
								if (selectedIssueSeverityType != null)
								{
									issueSeverityCombo.setSelection(selectedIssueSeverityType);
									selectedIssueSeverityType = null;
								}
								else
									issueSeverityCombo.setSelection(ISSUE_SEVERITY_TYPE_ALL);

								issuePriorityCombo.removeAll();
								issuePriorityCombo.addElement(ISSUE_PRIORITY_ALL);
								for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
									if (!issuePriorityCombo.contains(ip))
										issuePriorityCombo.addElement(ip);
								}
								if (selectedIssuePriority != null)
								{
									issuePriorityCombo.setSelection(selectedIssuePriority);
									selectedIssuePriority = null;
								}
								else
									issuePriorityCombo.setSelection(ISSUE_PRIORITY_ALL);

								issueResolutionCombo.removeAll();
								issueResolutionCombo.addElement(ISSUE_RESOLUTION_ALL);
								for (IssueResolution ir : selectedIssueType.getIssueResolutions()) {
									if (!issueResolutionCombo.contains(ir))
										issueResolutionCombo.addElement(ir);
								}
								if (selectedIssueResolution != null)
								{
									issueResolutionCombo.setSelection(selectedIssueResolution);
									selectedIssueResolution = null;
								}
								else
									issueResolutionCombo.setSelection(ISSUE_RESOLUTION_ALL);

								selectedIssueType = null;
//								issueTypeCombo.selectElement(ISSUE_TYPE_ALL);
//								issueSeverityCombo.selectElement(ISSUE_SEVERITY_TYPE_ALL);
//								issuePriorityCombo.selectElement(ISSUE_PRIORITY_ALL);
//								issueResolutionCombo.selectElement(ISSUE_RESOLUTION_ALL);
							}
						});
					}catch (Exception e1) {
						ExceptionHandlerRegistry.asyncHandleException(e1);
						throw new RuntimeException(e1);
					}

					return Status.OK_STATUS;
				} finally {
					synchronized (mutex) {
//						if (storedIssueQueryRunnable != null) {
//						logger.debug("Running storedIssueQueryRunnable from load Job.");
//						storedIssueQueryRunnable.run(monitor);
//						storedIssueQueryRunnable = null;
//						}
//						loadJobRunning = false;
						logger.debug("Load Job finished."); //$NON-NLS-1$
					}
				}
			}
		};

		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(4);
		fieldNames.add(IssueQuery.FieldName.issueTypeID);
		fieldNames.add(IssueQuery.FieldName.issueSeverityTypeID);
		fieldNames.add(IssueQuery.FieldName.issuePriorityID);
		fieldNames.add(IssueQuery.FieldName.issueResolutionID);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositePropertyRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
}