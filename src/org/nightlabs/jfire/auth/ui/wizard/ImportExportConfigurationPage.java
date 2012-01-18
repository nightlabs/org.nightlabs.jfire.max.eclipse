package org.nightlabs.jfire.auth.ui.wizard;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.swt.widgets.Label;
import org.nightlabs.base.ui.resource.SharedImages;
import org.nightlabs.base.ui.resource.SharedImages.ImageDimension;
import org.nightlabs.base.ui.resource.SharedImages.ImageFormat;
import org.nightlabs.base.ui.table.AbstractTableComposite;
import org.nightlabs.base.ui.wizard.DynamicPathWizard;
import org.nightlabs.base.ui.wizard.IWizardHop;
import org.nightlabs.base.ui.wizard.WizardHopPage;
import org.nightlabs.jfire.auth.ui.JFireAuthUIPlugin;
import org.nightlabs.jfire.auth.ui.UserManagementSystemTable;
import org.nightlabs.jfire.auth.ui.UserManagementSystemUIMappingRegistry;
import org.nightlabs.jfire.auth.ui.actions.CreateUserManagementSystemAction;
import org.nightlabs.jfire.auth.ui.resource.Messages;
import org.nightlabs.jfire.auth.ui.wizard.ISynchronizationPerformerHop.SyncDirection;
import org.nightlabs.jfire.security.integration.SynchronizableUserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystem;
import org.nightlabs.jfire.security.integration.UserManagementSystemType;
import org.nightlabs.jfire.security.integration.id.UserManagementSystemID;

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
	private SynchronizableUserManagementSystem<?> currentUserManagementSystem;
	
	private Composite mainWrapper;
	private Button importButton;
	private Button exportButton;
	private Label loadingLabel;
	private UserManagementSystemTable userManagementSystemTable;

	private SyncDirection syncDirection = SyncDirection.IMPORT;
	
	/**
	 * Default constructor
	 */
	public ImportExportConfigurationPage()	{
		super(ImportExportConfigurationPage.class.getName(), Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.pageTitle"), SharedImages.getWizardPageImageDescriptor(JFireAuthUIPlugin.sharedInstance(), ImportExportConfigurationPage.class)); //$NON-NLS-1$
		setDescription(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.pageDescription")); //$NON-NLS-1$
	}

	/**
	 * Sets selected {@link UserManagementSystem} and {@link SyncDirection} so this page could be omitted and shows the next one.
	 * 
	 * @param userManagementSystem {@link UserManagementSystem} selected for synchronization
	 * @param syncDirection Direction of synchronization, either import or export
	 */
	public void proceedToNextPage(SynchronizableUserManagementSystem<?> userManagementSystem, SyncDirection syncDirection) {
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
		gridLayout.horizontalSpacing = 10;
		mainWrapper.setLayout(gridLayout);
		
		loadingLabel = new Label(mainWrapper, SWT.NONE);
		loadingLabel.setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.loadingLabel")); //$NON-NLS-1$
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
	public void setUserManagementSystems(List<SynchronizableUserManagementSystem<?>> allUserManagementSystems) {
		if (mainWrapper == null){
			throw new IllegalStateException(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.setUserManagementSystemsIllegalyCalledExceptionText")); //$NON-NLS-1$
		}
		
		if (allUserManagementSystems != null && !allUserManagementSystems.isEmpty()){
			
			if (userManagementSystemTable == null){
				loadingLabel.dispose();
				
				userManagementSystemTable = new UserManagementSystemTable(mainWrapper, AbstractTableComposite.DEFAULT_STYLE_SINGLE_BORDER);
				userManagementSystemTable.setLinesVisible(true);
				userManagementSystemTable.setHeaderVisible(true);
				userManagementSystemTable.addSelectionChangedListener(syncPerfomerfSelectListener);
				userManagementSystemTable.getTableViewer().setSorter(new ViewerSorter());
				userManagementSystemTable.setLayoutData(new GridData(GridData.FILL_BOTH));
				
				Composite buttonsWrapper = new Composite(mainWrapper, SWT.NONE);
				buttonsWrapper.setLayout(new GridLayout(1, false));
				buttonsWrapper.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_VERTICAL));
				
				importButton = new Button(buttonsWrapper, SWT.RADIO);
				importButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.importCheckButtonLabel")); //$NON-NLS-1$
				importButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.importCheckButtonTooltip")); //$NON-NLS-1$
				importButton.setSelection(true);
				importButton.setEnabled(false);
				importButton.setImage(
						SharedImages.sharedInstance().getImage(JFireAuthUIPlugin.sharedInstance(), ImportExportConfigurationPage.class, "importButton", ImageDimension._16x16.toString(), ImageFormat.png)); //$NON-NLS-1$
				importButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (importButton.getSelection()){
							syncDirection = SyncDirection.IMPORT;
							
							SelectionChangedEvent event = new SelectionChangedEvent(
									userManagementSystemTable, userManagementSystemTable.getSelection());
							syncPerfomerfSelectListener.selectionChanged(event);
						}
					}
				});
	
				exportButton = new Button(buttonsWrapper, SWT.RADIO);
				exportButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.exportCheckButtonLabel")); //$NON-NLS-1$
				exportButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.exportCheckButtonDescription")); //$NON-NLS-1$
				exportButton.setEnabled(false);
				exportButton.setImage(
						SharedImages.sharedInstance().getImage(JFireAuthUIPlugin.sharedInstance(), ImportExportConfigurationPage.class, "exportButton", ImageDimension._16x16.toString(), ImageFormat.png)); //$NON-NLS-1$
				exportButton.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (exportButton.getSelection()){
							syncDirection = SyncDirection.EXPORT;

							SelectionChangedEvent event = new SelectionChangedEvent(
									userManagementSystemTable, userManagementSystemTable.getSelection());
							syncPerfomerfSelectListener.selectionChanged(event);
						}
					}
				});
				
			}
			
			Set<UserManagementSystemID> userManagementSystemIDs = new HashSet<UserManagementSystemID>();
			for (SynchronizableUserManagementSystem<?> syncUserManagementSystem : allUserManagementSystems){
				if (syncUserManagementSystem instanceof UserManagementSystem){
					userManagementSystemIDs.add(((UserManagementSystem) syncUserManagementSystem).getUserManagementSystemObjectID());
				}
			}
			userManagementSystemTable.setUserManagementSystemsIDs(userManagementSystemIDs);
			userManagementSystemTable.reload();

		}else{
			
			loadingLabel.setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.loadingLabel_noUserManagementSystemsExist")); //$NON-NLS-1$
			
			Button openCreationWizardButton = new Button(mainWrapper, SWT.PUSH);
			openCreationWizardButton.setText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.openCreationWizardButtonLabel")); //$NON-NLS-1$
			openCreationWizardButton.setToolTipText(Messages.getString("org.nightlabs.jfire.auth.ui.wizard.ImportExportConfigurationPage.openCreationWizardButtonTooltip")); //$NON-NLS-1$
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
	 * Get selected {@link SynchronizableUserManagementSystem}
	 * 
	 * @return selected {@link SynchronizableUserManagementSystem}
	 */
	public SynchronizableUserManagementSystem<?> getSelectedUserManagementSystem(){
		return currentUserManagementSystem;
	}
	
	private ISelectionChangedListener syncPerfomerfSelectListener = new ISelectionChangedListener() {
		
		@Override
		public void selectionChanged(SelectionChangedEvent e) {
			if (e.getSelection() instanceof StructuredSelection){
			
				SynchronizableUserManagementSystem<?> selectedUserManagementSystem = (SynchronizableUserManagementSystem<?>) ((StructuredSelection) e.getSelection()).getFirstElement();
				importButton.setEnabled(selectedUserManagementSystem != null);
				exportButton.setEnabled(selectedUserManagementSystem != null);
				if (selectedUserManagementSystem == null){
					return;
				}
				
				setSelectedUserManagementSystemInternal(selectedUserManagementSystem);
				getContainer().updateButtons();
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	private void setSelectedUserManagementSystemInternal(SynchronizableUserManagementSystem<?> selectedUserManagementSystem){
		currentUserManagementSystem = selectedUserManagementSystem;
		
		if (currentSynchronizationHop != null) {
			getWizardHop().removeHopPage(currentSynchronizationHop.getEntryPage());
		}
		
		IWizardHop wizardHop = UserManagementSystemUIMappingRegistry.sharedInstance().getWizardHop(
				(Class<? extends UserManagementSystemType<?>>) ((UserManagementSystem) currentUserManagementSystem).getType().getClass(),
				(Class<? extends DynamicPathWizard>) getWizard().getClass()
				);
		if (wizardHop instanceof ISynchronizationPerformerHop) {
			currentSynchronizationHop = (ISynchronizationPerformerHop) wizardHop;
			currentSynchronizationHop.configurePages(selectedUserManagementSystem, getSyncDirection());
			getWizardHop().addHopPage(currentSynchronizationHop.getEntryPage());
		}
	}
}
