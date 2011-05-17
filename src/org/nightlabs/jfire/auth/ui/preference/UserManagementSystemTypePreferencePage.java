package org.nightlabs.jfire.auth.ui.preference;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import javax.jdo.FetchPlan;
import javax.security.auth.login.LoginException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.nightlabs.base.ui.composite.XComposite;
import org.nightlabs.base.ui.composite.XComposite.LayoutMode;
import org.nightlabs.base.ui.job.Job;
import org.nightlabs.base.ui.language.I18nTextEditor;
import org.nightlabs.base.ui.language.I18nTextEditor.EditMode;
import org.nightlabs.base.ui.language.I18nTextEditorMultiLine;
import org.nightlabs.base.ui.progress.ProgressMonitorWrapper;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.base.ui.login.Login;
import org.nightlabs.jfire.security.dao.UserManagementSystemTypeDAO;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.progress.ProgressMonitor;

/**
 * Preference page for editing names and descriptions for existent {@link UserManagementSystemType} objects.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserManagementSystemTypePreferencePage extends PreferencePage implements IWorkbenchPreferencePage  {

	private final static String[] FETCH_GROUPS_USER_MANAGEMENT_SYSTEM_TYPES = new String[]{
		UserManagementSystemType.FETCH_GROUP_NAME,
		UserManagementSystemType.FETCH_GROUP_DESCRIPTION,
		FetchPlan.DEFAULT
	};
	
	private final static int FETCH_DEPTH_USER_MANAGEMENT_SYSTEM_TYPES = 3;

	
	private Job loadTypesJob;
	private UserManagementSystemTypeTable userManagementSystemTypeTable;

	private I18nTextEditor nameEditor;
	private I18nTextEditorMultiLine descriptionEditor;
	
	boolean isDirty = false;

	private UserManagementSystemType<?> selectedUserManagementSystemType;

	/**
	 * Default constructor
	 */
	public UserManagementSystemTypePreferencePage() {
		super();
		setDescription("Name and describe available user management system types");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createContents(Composite parent) {
		
		Composite content = new XComposite(parent, SWT.NONE, LayoutMode.TIGHT_WRAPPER);
		content.setLayout(new GridLayout(1, false));
		content.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		try {
			Login.getLogin(false).setForceLogin(true);
			Login.getLogin();
		} catch (LoginException e) {
			setValid(false);
			return content;
		}
		
		userManagementSystemTypeTable = new UserManagementSystemTypeTable(content, SWT.NONE, true, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
		userManagementSystemTypeTable.setLinesVisible(true);
		userManagementSystemTypeTable.setHeaderVisible(true);
		userManagementSystemTypeTable.addSelectionChangedListener(defaultSelectionListener);
		userManagementSystemTypeTable.getTableViewer().setSorter(new ViewerSorter());
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.minimumHeight = 100;
		gd.heightHint = 150;
		userManagementSystemTypeTable.setLayoutData(gd);
		
		userManagementSystemTypeTable.setInput("loading...");
		loadTypesJob = new Job("loading all user management system types...") {
			@Override
			protected IStatus run(ProgressMonitor monitor){
				try {
					final Collection<UserManagementSystemType<?>> allUserManagementSystemTypes = UserManagementSystemTypeDAO.sharedInstance().getAllUserManagementSystemTypes(
							FETCH_GROUPS_USER_MANAGEMENT_SYSTEM_TYPES, FETCH_DEPTH_USER_MANAGEMENT_SYSTEM_TYPES, monitor);
					Display display = userManagementSystemTypeTable.getDisplay();
					if (!display.isDisposed()) {
						final Job thisJob = this;
						display.asyncExec(new Runnable() {
							public void run() {
								if (loadTypesJob != thisJob){
									return;
								}
								userManagementSystemTypeTable.setInput(allUserManagementSystemTypes);
							}
						});
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				return Status.OK_STATUS;
			}
		};
		loadTypesJob.schedule();
		
		nameEditor = new I18nTextEditor(content, "Name:");
		nameEditor.setToolTipText("Name this user management system type in defferent languages");
		nameEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		nameEditor.setVisible(false);
		nameEditor.getText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				isDirty = true;
				setValid(nameEditor.getText() != null && !"".equals(nameEditor.getText().getText()));
			}
		});

		descriptionEditor = new I18nTextEditorMultiLine(content, "Description:");
		descriptionEditor.setToolTipText("Describe this user management system type in different languages");
		descriptionEditor.setVisibleLineCount(I18nTextEditorMultiLine.DEFAULT_LINECOUNT + 3);
		descriptionEditor.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.VERTICAL_ALIGN_BEGINNING));
		descriptionEditor.setVisible(false);
		descriptionEditor.getText().addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				isDirty = true;
			}
		});

		return content;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		if (Login.isLoggedIn() 
				&& selectedUserManagementSystemType != null
				&& nameEditor.getText() != null
				&& !"".equals(nameEditor.getText().getText())
				&& isDirty){
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(RCPUtil.getActiveShell());
			progressDialog.setOpenOnRun(true);
			try {
				progressDialog.run(false, false, new IRunnableWithProgress() {
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						if (selectedUserManagementSystemType.getName() != null){
							nameEditor.copyToOriginal();
						}else{
							selectedUserManagementSystemType.setName(nameEditor.getI18nText());
						}
						if (selectedUserManagementSystemType.getDescription() != null){
							descriptionEditor.copyToOriginal();
						}else{
							selectedUserManagementSystemType.setDescription(descriptionEditor.getI18nText());
						}
						selectedUserManagementSystemType = UserManagementSystemTypeDAO.sharedInstance().storeUserManagementSystemType(
								selectedUserManagementSystemType, true, 
								FETCH_GROUPS_USER_MANAGEMENT_SYSTEM_TYPES, 
								FETCH_DEPTH_USER_MANAGEMENT_SYSTEM_TYPES, new ProgressMonitorWrapper(monitor));
						isDirty = false;
					}
				});
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return super.performOk();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// do nothing
	}

	private ISelectionChangedListener defaultSelectionListener = new ISelectionChangedListener(){
		public void selectionChanged(SelectionChangedEvent event) {
			if (!event.getSelection().isEmpty() 
					&& event.getSelection() instanceof StructuredSelection) {
				StructuredSelection sel = (StructuredSelection) event.getSelection();
				if (sel.getFirstElement() instanceof UserManagementSystemType) {
					selectedUserManagementSystemType = (UserManagementSystemType<?>) sel.getFirstElement();
					
					nameEditor.setI18nText(selectedUserManagementSystemType.getName(), EditMode.BUFFERED);
					descriptionEditor.setI18nText(selectedUserManagementSystemType.getDescription(), EditMode.BUFFERED);
					
					nameEditor.setVisible(true);
					descriptionEditor.setVisible(true);
				}
			}else{
				nameEditor.setVisible(false);
				descriptionEditor.setVisible(false);
			}
		}
	};

}
