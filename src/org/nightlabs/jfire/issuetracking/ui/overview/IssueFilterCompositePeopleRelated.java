package org.nightlabs.jfire.issuetracking.ui.overview;

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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jdo.query.QueryEvent;
import org.nightlabs.jdo.query.QueryProvider;
import org.nightlabs.jdo.query.AbstractSearchQuery.FieldChangeCarrier;
import org.nightlabs.jfire.base.ui.search.AbstractQueryFilterComposite;
import org.nightlabs.jfire.base.ui.security.UserSearchDialog;
import org.nightlabs.jfire.issue.query.IssueQuery;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun <!-- chairat [AT] nightlabs [DOT] de -->
 *
 */
public class IssueFilterCompositePeopleRelated 
	extends AbstractQueryFilterComposite<IssueQuery> 
{	
	private User selectedReporter;
	private User selectedAssignee;

	private Text reporterText;
	private Text assigneeText;

	private Button allReporterButton;
	private Button reporterButton;
	private Button allAssigneeButton;
	private Button assigneeButton;

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
		createComposite(this);
	}

	public IssueFilterCompositePeopleRelated(Composite parent, int style,
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
		Group userGroup = new Group(parent, SWT.NONE);
		userGroup.setText("People Related");
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 10;
		userGroup.setLayout(gridLayout);
		userGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label rLabel = new Label(userGroup, SWT.NONE);
		rLabel.setText("Reporter: ");
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		rLabel.setLayoutData(gridData);

		XComposite rComposite = new XComposite(userGroup, SWT.NONE);
		rComposite.setLayout(new GridLayout(2, false));

		allReporterButton = new Button(rComposite, SWT.CHECK);
		allReporterButton.setText("All");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		allReporterButton.setLayoutData(gridData);
		allReporterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean selectAll = ((Button) e.getSource()).getSelection();
				reporterText.setEnabled(!selectAll);
				reporterButton.setEnabled(!selectAll);
			}
		});

		reporterText = new Text(rComposite, getBorderStyle());
		reporterText.setEditable(false);
		reporterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reporterButton = new Button(rComposite, SWT.PUSH);
		reporterButton.setText("...");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedReporter == null ? "" : selectedReporter.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
					getQuery().setReporterID((UserID) JDOHelper.getObjectId(selectedReporter));
					if (selectedReporter != null)
						reporterText.setText(selectedReporter.getName());
				}//if
			}
		});
		allReporterButton.setSelection(true);
		reporterButton.setEnabled(false);
		reporterText.setEnabled(false);

		Label aLabel = new Label(userGroup, SWT.NONE);
		aLabel.setText("Assignee: ");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalAlignment = GridData.CENTER;
		aLabel.setLayoutData(gridData);

		XComposite aComposite = new XComposite(userGroup, SWT.NONE);
		aComposite.setLayout(new GridLayout(2, false));

		allAssigneeButton = new Button(aComposite, SWT.CHECK);
		allAssigneeButton.setText("All");
		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		allAssigneeButton.setLayoutData(gridData);
		allAssigneeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean selectAll = ((Button) e.getSource()).getSelection();
				assigneeText.setEnabled(!selectAll);
				assigneeButton.setEnabled(!selectAll);
			}
		});
		allAssigneeButton.setSelection(true);

		assigneeText = new Text(aComposite, getBorderStyle());
		assigneeText.setEditable(false);
		assigneeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		assigneeButton = new Button(aComposite, SWT.PUSH);
		assigneeButton.setText("...");

		assigneeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), selectedAssignee == null ? "" : selectedAssignee.getUserID());
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedAssignee = userSearchDialog.getSelectedUser();
					getQuery().setAssigneeID((UserID) JDOHelper.getObjectId(selectedAssignee));
					if (selectedAssignee != null)
						assigneeText.setText(selectedAssignee.getName());
				}//if
			}
		});
		assigneeButton.setEnabled(false);
		assigneeText.setEnabled(false);
	}
	
	@Override
	protected void resetSearchQueryValues(IssueQuery query)
	{
		query.setReporterID((UserID)JDOHelper.getObjectId(selectedReporter));		
		query.setAssigneeID((UserID)JDOHelper.getObjectId(selectedAssignee));
	}

	@Override
	protected void unsetSearchQueryValues(IssueQuery query)
	{
		query.setAssigneeID(null);
		query.setReporterID(null);
	}

	@Override
	protected void updateUI(QueryEvent event)
	{
		if (event.getChangedQuery() == null)
		{
			selectedAssignee = null;
			allAssigneeButton.setSelection(true);
			assigneeButton.setEnabled(false);
			assigneeText.setEnabled(false);
			assigneeText.setText("");

			selectedReporter = null;
			allReporterButton.setSelection(true);
			reporterButton.setEnabled(false);
			reporterText.setEnabled(false);			
			reporterText.setText("");
		}
		else
		{ // there is a new Query -> the changedFieldList is not null!
			for (FieldChangeCarrier changedField : event.getChangedFields())
			{
				boolean all = isValueIntentionallySet();
				if (IssueQuery.PROPERTY_ASSIGNEE_ID.equals(changedField.getPropertyName()))
				{
					UserID tmpAssigneeID = (UserID) changedField.getNewValue();
					if (tmpAssigneeID == null)
					{
						assigneeText.setText("");
					}
					else
					{
						selectedAssignee = UserDAO.sharedInstance().getUser(tmpAssigneeID,
							new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
							new NullProgressMonitor());
						assigneeText.setText(selectedAssignee.getName());
					}
					all |= tmpAssigneeID == null;
					setSearchSectionActive(allAssigneeButton, all);
					assigneeButton.setEnabled(!all);
					assigneeText.setEnabled(!all);
				}
				
				if (IssueQuery.PROPERTY_REPORTER_ID.equals(changedField.getPropertyName()))
				{
					UserID tmpReporterID = (UserID) changedField.getNewValue();
					if (tmpReporterID == null)
					{
						reporterText.setText("");
					}
					else
					{
						selectedReporter = UserDAO.sharedInstance().getUser(tmpReporterID,
							new String[] { FetchPlan.DEFAULT }, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, 
							new NullProgressMonitor());
						reporterText.setText(selectedReporter.getName());
					}
					all |= tmpReporterID == null;
					allReporterButton.setSelection(all);
					reporterText.setEnabled(! all);
					reporterButton.setEnabled(! all);
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}
	
}
