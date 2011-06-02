package org.nightlabs.jfire.auth.ui.wizard;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.jdo.FetchPlan;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jdo.NLJDOHelper;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchComposite;
import org.nightlabs.jfire.base.ui.person.search.PersonSearchUseCaseConstants;
import org.nightlabs.jfire.base.ui.security.UserTable;
import org.nightlabs.jfire.person.Person;
import org.nightlabs.jfire.prop.id.PropertySetID;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.jfire.security.User;
import org.nightlabs.jfire.security.dao.UserDAO;
import org.nightlabs.jfire.security.id.UserID;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemManagerRemote;
import org.nightlabs.progress.ProgressMonitor;

/**
 * This page represents options for export of user data to some {@link UserManagementSystem}. It could contributed to {@link ImportExportWizard}
 * by specific {@link ISynchronizationPerformerHop} implementations or just added to wizard as is.
 * 
 * Export options are: export all data (which means getting all related JFire object IDs by {@link UserManagementSystemManagerRemote#getAllUserManagementSystemRelatedEntityIDs()}),
 * export selected {@link User} objects, export selected {@link Person} objects.
 * 
 * {@link User}s could are selected using {@link UserTable}, {@link Person}s are selected using {@link PersonSearchComposite}.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class GenericExportWizardPage extends WizardHopPage{

	private Button exportAllButton;
	private Button exportSelectedUsers;
	private Button exportSelectedPersons;
	
	private StackLayout tableParentLayout;
	private UserTable userTable;
	private PersonSearchComposite personSearchComposite;
	
	private Collection<Object> selectedObjectsIDs = new HashSet<Object>();
	private boolean shouldExportAll = true;
	
	private boolean canFinish = false;

	/**
	 * Default constructor
	 */
	public GenericExportWizardPage() {
		super(GenericExportWizardPage.class.getName(), "Export JFire objects to LDAP server");
		setDescription("Select JFire objects for export");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite wizardParent) {
		
		Composite parent = new Composite(wizardParent, SWT.NONE);
		parent.setLayout(new GridLayout(1, false));
		
		exportAllButton = new Button(parent, SWT.RADIO);
		exportAllButton.setText("Export all");
		exportAllButton.setSelection(true);
		exportAllButton.setToolTipText("Export all user management system related JFire entities");
		exportAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (exportAllButton.getSelection()){
					shouldExportAll = true;
					if (userTable != null){
						userTable.setEnabled(false);
					}
					if (personSearchComposite != null){
						setEnabledRecursively(personSearchComposite, false);
					}
					updateStatus(null);
				}
			}
		});
		
		exportSelectedUsers = new Button(parent, SWT.RADIO);
		exportSelectedUsers.setText("Export selected Users only");
		exportSelectedUsers.setToolTipText("Export only User objects selected in a table below");
		exportSelectedUsers.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (exportSelectedUsers.getSelection() && userTable != null){
					shouldExportAll = false;
					if (!(userTable.getTableViewer().getInput() instanceof Collection)){
						loadUserTable();
					}
					userTable.setEnabled(true);
					tableParentLayout.topControl = userTable;
					userTable.getParent().layout();
					updateStatus(null);
				}
			}
		});

		exportSelectedPersons = new Button(parent, SWT.RADIO);
		exportSelectedPersons.setText("Export selected Persons only");
		exportSelectedPersons.setToolTipText("Export only Person objects found and selected in search form result table below");
		exportSelectedPersons.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (exportSelectedPersons.getSelection() && personSearchComposite != null){
					shouldExportAll = false;
					setEnabledRecursively(personSearchComposite, true);
					tableParentLayout.topControl = personSearchComposite;
					personSearchComposite.getParent().layout();
					updateStatus(null);
				}
			}
		});

		Composite tableParent = new Composite(parent, SWT.NONE);
		tableParent.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableParentLayout = new StackLayout();
		tableParent.setLayout(tableParentLayout);
		
		userTable = new UserTable(tableParent, SWT.MULTI, true, AbstractTableComposite.DEFAULT_STYLE_MULTI_BORDER);
		userTable.setLinesVisible(true);
		userTable.setHeaderVisible(true);
		userTable.getTableViewer().setSorter(new ViewerSorter());
		userTable.setEnabled(false);
		userTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent e) {
				selectedObjectsIDs.clear();
				for(User user : userTable.getSelectedElements()){
					selectedObjectsIDs.add(UserID.create(user.getOrganisationID(), user.getUserID()));
				}
				if (selectedObjectsIDs.isEmpty()){
					updateStatus("Please select at least on User!");
				}else{
					canFinish = true;
					updateStatus(null);
				}
			}
		});
		tableParentLayout.topControl = userTable;
		
		personSearchComposite = new PersonSearchComposite(tableParent, SWT.NONE, "", PersonSearchUseCaseConstants.USE_CASE_ID_DEFAULT);
		personSearchComposite.createSearchButton(personSearchComposite.getButtonBar());
		personSearchComposite.getResultViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				selectedObjectsIDs.clear();
				for (Person person : personSearchComposite.getResultViewer().getSelectedElements()){
					selectedObjectsIDs.add(PropertySetID.create(person.getOrganisationID(), person.getPropertySetID()));
				}
				if (selectedObjectsIDs.isEmpty()){
					updateStatus("Please find and select at least on Person!");
				}else{
					canFinish = true;
					updateStatus(null);
				}
			}
		});
		setEnabledRecursively(personSearchComposite, false);

		
		setControl(parent);
		return parent;
	}
	
	@Override
	public boolean canBeLastPage() {
		if (shouldExportAll){
			return true;
		}
		return canFinish;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPageComplete() {
		if (exportAllButton != null && !exportAllButton.isDisposed() && exportAllButton.getSelection()){
			return true;
		}else if (exportSelectedUsers != null && !exportSelectedUsers.isDisposed() && exportSelectedUsers.getSelection()
				&& userTable != null && !userTable.isDisposed()
				&& userTable.getSelection() != null && !userTable.getSelection().isEmpty()){
			return true;
		}else if (exportSelectedPersons != null && !exportSelectedPersons.isDisposed() && exportSelectedPersons.getSelection()
				&& personSearchComposite != null && !personSearchComposite.isDisposed()
				&& personSearchComposite.getResultViewer() != null 
				&& personSearchComposite.getResultViewer().getFirstSelectedElement() instanceof Person){
			return true;
		}
		return false;
	}
	
	/**
	 * Check whether everything should be exported (corresponding option was selected)
	 * 
	 * @return <code>true</code> if all related entities should be exported
	 */
	public boolean shouldExportAll() {
		return shouldExportAll;
	}
	
	/**
	 * Get object IDs for export
	 * 
	 * @return {@link Collection} of selected objects' IDs
	 */
	public Collection<Object> getSelectedObjectIDs(){
		return selectedObjectsIDs;
	}

	private static final String[] USER_TYPES = new String[]{User.USER_TYPE_USER};
	private static final String[] USER_FETCH_GROUPS = new String[] {FetchPlan.DEFAULT};
	
	private void loadUserTable(){
		userTable.setInput("loading users...");
		Job job = new Job("loading users...") {
			@Override
			protected IStatus run(ProgressMonitor monitor){
				try {
					String organisationID = GlobalSecurityReflector.sharedInstance().getUserDescriptor().getOrganisationID();
					final List<User> users = UserDAO.sharedInstance().getUsers(
							organisationID, USER_TYPES, USER_FETCH_GROUPS, NLJDOHelper.MAX_FETCH_DEPTH_NO_LIMIT, monitor);
					if (!getContainer().getShell().isDisposed()){
						getContainer().getShell().getDisplay().asyncExec(new Runnable(){
							public void run() {
								userTable.setInput(users);
							}
						});
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
	
	private static void setEnabledRecursively(Composite parent, boolean enabled){
		parent.setEnabled(enabled);
		for (Control c : parent.getChildren()){
			if (c instanceof Composite){
				setEnabledRecursively((Composite) c, enabled);
			}else{
				c.setEnabled(enabled);
			}
		}
	}
}
