package org.nightlabs.jfire.issuetracking.ui.issue.remind;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.composite.ListComposite;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutDataMode;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.issue.Issue;
import org.nightlabs.jfire.issue.IssueType;
import org.nightlabs.jfire.issuetracking.ui.IssueTrackingPlugin;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.progress.NullProgressMonitor;

/**
 * @author Chairat Kongarayawetchakun - chairat[at]nightlabs[dot]de
 */
public class RemindIssueUserWizardPage 
extends WizardHopPage
{
	//GUI
	private ListComposite<User> userList;
	private ListComposite<User> selectedList;
	
	//Used objects
	private java.util.List<IssueType> issueTypes;


	public RemindIssueUserWizardPage(Issue issue) {
		super(RemindIssueUserWizardPage.class.getName(), "Remind Issue", SharedImages.getWizardPageImageDescriptor(IssueTrackingPlugin.getDefault(), RemindIssueWizard.class));
		setDescription("Choose users.");
	}

	@Override
	public Control createPageContents(Composite parent) 
	{
		XComposite mainComposite = new XComposite(parent, SWT.NONE, LayoutMode.TOP_BOTTOM_WRAPPER, LayoutDataMode.GRID_DATA);
		mainComposite.getGridLayout().numColumns = 6;

		Label uLabel = new Label(mainComposite, SWT.NONE);
		uLabel.setText("Users");
		uLabel.setAlignment(SWT.CENTER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		uLabel.setLayoutData(gd);
		
		Label tmpLabel = new Label(mainComposite, SWT.NONE);
		tmpLabel.setText("");
		gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		tmpLabel.setLayoutData(gd);
		
		Label sLabel = new Label(mainComposite, SWT.NONE);
		sLabel.setText("Selected Users");
		sLabel.setAlignment(SWT.CENTER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		sLabel.setLayoutData(gd);

		userList = new ListComposite<User>(mainComposite, SWT.MULTI);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.minimumWidth = 30;
		userList.setLayoutData(gd);
		userList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((User)element).getName();
			}
		});
		
		Job job = new Job("Loading users...") {
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					final java.util.List<User> users = UserDAO.sharedInstance().getUsers(
							Login.getLogin().getOrganisationID(),
							(String[]) null,
							new String[] {
								User.FETCH_GROUP_NAME,
								User.FETCH_GROUP_USER_LOCAL
							},
							NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT,
							new NullProgressMonitor()
					);
					
					Display.getDefault().asyncExec(new Runnable()
					{
						public void run()
						{
							userList.setInput(users);
						}
					});
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.SHORT);
		job.schedule();
		
		XComposite buttonComposite = new XComposite(mainComposite, SWT.PUSH);
		buttonComposite.getGridLayout().numColumns = 1;
		buttonComposite.getGridData().grabExcessHorizontalSpace = false;
		
		Button addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText(" Add >> "); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER,
				GridData.VERTICAL_ALIGN_CENTER);
		gd.heightHint = 20;
		addButton.setLayoutData(gd);
		
		addButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				selectedList.addElements(userList.getSelectedElements());
				userList.removeAllSelected();
			}
		});
		
		Button removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText(" << Remove "); //$NON-NLS-1$
		gd = new GridData(GridData.HORIZONTAL_ALIGN_CENTER,
				GridData.VERTICAL_ALIGN_CENTER);
		gd.heightHint = 20;
		removeButton.setLayoutData(gd);
		
		removeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				userList.addElements(selectedList.getSelectedElements());
				selectedList.removeAllSelected();
			}
		});
		
		selectedList = new ListComposite<User>(mainComposite, SWT.MULTI);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.minimumWidth = 30;
		selectedList.setLayoutData(gd);
		selectedList.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((User)element).getName();
			}
		});
		
		return mainComposite;
	}

	@Override
	public void onShow() {
	}
	
	@Override
	public boolean canFlipToNextPage() {
		return isPageComplete();
	}
	
	private WizardHopPage optionalPage; 
	
	@Override
	public boolean isPageComplete() {
		boolean result = true;
		setErrorMessage(null);
		
		return result;
	}
}