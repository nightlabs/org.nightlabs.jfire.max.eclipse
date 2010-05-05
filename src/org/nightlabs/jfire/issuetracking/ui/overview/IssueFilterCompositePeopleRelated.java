package org.nightlabs.jfire.issuetracking.ui.overview;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.FetchPlan;
import javax.jdo.JDOHelper;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.issuetracking.ui.resource.Messages;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.progress.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositePeopleRelated
	extends AbstractQueryFilterComposite<IssueQuery>
{
//	private User selectedReporter;
//	private UserID selectedReportID;
//	private User selectedAssignee;
//	private UserID selectedAssigneeID;

	private static final Logger LOGGER = LoggerFactory.getLogger(IssueFilterCompositePeopleRelated.class);

	private Text reporterText;
	private Text assigneeText;

	private Button allReporterButton;
	private Button reporterButton;
	private Button allAssigneeButton;
	private Button assigneeButton;

	boolean isAllReporterButtonActive = true;
	boolean isAllAssigneeButtonActive = true;

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public IssueFilterCompositePeopleRelated(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode,
			QueryProvider<? super IssueQuery> queryProvider)
	{
		super(parent, style, layoutMode, layoutDataMode, queryProvider);
		createComposite();
	}

	public IssueFilterCompositePeopleRelated(Composite parent, int style,
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
		GridLayout gridLayout = new GridLayout(8, false);
		setLayout(gridLayout);
		setLayoutData(new GridData(GridData.FILL_BOTH));

		Label rLabel = new Label(this, SWT.NONE);
		rLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePeopleRelated.label.reporter.text")); //$NON-NLS-1$
		allReporterButton = new Button(this, SWT.CHECK);
		allReporterButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePeopleRelated.button.allreporter.text")); //$NON-NLS-1$
		allReporterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean selectAll = ((Button) e.getSource()).getSelection();
				getQuery().setFieldEnabled(IssueQuery.FieldName.reporterID, ! selectAll);
//				reporterText.setEnabled(!selectAll);
//				reporterButton.setEnabled(!selectAll);
//
//				if (selectAll) {
//					setValueIntentionally(true);
//					reporterText.setText("");
//					selectedReporter = null;
//					getQuery().setReporterID(null);
//					setValueIntentionally(false);
//				}
//				else {
//					getQuery().setReporterID(selectedReportID);
//				}
			}
		});

		reporterText = new Text(this, getBorderStyle());
		reporterText.setEditable(false);
		reporterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reporterButton = new Button(this, SWT.PUSH);
		reporterButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePeopleRelated.button.reporter.text")); //$NON-NLS-1$

		reporterButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e) {
				final UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), ""); //$NON-NLS-1$
				if (Window.OK == userSearchDialog.open())
				{
					final User selectedUser = userSearchDialog.getSelectedUser();
					if (selectedUser != null) {
						final UserID reporterID = (UserID) JDOHelper.getObjectId(selectedUser);
						reporterText.setText(selectedUser.getName());
						getQuery().setReporterID(reporterID);
					}
				}//if
			}
		});

		allReporterButton.setSelection(true);
		reporterButton.setEnabled(false);
		reporterText.setEnabled(false);

		Label aLabel = new Label(this, SWT.NONE);
		aLabel.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePeopleRelated.label.assignee.text")); //$NON-NLS-1$

		allAssigneeButton = new Button(this, SWT.CHECK);
		allAssigneeButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePeopleRelated.button.allassignee.text")); //$NON-NLS-1$
		allAssigneeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean selectAll = ((Button) e.getSource()).getSelection();
				getQuery().setFieldEnabled(IssueQuery.FieldName.assigneeID, ! selectAll);
			}
		});
		allAssigneeButton.setSelection(true);

		assigneeText = new Text(this, getBorderStyle());
		assigneeText.setEditable(false);
		assigneeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		assigneeButton = new Button(this, SWT.PUSH);
		assigneeButton.setText(Messages.getString("org.nightlabs.jfire.issuetracking.ui.overview.IssueFilterCompositePeopleRelated.button.assignee.text")); //$NON-NLS-1$

		assigneeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				final UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), ""); //$NON-NLS-1$
				if (Window.OK == userSearchDialog.open())
				{
					final User selectedAssignee = userSearchDialog.getSelectedUser();
					if (selectedAssignee != null) {
						final UserID selectedAssigneeID = (UserID) JDOHelper.getObjectId(selectedAssignee);
						assigneeText.setText(selectedAssignee.getName());
						getQuery().setAssigneeID(selectedAssigneeID);
					}
				}//if
			}
		});
		assigneeButton.setEnabled(false);
		assigneeText.setEnabled(false);
	}

	@Override
	protected void updateUI(final QueryEvent event, final List<FieldChangeCarrier> changedFields)
	{
		for (final FieldChangeCarrier changedField : changedFields)
		{
			if (IssueQuery.FieldName.assigneeID.equals(changedField.getPropertyName()))
			{
				// old
//				final UserID newAssigneeID = (UserID) changedField.getNewValue();
//				if (newAssigneeID == null)
//				{
//					assigneeText.setText(""); //$NON-NLS-1$
//					assigneeButton.setEnabled(true);
//					assigneeText.setEnabled(false);
//					allAssigneeButton.setSelection(true);
//				}
//				else
//				{
//					final User newAssignee = UserDAO.sharedInstance().getUser(newAssigneeID,
//							new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							new NullProgressMonitor());
//
//					assigneeText.setText(newAssignee.getName());
//				}

				// begin new
				final UserID tmpAssigneeID = (UserID) changedField.getNewValue();
				if (tmpAssigneeID != null)
				{
					final User selectedAssignee = UserDAO.sharedInstance().getUser(tmpAssigneeID,
						new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
					if (selectedAssignee != null) {
						assigneeText.setText(selectedAssignee.getName());
						setSearchSectionActiveBySelection(true);
					}
				}
				// end new
			}
			else if (getEnableFieldName(IssueQuery.FieldName.assigneeID).equals(changedField.getPropertyName()))
			{
				boolean active = (Boolean) changedField.getNewValue();
				assigneeButton.setEnabled(active);
				assigneeText.setEnabled(active);
				allAssigneeButton.setSelection(!active);

				// old
//				setSearchSectionActive(isActive);
//				if (!isActive) {
//					assigneeText.setText(""); //$NON-NLS-1$
//					getQuery().setAssigneeID(null);
//				}

				// begin new
				isAllAssigneeButtonActive = !active;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Selection state of allAssigneeButton is " + isAllAssigneeButtonActive); //$NON-NLS-1$
					LOGGER.debug("Selection state of allReporterButton is " + isAllReporterButtonActive); //$NON-NLS-1$
				}
				if (isAllAssigneeButtonActive && isAllReporterButtonActive)
					setSearchSectionActiveBySelection(false);
				else
					setSearchSectionActiveBySelection(true);
				if (!active)
					getQuery().setAssigneeID(null);
				// end new
			}
			else if (IssueQuery.FieldName.reporterID.equals(changedField.getPropertyName()))
			{
				final UserID tmpReporterID = (UserID) changedField.getNewValue();

				// old
//				if (tmpReporterID == null)
//				{
//					reporterText.setText(""); //$NON-NLS-1$
//					reporterButton.setEnabled(true);
//					reporterText.setEnabled(false);
//					allReporterButton.setSelection(true);
//				}
//				else
//				{
//					final User selectedReporter = UserDAO.sharedInstance().getUser(tmpReporterID,
//							new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
//							new NullProgressMonitor());
//
//					reporterText.setText(selectedReporter.getName());
//				}

				// begin new
				if (tmpReporterID != null)
				{
					final User selectedReporter = UserDAO.sharedInstance().getUser(tmpReporterID,
						new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
						new NullProgressMonitor());
					if (selectedReporter != null) {
						reporterText.setText(selectedReporter.getName());
						setSearchSectionActiveBySelection(true);
					}
				}
				// end new
			}
			else if (getEnableFieldName(IssueQuery.FieldName.reporterID).equals(changedField.getPropertyName()))
			{
				final boolean active = (Boolean) changedField.getNewValue();
				reporterText.setEnabled(active);
				reporterButton.setEnabled(active);
				allReporterButton.setSelection(!active);

				// old
//				setSearchSectionActive(active);
//				if (!active) {
//					reporterText.setText(""); //$NON-NLS-1$
//					getQuery().setReporterID(null);
//				}

				// begin new
				isAllReporterButtonActive = !active;
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Selection state of allAssigneeButton is " + isAllAssigneeButtonActive); //$NON-NLS-1$
					LOGGER.debug("Selection state of allReporterButton is " + isAllReporterButtonActive); //$NON-NLS-1$
				}
				if (isAllAssigneeButtonActive && isAllReporterButtonActive)
					setSearchSectionActiveBySelection(false);
				else
					setSearchSectionActiveBySelection(true);
				if (!active)
					getQuery().setReporterID(null);
				// end new

			}
		}
	}

	private static final Set<String> fieldNames;
	static
	{
		fieldNames = new HashSet<String>(2);
		fieldNames.add(IssueQuery.FieldName.assigneeID);
		fieldNames.add(IssueQuery.FieldName.reporterID);
	}

	@Override
	protected Set<String> getFieldNames()
	{
		return fieldNames;
	}

	/**
	 * Group ID for storing active states in the query.
	 */
	public static final String FILTER_GROUP_ID = "IssueFilterCompositePeopleRelated"; //$NON-NLS-1$

	@Override
	protected String getGroupID()
	{
		return FILTER_GROUP_ID;
	}
}
