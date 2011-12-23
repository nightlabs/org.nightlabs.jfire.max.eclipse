package org.nightlabs.jfire.auth.ui.editor;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.statushandlers.StatusManager;
import org.nightlabs.base.ui.editor.ToolBarSectionPart;
import org.nightlabs.base.ui.entity.editor.EntityEditorUtil;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.util.RCPUtil;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.auth.ui.UserManagementSystemTable;
import org.nightlabs.jfire.auth.ui.UserManagementSystemUIMappingRegistry;
import org.nightlabs.jfire.base.JFireEjb3Factory;
import org.nightlabs.jfire.security.GlobalSecurityReflector;
import org.nightlabs.jfire.security.UserSecurityGroup;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemManagerRemote;
import org.nightlabs.jfire.security.integration.UserManagementSystemSyncEvent;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfig;
import org.nightlabs.jfire.security.integration.UserSecurityGroupSyncConfigContainer;

/**
 * Section of {@link UserSecurityGroupEditorSyncConfigPage} for editing generic options:
 * select {@link UserManagementSystem}, configure if synchronization should be performed for selected {@link UserManagementSystem}
 * (via {@link UserSecurityGroupSyncConfig}), run synchronization manually for this {@link UserSecurityGroup} and selected {@link UserManagementSystem}s.
 * 
 * Does not work directly with edited {@link UserSecurityGroup} or {@link UserSecurityGroupSyncConfigContainer} instances 
 * but with {@link UserSecurityGroupSyncConfigGenericModel} which wraps around and performs model specific actions.
 *  
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class UserSecurityGroupSyncConfigGenericSection extends ToolBarSectionPart {

	/**
	 * Set to <code>true</code> while automatic refreshing of UI elements
	 * happens. Some listeners are enabled at this time.
	 */
	private boolean refreshing = false;

	private UserSecurityGroupSyncConfigGenericModel model;
	private UserManagementSystemTable userManagementSystemTable;
	
	public UserSecurityGroupSyncConfigGenericSection(IFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, "General");
		createContents(getSection(), page.getEditor().getToolkit());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setFormInput(Object input) {
		this.model = new UserSecurityGroupSyncConfigGenericModel((UserSecurityGroupSyncConfigContainer) input);
		return super.setFormInput(input);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void refresh() {
		refreshing = true;
		try{
			if (model != null){
				Collection<UserManagementSystem> selectedElements = userManagementSystemTable.getSelectedElements();
				selectedElements.retainAll(model.getAllRelatedUserManagementSystems());
				userManagementSystemTable.setUserManagementSystemsIDs(model.getAllRelatedUserManagementSystemIDs());
				userManagementSystemTable.reload();
				userManagementSystemTable.setSelectedElements(selectedElements);
			}
		}finally{
			refreshing = false;
		}
		super.refresh();
	}
	
	/**
	 * Adds given {@link ISelectionChangedListener} to underlying {@link UserManagementSystemTable}
	 * 
	 * @param listener Can't be <code>null</code>, {@link IllegalArgumentException} will be thrown in this case 
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener){
		if (listener == null){
			throw new IllegalArgumentException("ISelectionChangedListener can't be null!");
		}
		userManagementSystemTable.addSelectionChangedListener(listener);
	}
	
	/**
	 * Removes given {@link ISelectionChangedListener} from underlying {@link UserManagementSystemTable}
	 * 
	 * @param listener Can't be <code>null</code>, {@link IllegalArgumentException} will be thrown in this case 
	 */
	public void removeSelectionChangedListener(ISelectionChangedListener listener){
		if (listener == null){
			throw new IllegalArgumentException("ISelectionChangedListener can't be null!");
		}
		userManagementSystemTable.removeSelectionChangedListener(listener);
	}
	
	/**
	 * Get {@link UserSecurityGroupSyncConfig}s by selected {@link UserManagementSystem}s
	 * 
	 * @return a {@link Collection} of {@link UserSecurityGroupSyncConfig}s which correspond to selected {@link UserManagementSystem}s
	 */
	public Collection<UserSecurityGroupSyncConfig<?, ?>> getSelectedSyncConfigs(){
		if (!userManagementSystemTable.isDisposed()){
			return model.getSyncConfigsForUserManagementSystems(userManagementSystemTable.getSelectedElements());
		}
		return Collections.emptyList();
	}

	private void createContents(Section section, FormToolkit toolkit){
		
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite parent = EntityEditorUtil.createCompositeClient(toolkit, section, 1);
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		parentLayout.verticalSpacing = 10;
		parentLayout.marginTop = 10;
		parentLayout.marginRight = 20;
		
		Composite wrapper = toolkit.createComposite(parent, SWT.NONE);
		GridLayout gLayout = new GridLayout(5, false);
		gLayout.verticalSpacing = 10;
		gLayout.horizontalSpacing = 10;
		gLayout.marginWidth = 0;
		gLayout.marginHeight = 0;
		wrapper.setLayout(gLayout);
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gd.minimumWidth = 600;
		gd.widthHint = 600;
		wrapper.setLayoutData(gd);
		

		Button addSystemButton = toolkit.createButton(wrapper, "Add...", SWT.PUSH);
		addSystemButton.setToolTipText("Open selector with available UserManagementSystems to be added for sync configuration");
		gd = new GridData();
		gd.widthHint = 100;
		addSystemButton.setLayoutData(gd);
		addSystemButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				SelectUserManagementSystemDialog dialog = new SelectUserManagementSystemDialog(getSection().getShell(), null);
				if (Window.OK == dialog.open()){
					if (model != null){
						Collection<UserManagementSystem> selectedElements = dialog.getSelectedElements();
						for (UserManagementSystem userManagementSystem : selectedElements) {
							if (!model.syncConfigExistsForUserManagementSystem(userManagementSystem)){
								@SuppressWarnings("unchecked")
								IUserSecurityGroupSyncConfigDelegate delegate = UserManagementSystemUIMappingRegistry.sharedInstance().getUserGroupSyncConfigDelegate(
										(Class<? extends UserManagementSystemType<?>>) userManagementSystem.getType().getClass());
								if (delegate != null){
									model.addSyncConfig(
											delegate.createSyncConfig(model.getSyncConfigsContainer(), userManagementSystem));
								}
							}
						}
						if (!refreshing) {
							markDirty();
							refresh();
						}
					}
				}
			}
		});

		final Button removeSystemButton = toolkit.createButton(wrapper, "Remove", SWT.PUSH);
		removeSystemButton.setToolTipText("Remove selected UserManagementSystem(s) from this list, so synchronization of this group is no longer configured for them");
		gd = new GridData();
		gd.widthHint = 100;
		removeSystemButton.setLayoutData(gd);
		removeSystemButton.setEnabled(false);
		removeSystemButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (model != null){
					Collection<UserManagementSystem> selectedElements = userManagementSystemTable.getSelectedElements();
					model.removeSyncConfigsForUserManagementSystems(selectedElements);
					if (!refreshing) {
						markDirty();
						refresh();
					}
				}
			}
		});

		final Button editSystemButton = toolkit.createButton(wrapper, "Open editor...", SWT.PUSH);
		editSystemButton.setToolTipText("Open editor(s) for selected UserManagementSystem(s)");
		gd = new GridData();
		gd.widthHint = 100;
		editSystemButton.setLayoutData(gd);
		editSystemButton.setEnabled(false);
		editSystemButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent event) {
				Collection<UserManagementSystem> selectedElements = userManagementSystemTable.getSelectedElements();
				Throwable lastThrowable = null;
				for (UserManagementSystem userManagementSystem : selectedElements) {
					if (userManagementSystem != null){
						try {
							RCPUtil.openEditor(
									new UserManagementSystemEditorInput(
											userManagementSystem.getUserManagementSystemObjectID(), 
											(Class<? extends UserManagementSystemType<?>>) userManagementSystem.getType().getClass()), 
									UserManagementSystemEditor.EDITOR_ID);
						} catch (PartInitException e) {
							lastThrowable = e;
							StatusManager.getManager().handle(
									new Status(Status.ERROR, JFireAuthUIPlugin.PLUGIN_ID, e.getMessage(), e), 
									StatusManager.LOG);
						}
					}
				}
				if (lastThrowable != null){
					StatusManager.getManager().handle(
							new Status(
									Status.ERROR, JFireAuthUIPlugin.PLUGIN_ID, 
									"Exception(s) occured while opening editors for UserManagementSystems! See log for details, last one was: " + lastThrowable.getMessage(), lastThrowable), 
							StatusManager.SHOW);
				}
			}
		});

		final Button editSyncEnabledButton = toolkit.createButton(wrapper, "sync enabled?", SWT.CHECK);
		editSyncEnabledButton.setToolTipText("Enable/disable synchronization of this UserSecurityGroup to/from selected UserManagementSystem(s)");
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.HORIZONTAL_ALIGN_END);
		editSyncEnabledButton.setLayoutData(gd);
		editSyncEnabledButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (model != null){
					Collection<UserManagementSystem> selectedElements = userManagementSystemTable.getSelectedElements();
					model.setSyncEnabledForUserManagementSystems(selectedElements, editSyncEnabledButton.getSelection());
					updateSyncEnabledCheckBoxButton(editSyncEnabledButton, selectedElements);
					if (!refreshing) {
						markDirty();
					}
				}
			}
		});

		final Button startSyncButton = toolkit.createButton(wrapper, "Sync to all", SWT.PUSH);
		startSyncButton.setToolTipText("Performs synchronization for this UserSecurityGroup to/from selected UserManagementSystem(s) or all if non selected, sync should be enabled");
		gd = new GridData();
		gd.widthHint = 100;
		startSyncButton.setLayoutData(gd);
		startSyncButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (model == null){
					return;
				}
				final Collection<UserManagementSystem> systemsToSync = userManagementSystemTable.getSelectedElements();
				if (systemsToSync.isEmpty()){
					systemsToSync.addAll(userManagementSystemTable.getElements());
				}
				
				ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(getSection().getShell());
				progressMonitorDialog.setOpenOnRun(true);
				progressMonitorDialog.setCancelable(true);
				try {
					progressMonitorDialog.run(true, true, new IRunnableWithProgress() {
						
						@SuppressWarnings("unchecked")
						@Override
						public void run(IProgressMonitor monitor) throws InvocationTargetException,
								InterruptedException {
							monitor.beginTask(
									"Synchronizing this User security group with user management system(s)...", systemsToSync.size());
							try{
								Throwable lastThrowable = null;
								for (UserManagementSystem userManagementSystem : systemsToSync){
									if (monitor.isCanceled()){
										break;
									}
									if (!model.syncConfigExistsForUserManagementSystem(userManagementSystem)
											|| !model.getSyncConfigForUserManagementSystem(userManagementSystem).isSyncEnabled()){
										continue;
									}
									try {
										Class<? extends UserManagementSystemType<?>> umsTypeClass = (Class<? extends UserManagementSystemType<?>>) userManagementSystem.getType().getClass();
										IUserSecurityGroupSyncConfigDelegate delegate = UserManagementSystemUIMappingRegistry.sharedInstance().getUserGroupSyncConfigDelegate(umsTypeClass);
										
										if (delegate == null){
											StatusManager.getManager().handle(
													new Status(
															Status.ERROR, JFireAuthUIPlugin.PLUGIN_ID, 
															"Can't proceed with synchronization because IUserSecurityGroupSyncConfigDelegate is null for " + umsTypeClass.getName()), 
													StatusManager.LOG);
											continue;
										}
										
										UserManagementSystemManagerRemote remoteBean = JFireEjb3Factory.getRemoteBean(
												UserManagementSystemManagerRemote.class, GlobalSecurityReflector.sharedInstance().getInitialContextProperties());
										
										UserManagementSystemSyncEvent syncEvent = delegate.createSyncEvent(
												model.getSyncConfigForUserManagementSystem(userManagementSystem), userManagementSystem.isLeading());
										remoteBean.runLDAPServerSynchronization(
												userManagementSystem.getUserManagementSystemObjectID(), syncEvent);
										
									} catch (Exception e) {
										lastThrowable = e;
										StatusManager.getManager().handle(
												new Status(
														Status.ERROR, JFireAuthUIPlugin.PLUGIN_ID, lastThrowable.getMessage(), lastThrowable), 
												StatusManager.LOG);
									} finally {
										monitor.worked(1);
									}
								}
								if (lastThrowable != null){
									throw new InvocationTargetException(lastThrowable);
								}
							}finally{
								monitor.done();
							}
						}
					});
				} catch (InvocationTargetException e) {
					Throwable lastThrowable = e.getTargetException();
					StatusManager.getManager().handle(
							new Status(
									Status.ERROR, JFireAuthUIPlugin.PLUGIN_ID, 
									"Exception(s) occured while running synchronization! See log for details, last one was: " + lastThrowable.getMessage(), lastThrowable), 
							StatusManager.SHOW);
				} catch (InterruptedException e) {
					StatusManager.getManager().handle(
							new Status(
									Status.WARNING, JFireAuthUIPlugin.PLUGIN_ID, 
									e.getMessage(), e), 
							StatusManager.SHOW);
				}
			}
		});

		userManagementSystemTable = new UserManagementSystemTable(wrapper, AbstractTableComposite.DEFAULT_STYLE_SINGLE);
		userManagementSystemTable.setLinesVisible(false);
		userManagementSystemTable.setHeaderVisible(true);
		userManagementSystemTable.getTableViewer().setSorter(new ViewerSorter());
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;			
		gd.heightHint = 200;
		gd.minimumHeight = 100;
		userManagementSystemTable.setLayoutData(gd);
		userManagementSystemTable.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Collection<UserManagementSystem> selectedElements = userManagementSystemTable.getSelectedElements();
				boolean isEmpty = selectedElements.isEmpty();
				removeSystemButton.setEnabled(!isEmpty);
				editSystemButton.setEnabled(!isEmpty);
				startSyncButton.setText(isEmpty ? "Sync to all" : "Sync to selected");
				updateSyncEnabledCheckBoxButton(editSyncEnabledButton, selectedElements);
			}
		});
		
	}

	private void updateSyncEnabledCheckBoxButton(Button editSyncEnabledButton, Collection<UserManagementSystem> selectedElements){
		if (model != null){
			boolean syncEnabledAll = true;
			boolean syncDisabledAll = true;
			for (UserManagementSystem userManagementSystem : selectedElements) {
				UserSecurityGroupSyncConfig<?, ?> syncConfig = model.getSyncConfigForUserManagementSystem(userManagementSystem);
				if (syncConfig.isSyncEnabled()){
					syncDisabledAll = false;
				}else{
					syncEnabledAll = false;
				}
			}
			editSyncEnabledButton.setSelection(syncEnabledAll || (!syncEnabledAll && !syncDisabledAll));
			editSyncEnabledButton.setGrayed(!syncEnabledAll && !syncDisabledAll);
		}
	}
}
