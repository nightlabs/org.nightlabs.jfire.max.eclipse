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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
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
	private UserID selectedReportID;
	private User selectedAssignee;
	private UserID selectedAssigneeID;

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
		GridLayout gridLayout = new GridLayout(4, false);
		parent.setLayout(gridLayout);
		parent.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label rLabel = new Label(parent, SWT.NONE);
		rLabel.setText("Reporter: ");
		allReporterButton = new Button(parent, SWT.CHECK);
		allReporterButton.setText("All");
		allReporterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean selectAll = ((Button) e.getSource()).getSelection();
				reporterText.setEnabled(!selectAll);
				reporterButton.setEnabled(!selectAll);
				
				if (selectAll) {
					setValueIntentionally(true);
					reporterText.setText("");
					selectedReporter = null;
					getQuery().setReporterID(null);
					setValueIntentionally(false);
				}
				else {
					getQuery().setReporterID(selectedReportID);
				}
			}
		});

		reporterText = new Text(parent, getBorderStyle());
		reporterText.setEditable(false);
		reporterText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		reporterButton = new Button(parent, SWT.PUSH);
		reporterButton.setText("...");

		reporterButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), "");
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedReporter = userSearchDialog.getSelectedUser();
					selectedReportID = (UserID) JDOHelper.getObjectId(selectedReporter);
					reporterText.setText(selectedReporter.getName());

					getQuery().setReporterID(selectedReportID);
				}//if
			}
		});
		
		allReporterButton.setSelection(true);
		reporterButton.setEnabled(false);
		reporterText.setEnabled(false);

		Label aLabel = new Label(parent, SWT.NONE);
		aLabel.setText("Assignee: ");
		
		allAssigneeButton = new Button(parent, SWT.CHECK);
		allAssigneeButton.setText("All");
		allAssigneeButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final boolean selectAll = ((Button) e.getSource()).getSelection();
				assigneeText.setEnabled(!selectAll);
				assigneeButton.setEnabled(!selectAll);
				
				if (selectAll) {
					setValueIntentionally(true);
					assigneeText.setText("");
					selectedAssignee = null;
					getQuery().setAssigneeID(null);
					setValueIntentionally(false);
				}
				else {
					getQuery().setAssigneeID(selectedAssigneeID);
				}
			}
		});
		allAssigneeButton.setSelection(true);

		assigneeText = new Text(parent, getBorderStyle());
		assigneeText.setEditable(false);
		assigneeText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		assigneeButton = new Button(parent, SWT.PUSH);
		assigneeButton.setText("...");

		assigneeButton.addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				UserSearchDialog userSearchDialog = new UserSearchDialog(getShell(), "");
				int returnCode = userSearchDialog.open();
				if (returnCode == Window.OK) {
					selectedAssignee = userSearchDialog.getSelectedUser();
					selectedAssigneeID = (UserID) JDOHelper.getObjectId(selectedAssignee);
					assigneeText.setText(selectedAssignee.getName());
					
					getQuery().setAssigneeID(selectedAssigneeID);
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
				boolean active = isValueIntentionallySet();
				boolean sectionActive = selectedAssignee != null || selectedReporter != null;
				setSearchSectionActive(sectionActive);
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
					}
					assigneeButton.setEnabled(!active);
					assigneeText.setEnabled(!active);
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
					}
					reporterText.setEnabled(! active);
					reporterButton.setEnabled(! active);
				}
			} // for (FieldChangeCarrier changedField : event.getChangedFields())
		} // changedQuery != null		
	}
	
}
