package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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
import org.nightlabs.jfire.base.ui.overview.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.search.JDOQueryComposite;
import org.nightlabs.jfire.issue.Issue;
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
import org.nightlabs.jfire.organisation.Organisation;
import org.nightlabs.progress.NullProgressMonitor;
import org.nightlabs.progress.ProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun 
 *
 */
public class IssueSearchCompositeIssueRelated
extends JDOQueryComposite<Issue, IssueQuery>
{
	private static final Logger logger = Logger.getLogger(IssueSearchCompositeIssueRelated.class);
	private Object mutex = new Object();

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueSearchCompositeIssueRelated(AbstractQueryFilterComposite<Issue, IssueQuery> parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode)
	{
		super(parent, style, layoutMode, layoutDataMode);

		createComposite(this);
		prepareIssueProperties();
	}

	/**
	 * @param parent
	 * @param style
	 */
	public IssueSearchCompositeIssueRelated(AbstractQueryFilterComposite<Issue, IssueQuery> parent, int style)
	{
		super(parent, style);

		createComposite(this);
		prepareIssueProperties();
	}

	/*******Issue Related Section**********/
//	private SectionPart issueRelatedSection;

//	private Text issueIDText;

	private XComboComposite<IssueType> issueTypeCombo;
	private XComboComposite<IssueSeverityType> issueSeverityCombo;
	private XComboComposite<IssuePriority> issuePriorityCombo;
	private XComboComposite<IssueResolution> issueResolutionCombo;

	private IssueType selectedIssueType;
	private IssueSeverityType selectedIssueSeverityType;
	private IssuePriority selectedIssuePriority;
	private IssueResolution selectedIssueResolution;

	@Override
	protected void createComposite(Composite parent) {
		parent.setLayout(new GridLayout(3, false));

		XComposite issueTypeComposite = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		issueTypeComposite.getGridLayout().numColumns = 2;

		new Label(issueTypeComposite, SWT.NONE).setText("Issue Type: ");
		issueTypeCombo = new XComboComposite<IssueType>(issueTypeComposite, SWT.NONE);
		issueTypeCombo.setLabelProvider(labelProvider);
		issueTypeCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				selectedIssueType = issueTypeCombo.getSelectedElement();

				if (selectedIssueType.equals(ISSUE_TYPE_ALL)) {
					loadProperties();
					return;
				}

				issueSeverityCombo.removeAll();
				issueSeverityCombo.addElement(ISSUE_SEVERITY_TYPE_ALL);
				for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
					issueSeverityCombo.addElement(is);
				}
//				selectedIssueSeverityType = ISSUE_SEVERITY_TYPE_ALL;
				getQuery().setIssueSeverityTypeID(null); // null <=> ISSUE_SEVERITY_TYPE_ALL

				issuePriorityCombo.removeAll();
				issuePriorityCombo.addElement(ISSUE_PRIORITY_ALL);
				for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
					issuePriorityCombo.addElement(ip);
				}
				selectedIssuePriority = ISSUE_PRIORITY_ALL;
				getQuery().setIssuePriorityID(null); // null <=> ISSUE_PRIORITY_ALL
			}
		});

		new Label(issueTypeComposite, SWT.NONE).setText("Severity: ");
		issueSeverityCombo = new XComboComposite<IssueSeverityType>(issueTypeComposite, SWT.NONE);
		issueSeverityCombo.setLabelProvider(labelProvider);
		issueSeverityCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				if (isUpdatingUI())
					return;
				
				selectedIssueSeverityType = issueSeverityCombo.getSelectedElement();
				getQuery().setIssueSeverityTypeID((IssueSeverityTypeID) JDOHelper.getObjectId(selectedIssueSeverityType));
			}
		});

		new Label(issueTypeComposite, SWT.NONE).setText("Priority: ");
		issuePriorityCombo = new XComboComposite<IssuePriority>(issueTypeComposite, SWT.NONE);
		issuePriorityCombo.setLabelProvider(labelProvider);
		issuePriorityCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				if (isUpdatingUI())
					return;
				
				selectedIssuePriority = issuePriorityCombo.getSelectedElement();
			}
		});

		new Label(issueTypeComposite, SWT.NONE).setText("Resolution: ");
		issueResolutionCombo = new XComboComposite<IssueResolution>(issueTypeComposite, SWT.NONE);
		issueResolutionCombo.setLabelProvider(labelProvider);
		issueResolutionCombo.addSelectionChangedListener(new ISelectionChangedListener()
		{
			public void selectionChanged(SelectionChangedEvent e)
			{
				if (isUpdatingUI())
					return;

				selectedIssueResolution = issueResolutionCombo.getSelectedElement();
			}
		});

//		//-------------------------------------------------------------
//		reporterText.setEnabled(false);
//		reporterButton.setEnabled(false);
//		assigneeText.setEnabled(false);
//		assigneeButton.setEnabled(false);

		loadProperties();		
	}

	@Override
	protected void resetSearchQueryValues(IssueQuery query)
	{
		if (selectedIssueType.equals(ISSUE_TYPE_ALL))
		{
			query.setIssueTypeID(null);
		}
		else
		{
			query.setIssueTypeID((IssueTypeID) JDOHelper.getObjectId(selectedIssueType));
		}
		
		if (selectedIssueSeverityType.equals(ISSUE_SEVERITY_TYPE_ALL))
		{
			query.setIssueSeverityTypeID(null);
		}
		else
		{
			query.setIssueSeverityTypeID((IssueSeverityTypeID) JDOHelper.getObjectId(selectedIssueSeverityType));
		}

		if (selectedIssuePriority.equals(ISSUE_PRIORITY_ALL))
		{
			query.setIssuePriorityID(null);
		}
		else
		{
			query.setIssuePriorityID((IssuePriorityID)JDOHelper.getObjectId(selectedIssuePriority));
		}

		if (selectedIssueResolution.equals(ISSUE_RESOLUTION_ALL))
		{
			query.setIssueResolutionID(null);
		}
		else
		{
			query.setIssueResolutionID((IssueResolutionID)JDOHelper.getObjectId(selectedIssueResolution));
		}
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query)
	{
		query.setIssueTypeID(null);
		query.setIssueSeverityTypeID(null);
		query.setIssuePriorityID(null);
		query.setIssueResolutionID(null);
	}

	@Override
	protected void doUpdateUI(QueryEvent event)
	{
		boolean wholeQueryChanged = isWholeQueryChanged(event);
		final IssueQuery changedQuery = (IssueQuery) event.getChangedQuery();
		
		if (changedQuery == null)
		{
			selectedIssuePriority = ISSUE_PRIORITY_ALL;
			selectedIssueResolution = ISSUE_RESOLUTION_ALL;
			selectedIssueSeverityType = ISSUE_SEVERITY_TYPE_ALL;
			selectedIssueType = ISSUE_TYPE_ALL;
		}
		else
		{
			if (wholeQueryChanged || IssueQuery.PROPERTY_ISSUE_PRIORITY_ID.equals(event.getPropertyName()))
			{
				selectedIssuePriority = changedQuery.getIssuePriorityID() == null ? ISSUE_PRIORITY_ALL :
					IssuePriorityDAO.sharedInstance().getIssuePriority(
						changedQuery.getIssuePriorityID(), new String[] { IssuePriority.FETCH_GROUP_NAME }, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
					);
			}
			
			if (wholeQueryChanged || IssueQuery.PROPERTY_ISSUE_RESOLUTION_ID.equals(event.getPropertyName()))
			{
				selectedIssueResolution = changedQuery.getIssueResolutionID() == null ? ISSUE_RESOLUTION_ALL :
					IssueResolutionDAO.sharedInstance().getIssueResolution(
						changedQuery.getIssueResolutionID(), 
						new String[] { IssueResolution.FETCH_GROUP_THIS_ISSUE_RESOLUTION }, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						);
			}
			
			if (wholeQueryChanged || IssueQuery.PROPERTY_ISSUE_SEVERITY_TYPE_ID.equals(event.getPropertyName()))
			{
				selectedIssueSeverityType = changedQuery.getIssueSeverityTypeID() == null ? ISSUE_SEVERITY_TYPE_ALL :
					IssueSeverityTypeDAO.sharedInstance().getIssueSeverityType(
						changedQuery.getIssueSeverityTypeID(), 
						new String[] { IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE }, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						);
			}
			
			if (wholeQueryChanged || IssueQuery.PROPERTY_ISSUE_TYPE_ID.equals(event.getPropertyName()))
			{
				selectedIssueType = changedQuery.getIssueTypeID() == null ? ISSUE_TYPE_ALL :
					IssueTypeDAO.sharedInstance().getIssueType(
						changedQuery.getIssueTypeID(), 
						new String[] { IssueType.FETCH_GROUP_THIS_ISSUE_TYPE }, 
						NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, new NullProgressMonitor()
						);
			}
		}
		
		issuePriorityCombo.selectElement(selectedIssuePriority);
		issueResolutionCombo.selectElement(selectedIssueResolution);
		issueSeverityCombo.selectElement(selectedIssueSeverityType);
		issueTypeCombo.selectElement(selectedIssueType);
	}
	
	private static IssueType ISSUE_TYPE_ALL = new IssueType(Organisation.DEV_ORGANISATION_ID, "Issue_Type_All");
	private static IssueSeverityType ISSUE_SEVERITY_TYPE_ALL = new IssueSeverityType(Organisation.DEV_ORGANISATION_ID, "Issue_Severity_Type_All");
	private static IssuePriority ISSUE_PRIORITY_ALL = new IssuePriority(Organisation.DEV_ORGANISATION_ID, "Issue_Priority_All");
	private static IssueResolution ISSUE_RESOLUTION_ALL = new IssueResolution(Organisation.DEV_ORGANISATION_ID, "Issue_Resolution_All");

	private void prepareIssueProperties(){
		ISSUE_TYPE_ALL.getName().setText(Locale.ENGLISH.getLanguage(), "All");
		ISSUE_SEVERITY_TYPE_ALL.getIssueSeverityTypeText().setText(Locale.ENGLISH.getLanguage(), "All");
		ISSUE_PRIORITY_ALL.getIssuePriorityText().setText(Locale.ENGLISH.getLanguage(), "All");
		ISSUE_RESOLUTION_ALL.getName().setText(Locale.ENGLISH.getLanguage(), "All");
	}


	private static final String[] FETCH_GROUPS_ISSUE_TYPE = { IssueType.FETCH_GROUP_THIS_ISSUE_TYPE,
		IssueSeverityType.FETCH_GROUP_THIS_ISSUE_SEVERITY_TYPE, 
		IssuePriority.FETCH_GROUP_NAME, 
		IssueResolution.FETCH_GROUP_THIS_ISSUE_RESOLUTION, FetchPlan.DEFAULT };
	private IssueLabelProvider labelProvider = new IssueLabelProvider();
	// TODO: Why does this flag exist? It is never read, but only set. If it isn't used anymore then remove this please.
	private boolean loadJobRunning = false;

	private void loadProperties(){
		Job loadJob = new Job("Loading Issue Properties....") {
			@Override
			protected IStatus run(final ProgressMonitor monitor) {				
				synchronized (mutex) {
					loadJobRunning = true;
					logger.debug("Load Job running....");
				}
				try {
					try {
						final List<IssueType> issueTypeList = new ArrayList<IssueType>(IssueTypeDAO.sharedInstance().getIssueTypes(FETCH_GROUPS_ISSUE_TYPE, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor));
						final List<IssuePriority> issuePriorityList = new ArrayList<IssuePriority>();
						final List<IssueSeverityType> issueSeverityTypeList = new ArrayList<IssueSeverityType>();

						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								issueTypeCombo.removeAll();
								issueTypeCombo.addElement(ISSUE_TYPE_ALL);
								for (Iterator<IssueType> it = issueTypeList.iterator(); it.hasNext(); ) {
									IssueType issueType = it.next();
									issueTypeCombo.addElement(issueType);
									for (IssuePriority p : issueType.getIssuePriorities())
										issuePriorityList.add(p);
									for (IssueSeverityType s : issueType.getIssueSeverityTypes())
										issueSeverityTypeList.add(s);
								}
//								issueTypeCombo.selectElementByIndex(0);
//								selectedIssueType = ISSUE_TYPE_ALL;
								getQuery().setIssueTypeID(null); // null <=> ISSUE_TYPE_ALL

								/**************************************************/
								ISSUE_TYPE_ALL.getIssuePriorities().addAll(issuePriorityList);
								ISSUE_TYPE_ALL.getIssueSeverityTypes().addAll(issueSeverityTypeList);
								/**************************************************/

								issueSeverityCombo.removeAll();
								issueSeverityCombo.addElement(ISSUE_SEVERITY_TYPE_ALL);
								for (IssueSeverityType is : selectedIssueType.getIssueSeverityTypes()) {
									if (!issueSeverityCombo.contains(is))
										issueSeverityCombo.addElement(is);
								}
//								issueSeverityCombo.selectElementByIndex(0);
//								selectedIssueSeverityType = ISSUE_SEVERITY_TYPE_ALL;
								getQuery().setIssueSeverityTypeID(null); // null <=> ISSUE_SEVERITY_TYPE_ALL

								issuePriorityCombo.removeAll();
								issuePriorityCombo.addElement(ISSUE_PRIORITY_ALL);
								for (IssuePriority ip : selectedIssueType.getIssuePriorities()) {
									if (!issuePriorityCombo.contains(ip))
										issuePriorityCombo.addElement(ip);
								}
//								issuePriorityCombo.selectElementByIndex(0);
//								selectedIssuePriority = issuePriorityCombo.getSelectedElement();

								issueResolutionCombo.removeAll();
								issueResolutionCombo.addElement(ISSUE_RESOLUTION_ALL);
								for (IssueResolution ir : selectedIssueType.getIssueResolutions()) {
									if (!issueResolutionCombo.contains(ir))
										issueResolutionCombo.addElement(ir);
								}
//								issueResolutionCombo.selectElementByIndex(0);
//								selectedIssueResolution = ISSUE_RESOLUTION_ALL;
								getQuery().setIssueResolutionID(null); // null <=> ISSUE_RESOLUTION_ALL
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
//							logger.debug("Running storedIssueQueryRunnable from load Job.");
//							storedIssueQueryRunnable.run(monitor);
//							storedIssueQueryRunnable = null;
//						}
						loadJobRunning = false;
						logger.debug("Load Job finished.");
					}
				}
			} 
		};
		
		loadJob.setPriority(org.eclipse.core.runtime.jobs.Job.SHORT);
		loadJob.schedule();
	}
 
}
