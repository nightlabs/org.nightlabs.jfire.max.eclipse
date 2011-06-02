package org.nightlabs.jfire.auth.ui.wizard;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IWizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.auth.ui.UserManagementSystemUIMappingRegistry;
import org.nightlabs.jfire.auth.ui.actions.CreateUserManagementSystemAction;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;

/**
 * Page for selecting {@link UserManagementSystem} for import or export. Contributed to {@link ImportExportWizard} by {@link ImportExportWizardHop}.
 * 
 * New wizard pages are added dynamically to {@link ImportExportWizard} when selecting specific {@link UserManagementSystem}.
 * See {@link #syncPerfomerfSelectListener}.
 * 
 * If there's no created {@link UserManagementSystem} this page will offer to open a {@link CreateUserManagementSystemWizard} for 
 * creating new {@link UserManagementSystem} instance.
 * 
 * @author Denis Dudnik <deniska.dudnik[at]gmail{dot}com>
 *
 */
public class ImportExportConfigurationPage extends WizardHopPage{
	
	private ISynchronizationPerformerHop currentSynchronizationHop;
	private UserManagementSystem currentUserManagementSystem;
	
	private Composite mainWrapper;
	private Button importButton;
	private Button exportButton;
	private Label loadingLabel;
	private Label descriptionLabel;
	private UserManagementSystemTable userManagementSystemTable;

	private SyncDirection syncDirection = SyncDirection.IMPORT;
	
	/**
	 * Default constructor
	 */
	public ImportExportConfigurationPage()	{
		super(ImportExportConfigurationPage.class.getName(), "Select user management system", SharedImages.getWizardPageImageDescriptor(JFireAuthUIPlugin.sharedInstance(), ImportExportConfigurationPage.class));
		setDescription("Please select one of the available user management systems for import or export and proceed to the next step");
	}

	/**
	 * Sets selected {@link UserManagementSystem} and {@link SyncDirection} so this page could be omitted and shows the next one.
	 * 
	 * @param userManagementSystem {@link UserManagementSystem} selected for synchronization
	 * @param syncDirection Direction of synchronization, either import or export
	 */
	public void proceedToNextPage(UserManagementSystem userManagementSystem, SyncDirection syncDirection) {
		if (userManagementSystem != null && syncDirection != null){
			this.syncDirection = syncDirection;
			setSelectedUserManagementSystemInternal(userManagementSystem);
			getContainer().showPage(getNextPage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Control createPageContents(Composite parent) {
		mainWrapper = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 20;
		mainWrapper.setLayout(gridLayout);
		
		loadingLabel = new Label(mainWrapper, SWT.NONE);
		loadingLabel.setText("Loading all usermanagement systems...");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		loadingLabel.setLayoutData(gd);
		
		setControl(mainWrapper);
		return mainWrapper;
	}

	/**
	 * This page could not be the last one, it should be followed by specific {@link UserManagementSystem} page(s).
	 */
	@Override
	public boolean canBeLastPage() {
		return false;
	}
	
	/**
	 * Set loaded {@link UserManagementSystem} objects to display corresponding UI elements.
	 * 
	 * @param allUserManagementSystems
	 */
	public void setUserManagementSystems(List<UserManagementSystem> allUserManagementSystems) {
		if (mainWrapper == null){
			throw new IllegalStateException("This method should be called after wizard page contents were created!");
		}
		
		if (allUserManagementSystems != null && !allUserManagementSystems.isEmpty()){
			
			if (userManagementSystemTable == null){
				loadingLabel.dispose();
				
				userManagementSystemTable = new UserManagementSystemTable(mainWrapper, SWT.NONE, true, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
				userManagementSystemTable.setLinesVisible(true);
				userManagementSystemTable.setHeaderVisible(true);
				userManagementSystemTable.addSelectionChangedListener(syncPerfomerfSelectListener);
				userManagementSystemTable.getTableViewer().setSorter(new ViewerSorter());
				userManagementSystemTable.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				Composite buttonsWrapper = new Composite(mainWrapper, SWT.NONE);
				buttonsWrapper.setLayout(new GridLayout(1, false));
				buttonsWrapper.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_VERTICAL));
				
				importButton = new Button(buttonsWrapper, SWT.RADIO);
				importButton.setText("Import from selected system");
				importButton.setToolTipText("If selected next page will configure import options for fetching user data from selected system to JFire");
				importButton.setSelection(true);
				importButton.setEnabled(false);
				importButton.setImage(
						SharedImages.sharedInstance().getImage(JFireAuthUIPlugin.sharedInstance(), ImportExportConfigurationPage.class, "importButton", ImageDimension._16x16.toString(), ImageFormat.png));
				importButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (importButton.getSelection()){
							syncDirection = SyncDirection.IMPORT;
							descriptionLabel.setText("Import was selected! TODO: description");
							
							SelectionChangedEvent event = new SelectionChangedEvent(
									userManagementSystemTable, userManagementSystemTable.getSelection());
							syncPerfomerfSelectListener.selectionChanged(event);
						}
					}
				});
	
				exportButton = new Button(buttonsWrapper, SWT.RADIO);
				exportButton.setText("Export to selected system");
				exportButton.setToolTipText("If selected next page will configure export options for sending user data from JFire to selected system");
				exportButton.setEnabled(false);
				exportButton.setImage(
						SharedImages.sharedInstance().getImage(JFireAuthUIPlugin.sharedInstance(), ImportExportConfigurationPage.class, "exportButton", ImageDimension._16x16.toString(), ImageFormat.png));
				exportButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (exportButton.getSelection()){
							syncDirection = SyncDirection.EXPORT;
							descriptionLabel.setText("Export was selected! TODO: description");

							SelectionChangedEvent event = new SelectionChangedEvent(
									userManagementSystemTable, userManagementSystemTable.getSelection());
							syncPerfomerfSelectListener.selectionChanged(event);
						}
					}
				});
				
				Group descriptionGroup = new Group(buttonsWrapper, SWT.NONE);
				descriptionGroup.setText("Description");
				descriptionGroup.setLayout(new GridLayout(1, false));
				GridData gd = new GridData(GridData.FILL_VERTICAL | GridData.GRAB_VERTICAL);
				gd.verticalIndent = 20;
				gd.widthHint = 300;
				gd.minimumWidth = 300;
				descriptionGroup.setLayoutData(gd);
				
				descriptionLabel = new Label(descriptionGroup, SWT.WRAP);
				descriptionLabel.setText("Select user management system");
				descriptionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
			}
			
			userManagementSystemTable.setInput(allUserManagementSystems);

		}else{
			
			loadingLabel.setText("No User management system exist! Want to create one?");
			
			Button openCreationWizardButton = new Button(mainWrapper, SWT.PUSH);
			openCreationWizardButton.setText("Create new User management system...");
			openCreationWizardButton.setToolTipText("New wizard for user management system creation will open, current wizard will be closed");
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			gd.verticalIndent = 5;
			openCreationWizardButton.setLayoutData(gd);
			openCreationWizardButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					
					getWizard().performCancel();
					getContainer().getShell().close();
					
					new CreateUserManagementSystemAction().run(null);
				}
			});
			
		}
		
		mainWrapper.layout();
	}
	
	/**
	 * Get {@link ISynchronizationPerformerHop} based on currently selected {@link UserManagementSystem}.
	 * 
	 * @return {@link ISynchronizationPerformerHop} implementation
	 */
	public ISynchronizationPerformerHop getSynchronizationHop() {
		return currentSynchronizationHop;
	}
	
	/**
	 * Get what next page should be doing: import or export.
	 * 
	 * @return selected {@link SyncDirection}
	 */
	public SyncDirection getSyncDirection(){
		return syncDirection;
	}
	
	/**
	 * Get selected {@link UserManagementSystem}
	 * 
	 * @return selected {@link UserManagementSystem}
	 */
	public UserManagementSystem getSelectedUserManagementSystem(){
		return currentUserManagementSystem;
	}
	
	private ISelectionChangedListener syncPerfomerfSelectListener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent e) {
			if (e.getSelection() instanceof StructuredSelection){
			
				UserManagementSystem selectedUserManagementSystem = (UserManagementSystem) ((StructuredSelection) e.getSelection()).getFirstElement();
				importButton.setEnabled(selectedUserManagementSystem != null);
				exportButton.setEnabled(selectedUserManagementSystem != null);
				if (selectedUserManagementSystem == null){
					return;
				}
				descriptionLabel.setText(importButton.getSelection() ? "Import was selected! TODO: description" : "Export was selected! TODO: description");
				
				setSelectedUserManagementSystemInternal(selectedUserManagementSystem);
				getContainer().updateButtons();
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	private void setSelectedUserManagementSystemInternal(UserManagementSystem selectedUserManagementSystem){
		currentUserManagementSystem = selectedUserManagementSystem;
		
		if (currentSynchronizationHop != null) {
			if (getWizard() instanceof DynamicPathWizard) {
				DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
				wiz.removeDynamicWizardPage(currentSynchronizationHop.getEntryPage());
			}
		}
		
		IWizardHop wizardHop = UserManagementSystemUIMappingRegistry.sharedInstance().getWizardHop(
				(Class<? extends UserManagementSystemType<?>>) currentUserManagementSystem.getType().getClass(),
				(Class<? extends DynamicPathWizard>) getWizard().getClass()
				);
		if (wizardHop instanceof ISynchronizationPerformerHop) {
			currentSynchronizationHop = (ISynchronizationPerformerHop) wizardHop;
			currentSynchronizationHop.configurePages(selectedUserManagementSystem, getSyncDirection());
			if (getWizard() instanceof DynamicPathWizard) {
				DynamicPathWizard wiz = (DynamicPathWizard)getWizard();
				wiz.addDynamicWizardPage(currentSynchronizationHop.getEntryPage());
			}
		}
	}
}
